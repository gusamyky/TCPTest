package org.example.config;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/tcp_test_db";
    private static final String USER = "tcp_user";
    private static final String PASSWORD = "tcp_password";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static String getDriver() {
        return DRIVER;
    }
} 