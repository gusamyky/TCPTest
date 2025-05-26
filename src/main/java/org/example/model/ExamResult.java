package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExamResult implements Serializable {
    private final String studentId;
    private final int correctAnswers;
    private final int totalQuestions;
    private final double score;
    private final LocalDateTime examDate;

    public ExamResult(String studentId, int correctAnswers, int totalQuestions) {
        this.studentId = studentId;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.score = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0;
        this.examDate = LocalDateTime.now();
    }

    public static ExamResult fromFileFormat(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid result format: " + line);
        }

        String studentId = parts[0];
        int correctAnswers = Integer.parseInt(parts[1]);
        int totalQuestions = Integer.parseInt(parts[2]);

        return new ExamResult(studentId, correctAnswers, totalQuestions);
    }
    
    public String toFileFormat() {
        return String.format("%s|%d|%d|%.2f|%s", 
                studentId, 
                correctAnswers, 
                totalQuestions, 
                score, 
                examDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public String getStudentId() {
        return studentId;
    }

    public double getScore() {return score;}

    public int getTotalQuestions(){return  totalQuestions;}

    @Override
    public String toString() {
        return String.format("Wynik testu: %d/%d (%.2f%%) poprawnych odpowiedzi", 
                correctAnswers, totalQuestions, score);
    }
}
