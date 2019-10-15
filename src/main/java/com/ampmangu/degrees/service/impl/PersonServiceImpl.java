package com.ampmangu.degrees.service.impl;

import com.ampmangu.degrees.domain.Person;
import com.ampmangu.degrees.repository.ActorDataRepository;
import com.ampmangu.degrees.repository.PersonRepository;
import com.ampmangu.degrees.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);
    private final PersonRepository personRepository;
    private final ActorDataRepository actorDataRepository;

    public PersonServiceImpl(PersonRepository personRepository, ActorDataRepository actorDataRepository) {
        this.personRepository = personRepository;
        this.actorDataRepository = actorDataRepository;
    }

    @Override
    public Person save(Person person) {
        log.debug("Request to save person : {}", person);
        if (person.getActorDataList() != null) {
            person.getActorDataList().forEach(actorData -> actorData.setPerson(person));
        }
        Person person1 = personRepository.save(person);
        if (person.getActorDataList() != null) {
            actorDataRepository.saveAll(person.getActorDataList());
            actorDataRepository.flush();
        }
        return person1;
    }

    @Override
    public List<Person> findAll() {
        log.debug("Request to find all persons");
        return personRepository.findAll();
    }

    @Override
    public Optional<Person> findOne(Long id) {
        log.debug("Request to find person by id {}", id);
        return personRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete person by id {}", id);
        personRepository.deleteById(id);
    }

    public boolean existsByRemoteId(Integer remoteId) {
        log.debug("Looking for actor with remote id {}", remoteId);
        return personRepository.findAll().stream().anyMatch(person -> person.getRemoteDbId() != null && person.getRemoteDbId().equals(remoteId));
    }

    public Optional<Person> findByRemoteId(Integer remoteId) {
        return Optional.of(personRepository.findAll().stream().filter(person -> person.getRemoteDbId() != null && person.getRemoteDbId().equals(remoteId)).collect(Collectors.toList()).get(0));
    }

    @Override
    public Optional<Person> findByName(String name) {
        List<Person> persons = personRepository.findAll().stream().filter(person -> person.getName() != null && person.getName().toUpperCase().contains(name.toUpperCase())).collect(Collectors.toList());
        if (persons.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(persons.get(0));
    }
}
