package com.unit.test;

/*
 * Importing the actual classes we want to test or mock
 */

// Static Hibernate utility class that provides SessionFactory
import com.test.training.config.HibernateUtil;

// Entity class mapped to DB table
import com.test.training.entity.School;

// Service class under test
import com.test.training.service.SchoolDetailService;

// Hibernate core interfaces
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

// JUnit 5 annotations
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Mockito annotations and helpers
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

// Java utility classes
import java.util.Collections;
import java.util.List;

// JUnit assertion methods (assertEquals, assertTrue, assertFalse, etc.)
import static org.junit.jupiter.api.Assertions.*;

// Mockito static utility methods (when, verify, mockStatic, any, eq, etc.)
import static org.mockito.Mockito.*;

/*
 * @ExtendWith(MockitoExtension.class)
 * --------------------------------
 * This tells JUnit 5 to enable Mockito support.
 * Without this, @Mock annotations will NOT work.
 */
@ExtendWith(MockitoExtension.class)
class SchoolDetailServiceTest {

    /*
     * This is the class we are testing.
     * We are NOT mocking this.
     */
    private SchoolDetailService service;

    /*
     * Below are mocked Hibernate objects.
     * These replace real Hibernate behavior so:
     * - No DB is used
     * - No actual session is opened
     */

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @Mock
    private Query<School> query;

    /*
     * Test data object (Entity)
     */
    private School school;

    /*
     * @BeforeEach
     * ------------
     * This method runs BEFORE every test method.
     * Used to initialize common objects.
     */
    @BeforeEach
    void setUp() {
        // Creating service manually (no Spring context)
        service = new SchoolDetailService();

        // Creating a sample School object for tests
        school = new School();
        school.setSchool_name("ABC School");
        school.setSchool_address("Pune");
    }

    // ================= ADD SCHOOL TESTS =================

    /*
     * Test case:
     * If school name is null → return "NAME_REQUIRED"
     */
    @Test
    void addSchool_shouldReturnNameRequired() {
        // Setting invalid input
        school.setSchool_name(null);

        // Calling service method
        String result = service.addSchool(school);

        // JUnit assertion
        assertEquals("NAME_REQUIRED", result);
    }

    /*
     * Test case:
     * If school address is blank → return "ADDRESS_REQUIRED"
     */
    @Test
    void addSchool_shouldReturnAddressRequired() {
        school.setSchool_address(" ");

        String result = service.addSchool(school);

        assertEquals("ADDRESS_REQUIRED", result);
    }

    /*
     * Test case:
     * If school already exists in DB → return "EXISTS"
     */
    @Test
    void addSchool_shouldReturnExists_whenDuplicateFound() {

        /*
         * mockStatic(HibernateUtil.class)
         * --------------------------------
         * HibernateUtil.getSessionFactory() is a STATIC method.
         * Static methods CANNOT be mocked normally.
         * Mockito-inline allows this.
         */
        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {

            // When HibernateUtil.getSessionFactory() is called → return mocked factory
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

            // Mock session opening and transaction
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);

            // Mock HQL query
            when(session.createQuery(anyString(), eq(School.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);

            // Simulate duplicate record found
            when(query.list()).thenReturn(List.of(school));

            String result = service.addSchool(school);

            assertEquals("EXISTS", result);
        }
    }

    /*
     * Test case:
     * If no duplicate → save school → return "CREATED"
     */
    @Test
    void addSchool_shouldCreateSchoolSuccessfully() {

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {

            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);

            when(session.createQuery(anyString(), eq(School.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);

            // No duplicate found
            when(query.list()).thenReturn(Collections.emptyList());

            String result = service.addSchool(school);

            assertEquals("CREATED", result);

            // Verify Hibernate save & commit
            verify(session).save(school);
            verify(transaction).commit();
        }
    }

    // ================= GET ALL SCHOOLS =================

    /*
     * Test case:
     * Fetch all schools
     */
    @Test
    void getAllSchools_shouldReturnList() {

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {

            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);

            when(session.createQuery("FROM School", School.class)).thenReturn(query);
            when(query.list()).thenReturn(List.of(school));

            List<School> result = service.getAllSchools();

            assertEquals(1, result.size());
        }
    }

    // ================= DELETE SCHOOL =================

    /*
     * Test case:
     * If both ID and name are null → return false
     */
    @Test
    void deleteSchool_shouldReturnFalse_whenNoInput() {
        assertFalse(service.deleteSchool(null, null));
    }

    /*
     * Test case:
     * Delete school using ID
     */
    @Test
    void deleteSchool_shouldDeleteById() {

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {

            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);

            // Mock finding entity by ID
            when(session.get(School.class, 1L)).thenReturn(school);

            boolean result = service.deleteSchool(1, null);

            assertTrue(result);
            verify(session).delete(school);
            verify(transaction).commit();
        }
    }

    // ================= UPDATE ADDRESS =================

    /*
     * Test case:
     * Blank address → INVALID_ADDRESS
     */
    @Test
    void updateAddress_shouldReturnInvalidAddress() {
        assertEquals("INVALID_ADDRESS", service.updateAddress(1, " "));
    }

    /*
     * Test case:
     * School not found → NOT_FOUND
     */
    @Test
    void updateAddress_shouldReturnNotFound() {

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {

            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);

            when(session.get(School.class, 1L)).thenReturn(null);

            String result = service.updateAddress(1, "Mumbai");

            assertEquals("NOT_FOUND", result);
        }
    }

    /*
     * Test case:
     * Valid update → UPDATED
     */
    @Test
    void updateAddress_shouldUpdateSuccessfully() {

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {

            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);

            when(session.get(School.class, 1L)).thenReturn(school);

            String result = service.updateAddress(1, "Mumbai");

            assertEquals("UPDATED", result);
            verify(session).update(school);
            verify(transaction).commit();
        }
    }
}
