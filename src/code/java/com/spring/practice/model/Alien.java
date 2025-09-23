package com.spring.practice.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // @Component in Spring marks a class as a Spring-managed bean, allowing it to be automatically detected and registered in the application context during component scanning.
public class Alien {

    /*
    In Spring Framework, @Autowired is used for dependency injection, allowing Spring’s IoC container to automatically resolve and inject collaborating beans into a class.
    It eliminates the need to manually create objects with new, promoting loose coupling and easier testing. It can be applied on constructors, fields, or setter methods.
    By default, Spring injects beans by type, and if multiple beans exist, @Qualifier can be used to specify which one to inject.
     */
    @Autowired
    Laptop laptop;
    public void code(){
        System.out.println("Coding");
        laptop.compile();
    }
}
