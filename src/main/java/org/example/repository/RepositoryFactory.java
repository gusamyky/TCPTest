package org.example.repository;

import org.example.repository.implementations.SqlQuestionRepository;
import org.example.repository.implementations.SqlExamResultRepository;
import org.example.repository.implementations.SqlStudentResponseRepository;
import org.example.repository.implementations.SqlStudentRepository;
import org.example.repository.interfaces.QuestionRepository;
import org.example.repository.interfaces.ExamResultRepository;
import org.example.repository.interfaces.StudentResponseRepository;
import org.example.repository.interfaces.StudentRepository;

public class RepositoryFactory {
    private static final QuestionRepository questionRepository = new SqlQuestionRepository();
    private static final ExamResultRepository examResultRepository = new SqlExamResultRepository();
    private static final StudentResponseRepository studentResponseRepository = new SqlStudentResponseRepository();
    private static final StudentRepository studentRepository = new SqlStudentRepository();

    public static QuestionRepository getQuestionRepository() {
        return questionRepository;
    }

    public static ExamResultRepository getExamResultRepository() {
        return examResultRepository;
    }

    public static StudentResponseRepository getStudentResponseRepository() {
        return studentResponseRepository;
    }

    public static StudentRepository getStudentRepository() {
        return studentRepository;
    }
} 