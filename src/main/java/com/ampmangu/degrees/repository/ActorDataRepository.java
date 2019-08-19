package com.ampmangu.degrees.repository;

import com.ampmangu.degrees.domain.ActorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ActorData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActorDataRepository extends JpaRepository<ActorData, Long> {

}
