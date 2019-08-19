package com.ampmangu.degrees.service;

import com.ampmangu.degrees.domain.ActorData;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link ActorData}.
 */
public interface ActorDataService {

    /**
     * Save a actorData.
     *
     * @param actorData the entity to save.
     * @return the persisted entity.
     */
    ActorData save(ActorData actorData);

    /**
     * Get all the actorData.
     *
     * @return the list of entities.
     */
    List<ActorData> findAll();


    /**
     * Get the "id" actorData.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ActorData> findOne(Long id);

    /**
     * Delete the "id" actorData.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Optional<ActorData> findByPersonAndRemoteId(Long actorId, Integer remoteId);
}
