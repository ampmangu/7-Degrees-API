package com.ampmangu.degrees.web.rest;

import com.ampmangu.degrees.remote.MovieDBService;
import com.ampmangu.degrees.remote.models.PeopleDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

import static com.ampmangu.degrees.remote.MovieDBUtils.processPersonRequest;

@RestController
@RequestMapping("/api")
public class PersonResource {
    private final Logger log = LoggerFactory.getLogger(PersonResource.class);

    @Autowired
    private MovieDBService movieDBService;

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
    public ResponseEntity<String> getPersonf(@PathVariable String id) {
        log.debug("REST request to get Person : {}", id);
        throw new UnsupportedOperationException("Not implemented");

    }

    @GetMapping("/people/{name}/actor")
    public ResponseEntity<PeopleDetail> getPerson(@PathVariable String name) {
        final PeopleDetail[] result = {new PeopleDetail()};
        movieDBService.getActorList(name)
                .subscribe(actor -> result[0] = processPersonRequest(actor.getResults().get(0).getId(), movieDBService), this::processError);
        return ResponseEntity.ok().body(result[0]);
    }

    private void processError(Throwable err) {
        log.error(err.getLocalizedMessage());
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
