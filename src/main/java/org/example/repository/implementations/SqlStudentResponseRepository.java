package org.example.repository.implementations;

import org.example.config.DatabaseManager;
import org.example.model.StudentResponse;
import org.example.repository.interfaces.StudentResponseRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlStudentResponseRepository implements StudentResponseRepository {
    private final DatabaseManager databaseManager;

    public SqlStudentResponseRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    @Override
    public void save(StudentResponse response) throws Exception {
        String insertResponseSQL = "INSERT INTO student_responses (student_id, question_id, answer_number) VALUES (?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertResponseSQL)) {

            for (Integer answer : response.getSelectedAnswers()) {
                stmt.setString(1, response.getStudentId());
                stmt.setInt(2, response.getQuestionId());
                stmt.setInt(3, answer);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public List<StudentResponse> findByStudentId(String studentId) throws Exception {
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
        }
        return responses;
    }

    @Override
    public List<StudentResponse> findAll() throws Exception {
        String sql = "SELECT student_id, question_id, answer_number FROM student_responses";
        List<StudentResponse> responses = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                int questionId = rs.getInt("question_id");
                int answerNumber = rs.getInt("answer_number");
                List<Integer> answers = new ArrayList<>();
                answers.add(answerNumber);
                responses.add(new StudentResponse(studentId, questionId, answers));
            }
        }
        return responses;
    }
} 