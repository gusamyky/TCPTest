package org.example.repository;

import org.example.config.DatabaseManager;
import org.example.model.ExamResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamResultRepository {
    private final DatabaseManager databaseManager;

    public ExamResultRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void saveExamResult(ExamResult result) {
        String sql = "INSERT INTO exam_results (student_id, score, total_questions) VALUES (?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, result.getStudentId());
            stmt.setDouble(2, result.getScore());
            stmt.setInt(3, result.getTotalQuestions());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save exam result: " + e.getMessage(), e);
        }
    }

    public List<ExamResult> getStudentResults(String studentId) {
        String sql = "SELECT * FROM exam_results WHERE student_id = ? ORDER BY submission_time DESC";
        List<ExamResult> results = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new ExamResult(
                    studentId,
                    rs.getInt("score"),
                    rs.getInt("total_questions")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get student results: " + e.getMessage(), e);
        }

        return results;
    }

    public List<ExamResult> getAllResults() {
        String sql = "SELECT * FROM exam_results ORDER BY submission_time DESC";
        List<ExamResult> results = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                results.add(new ExamResult(
                    rs.getString("student_id"),
                    rs.getInt("score"),
                    rs.getInt("total_questions")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all results: " + e.getMessage(), e);
        }

        return results;
    }

    public void deleteStudentResults(String studentId) {
        String sql = "DELETE FROM exam_results WHERE student_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete student results: " + e.getMessage(), e);
        }
    }
} 