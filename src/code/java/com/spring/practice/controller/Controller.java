package com.spring.practice.controller;

import com.spring.practice.entity.Schools;
import com.spring.practice.service.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @RestController
 * Combines @Controller + @ResponseBody.
 * All methods return JSON response.
 */
@RestController

/**
 * @RequestMapping("/api")
 * Base URL for all endpoints in this controller.
 */
@RequestMapping("/api")
public class Controller {

    private final SchoolService schoolService;

    public Controller(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    // ------------------- GET ALL SCHOOLS -------------------
    @GetMapping("/getSchoolDetails")
    public List<Schools> getSchoolDetails() {
        return schoolService.getAllSchools(); // Returns JSON array of School objects
    }

    // ------------------- ADD NEW SCHOOL -------------------
    @PostMapping("/add/schoolDetails")
    public ResponseEntity<Map<String, Object>> addSchoolDetails(@RequestBody Schools school) {
        Map<String, Object> response = new HashMap<>();
        String result = schoolService.addSchool(school);

        switch (result) {
            case "NAME_REQUIRED":
                response.put("Status", "Failed");
                response.put("Message", "School name is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            case "ADDRESS_REQUIRED":
                response.put("Status", "Failed");
                response.put("Message", "School address is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            case "EXISTS":
                response.put("Status", "Failed");
                response.put("Message", "School already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            case "CREATED":
                response.put("Status", "Success");
                response.put("Message", "New school inserted successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            default:
                response.put("Status", "Error");
                response.put("Message", "Unknown error occurred");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ------------------- DELETE SCHOOL -------------------
    @DeleteMapping("/delete/schoolDetails")
    public ResponseEntity<Map<String, Object>> deleteSchool(@RequestBody Map<String, Object> req) {
        Map<String, Object> res = new HashMap<>();
        String operationType = (String) req.get("operation_type");
        Integer schoolId = (Integer) req.get("school_id");
        String schoolName = (String) req.get("school_name");

        if (!"delete".equalsIgnoreCase(operationType)) {
            res.put("Status", "Failed");
            res.put("Message", "'operation_type' must be DELETE");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        boolean deleted = schoolService.deleteSchool(schoolId, schoolName);
        if (!deleted) {
            res.put("Status", "Failed");
            res.put("Message", "Record not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        res.put("Status", "Success");
        res.put("Message", "Record deleted successfully");
        return ResponseEntity.ok(res);
    }

    // ------------------- UPDATE SCHOOL ADDRESS -------------------
    @PutMapping("/updateSchoolAddress")
    public ResponseEntity<Map<String, Object>> updateSchoolAddress(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Integer schoolId = Integer.valueOf(request.get("school_id").toString());
        String newAddress = (String) request.get("school_address");

        String result = schoolService.updateAddress(schoolId, newAddress);

        switch (result) {
            case "INVALID_ADDRESS":
                response.put("Status", "Failed");
                response.put("Message", "School address is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            case "NOT_FOUND":
                response.put("Status", "Failed");
                response.put("Message", "No school found with id=" + schoolId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            case "UPDATED":
                response.put("Status", "Success");
                response.put("Message", "School address updated successfully");
                return ResponseEntity.ok(response);
            default:
                response.put("Status", "Error");
                response.put("Message", "Unknown error occurred");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
