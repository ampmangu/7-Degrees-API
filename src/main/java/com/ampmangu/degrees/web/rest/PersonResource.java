package com.ampmangu.degrees.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonResource {
    private final Logger log = LoggerFactory.getLogger(PersonResource.class);

    PersonResource() {

    }

    /**
     * {@code POST  /people} : Create a new person.
     *
     * @param person the person to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new person, or with status {@code 400 (Bad Request)} if the person has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/people")
    public ResponseEntity<String> createPerson(@Valid @RequestBody String person) throws URISyntaxException {
        log.debug("REST request to save Person : {}", person);
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * {@code PUT  /people} : Updates an existing person.
     *
     * @param person the person to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated person,
     * or with status {@code 400 (Bad Request)} if the person is not valid,
     * or with status {@code 500 (Internal Server Error)} if the person couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/people")
    public ResponseEntity<String> updatePerson(@Valid @RequestBody String person) throws URISyntaxException {
        log.debug("REST request to update Person : {}", person);
        throw new UnsupportedOperationException("Not implemented");

    }

    /**
     * {@code GET  /people} : get all the people.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of people in body.
     */
    @GetMapping("/people")
    public List<String> getAllPeople() {
        log.debug("REST request to get all People");
        throw new UnsupportedOperationException("Not implemented");

    }

    /**
     * {@code GET  /people/:id} : get the "id" person.
     *
     * @param id the id of the person to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the person, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/people/{id}")
    public ResponseEntity<String> getPerson(@PathVariable String id) {
        log.debug("REST request to get Person : {}", id);
        throw new UnsupportedOperationException("Not implemented");

    }

    /**
     * {@code DELETE  /people/:id} : delete the "id" person.
     *
     * @param id the id of the person to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/people/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable String id) {
        log.debug("REST request to delete Person : {}", id);
        throw new UnsupportedOperationException("Not implemented");
    }
}
