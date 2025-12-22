package com.spring.practice.repository;

import com.spring.practice.entity.Schools;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Repository marks this interface as a Spring Data Repository.
 * It tells Spring:
 *  - Create a bean for this repository
 *  - Make it eligible for component scanning
 *  - Provide automatic exception translation (convert SQL exceptions into Spring exceptions)
 *
 * This interface is responsible for communicating with the database.
 */
@Repository
public interface SchoolRepository extends JpaRepository<Schools, Integer> {

    /**
     * SchoolRepository extends JpaRepository<T, ID>
     *
     * T  = Entity class (School)
     * ID = Primary key type (Integer)
     *
     * JpaRepository provides built-in CRUD operations:
     *  - save()
     *  - findById()
     *  - findAll()
     *  - deleteById()
     *  - count()
     *  - existsById()
     *  and many more…
     *
     * This eliminates the need to write SQL or Hibernate code manually.
     */

    /**
     * Custom finder method:
     * boolean existsBySchoolName(String schoolName)
     *
     * Spring Data JPA automatically creates the query:
     * SELECT COUNT(*) FROM schools WHERE school_name = ?;
     *
     * Returns:
     *  - true  → if a record with the given schoolName exists
     *  - false → otherwise
     *
     * Used to prevent duplicate school names.
     */
    boolean existsBySchoolName(String schoolName);

    /**
     * Another custom finder:
     * School findBySchoolIdAndSchoolName(Integer schoolId, String schoolName)
     *
     * Spring automatically generates the query based on method name:
     *
     * SELECT * FROM schools
     * WHERE school_id = ? AND school_name = ?;
     *
     * Returns:
     *  - The School object if row matches both conditions
     *  - null if not found
     *
     * This is useful for safe delete operations or validations.
     */
    Schools findBySchoolIdAndSchoolName(Integer schoolId, String schoolName);
}
