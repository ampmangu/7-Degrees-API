package com.ampmangu.degrees.config;

public final class Constants {
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_TEST = "test";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String MOVIE_DB_API_KEY = System.getProperty("MOVIE_DB_API_KEY", "unset");
    public static final String SYSTEM_MOVIE_DB_API_KEY = System.getenv("MOVIE_DB_API_KEY");

    public static final String JWT_MASTER_TOKEN = System.getProperty("JWT_MASTER_TOKEN", "unset");
    public static final String JWT_MASTER_TOKEN_SYSTEM = System.getenv("JWT_MASTER_TOKEN");
    private Constants() {
    }
}
