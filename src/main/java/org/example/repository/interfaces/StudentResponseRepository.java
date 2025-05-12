package org.example.repository.interfaces;

import org.example.model.StudentResponse;

import java.util.List;

public interface StudentResponseRepository {
    void save(StudentResponse response) throws Exception;

    List<StudentResponse> findByStudentId(String studentId) throws Exception;

    List<StudentResponse> findAll() throws Exception;
} 