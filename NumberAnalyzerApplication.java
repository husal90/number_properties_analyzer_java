package com.example.numberanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Class.
 * This annotation enables auto-configuration, component scanning, etc.
 * This is the entry point for running the Spring Boot application.
 */
@SpringBootApplication
public class NumberAnalyzerApplication {

    /**
     * The main method which serves as the entry point for the JVM.
     * It delegates to Spring Boot's SpringApplication class to bootstrap the application.
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(NumberAnalyzerApplication.class, args);
        System.out.println("\nNumber Analyzer Application Started!");
        System.out.println("Access the frontend at: http://localhost:8080");
        System.out.println("API endpoint example: http://localhost:8080/api/number/analyze/16\n");
    }
}
