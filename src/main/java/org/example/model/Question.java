package org.example.model;

import java.io.Serializable;
import java.util.Arrays;
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
        if (correctAnswersStr.length() > 0) {
            correctAnswersStr.deleteCharAt(correctAnswersStr.length() - 1);
        }
        
        sb.append(correctAnswersStr);
        return sb.toString();
    }
    
    public static Question fromFileFormat(String line) {
        String[] parts = line.split("\\|");
        int id = Integer.parseInt(parts[0]);
        String content = parts[1];
        List<String> options = Arrays.asList(parts[2].split(";"));
        
        String[] correctAnswersStr = parts[3].split(",");
        List<Integer> correctAnswers = Arrays.stream(correctAnswersStr)
                .map(Integer::parseInt)
                .toList();
        
        return new Question(id, content, options, correctAnswers);
    }
}
