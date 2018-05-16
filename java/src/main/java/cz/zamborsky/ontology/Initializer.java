package cz.zamborsky.ontology;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application entry. Fires up Spring boot initialization.
 */
@SpringBootApplication
@ComponentScan(basePackages = "cz.zamborsky.ontology")
public class Initializer {
    public static void main(String[] args) {
        SpringApplication.run(Initializer.class, args);
    }
}
