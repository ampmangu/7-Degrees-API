package com.ampmangu.degrees.service.impl;


import com.ampmangu.degrees.domain.ActorData;
import com.ampmangu.degrees.repository.ActorDataRepository;
import com.ampmangu.degrees.service.ActorDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link ActorData}.
 */
@Service
@Transactional
public class ActorDataServiceImpl implements ActorDataService {

    private final Logger log = LoggerFactory.getLogger(ActorDataServiceImpl.class);

    private final ActorDataRepository actorDataRepository;

    public ActorDataServiceImpl(ActorDataRepository actorDataRepository) {
        this.actorDataRepository = actorDataRepository;
    }

    /**
     * Save a actorData.
     *
     * @param actorData the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ActorData save(ActorData actorData) {
        log.debug("Request to save ActorData : {}", actorData);
        return actorDataRepository.save(actorData);
    }

    /**
     * Get all the actorData.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ActorData> findAll() {
        log.debug("Request to get all ActorData");
        return actorDataRepository.findAll();
    }


    /**
     * Get one actorData by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ActorData> findOne(Long id) {
        log.debug("Request to get ActorData : {}", id);
        return actorDataRepository.findById(id);
    }

    /**
     * Delete the actorData by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ActorData : {}", id);
        actorDataRepository.deleteById(id);
    }

    @Override
    public Optional<ActorData> findByPersonAndRemoteId(Long actorId, Integer remoteId) {
        log.debug("Request to find data with id {} and remote id {}", actorId, remoteId);
        List<ActorData> data = actorDataRepository.findAll().stream().filter(
                actorData -> actorData.getPerson() != null && actorData.getPerson().getId().equals(actorId) &&
                        actorData.getRemoteDbId() != null && actorData.getRemoteDbId().equals(remoteId)
        ).collect(Collectors.toList());
        if (data.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(data.get(0));
    }


}
