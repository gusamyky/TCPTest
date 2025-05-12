package org.example.repository.implementations;

import org.example.config.Config;
import org.example.model.StudentResponse;
import org.example.repository.interfaces.StudentResponseRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileStudentResponseRepository implements StudentResponseRepository {
    private static final String DATA_DIR = Config.getDataDir();
    private static final String RESPONSES_FILE = DATA_DIR + "/odpowiedzi.txt";

    @Override
    public void save(StudentResponse response) throws Exception {
        Path responsesPath = Paths.get(RESPONSES_FILE);
        String line = response.toFileFormat() + "\n";
        Files.writeString(responsesPath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @Override
    public List<StudentResponse> findByStudentId(String studentId) throws Exception {
        return findAll().stream()
                .filter(response -> response.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResponse> findAll() throws Exception {
        List<StudentResponse> responses = new ArrayList<>();
        Path responsesPath = Paths.get(RESPONSES_FILE);

        if (Files.exists(responsesPath)) {
            List<String> lines = Files.readAllLines(responsesPath);
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    responses.add(StudentResponse.fromFileFormat(line));
                }
            }
        }

        return responses;
    }
} 