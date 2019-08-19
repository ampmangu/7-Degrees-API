package com.ampmangu.degrees.repository;

import com.ampmangu.degrees.domain.PersonRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PersonRelation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PersonRelationRepository extends JpaRepository<PersonRelation, Long> {

}
