package com.epam.esm;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class TestConfig {
    // integration tests require a class annotated with @EnableAutoConfiguration
}
