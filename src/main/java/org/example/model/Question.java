package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
    private int id;
    private String content;
    private List<String> options;
    private List<Integer> correctAnswers;

    public Question() {
    }

    public Question(int id, String content, List<String> options, List<Integer> correctAnswers) {
        this.id = id;
        this.content = content;
        this.options = options;
        this.correctAnswers = correctAnswers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<Integer> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<Integer> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public boolean isCorrectAnswer(int answer) {
        return correctAnswers.contains(answer);
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
    
    public String toFileFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|").append(content).append("|");
        sb.append(String.join(";", options)).append("|");
        
        StringBuilder correctAnswersStr = new StringBuilder();
        for (Integer answer : correctAnswers) {
            correctAnswersStr.append(answer).append(",");
        }
        // Remove the last comma
        if (!correctAnswersStr.isEmpty()) {
            correctAnswersStr.deleteCharAt(correctAnswersStr.length() - 1);
        }
        
        sb.append(correctAnswersStr);
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
