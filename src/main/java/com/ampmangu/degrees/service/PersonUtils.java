package com.ampmangu.degrees.service;

import com.ampmangu.degrees.domain.ActorData;
import com.ampmangu.degrees.domain.Person;
import com.ampmangu.degrees.domain.PersonRelation;
import com.ampmangu.degrees.utils.DegreeResponse;
import com.ampmangu.degrees.utils.DegreeResponseBuilder;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Component
public class PersonUtils {
    private static final Logger log = LoggerFactory.getLogger(PersonUtils.class);

    private static EntityManager entityManager;

    private static final String PERSON_ID_QUERY = "select * from person where id in (select distinct person_id from actor_data where remote_db_id in (select distinct remote_db_id from actor_data where person_id = ?1 order by remote_db_id desc) and person_id != ?1)";
    private static final String PERSON_ID_QUERY_FILTER = PERSON_ID_QUERY + "and id not in (?2)";
    private static final String PERSON_MATCHES = "select l.* from actor_data l inner join actor_data r on l.title = r.title where l.person_id = ?1 and r.person_id = ?2";

    public PersonUtils(EntityManager entityManager) {
        PersonUtils.entityManager = entityManager;
    }

    public static void saveRelation(List<Person> actorDataList, PersonRelationService personRelationService, PersonService personService) {
        int position = 0;
        for (Person person : actorDataList) {
            List<Person> nextPersons = new ArrayList<>(actorDataList);
            nextPersons.remove(position);
            for (Person otherPerson : nextPersons) {
                saveRelation(person, otherPerson, personRelationService, personService);
                saveRelation(otherPerson, person, personRelationService, personService);
            }
            position = position + 1;
        }
    }

    private static void saveRelation(Person person, Person otherPerson, PersonRelationService personRelationService, PersonService personService) {
        PersonRelation personRelation = new PersonRelation();
        personRelation.setLeftSidePerson(person);
        personRelation.setRightSidePerson(otherPerson);
        personRelation.setStillValid(true);
        PersonRelation.PersonRelationId personRelationId = new PersonRelation.PersonRelationId();
        personRelationId.setLeftSideId(Math.toIntExact(person.getId()));
        personRelationId.setRightSideId(Math.toIntExact(otherPerson.getId()));
        personRelation.setId(personRelationId);
        try {
            personRelationService.save(personRelation);
        } catch (HibernateException ex) {
            log.warn("Not saving this one:", ex);
        }
        person.addRelations(personRelation);
        personService.save(person);
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, List<DegreeResponse>> degreesSeparation(Person leftSide, Person rightSide, Integer accDegree, List<DegreeResponse> degreeResponseList) {
        Map<Integer, List<DegreeResponse>> rtn = new HashMap<>();
        DegreeResponseBuilder degreeResponseBuilder = new DegreeResponseBuilder();
        if (accDegree >= 7) {
            rtn.put(accDegree, degreeResponseList);
            return rtn;
        }
        Long leftSideId = leftSide.getId();
        Long rightSideId = rightSide.getId();
        Integer degree = accDegree;
        Optional<ActorData> match = getMatch(leftSideId, rightSideId);
        if (match.isPresent()) {
            ActorData data = match.get();
            Person dataPersonTo = new Person();
            dataPersonTo.setId(data.getId());
            dataPersonTo.setName(data.getTitle());
            degree = degree + 1;
            List<DegreeResponse> degreeResponses = new ArrayList<>();
            degreeResponses.add(new DegreeResponse(leftSide, dataPersonTo, null));
            rtn.put(degree, degreeResponses);
        } else {
            degree = degree + 1;
            Set<Person> personSet = getListsOfIdToTraverse(leftSideId, null);
            DegreeResponseBuilder builder = new DegreeResponseBuilder();
            builder.addFilter(leftSide);
            for (Person person : personSet) {
                DegreeResponseBuilder maps = degreeSeparationRec(person, rightSide, degree, builder);
                if (maps.isEnded()) {
                    maps.addPerson(leftSide);
                    maps.addActorData(getMatch(leftSideId, person.getId()).get());
                    degreeResponseBuilder = maps;
                    break;
                }
            }
            rtn = degreeResponseBuilder.buildResponse();
        }
        return rtn;
    }

    @SuppressWarnings("unchecked")
    private static DegreeResponseBuilder degreeSeparationRec(Person leftSide, Person rightSide, Integer accDegree, DegreeResponseBuilder responseBuilder) {
        if (accDegree >= 7) {
            return responseBuilder;
        }
        Long leftSideId = leftSide.getId();
        Long rightSideId = rightSide.getId();
        Integer degree = accDegree;
        degree = degree + 1;
        Optional<ActorData> match = getMatch(leftSideId, rightSideId);
        if (match.isPresent()) {
            ActorData data = match.get();
            responseBuilder.addPerson(leftSide);
            responseBuilder.addActorData(data);
            responseBuilder.setEnded(true);
            return responseBuilder;
        } else {
            Set<Person> personSet = getListsOfIdToTraverse(leftSideId, responseBuilder.getFilters());
            for (Person person : personSet) {
                responseBuilder = degreeSeparationRec(person, rightSide, degree, responseBuilder);
                if (responseBuilder.isEnded()) {
                    ActorData localMatch = getMatch(leftSideId, person.getId()).get();
                    responseBuilder.addPerson(leftSide);
                    responseBuilder.addActorData(localMatch);
                    break;
                }
            }
        }
        return responseBuilder;
    }

    @SuppressWarnings("unchecked")
    private static Set<Person> getListsOfIdToTraverse(Long leftSideId, List<Long> beginningId) {
        if (beginningId == null) {
            Query query = PersonUtils.entityManager.createNativeQuery(PERSON_ID_QUERY, Person.class);
            query.setParameter(1, leftSideId);
            List resultList = query.getResultList();
            return new HashSet<>(resultList);
        } else {
            Query query = PersonUtils.entityManager.createNativeQuery(PERSON_ID_QUERY_FILTER, Person.class);
            query.setParameter(1, leftSideId);
            query.setParameter(2, beginningId);
            List resultList = query.getResultList();
            return new HashSet<>(resultList);
        }
    }

    private static Optional<ActorData> getMatch(Long leftSideId, Long rightSideId) {
        Query query = PersonUtils.entityManager.createNativeQuery(PERSON_MATCHES, ActorData.class);
        query.setParameter(1, leftSideId);
        query.setParameter(2, rightSideId);
        List resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return Optional.of((ActorData) resultList.get(0));
        }
        return Optional.empty();
    }
}
