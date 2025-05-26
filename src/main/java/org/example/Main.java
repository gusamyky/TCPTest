package org.example;

import org.example.config.ServerConfig;
import org.example.server.ExamServer;
import org.example.util.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        // Initialize database and load questions
        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.initializeDatabase();

        // Start the server
        int port = ServerConfig.getPort();
        ExamServer server = new ExamServer(port);
        server.start();
    }
}
