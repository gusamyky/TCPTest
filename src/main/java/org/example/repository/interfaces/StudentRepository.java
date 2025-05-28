package org.example.repository.interfaces;

import org.example.model.Student;

public interface StudentRepository {
    boolean existsById(String id);
    void saveIfNotExists(Student student);
} 