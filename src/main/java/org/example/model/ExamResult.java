package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExamResult implements Serializable {
    private String studentId;
    private int correctAnswers;
    private int totalQuestions;
    private double score;
    private LocalDateTime examDate;

    public ExamResult() {
        this.examDate = LocalDateTime.now();
    }

    public ExamResult(String studentId, int correctAnswers, int totalQuestions) {
        this.studentId = studentId;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.score = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0;
        this.examDate = LocalDateTime.now();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
        this.calculateScore();
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
        this.calculateScore();
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDateTime examDate) {
        this.examDate = examDate;
    }
    
    private void calculateScore() {
        this.score = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0;
    }
    
    public String toFileFormat() {
        return String.format("%s|%d|%d|%.2f|%s", 
                studentId, 
                correctAnswers, 
                totalQuestions, 
                score, 
                examDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    public static ExamResult fromFileFormat(String line) {
        String[] parts = line.split("\\|");
        
        ExamResult result = new ExamResult();
        result.setStudentId(parts[0]);
        result.setCorrectAnswers(Integer.parseInt(parts[1]));
        result.setTotalQuestions(Integer.parseInt(parts[2]));
        result.setExamDate(LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("Wynik testu: %d/%d (%.2f%%) poprawnych odpowiedzi", 
                correctAnswers, totalQuestions, score);
    }
}
