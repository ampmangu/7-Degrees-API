package com.ampmangu.degrees.service;

import com.ampmangu.degrees.domain.Person;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Person}.
 */
public interface PersonService {
    /**
     * Save a person
     *
     * @param person the entity to save
     * @return the persisted entity
     */
    Person save(Person person);

    /**
     * Get all the persons
     *
     * @return the list of entities
     */
    List<Person> findAll();

    /**
     * Get the "id" person
     *
     * @param id of the entity
     * @return the entity
     */
    Optional<Person> findOne(Long id);

    /**
     * Delete the person
     *
     * @param id of the entity
     */
    void delete(Long id);

    boolean existsByRemoteId(Integer remoteId);

    Optional<Person> findByRemoteId(Integer remoteId);
}
