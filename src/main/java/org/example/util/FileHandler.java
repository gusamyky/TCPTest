package org.example.util;

import org.example.config.Config;
import org.example.model.ExamResult;
import org.example.model.Question;
import org.example.model.StudentResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String DATA_DIR = Config.getDataDir();
    private static final String RESOURCES_DIR = Config.getResourcesDir();
    private static final String QUESTIONS_FILE = RESOURCES_DIR + "/bazaPytan.txt";
    private static final String RESPONSES_FILE = DATA_DIR + "/bazaOdpowiedzi.txt";
    private static final String RESULTS_FILE = DATA_DIR + "/wyniki.txt";

    public static List<Question> loadQuestions() throws IOException {
        List<Question> questions = new ArrayList<>();
        
        Path questionsPath = Paths.get(QUESTIONS_FILE);
        if (!Files.exists(questionsPath)) {
            throw new IOException("Questions file not found: " + QUESTIONS_FILE);
        }

        List<String> lines = Files.readAllLines(questionsPath);
        StringBuilder currentBlock = new StringBuilder();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (!currentBlock.isEmpty()) {
                    questions.add(Question.fromFileFormat(currentBlock.toString()));
                    currentBlock = new StringBuilder();
                }
            } else {
                if (!currentBlock.isEmpty()) {
                    currentBlock.append("\n");
                }
                currentBlock.append(line);
            }
        }

        if (!currentBlock.isEmpty()) {
            questions.add(Question.fromFileFormat(currentBlock.toString()));
        }
        
        return questions;
    }
    
    public static void saveStudentResponse(StudentResponse response) {
        try {
            Path responsesPath = Paths.get(RESPONSES_FILE);
            String line = response.toFileFormat() + "\n";
            Files.writeString(responsesPath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error saving student response: " + e.getMessage());
        }
    }
    
    public static void saveExamResult(ExamResult result) {
        try {
            Path resultsPath = Paths.get(RESULTS_FILE);
            String line = result.toFileFormat() + "\n";
            Files.writeString(resultsPath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error saving exam result: " + e.getMessage());
        }
    }
}

