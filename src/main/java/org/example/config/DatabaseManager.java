package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            // Register JDBC driver
            Class.forName(DatabaseConfig.getDriver());

            // Create database if not exists
            createDatabaseIfNotExists();

            // Connect to the database
            connection = DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
            );

            // Create tables
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    private void createDatabaseIfNotExists() throws SQLException {
        try (Connection tempConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306",
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword());
             Statement statement = tempConnection.createStatement()) {
            
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS tcp_test_db");
        }
    }

    private void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Enable GROUP_CONCAT functionality
            statement.execute("SET SESSION group_concat_max_len = 1000000");
            statement.execute("SET SESSION sql_mode = ''");

            // Create students table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    id VARCHAR(20) PRIMARY KEY,
                    name VARCHAR(255) NOT NULL
                )
            """);

            // Create questions table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS questions (
                    id INT PRIMARY KEY,
                    content TEXT NOT NULL,
                    option1 TEXT NOT NULL,
                    option2 TEXT NOT NULL,
                    option3 TEXT NOT NULL,
                    option4 TEXT NOT NULL
                )
            """);

            // Create correct_answers table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS correct_answers (
                    question_id INT,
                    answer_number INT,
                    PRIMARY KEY (question_id, answer_number),
                    FOREIGN KEY (question_id) REFERENCES questions(id)
                )
            """);

            // Create student_responses table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS student_responses (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id VARCHAR(20) NOT NULL,
                    question_id INT,
                    answer_number INT,
                    submission_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (student_id) REFERENCES students(id),
                    FOREIGN KEY (question_id) REFERENCES questions(id)
                )
            """);

            // Create exam_results table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS exam_results (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id VARCHAR(20) NOT NULL,
                    score DOUBLE NOT NULL,
                    total_questions INT NOT NULL,
                    submission_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (student_id) REFERENCES students(id)
                )
            """);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initializeDatabase();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection: " + e.getMessage(), e);
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close database connection: " + e.getMessage(), e);
            }
        }
    }
} 