package com.ampmangu.degrees.service;

import com.ampmangu.degrees.domain.ActorData;
import com.ampmangu.degrees.domain.Person;
import com.ampmangu.degrees.domain.PersonRelation;
import com.ampmangu.degrees.utils.DegreeResponse;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PersonUtils {
    private static final Logger log = LoggerFactory.getLogger(PersonUtils.class);

    private PersonUtils() {

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

    public static Map<Integer, List<DegreeResponse>> degreesSeparation(Person leftSide, Person rightSide, ActorDataService actorDataService, PersonRelationService personRelationService, Integer accDegree) {
        Map<Integer, List<DegreeResponse>> rtn = new HashMap<>();
        if (accDegree >= 7) {
            rtn.put(accDegree, new ArrayList<>());
            return rtn;
        }
        Long leftSideId = leftSide.getId();
        Long rightSideId = rightSide.getId();
        Integer degree = accDegree;
        List<ActorData> allActorData = actorDataService.findAll();
        List<ActorData> actorDataListFromLeftSide = allActorData.stream().filter(actorData -> actorData.getPerson().getId().equals(leftSideId)).collect(Collectors.toList());
        List<ActorData> actorDataListFromRightSide = allActorData.stream().filter(actorData -> actorData.getPerson().getId().equals(rightSideId)).collect(Collectors.toList());
//        List<PersonRelation> relationsOfLeftSide = personRelationService.allPersonsOfLeftSide(leftSideId);
        Optional<ActorData> match = getDuplicate(actorDataListFromLeftSide, actorDataListFromRightSide);
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
//            degree = degree + 1;
//            List<Person> personsOfLeftSide = relationsOfLeftSide.stream().map(PersonRelation::getRightSidePerson).collect(Collectors.toList());
//            for(Person p: personsOfLeftSide) {
//                degreesSeparation(p, rightSide, actorDataService, personRelationService, degree);
//            }

        }
        return rtn;
    }

    private static Optional<ActorData> getDuplicate(List<ActorData> actorDataList, List<ActorData> actorDataRightSide) {
        ActorData actorData = null;
        Set<Integer> remoteIds = new HashSet<>();
        actorDataList.forEach(actorData1 -> remoteIds.add(actorData1.getRemoteDbId()));
        for (ActorData data : actorDataRightSide) {
            boolean added = remoteIds.add(data.getRemoteDbId());
            if (!added) {
                actorData = data;
                break;
            }
        }
        if (actorData == null) {
            return Optional.empty();
        }
        return Optional.of(actorData);
    }
}
