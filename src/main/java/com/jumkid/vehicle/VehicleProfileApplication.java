package com.jumkid.vehicle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootApplication
public class VehicleProfileApplication implements CommandLineRunner {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String appPort;

    @Value("${spring.application.version}")
    private String version;

    public static void main(String[] args) {
        SpringApplication.run(VehicleProfileApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("{} v{} started at port {} ", appName, version, appPort);
    }

    @Bean(name = "restTemplate")
    public RestTemplate restTemplateBean(){
        return new RestTemplate();
    }

}
