package com.example.demo.service;

import com.example.demo.database.Connect;
import com.example.demo.entity.*;
import com.example.demo.repository.PostRepository;
import com.example.demo.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public interface PostService {
    List<Post> getAllPostsByUser(String userId);

    Post getPostById(String id, String userId);

    List<Post> createNewPost(NewPost post);

    Post updatePost(UpdatePost entity);

    List<Post> deletePostById(String id, String userId);

    Post addCommentToPost(NewComment entity);
}

@Service
class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository repository;

    @Autowired
    private CommentService commentService;

    @Override
    public List<Post> getAllPostsByUser(String userId) {
        List<Post> allPost = repository.getAllPost();
        List<Post> result = allPost.stream()
                .filter(post -> post.getUserId().equals(userId))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public Post getPostById(String id, String userId) throws NotFoundException {
        Post post = repository.getPostById(id, userId);
        return post;
    }

    @Override
    public List<Post> createNewPost(NewPost entity) throws NotFoundException {
        Post post = new Post(
                UUID.randomUUID().toString(),
                entity.getTxt(),
                LocalDateTime.now().toString(),
                entity.getUserId(),
                entity.getTagsList()
        );

        try {
            repository.createNewPost(post);
        } catch (ResourceNotFoundException e) {
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return getAllPostsByUser(entity.getUserId());
    }

    @Override
    public Post updatePost(UpdatePost entity) throws NotFoundException {
        try {
            repository.updatePost(entity);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return getPostById(entity.getId(), entity.getUserId());
    }

    @Override
    public List<Post> deletePostById(String id, String userId) throws NotFoundException {
        try {
            repository.deletePost(id, userId);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return getAllPostsByUser(userId);
    }

    @Override
    public Post addCommentToPost(NewComment entity) throws NotFoundException {
        Comment newComment = new Comment(
                UUID.randomUUID().toString(),
                entity.getTxt(),
                LocalDateTime.now().toString(),
                entity.getUserId()
        );
        String commentId = commentService.createNewComment(newComment);
        Post post = getPostById(entity.getPostId(), entity.getUserId());
        List<String> newCommentList = new ArrayList<>(post.getCommentsList());
        newCommentList.add(commentId);
        repository.addCommentToPost(entity, newCommentList);
        post.setCommentsList(newCommentList);
        return post;
    }
}