package org.example.repository;

import org.example.config.DatabaseManager;
import org.example.model.StudentResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentResponseRepository {
    private final DatabaseManager databaseManager;

    public StudentResponseRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void saveStudentResponse(StudentResponse response) {
        String insertResponseSQL = "INSERT INTO student_responses (student_id, question_id, answer_number) VALUES (?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertResponseSQL)) {

            for (Integer answer : response.getSelectedAnswers()) {
                stmt.setString(1, response.getStudentId());
                stmt.setInt(2, response.getQuestionId());
                stmt.setInt(3, answer);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save student response: " + e.getMessage(), e);
        }
    }

    public List<StudentResponse> getStudentResponses(String studentId) {
        String sql = "SELECT question_id, GROUP_CONCAT(answer_number) as answers " +
                    "FROM student_responses " +
                    "WHERE student_id = ? " +
                    "GROUP BY student_id, question_id";

        List<StudentResponse> responses = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                String[] answerStrings = rs.getString("answers").split(",");
                List<Integer> answers = new ArrayList<>();
                for (String answer : answerStrings) {
                    answers.add(Integer.parseInt(answer.trim()));
                }
                responses.add(new StudentResponse(studentId, questionId, answers));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get student responses: " + e.getMessage(), e);
        }

        return responses;
    }

    public void deleteStudentResponses(String studentId) {
        String sql = "DELETE FROM student_responses WHERE student_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete student responses: " + e.getMessage(), e);
        }
    }
} 