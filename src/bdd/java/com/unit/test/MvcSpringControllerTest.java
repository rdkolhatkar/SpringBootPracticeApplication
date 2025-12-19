package com.unit.test;

/*
 * ================================
 * IMPORT SECTION
 * ================================
 */

/*
 * ObjectMapper
 * --------------
 * Comes from Jackson library.
 * Used to convert Java objects (Map, POJO) into JSON strings
 * and JSON strings back into Java objects.
 *
 * Dependency:
 * spring-boot-starter-web (internally includes jackson-databind)
 */
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Main Spring Boot application class.
 * This class is annotated with @SpringBootApplication.
 * We explicitly reference this class so Spring knows
 * where to start scanning configuration from.
 */
import com.spring.ratnakar.SpringBootPracticeApplication;

/*
 * Controller under test.
 * Only this controller will be loaded into the test context.
 */
import com.spring.ratnakar.controller.Controller;

/*
 * JUnit 5 @Test annotation.
 * Marks a method as a test case.
 *
 * Dependency:
 * spring-boot-starter-test → junit-jupiter
 */
import org.junit.jupiter.api.Test;

/*
 * Mockito core class.
 * Provides stubbing methods like:
 * - when(...)
 * - anyString()
 * - eq(...)
 *
 * Dependency:
 * spring-boot-starter-test → mockito-core
 */
import org.mockito.Mockito;

/*
 * Spring Dependency Injection annotation.
 * Injects beans managed by Spring into this test class.
 */
import org.springframework.beans.factory.annotation.Autowired;

/*
 * @WebMvcTest
 * -----------
 * Loads ONLY:
 * - Controller layer
 * - MockMvc infrastructure
 *
 * Does NOT load:
 * - Service layer
 * - Repository layer
 * - Database
 *
 * Dependency:
 * spring-boot-starter-test
 */
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

/*
 * @MockBean
 * ----------
 * Creates a Mockito mock AND registers it inside Spring's
 * ApplicationContext.
 *
 * This mock replaces the real JdbcTemplate bean.
 *
 * Dependency:
 * spring-boot-starter-test
 */
import org.springframework.boot.test.mock.mockito.MockBean;

/*
 * MediaType
 * ----------
 * Used to specify request/response content types
 * such as application/json.
 *
 * Dependency:
 * spring-web
 */
import org.springframework.http.MediaType;

/*
 * JdbcTemplate
 * -------------
 * Spring JDBC helper class.
 * We mock this so no real database is accessed.
 *
 * Dependency:
 * spring-jdbc
 */
import org.springframework.jdbc.core.JdbcTemplate;

/*
 * @ContextConfiguration
 * ---------------------
 * Explicitly tells Spring which configuration class
 * to use to bootstrap the context.
 *
 * VERY IMPORTANT for custom BDD source sets.
 */
import org.springframework.test.context.ContextConfiguration;

/*
 * MockMvc
 * --------
 * Core class used to simulate HTTP requests.
 *
 * Allows testing controllers WITHOUT starting a server.
 *
 * Dependency:
 * spring-test
 */
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

/*
 * Hamcrest matcher used to validate collection sizes.
 *
 * Dependency:
 * spring-boot-starter-test → hamcrest
 */
import static org.hamcrest.Matchers.hasSize;

/*
 * MockMvc request builder static imports:
 * - get()
 * - post()
 * - put()
 * - delete()
 *
 * From:
 * org.springframework.test.web.servlet.request.MockMvcRequestBuilders
 */
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/*
 * MockMvc result matchers:
 * - status()
 * - content()
 * - jsonPath()
 *
 * From:
 * org.springframework.test.web.servlet.result.MockMvcResultMatchers
 */
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * =========================================================
 * MVC Controller Unit Test (BDD-compatible)
 * =========================================================
 *
 * PURPOSE:
 * --------
 * This class tests ONLY the Controller layer.
 *
 * - No real database
 * - No Tomcat server
 * - No full application startup
 *
 * Tools used:
 * ------------
 * ✔ JUnit 5       → Test framework
 * ✔ Mockito       → Mocking dependencies
 * ✔ MockMvc       → Simulating HTTP requests
 * ✔ Jackson       → JSON serialization
 */
@WebMvcTest(controllers = Controller.class)
/*
 * Loads only Controller + MVC infrastructure.
 */
@ContextConfiguration(classes = SpringBootPracticeApplication.class)
        /*
         * Explicitly tells Spring:
         * "This is my @SpringBootApplication class"
         */
class MvcSpringControllerTest {

    /*
     * MockMvc instance injected by Spring.
     *
     * Used to perform HTTP calls like:
     * - GET
     * - POST
     * - PUT
     * - DELETE
     */
    @Autowired
    private MockMvc mockMvc;

    /*
     * JdbcTemplate is mocked using Mockito.
     *
     * This prevents real DB calls and allows us
     * to control returned values.
     */
    @MockBean
    private JdbcTemplate jdbcTemplate;

    /*
     * ObjectMapper injected by Spring.
     *
     * Used to convert Java Map → JSON string.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /* =========================================================
     * TEST CASE 1: GET /api/greetings
     * =========================================================
     *
     * Scenario:
     * ----------
     * When client hits /api/greetings
     *
     * Expected:
     * ----------
     * - HTTP 200 OK
     * - Response body = "Hello World!"
     */
    @Test
    void testGreetings() throws Exception {

        mockMvc.perform(
                        get("/api/greetings")
                        /*
                         * get() comes from MockMvcRequestBuilders
                         * Simulates HTTP GET request
                         */
                )
                .andExpect(status().isOk())
                /*
                 * status() comes from MockMvcResultMatchers
                 * Verifies HTTP status code
                 */
                .andExpect(content().string("Hello World!"));
        /*
         * content().string() validates raw response body
         */
    }

    /* =========================================================
     * TEST CASE 2: GET /api/greetings/list
     * =========================================================
     *
     * Scenario:
     * ----------
     * API returns a static list of greetings.
     *
     * Validations:
     * -------------
     * - Status 200
     * - JSON array size = 2
     * - First element values match expected
     */
    @Test
    void testGreetingsList() throws Exception {

        mockMvc.perform(get("/api/greetings/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                /*
                 * jsonPath("$") refers to root JSON array
                 */
                .andExpect(jsonPath("$[0].Name").value("Ratnakar"))
                .andExpect(jsonPath("$[0].Company").value("Cognizant"));
    }

    /* =========================================================
     * TEST CASE 3: POST /search/school
     * =========================================================
     *
     * Scenario:
     * ----------
     * Client searches schools based on filters.
     *
     * This endpoint does NOT hit DB.
     */
    @Test
    void testSearchSchoolDetails() throws Exception {

        Map<String, String> request = Map.of(
                "Name", "ABC",
                "AreaLocation", "Mumbai",
                "Catagory", "Private"
        );

        mockMvc.perform(
                        post("/search/school")
                                .contentType(MediaType.APPLICATION_JSON)
                                /*
                                 * Sets Content-Type header
                                 */
                                .content(objectMapper.writeValueAsString(request))
                        /*
                         * Converts Java Map → JSON
                         */
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].SchoolName").value("RosarySchool"));
    }

    /* =========================================================
     * TEST CASE 4: GET /api/getSchoolDetails
     * =========================================================
     *
     * Scenario:
     * ----------
     * Controller fetches school data using JdbcTemplate.
     *
     * Mockito mocking:
     * -----------------
     * We mock jdbcTemplate.queryForList()
     * to return fake DB data.
     */
    @Test
    void testGetSchoolDetails() throws Exception {

        Mockito.when(jdbcTemplate.queryForList(Mockito.anyString()))
                /*
                 * anyString() → matches any SQL query
                 */
                .thenReturn(List.of(
                        Map.of(
                                "school_id", 1,
                                "school_name", "ABC School",
                                "school_address", "Mumbai",
                                "school_type", "Private"
                        )
                ));

        mockMvc.perform(get("/api/getSchoolDetails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].school_name").value("ABC School"));
    }

    /* =========================================================
     * TEST CASE 5: POST /api/add/schoolDetails (SUCCESS)
     * =========================================================
     *
     * Scenario:
     * ----------
     * - Check duplicate → returns 0
     * - Insert record → returns 1
     *
     * Both DB calls are mocked.
     */
    @Test
    void testAddSchoolDetailsSuccess() throws Exception {

        Mockito.when(
                jdbcTemplate.queryForObject(
                        Mockito.anyString(),
                        Mockito.eq(Integer.class),
                        Mockito.any()
                )
        ).thenReturn(0);
        /*
         * queryForObject mocked to simulate
         * "record does not exist"
         */

        Mockito.when(
                jdbcTemplate.update(
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()
                )
        ).thenReturn(1);
        /*
         * update mocked to simulate
         * successful insert
         */

        mockMvc.perform(
                        post("/api/add/schoolDetails")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        Map.of(
                                                "school_name", "New School",
                                                "school_address", "Delhi",
                                                "school_type", "Public"
                                        )
                                ))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.Status").value("Success"));
    }
}
