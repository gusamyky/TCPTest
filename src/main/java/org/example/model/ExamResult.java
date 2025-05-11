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
    
    public String toFileFormat() {
        return String.format("%s|%d|%d|%.2f|%s", 
                studentId, 
                correctAnswers, 
                totalQuestions, 
                score, 
                examDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    @Override
    public String toString() {
        return String.format("Wynik testu: %d/%d (%.2f%%) poprawnych odpowiedzi", 
                correctAnswers, totalQuestions, score);
    }
}
