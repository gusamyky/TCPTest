package org.example.repository.implementations;

import org.example.config.DatabaseManager;
import org.example.model.ExamResult;
import org.example.repository.interfaces.ExamResultRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlExamResultRepository implements ExamResultRepository {
    private final DatabaseManager databaseManager;

    public SqlExamResultRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    @Override
    public void save(ExamResult result) throws Exception {
        String sql = "INSERT INTO exam_results (student_id, score, total_questions) VALUES (?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, result.getStudentId());
            stmt.setDouble(2, result.getScore());
            stmt.setInt(3, result.getTotalQuestions());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<ExamResult> findByStudentId(String studentId) throws Exception {
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
        }
        return results;
    }

    @Override
    public List<ExamResult> findAll() throws Exception {
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