package com.test.training.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "schools")   // matches DB table name
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")    // matches DB column name
    private Long id;

    @Column(name = "school_name")
    private String school_name;

    @Column(name = "school_address")
    private String school_address;

    @Column(name = "school_type")
    private String school_type;  // Enum stored as string

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSchool_name() { return school_name; }
    public void setSchool_name(String school_name) { this.school_name = school_name; }

    public String getSchool_address() { return school_address; }
    public void setSchool_address(String school_address) { this.school_address = school_address; }

    public String getSchool_type() { return school_type; }
    public void setSchool_type(String school_type) { this.school_type = school_type; }
}
