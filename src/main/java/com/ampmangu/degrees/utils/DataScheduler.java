package com.ampmangu.degrees.utils;

import com.ampmangu.degrees.service.ActorDataService;
import com.ampmangu.degrees.service.PersonRelationService;
import com.ampmangu.degrees.service.PersonService;
import com.ampmangu.degrees.web.rest.PersonResource;
import org.mariadb.jdbc.internal.logging.Logger;
import org.mariadb.jdbc.internal.logging.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
public class DataScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DataScheduler.class);
    private final PersonService personService;
    private final ActorDataService actorDataService;
    private final PersonRelationService personRelationService;

    public DataScheduler(PersonService personService,
                         ActorDataService actorDataService, PersonRelationService personRelationService) {
        this.personService = personService;
        this.actorDataService = actorDataService;
        this.personRelationService = personRelationService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void scheduleDataRelation() {
        logger.info("Scheduled traversal");
        PersonResource.forceTraversal(actorDataService, personRelationService);
    }
}
