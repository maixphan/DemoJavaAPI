package com.example.demo.service;

import com.example.demo.database.Connect;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.List;

public interface CommentService {
    List<Comment> getAllComments();

    String createNewComment(Comment entity);
}

@Service
class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository repository;

    @Override
    public List<Comment> getAllComments() {
        return null;
    }

    @Override
    public String createNewComment(Comment entity) {
        try {
            repository.createNewComment(entity);
        } catch (ResourceNotFoundException e) {
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return entity.getId();
    }
}