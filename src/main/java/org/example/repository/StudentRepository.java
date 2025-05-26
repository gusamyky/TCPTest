package org.example.repository;

import org.example.config.DatabaseManager;
import org.example.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentRepository {
    private final DatabaseManager databaseManager;

    public StudentRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public boolean existsById(String id) {
        String sql = "SELECT 1 FROM students WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check student existence: " + e.getMessage(), e);
        }
    }

    public void saveIfNotExists(Student student) {
        if (!existsById(student.getId())) {
            String sql = "INSERT INTO students (id, name) VALUES (?, ?)";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.getId());
                stmt.setString(2, student.getName());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to save student: " + e.getMessage(), e);
            }
        }
    }
} 