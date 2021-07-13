package com.energizeglobal.egsinterviewtest.config;

public interface Constants {

    String SPRING_PROFILE_DEVELOPMENT = "dev";
    String SPRING_PROFILE_TEST = "test";
    String SPRING_PROFILE_PRODUCTION = "prod";
    String SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase";
    String SPRING_PROFILE_API_DOCS = "api-docs";

    // Regex for acceptable logins
    String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    String SYSTEM = "system";
}
