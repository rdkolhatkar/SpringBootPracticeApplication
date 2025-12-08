package com.spring.practice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

/**
 * The @Entity annotation marks this class as a JPA entity.
 * Hibernate will map this class to a table in the database.
 */
@Entity

/**
 * @Table specifies the table name in the database.
 * Here, it maps the class to the "schools" table.
 */
@Table(name = "schools")
public class School {

    /**
     * @Id marks this field as the PRIMARY KEY of the table.
     *
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * ➝ The database auto-generates this value (AUTO_INCREMENT in MySQL).
     *
     * @Column(name = "school_id")
     * ➝ Maps this field to the "school_id" column in the table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")
    private Integer schoolId;

    /**
     * @Column maps the field to the database column.
     * - nullable = false → NOT NULL constraint
     * - unique = true → UNIQUE constraint
     *
     * @JsonProperty("school_name") tells Jackson to map the JSON field "school_name"
     * to this Java field "schoolName".
     * This allows your JSON with snake_case to work with camelCase Java fields.
     */
    @Column(name = "school_name", nullable = false, unique = true)
    @JsonProperty("school_name")
    private String schoolName;

    /**
     * Maps to "school_address" column.
     * @JsonProperty maps JSON field "school_address" to this field.
     */
    @Column(name = "school_address")
    @JsonProperty("school_address")
    private String schoolAddress;

    /**
     * Maps to "school_type" column.
     * @JsonProperty maps JSON field "school_type" to this field.
     * Example values: "Pre-Primary", "Primary", "Secondary"
     */
    @Column(name = "school_type")
    @JsonProperty("school_type")
    private String schoolType;

    // -------------------------------
    // Getter and Setter methods
    // -------------------------------

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }
}
