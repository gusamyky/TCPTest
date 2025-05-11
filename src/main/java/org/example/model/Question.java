package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
    private final int id;
    private final String content;
    private final List<String> options;
    private final List<Integer> correctAnswers;

    public Question(int id, String content, List<String> options, List<Integer> correctAnswers) {
        this.id = id;
        this.content = content;
        this.options = options;
        this.correctAnswers = correctAnswers;
    }

    public int getId() {
        return id;
    }

    public boolean isCorrectAnswer(List<Integer> answers) {
        return correctAnswers.equals(answers);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pytanie ").append(id).append(": ").append(content).append("\n");
        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1).append(". ").append(options.get(i)).append("\n");
        }
        return sb.toString();
    }
    
    public static Question fromFileFormat(String block) {
        String[] lines = block.split("\n");
        if (lines.length < 6) {
            throw new IllegalArgumentException("Invalid question format: " + block);
        }

        // Parse question ID and content
        String firstLine = lines[0];
        int id = Integer.parseInt(firstLine.substring(0, firstLine.indexOf('.')));
        String content = firstLine.substring(firstLine.indexOf('.') + 1).trim();

        // Parse options
        List<String> options = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String option = lines[i].substring(lines[i].indexOf(')') + 1).trim();
            options.add(option);
        }

        // Parse correct answers
        String correctAnswersStr = lines[5].trim();
        List<Integer> correctAnswers = new ArrayList<>();
        String[] answers = correctAnswersStr.split(",");
        for (String answer : answers) {
            answer = answer.trim().toLowerCase();
            int correctIndex = switch (answer) {
                case "a" -> 1;
                case "b" -> 2;
                case "c" -> 3;
                case "d" -> 4;
                default -> throw new IllegalArgumentException("Invalid correct answer: " + answer);
            };
            correctAnswers.add(correctIndex);
        }

        return new Question(id, content, options, correctAnswers);
    }
}
