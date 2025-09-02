package com.spring.ratnakar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class that exposes REST APIs for:
 * - Greetings messages (basic examples)
 * - Serving image files
 * - Searching schools (static mock data)
 * - CRUD operations (Create, Read, Delete) on School details stored in DB
 *
 * Uses Spring Boot's {@link RestController} and {@link JdbcTemplate} for DB operations.
 */
@RestController
public class Controller {

    /**
     * GET API: Returns a simple greeting message.
     *
     * @return "Hello World!"
     */
    @GetMapping("/api/greetings")
    public String greetings() {
        return "Hello World!";
    }

    /**
     * GET API: Returns a static list of greetings with names and companies.
     *
     * @return List of key-value pairs (Name, Company)
     */
    @GetMapping("/api/greetings/list")
    public List<Map<String, String>> greetingsList() {
        return List.of(
                Map.of("Name", "Ratnakar", "Company", "Cognizant"),
                Map.of("Name", "Rajesh", "Company", "Google")
        );
    }

    /**
     * GET API: Serves an image from resources folder.
     *
     * @return ResponseEntity containing image as byte[] with headers
     * @throws IOException if image not found or cannot be read
     */
    @GetMapping("/school/image")
    public ResponseEntity<byte[]> getImage() throws IOException {
        // Load image from classpath: resources/data/BackToSchool.jpg
        ClassPathResource imgFile = new ClassPathResource("images/BackToSchool.jpg");
        byte[] bytes = Files.readAllBytes(imgFile.getFile().toPath());

        System.out.println("Image Bytes: " + Arrays.toString(bytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename='BackToSchool.jpg'")
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
    }

    /**
     * POST API: Example mock search API for schools.
     * Request is received but not used in filtering (static response for demo purpose).
     *
     * @param request Request payload containing Name, AreaLocation, Category
     * @return Static List of schools (mock response)
     */
    @PostMapping("/search/school")
    public ResponseEntity<List<Map<String, String>>> searchSchoolDetails(@RequestBody Map<String, String> request) {
        String name = request.get("Name");
        String areaLocation = request.get("AreaLocation");
        String category = request.get("Catagory");

        System.out.println("Request Received: Name=" + name + ", AreaLocation=" + areaLocation + ", Category=" + category);

        List<Map<String, String>> schools = List.of(
                Map.of("SchoolName", "RosarySchool", "Location", "Mumbai"),
                Map.of("SchoolName", "SaintVincent", "Location", "Mumbai"),
                Map.of("SchoolName", "NewEnglishSchool", "Location", "Mumbai")
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(schools);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * GET API: Fetch all school details from the database.
     *
     * @return List of school records (school_id, name, address, type)
     */
    @GetMapping("/api/getSchoolDetails")
    public List<Map<String, Object>> getSchoolDetails() {
        String query = "SELECT school_id, school_name, school_address, school_type FROM schools";
        return jdbcTemplate.queryForList(query);
    }

    /**
     * POST API: Insert a new school into the database.
     * - Checks if school with same name already exists
     * - If exists → returns 409 Conflict
     * - If not exists → inserts new record
     *
     * @param requestData JSON body containing school_name, school_address, school_type
     * @return ResponseEntity with status message
     */
    @PostMapping("/api/add/schoolDetails")
    public ResponseEntity<Map<String, Object>> addSchoolDetails(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> responseData = new HashMap<>();
        try {
            String schoolName = (String) requestData.get("school_name");
            String schoolAddress = (String) requestData.get("school_address");
            String schoolType = (String) requestData.get("school_type");

            // Step 1: Check if school already exists by name
            String checkSchoolDetails = "SELECT COUNT(*) FROM schools WHERE school_name = ?";
            Integer rowCount = jdbcTemplate.queryForObject(checkSchoolDetails, Integer.class, schoolName);

            if (rowCount != null && rowCount > 0) {
                // Duplicate school found → reject insertion
                responseData.put("Status", "Failed");
                responseData.put("Message", "School already exists with name: " + schoolName);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseData); // 409 Conflict
            } else {
                // Step 2: Insert new record
                String insertQuery = "INSERT INTO schools (school_name, school_address, school_type) VALUES (?, ?, ?)";
                int rows = jdbcTemplate.update(insertQuery, schoolName, schoolAddress, schoolType);

                if (rows > 0) {
                    responseData.put("Status", "Success");
                    responseData.put("Message", "New school inserted successfully");
                    return ResponseEntity.status(HttpStatus.CREATED).body(responseData); // 201 Created
                } else {
                    responseData.put("Status", "Failed");
                    responseData.put("Message", "Insertion failed");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData); // 400 Bad Request
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("Status", "Error");
            responseData.put("Message", "API is not working as expected: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData); // 500 Internal Server Error
        }
    }

    /**
     * DELETE API: Delete school details from DB.
     * - Requires operation_type = "delete" in request body for validation
     * - Deletes based on school_id and school_name
     *
     * @param requestData JSON body containing operation_type, school_id, school_name
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/api/delete/schoolDetails")
    public ResponseEntity<Map<String, Object>> deleteSchoolDetails(@RequestBody Map<String, Object> requestData) {
        String operationType = (String) requestData.get("operation_type");
        Integer schoolId = (Integer) requestData.get("school_id");
        String schoolName = (String) requestData.get("school_name");
        Map<String, Object> responseData = new HashMap<>();

        // Validate operation type
        if (!"delete".equalsIgnoreCase(operationType)) {
            responseData.put("Status", "Operation Type is Invalid");
            responseData.put("Message", "****** '{operation_type}' must be 'DELETE' ******");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        // Perform delete query
        String deleteQuery = "DELETE FROM schools WHERE school_id = ? AND school_name = ?";
        int rows = jdbcTemplate.update(deleteQuery, schoolId, schoolName);

        System.out.println("Executed Query: DELETE FROM schools WHERE school_id=" + schoolId + " AND school_name=" + schoolName);

        if (rows > 0) {
            responseData.put("Status", "Success");
            responseData.put("Message", "Records deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseData); // 200 OK
        } else {
            responseData.put("Status", "Failed");
            responseData.put("Message", "Delete operation failed (Record not found or DB issue)");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(responseData); // 503 Service Unavailable
        }
    }
}
