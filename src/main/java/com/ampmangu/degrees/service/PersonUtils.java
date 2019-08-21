package com.ampmangu.degrees.service;

import com.ampmangu.degrees.domain.Person;
import com.ampmangu.degrees.domain.PersonRelation;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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
}
