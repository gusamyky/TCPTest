package org.example.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerConfig {
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
                    System.out.println("ServerConfig loaded from " + CONFIG_FILE);
                }
            } else {
                System.out.println("ServerConfig: config file not found, using defaults.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load server configuration", e);
        }
    }

    private static void setDefaultValues() {
        properties.setProperty("server.address", "localhost");
        properties.setProperty("server.port", "8080");
        properties.setProperty("max.clients", "250");
        properties.setProperty("answer.timeout.seconds", "10");
        properties.setProperty("data.dir", "data");
        properties.setProperty("resources.dir", "src/main/resources");
    }

    public static String getAddress() {
        return properties.getProperty("server.address");
    }
    public static int getPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }
    public static int getMaxClients() {
        return Integer.parseInt(properties.getProperty("max.clients"));
    }
    public static int getAnswerTimeoutSeconds() {
        return Integer.parseInt(properties.getProperty("answer.timeout.seconds"));
    }
    public static String getDataDir() {
        return properties.getProperty("data.dir");
    }
    public static String getResourcesDir() {
        return properties.getProperty("resources.dir");
    }
} 