package com.ampmangu.degrees.web.rest;


import com.ampmangu.degrees.Application;
import com.ampmangu.degrees.domain.Person;
import com.ampmangu.degrees.domain.TypePerson;
import com.ampmangu.degrees.remote.MovieDBService;
import com.ampmangu.degrees.repository.PersonRepository;
import com.ampmangu.degrees.service.ActorDataService;
import com.ampmangu.degrees.service.PersonRelationService;
import com.ampmangu.degrees.service.PersonService;
import com.ampmangu.degrees.web.rest.errors.ExceptionTranslator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import redis.clients.jedis.Jedis;

import javax.persistence.EntityManager;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.ampmangu.degrees.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class PersonResourceIT {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private ActorDataService actorDataService;

    @Autowired
    private MovieDBService movieDBService;

    @Autowired
    private PersonRelationService personRelationService;

    @Autowired
    private Jedis jedisClient;

    @Autowired
    private JavaTimeModule javaTimeModule;

    @Autowired
    private EntityManager em;

    @Qualifier("defaultValidator")
    @Autowired
    private Validator validator;

    private MockMvc restPersonMockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PersonResource personResource = new PersonResource(personService, movieDBService, actorDataService, jedisClient, javaTimeModule, personRelationService);
        this.restPersonMockMvc = MockMvcBuilders.standaloneSetup(personResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(javaTimeModule);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Person createDefaultEntity(EntityManager em) {
        Person person = new Person();
        person.setName("ampmangu");
        person.setDateAdded(Instant.now().truncatedTo(ChronoUnit.MILLIS));
        person.setType(TypePerson.TWITTER);
        return person;
    }

    public static Person createEntity(EntityManager em, String name) {
        Person person = new Person();
        person.setName(name);
        person.setDateAdded(Instant.now().truncatedTo(ChronoUnit.MILLIS));
        person.setType(TypePerson.TWITTER);
        return person;
    }


    @Test
    @Transactional
    public void createPerson() throws Exception {
        int databaseSizeBeforeCreate = personRepository.findAll().size();
        assertThat(databaseSizeBeforeCreate).isEqualTo(0);
        Person person1 = createPerson("chris1.json");
        assertThat(person1).isNotNull();
        person1.setId(null);
        assertThat(person1).hasFieldOrPropertyWithValue("id", null);
        restPersonMockMvc.perform(post("/api/people")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(person1)))
                .andExpect(status().isCreated());

        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeCreate + 1);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualToIgnoringCase("Chris Hemsworth");
        assertThat(testPerson.getId()).isNotNull().isGreaterThan(0);
        assertThat(testPerson.getActorDataList()).isNotNull();
        assertThat(testPerson.getActorDataList().size()).isGreaterThan(1);
    }

    @Test
    @Transactional
    public void createPersonWithId() throws Exception {
        int databaseSizeBeforeCreate = personRepository.findAll().size();
        assertThat(databaseSizeBeforeCreate).isEqualTo(0);
        Person person1 = createPerson("chris1.json");
        assertThat(person1).isNotNull();
        restPersonMockMvc.perform(post("/api/people")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(person1)))
                .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeCreate);
    }

    String createObject(String path) throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream("/fixtures/" + path),
                "UTF-8"
        );
    }

    Person createPerson(String path) throws IOException {
        return mapper.readValue(this.createObject(path), Person.class);
    }
}
