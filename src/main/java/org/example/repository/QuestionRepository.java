package org.example.repository;

import org.example.config.DatabaseManager;
import org.example.model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {
    private final DatabaseManager databaseManager;

    public QuestionRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void saveQuestion(Question question) {
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

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save question: " + e.getMessage(), e);
        }
    }

    public Question getQuestionById(int id) {
        String questionSQL = "SELECT * FROM questions WHERE id = ?";
        String answersSQL = "SELECT answer_number FROM correct_answers WHERE question_id = ? ORDER BY answer_number";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement questionStmt = conn.prepareStatement(questionSQL);
             PreparedStatement answersStmt = conn.prepareStatement(answersSQL)) {

            questionStmt.setInt(1, id);
            ResultSet questionRs = questionStmt.executeQuery();

            if (!questionRs.next()) {
                return null;
            }

            String content = questionRs.getString("content");
            List<String> options = new ArrayList<>();
            options.add(questionRs.getString("option1"));
            options.add(questionRs.getString("option2"));
            options.add(questionRs.getString("option3"));
            options.add(questionRs.getString("option4"));

            answersStmt.setInt(1, id);
            ResultSet answersRs = answersStmt.executeQuery();
            List<Integer> correctAnswers = new ArrayList<>();
            while (answersRs.next()) {
                correctAnswers.add(answersRs.getInt("answer_number"));
            }

            return new Question(id, content, options, correctAnswers);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get question: " + e.getMessage(), e);
        }
    }

    public List<Question> getAllQuestions() {
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

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all questions: " + e.getMessage(), e);
        }

        return questions;
    }

    public void deleteQuestion(int id) {
        String deleteAnswersSQL = "DELETE FROM correct_answers WHERE question_id = ?";
        String deleteQuestionSQL = "DELETE FROM questions WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement deleteAnswersStmt = conn.prepareStatement(deleteAnswersSQL);
             PreparedStatement deleteQuestionStmt = conn.prepareStatement(deleteQuestionSQL)) {

            // Delete correct answers first (due to foreign key constraint)
            deleteAnswersStmt.setInt(1, id);
            deleteAnswersStmt.executeUpdate();

            // Delete question
            deleteQuestionStmt.setInt(1, id);
            deleteQuestionStmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete question: " + e.getMessage(), e);
        }
    }
} 