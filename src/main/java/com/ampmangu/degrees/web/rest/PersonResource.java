package com.ampmangu.degrees.web.rest;

import com.ampmangu.degrees.domain.ActorData;
import com.ampmangu.degrees.domain.Person;
import com.ampmangu.degrees.remote.MovieDBService;
import com.ampmangu.degrees.remote.models.PeopleDetail;
import com.ampmangu.degrees.remote.models.PeopleResults;
import com.ampmangu.degrees.service.ActorDataService;
import com.ampmangu.degrees.service.PersonRelationService;
import com.ampmangu.degrees.service.PersonService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ampmangu.degrees.remote.MovieDBUtils.processPersonRequest;
import static com.ampmangu.degrees.remote.MovieDBUtils.savePerson;
import static com.ampmangu.degrees.service.PersonUtils.saveRelation;

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
        return ResponseEntity.ok().body(forceTraversal(actorDataService, personRelationService));
    }

    public static List<Integer> forceTraversal(ActorDataService actorDataService, PersonRelationService personRelationService) {
        HashSet<Object> duplicates = new HashSet<>();
        List<ActorData> actorDataDistinctByTitle = actorDataService.findAll();
        actorDataDistinctByTitle.removeIf(actorData -> duplicates.add(actorData.getTitle()));
        List<Integer> idToTraverse =
                actorDataDistinctByTitle.stream().map(ActorData::getRemoteDbId).distinct().collect(Collectors.toList());
        for (Integer remoteId : idToTraverse) {
            List<Person> actorDataList = actorDataService.findAll().stream().filter(
                    actorData -> actorData.getRemoteDbId() != null && actorData.getRemoteDbId().equals(remoteId)).map(ActorData::getPerson).collect(Collectors.toList());
            if (actorDataList.size() > 1) {
                saveRelation(actorDataList, personRelationService);
            }
        }
        return idToTraverse;
    }

    @GetMapping("/people/{name}/actor")
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

    private Person getPersonFromProvider(@PathVariable String name) {
        Optional<Person> dbPerson = personService.findByName(name);
        if (dbPerson.isPresent()) {
            return dbPerson.get();
        } else {
            final PeopleDetail[] result = {new PeopleDetail()};
            Observable<PeopleResults> actorObs = movieDBService.getActorList(name);
            final String[] nameResult = {""};
            actorObs.subscribe(actor -> nameResult[0] = actor.getResults().get(0).getName() != null ? actor.getResults().get(0).getName() : name);
            actorObs.subscribe(actor -> result[0] = processPersonRequest(actor.getResults().get(0).getId(), movieDBService), this::processError);
            Person person = savePerson(result[0], nameResult[0], personService, actorDataService);
            try {
                log.info("Saving in cache {} ", nameResult[0]);
                jedisClient.set(nameResult[0].toUpperCase(), mapper.writeValueAsString(person));
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
