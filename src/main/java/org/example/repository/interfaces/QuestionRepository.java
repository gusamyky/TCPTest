package org.example.repository.interfaces;

import org.example.model.Question;

import java.util.List;

public interface QuestionRepository {
    List<Question> findAll() throws Exception;

    void save(Question question) throws Exception;

    void saveAll(List<Question> questions) throws Exception;
} 