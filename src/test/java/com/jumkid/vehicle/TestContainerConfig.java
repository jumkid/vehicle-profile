package com.jumkid.vehicle;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.HashMap;
import java.util.Map;

@DisplayName("No tests should be placed here")
public class TestContainerConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    static final MySQLContainer<?> dbContainer = new MySQLContainer<>("mysql:latest")
            .withReuse(true);

    static {
        Startables.deepStart(dbContainer).join();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("spring.datasource.url", dbContainer.getJdbcUrl());
        stringMap.put("spring.datasource.username", dbContainer.getUsername());
        stringMap.put("spring.datasource.password", dbContainer.getPassword());
        TestPropertyValues.of(stringMap).applyTo(applicationContext.getEnvironment());
    }
}
