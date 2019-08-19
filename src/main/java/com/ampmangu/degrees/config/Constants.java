package com.ampmangu.degrees.config;

public final class Constants {
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_TEST = "test";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String MOVIE_DB_API_KEY = System.getProperty("MOVIE_DB_API_KEY", "unset");

    private Constants() {
    }
}
