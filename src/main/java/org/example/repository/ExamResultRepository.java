package org.example.repository;

import org.example.model.ExamResult;

import java.util.List;

public interface ExamResultRepository {
    void save(ExamResult result) throws Exception;

    List<ExamResult> findByStudentId(String studentId) throws Exception;

    List<ExamResult> findAll() throws Exception;
} 