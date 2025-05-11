package org.example.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
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
                    System.out.println("Configuration loaded from " + CONFIG_FILE);
                }
            } else {
                System.out.println("Configuration file not found. Using default values.");
            }

            validateConfig();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
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
    
    private static void validateConfig() {
        try {
            String address = getServerAddress();
            if (address == null || address.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid server address");
            }
            
            int port = getServerPort();
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Invalid server port: " + port);
            }
            
            int maxClients = getMaxClients();
            if (maxClients < 1) {
                throw new IllegalArgumentException("Invalid max clients: " + maxClients);
            }
            
            int timeout = getAnswerTimeoutSeconds();
            if (timeout < 1) {
                throw new IllegalArgumentException("Invalid answer timeout: " + timeout);
            }
            
            String dataDir = getDataDir();
            if (dataDir == null || dataDir.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid data directory");
            }
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in configuration", e);
        }
    }
    
    public static String getServerAddress() {
        return properties.getProperty("server.address");
    }
    
    public static int getServerPort() {
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
