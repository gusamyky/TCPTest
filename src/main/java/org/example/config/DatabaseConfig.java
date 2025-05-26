package org.example.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            setDefaultValues();
            Path configPath = Paths.get(CONFIG_FILE);
            if (Files.exists(configPath)) {
                try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                    properties.load(input);
                    System.out.println("DatabaseConfig loaded from " + CONFIG_FILE);
                }
            } else {
                System.out.println("DatabaseConfig: config file not found, using defaults.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    private static void setDefaultValues() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/tcp_test_db");
        properties.setProperty("db.user", "tcp_user");
        properties.setProperty("db.password", "tcp_password");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUser() {
        return properties.getProperty("db.user");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    public static String getDriver() {
        return properties.getProperty("db.driver");
    }
} 