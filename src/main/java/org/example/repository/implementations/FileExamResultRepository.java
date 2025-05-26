package org.example.repository.implementations;

import org.example.config.ServerConfig;
import org.example.model.ExamResult;
import org.example.repository.interfaces.ExamResultRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileExamResultRepository implements ExamResultRepository {
    private static final String DATA_DIR = ServerConfig.getDataDir();
    private static final String RESULTS_FILE = DATA_DIR + "/wyniki.txt";

    @Override
    public void save(ExamResult result) throws Exception {
        Path resultsPath = Paths.get(RESULTS_FILE);
        String line = result.toFileFormat() + "\n";
        Files.writeString(resultsPath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @Override
    public List<ExamResult> findByStudentId(String studentId) throws Exception {
        return findAll().stream()
                .filter(result -> result.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResult> findAll() throws Exception {
        List<ExamResult> results = new ArrayList<>();
        Path resultsPath = Paths.get(RESULTS_FILE);

        if (Files.exists(resultsPath)) {
            List<String> lines = Files.readAllLines(resultsPath);
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    results.add(ExamResult.fromFileFormat(line));
                }
            }
        }

        return results;
    }
} 