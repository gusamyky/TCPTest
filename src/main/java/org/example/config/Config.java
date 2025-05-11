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
        // Default values
        properties.setProperty("server.port", "8080");
        properties.setProperty("max.clients", "250");
        properties.setProperty("answer.timeout.seconds", "10");
        properties.setProperty("data.dir", "data");
        
        // Try to load from file
        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                properties.load(input);
                System.out.println("Configuration loaded from " + CONFIG_FILE);
            } catch (IOException e) {
                System.err.println("Error loading configuration: " + e.getMessage());
            }
        } else {
            System.out.println("Configuration file not found. Using default values.");
        }
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
}
