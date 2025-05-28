package org.example.util;

import org.example.config.ServerConfig;
import org.example.model.Question;
import org.example.repository.implementations.SqlQuestionRepository;
import org.example.repository.interfaces.QuestionRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {
    private static final String QUESTIONS_FILE = ServerConfig.getResourcesDir() + "/bazaPytan.txt";
    private final QuestionRepository questionRepository;

    public DatabaseInitializer() {
        this.questionRepository = new SqlQuestionRepository();
    }

    public void initializeDatabase() {
        try {
            List<Question> questions = loadQuestionsFromFile();
            for (Question question : questions) {
                questionRepository.save(question);
            }
            System.out.println("Successfully loaded " + questions.size() + " questions into the database.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database from file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Question> loadQuestionsFromFile() throws IOException {
        List<Question> questions = new ArrayList<>();
        StringBuilder currentBlock = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(QUESTIONS_FILE, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (currentBlock.length() > 0) {
                        try {
                            questions.add(Question.fromFileFormat(currentBlock.toString()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Error parsing question: " + e.getMessage());
                        }
                        currentBlock.setLength(0);
                    }
                } else {
                    currentBlock.append(line).append("\n");
                }
            }
            
            // Process the last block if it exists
            if (currentBlock.length() > 0) {
                try {
                    questions.add(Question.fromFileFormat(currentBlock.toString()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing question: " + e.getMessage());
                }
            }
        }
        
        return questions;
    }

    public static void main(String[] args) {
        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.initializeDatabase();
    }
} 