package com.test.training.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "school")
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String school_name;
    private String school_address;
    private String school_type;

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
