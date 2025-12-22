package com.spring.practice.service;

import com.spring.practice.entity.Schools;
import com.spring.practice.repository.SchoolRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Service
 * Marks this class as a Spring service bean.
 * Contains business logic between Controller and Repository.
 */
@Service
public class SchoolService {

    private final SchoolRepository schoolRepository;

    // Constructor injection → dependency is immutable, easier to test
    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    // ------------------- GET ALL SCHOOLS -------------------
    public List<Schools> getAllSchools() {
        // Calls JpaRepository.findAll()
        // Maps to SQL: SELECT * FROM schools;
        return schoolRepository.findAll();
    }

    // ------------------- ADD NEW SCHOOL -------------------
    @Transactional
    public String addSchool(Schools school) {
        // Validate input
        if (school.getSchoolName() == null || school.getSchoolName().trim().isEmpty()) return "NAME_REQUIRED";
        if (school.getSchoolAddress() == null || school.getSchoolAddress().trim().isEmpty()) return "ADDRESS_REQUIRED";

        // Check duplicate
        if (schoolRepository.existsBySchoolName(school.getSchoolName())) return "EXISTS";

        // Save to DB → INSERT INTO schools (...)
        schoolRepository.save(school);
        return "CREATED";
    }

    // ------------------- DELETE SCHOOL -------------------
    @Transactional
    public boolean deleteSchool(Integer schoolId, String schoolName) {
        if (schoolName == null || schoolName.trim().isEmpty()) return false;

        // Find record matching both ID & Name
        Schools school = schoolRepository.findBySchoolIdAndSchoolName(schoolId, schoolName);
        if (school != null) {
            schoolRepository.delete(school); // DELETE FROM schools WHERE school_id = ? AND school_name = ?
            return true;
        }
        return false;
    }

    // ------------------- UPDATE SCHOOL ADDRESS -------------------
    @Transactional
    public String updateAddress(Integer schoolId, String newAddress) {
        if (newAddress == null || newAddress.trim().isEmpty()) return "INVALID_ADDRESS";

        // Optional avoids NullPointerException
        Optional<Schools> optionalSchool = schoolRepository.findById(schoolId);
        if (optionalSchool.isEmpty()) return "NOT_FOUND";

        Schools school = optionalSchool.get();
        school.setSchoolAddress(newAddress);  // Update entity field
        schoolRepository.save(school);        // Persists to DB → UPDATE schools SET school_address = ? WHERE school_id = ?

        return "UPDATED";
    }
}
