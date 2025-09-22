package com.spring.practice.model;

import org.springframework.stereotype.Component;

@Component // @Component in Spring marks a class as a Spring-managed bean, allowing it to be automatically detected and registered in the application context during component scanning.
public class Alien {
    public void code(){
        System.out.println("Coding");
    }
}
