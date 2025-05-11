package org.example;

import org.example.config.Config;
import org.example.server.ExamServer;

public class Main {
    public static void main(String[] args) {
        int port = Config.getServerPort();
        ExamServer server = new ExamServer(port);

        server.start();
    }
}
