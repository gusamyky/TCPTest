package org.example.server;

import org.example.config.Config;
import org.example.model.Question;
import org.example.repository.interfaces.QuestionRepository;
import org.example.repository.RepositoryFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExamServer {
    private final int port;
    private boolean running;
    private final List<Question> questions;
    private final ExecutorService executor;

    public ExamServer(int port) {
        this.port = port;
        int MAX_CLIENTS = Config.getMaxClients();
        this.executor = Executors.newFixedThreadPool(MAX_CLIENTS);
        
        try {
            QuestionRepository questionRepository = RepositoryFactory.getQuestionRepository();
            this.questions = questionRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load questions: " + e.getMessage(), e);
        }
    }

    public void start() {
        running = true;
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Exam server started on port " + port);
            System.out.println("Loaded " + questions.size() + " questions");
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                    
                    // Create a new client handler and execute it in a separate thread.
                    ClientHandler clientHandler = new ClientHandler(clientSocket, questions);
                    executor.execute(clientHandler);
                    
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server on port " + port + ": " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        if (executor != null) {
            executor.shutdown();
        }
        System.out.println("Server stopped");
    }
}
