package com.ampmangu.degrees.service.impl;

import com.ampmangu.degrees.domain.PersonRelation;
import com.ampmangu.degrees.repository.PersonRelationRepository;
import com.ampmangu.degrees.service.PersonRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonRelationServiceImpl implements PersonRelationService {
    private final Logger log = LoggerFactory.getLogger(PersonRelationServiceImpl.class);

    private final PersonRelationRepository personRelationRepository;

    public PersonRelationServiceImpl(PersonRelationRepository personRelationRepository) {
        this.personRelationRepository = personRelationRepository;
    }

    @Override
    public PersonRelation save(PersonRelation personRelation) {
        return personRelationRepository.save(personRelation);
    }

    @Override
    public List<PersonRelation> findAll() {
        log.info("Looking for all relations");
        return personRelationRepository.findAll();
    }

    @Override
    public Optional<PersonRelation> findOne(PersonRelation.PersonRelationId id) {
        log.info("Looking for relation with id {}", id);
        List<PersonRelation> pr = personRelationRepository.findAll().stream().filter(personRelation -> personRelation.getId().equals(id)).collect(Collectors.toList());
        if (pr.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pr.get(0));
    }

    @Override
    public void delete(PersonRelation.PersonRelationId id) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<PersonRelation> allPersonsOfLeftSide(Long leftSideId) {
        return personRelationRepository.findAll().stream().filter(personRelation -> personRelation.getLeftSidePerson().getId().equals(leftSideId)).collect(Collectors.toList());
    }

}
