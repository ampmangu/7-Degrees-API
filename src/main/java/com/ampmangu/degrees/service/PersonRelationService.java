package com.ampmangu.degrees.service;

import com.ampmangu.degrees.domain.PersonRelation;

import java.util.List;
import java.util.Optional;

public interface PersonRelationService {
    PersonRelation save(PersonRelation personRelation);

    List<PersonRelation> findAll();

    Optional<PersonRelation> findOne(PersonRelation.PersonRelationId id);

    void delete(PersonRelation.PersonRelationId id);

}
