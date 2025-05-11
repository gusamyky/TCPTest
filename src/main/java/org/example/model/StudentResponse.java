package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentResponse implements Serializable {
    private String studentId;
    private int questionId;
    private List<Integer> selectedAnswers;
    private LocalDateTime timestamp;

    public StudentResponse() {
        this.selectedAnswers = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    public StudentResponse(String studentId, int questionId, List<Integer> selectedAnswers) {
        this.studentId = studentId;
        this.questionId = questionId;
        this.selectedAnswers = selectedAnswers;
        this.timestamp = LocalDateTime.now();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public List<Integer> getSelectedAnswers() {
        return selectedAnswers;
    }

    public void setSelectedAnswers(List<Integer> selectedAnswers) {
        this.selectedAnswers = selectedAnswers;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String toFileFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(studentId).append("|")
          .append(questionId).append("|");
        
        for (Integer answer : selectedAnswers) {
            sb.append(answer).append(",");
        }
        // Remove the last comma if it exists
        if (!selectedAnswers.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        
        sb.append("|").append(timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return sb.toString();
    }
    
    public static StudentResponse fromFileFormat(String line) {
        String[] parts = line.split("\\|");
        String studentId = parts[0];
        int questionId = Integer.parseInt(parts[1]);
        
        List<Integer> selectedAnswers = new ArrayList<>();
        if (!parts[2].isEmpty()) {
            String[] answersStr = parts[2].split(",");
            for (String answer : answersStr) {
                selectedAnswers.add(Integer.parseInt(answer));
            }
        }
        
        StudentResponse response = new StudentResponse(studentId, questionId, selectedAnswers);
        response.setTimestamp(LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return response;
    }
}
