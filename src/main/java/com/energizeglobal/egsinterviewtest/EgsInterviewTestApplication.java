package com.energizeglobal.egsinterviewtest;

import com.energizeglobal.egsinterviewtest.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class EgsInterviewTestApplication {

    private final Logger log = LoggerFactory.getLogger(EgsInterviewTestApplication.class);

    private final Environment environment;

    public EgsInterviewTestApplication(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        log.debug("aaaaa");
    }

    public static void main(String[] args) {
        SpringApplication.run(EgsInterviewTestApplication.class, args);
    }

}
