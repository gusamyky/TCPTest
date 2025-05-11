package org.example.repository;

import org.example.config.Config;
import org.example.model.Question;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileQuestionRepository implements QuestionRepository {
    private static final String RESOURCES_DIR = Config.getResourcesDir();
    private static final String QUESTIONS_FILE = RESOURCES_DIR + "/bazaPytan.txt";

    @Override
    public List<Question> findAll() throws Exception {
        List<Question> questions = new ArrayList<>();

        Path questionsPath = Paths.get(QUESTIONS_FILE);
        if (!Files.exists(questionsPath)) {
            throw new IOException("Questions file not found: " + QUESTIONS_FILE);
        }

        List<String> lines = Files.readAllLines(questionsPath);
        StringBuilder currentBlock = new StringBuilder();

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (currentBlock.length() > 0) {
                    questions.add(Question.fromFileFormat(currentBlock.toString()));
                    currentBlock = new StringBuilder();
                }
            } else {
                if (currentBlock.length() > 0) {
                    currentBlock.append("\n");
                }
                currentBlock.append(line);
            }
        }

        if (currentBlock.length() > 0) {
            questions.add(Question.fromFileFormat(currentBlock.toString()));
        }

        return questions;
    }

    @Override
    public void save(Question question) throws Exception {
        throw new UnsupportedOperationException("Saving individual questions is not supported in file mode");
    }

    @Override
    public void saveAll(List<Question> questions) throws Exception {
        throw new UnsupportedOperationException("Saving multiple questions is not supported in file mode");
    }
} 