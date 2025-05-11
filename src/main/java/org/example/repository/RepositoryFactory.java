package org.example.repository;

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