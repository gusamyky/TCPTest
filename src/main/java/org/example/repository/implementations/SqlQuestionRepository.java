package org.example.repository.implementations;

import org.example.config.DatabaseManager;
import org.example.model.Question;
import org.example.repository.interfaces.QuestionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlQuestionRepository implements QuestionRepository {
    private final DatabaseManager databaseManager;

    public SqlQuestionRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    @Override
    public void save(Question question) throws Exception {
        String insertQuestionSQL = "INSERT IGNORE INTO questions (id, content, option1, option2, option3, option4) VALUES (?, ?, ?, ?, ?, ?)";
        String insertAnswerSQL = "INSERT IGNORE INTO correct_answers (question_id, answer_number) VALUES (?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement questionStmt = conn.prepareStatement(insertQuestionSQL);
             PreparedStatement answerStmt = conn.prepareStatement(insertAnswerSQL)) {

            // Insert question
            questionStmt.setInt(1, question.getId());
            questionStmt.setString(2, question.getContent());
            List<String> options = question.getOptions();
            for (int i = 0; i < 4; i++) {
                questionStmt.setString(i + 3, options.get(i));
            }
            questionStmt.executeUpdate();

            // Insert correct answers
            for (Integer answer : question.getCorrectAnswers()) {
                answerStmt.setInt(1, question.getId());
                answerStmt.setInt(2, answer);
                answerStmt.executeUpdate();
            }
        }
    }

    @Override
    public List<Question> findAll() throws Exception {
        List<Question> questions = new ArrayList<>();
        String sql = """
            SELECT q.*, GROUP_CONCAT(ca.answer_number ORDER BY ca.answer_number) as correct_answers 
            FROM questions q 
            LEFT JOIN correct_answers ca ON q.id = ca.question_id 
            GROUP BY q.id 
            ORDER BY q.id
            """;

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("content");
                List<String> options = new ArrayList<>();
                options.add(rs.getString("option1"));
                options.add(rs.getString("option2"));
                options.add(rs.getString("option3"));
                options.add(rs.getString("option4"));

                List<Integer> correctAnswers = new ArrayList<>();
                String correctAnswersStr = rs.getString("correct_answers");
                if (correctAnswersStr != null) {
                    for (String answer : correctAnswersStr.split(",")) {
                        correctAnswers.add(Integer.parseInt(answer));
                    }
                }

                questions.add(new Question(id, content, options, correctAnswers));
            }
        }
        return questions;
    }

    @Override
    public void saveAll(List<Question> questions) throws Exception {
        for (Question question : questions) {
            save(question);
        }
    }
} 