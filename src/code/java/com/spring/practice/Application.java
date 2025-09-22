package com.spring.practice;

import com.spring.practice.model.Alien;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        /*
        Dependency Injection (DI) in Spring is a design pattern where the Spring IoC container manages object creation and provides required dependencies to classes, instead of classes creating them directly.
        This promotes loose coupling, easier testing, and cleaner code. Dependencies can be injected through constructors, setters, or fields, with constructor injection recommended.
        Spring manages these objects as beans, wiring them automatically using annotations like @Autowired, @Inject, @Resource, @Qualifier, @Primary, @Bean
        */
        ApplicationContext context = SpringApplication.run(Application.class);
        Alien obj = context.getBean(Alien.class);
        obj.code();
    }
}
