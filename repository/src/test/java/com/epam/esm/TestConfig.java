package com.epam.esm;

import com.epam.esm.config.DatabaseConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@EnableAutoConfiguration
@ComponentScan(
        basePackages = "com.epam.esm",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = DatabaseConfig.class)
        }
)
public class TestConfig {

}
