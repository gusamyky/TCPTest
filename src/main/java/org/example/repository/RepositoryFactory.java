package org.example.repository;

import org.example.repository.implementations.FileExamResultRepository;
import org.example.repository.implementations.FileQuestionRepository;
import org.example.repository.implementations.FileStudentResponseRepository;
import org.example.repository.interfaces.ExamResultRepository;
import org.example.repository.interfaces.QuestionRepository;
import org.example.repository.interfaces.StudentResponseRepository;

public class RepositoryFactory {
    private static final QuestionRepository questionRepository = new FileQuestionRepository();
    private static final ExamResultRepository examResultRepository = new FileExamResultRepository();
    private static final StudentResponseRepository studentResponseRepository = new FileStudentResponseRepository();

    public static QuestionRepository getQuestionRepository() {
        return questionRepository;
    }

    public static ExamResultRepository getExamResultRepository() {
        return examResultRepository;
    }

    public static StudentResponseRepository getStudentResponseRepository() {
        return studentResponseRepository;
    }
} 