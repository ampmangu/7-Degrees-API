package com.ampmangu.degrees.web.rest;

import com.ampmangu.degrees.domain.ActorData;
import com.ampmangu.degrees.domain.Person;
import com.ampmangu.degrees.remote.MovieDBService;
import com.ampmangu.degrees.remote.models.PeopleDetail;
import com.ampmangu.degrees.remote.models.PeopleResults;
import com.ampmangu.degrees.service.ActorDataService;
import com.ampmangu.degrees.service.PersonRelationService;
import com.ampmangu.degrees.service.PersonService;
import com.ampmangu.degrees.utils.DegreeResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static com.ampmangu.degrees.remote.MovieDBUtils.processPersonRequest;
import static com.ampmangu.degrees.remote.MovieDBUtils.savePerson;
import static com.ampmangu.degrees.service.PersonUtils.degreesSeparation;
import static com.ampmangu.degrees.service.PersonUtils.saveRelation;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.stripAccents;

@RestController
@RequestMapping("/api")
public class PersonResource {
    private final Logger log = LoggerFactory.getLogger(PersonResource.class);

    private final PersonService personService;

    private final ActorDataService actorDataService;

    private final PersonRelationService personRelationService;

    private MovieDBService movieDBService;

    private Jedis jedisClient;

    private ObjectMapper mapper = new ObjectMapper();

    PersonResource(PersonService personService, MovieDBService dbService, ActorDataService actorDataService, Jedis jedisClient, JavaTimeModule javaTimeModule, PersonRelationService personRelationService) {
        this.personService = personService;
        this.movieDBService = dbService;
        this.actorDataService = actorDataService;
        this.jedisClient = jedisClient;
        this.personRelationService = personRelationService;
        mapper.findAndRegisterModules();
        mapper.registerModule(javaTimeModule);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * {@code POST  /people} : Create a new person.
     *
     * @param person the person to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new person, or with status {@code 400 (Bad Request)} if the person has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/people")
    public ResponseEntity<String> createPerson(@Valid @RequestBody String person) throws URISyntaxException {
        log.debug("REST request to save Person : {}", person);
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * {@code PUT  /people} : Updates an existing person.
     *
     * @param person the person to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated person,
     * or with status {@code 400 (Bad Request)} if the person is not valid,
     * or with status {@code 500 (Internal Server Error)} if the person couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/people")
    public ResponseEntity<String> updatePerson(@Valid @RequestBody String person) throws URISyntaxException {
        log.debug("REST request to update Person : {}", person);
        throw new UnsupportedOperationException("Not implemented");

    }

    /**
     * {@code GET  /people} : get all the people.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of people in body.
     */
    @GetMapping("/people")
    public List<String> getAllPeople() {
        log.debug("REST request to get all People");
        throw new UnsupportedOperationException("Not implemented");

    }

    @GetMapping("/people/forcetraversal")
    public ResponseEntity<List<Integer>> forceTraversal() {
        return ResponseEntity.ok().body(forceTraversal(actorDataService, personRelationService, personService));
    }

    public static List<Integer> forceTraversal(ActorDataService actorDataService, PersonRelationService personRelationService, PersonService personService) {
        HashSet<Object> duplicates = new HashSet<>();
        List<ActorData> actorDataDistinctByTitle = actorDataService.findAll();
        actorDataDistinctByTitle.removeIf(actorData -> duplicates.add(actorData.getTitle()));
        List<Integer> idToTraverse =
                actorDataDistinctByTitle.stream().map(ActorData::getRemoteDbId).distinct().collect(toList());
        for (Integer remoteId : idToTraverse) {
            List<Person> actorDataList = actorDataService.findAll().stream().filter(
                    actorData -> actorData.getRemoteDbId() != null && actorData.getRemoteDbId().equals(remoteId)).map(ActorData::getPerson).collect(toList());
            if (actorDataList.size() > 1) {
                saveRelation(actorDataList, personRelationService, personService);
            }
        }
        return idToTraverse;
    }

    @GetMapping("/people/actor/{name1}/{name2}")
    @ResponseBody
    public ResponseEntity<DegreeResponse> getDegrees(@PathVariable String name1, @PathVariable String name2) {
        //We either ensure having it or getting it fresh
        ResponseEntity<Person> firstResponseEntity = getPerson(name1);
        Person firstPerson = firstResponseEntity.getBody();
        ResponseEntity<Person> secondResponseEntity = getPerson(name2);
        Person secondPerson = secondResponseEntity.getBody();
        if (firstPerson.getName().equalsIgnoreCase(secondPerson.getName())) {
            return ResponseEntity.ok().body(new DegreeResponse(firstPerson, firstPerson, 0));
        }
        Map<Integer, List<DegreeResponse>> degreeMap = degreesSeparation(firstPerson, secondPerson, 0, new ArrayList<>());
        Integer degree = (Integer) degreeMap.keySet().toArray()[0];
        List<DegreeResponse> degreeResponseList = degreeMap.get(degree);
        return ResponseEntity.ok().body(new DegreeResponse(firstPerson, secondPerson, degree, degreeResponseList));
    }

    @GetMapping("/people/actor/filldatabase")
    public ResponseEntity<List<Integer>> fillDatabase() {
        List<Integer> idList = new ArrayList<>();
        Optional<Person> max = personService.findAll().stream().max(Comparator.comparing(Person::getId));
        if (max.isPresent()) {
            Integer maxId = max.get().getRemoteDbId();
            for (int remoteId = maxId; remoteId < maxId + 10; remoteId = remoteId + 1) {
                final String[] name = new String[1];
                final String[] profilePicPath = {"_"};
                movieDBService.getActorBasicInfo(remoteId).subscribe(
                        basicPerson -> {
                            name[0] = basicPerson.getName();
                            profilePicPath[0] = basicPerson.getProfilePath();
                        }
                );
                PeopleDetail peopleDetail = processPersonRequest(remoteId, movieDBService);
                Person person = savePerson(peopleDetail, name[0], profilePicPath[0], personService, actorDataService);
                idList.add(person.getRemoteDbId());
            }
        }
        return ResponseEntity.ok().body(idList);
    }

    @GetMapping("/people/actor/{name}/")
    public ResponseEntity<Person> getPerson(@PathVariable String name) {
        log.info("Looking for actor {} ", name);
        String personCached = jedisClient.get(name.toUpperCase());
        if (personCached != null && !personCached.isEmpty()) {
            Person person;
            try {
                person = mapper.readValue(personCached, Person.class);
            } catch (IOException e) {
                log.error("Couldn't find actor {} in cache, going to search", name);
                person = getPersonFromProvider(name);
            }
            return ResponseEntity.ok().body(person);
        } else {
            Person person = getPersonFromProvider(name);
            return ResponseEntity.ok().body(person);
        }
    }

    private void setAndExpire(Person person, String key) throws JsonProcessingException {
        jedisClient.set(key, mapper.writeValueAsString(person));
        jedisClient.expire(key, 3600);
    }

    private Person getPersonFromProvider(String name) {
        Optional<Person> dbPerson = personService.findByName(name);
        if (dbPerson.isPresent()) {
            Person person = dbPerson.get();
            try {
                setAndExpire(person, stripAccents(person.getName().toUpperCase()));
            } catch (JsonProcessingException e) {
                log.warn("Couldn't save in redis user of id " + person.getId(), e);
            }
            return dbPerson.get();
        } else {
            final PeopleDetail[] result = {new PeopleDetail()};
            Observable<PeopleResults> actorObs = movieDBService.getActorList(name);
            final String[] nameResult = {""};
            final String[] profilePicPath = {""};
            actorObs.subscribe(actor -> nameResult[0] = actor.getResults().get(0).getName() != null ? actor.getResults().get(0).getName() : name);
            actorObs.subscribe(actor -> profilePicPath[0] = actor.getResults().get(0).getProfilePath() != null ? actor.getResults().get(0).getProfilePath() : "_");
            actorObs.subscribe(actor -> result[0] = processPersonRequest(actor.getResults().get(0).getId(), movieDBService), this::processError);
            Person person = savePerson(result[0], nameResult[0], profilePicPath[0], personService, actorDataService);
            try {
                String savedName = stripAccents(nameResult[0].toUpperCase());
                log.info("Saving in cache {} ", savedName);
                setAndExpire(person, savedName);
                setAndExpire(person, name.toUpperCase());
            } catch (JsonProcessingException ex) {
                log.warn("Couldn't save in redis user of id " + person.getId(), ex);
            }
            return person;
        }
    }

    private void processError(Throwable err) {
        log.error(err.getLocalizedMessage());
    }

    /**
     * {@code DELETE  /people/:id} : delete the "id" person.
     *
     * @param id the id of the person to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/people/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable String id) {
        log.debug("REST request to delete Person : {}", id);
        throw new UnsupportedOperationException("Not implemented");
    }
}
