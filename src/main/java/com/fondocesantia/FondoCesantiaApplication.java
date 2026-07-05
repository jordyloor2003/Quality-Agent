package com.fondocesantia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for FondoCesantia Service.
 * Provides REST API for payment registration and distribution.
 */
@SpringBootApplication
public class FondoCesantiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FondoCesantiaApplication.class, args);
    }
}