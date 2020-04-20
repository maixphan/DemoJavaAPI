package com.example.demo.controller;

import com.example.demo.entity.NewComment;
import com.example.demo.entity.NewPost;
import com.example.demo.entity.Post;
import com.example.demo.entity.UpdatePost;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import com.example.demo.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @GetMapping(path = "/getAllPostsByUser/{id}",  produces = "application/json")
    public ResponseEntity<List<Post>> getAllPostsByUser(@PathVariable("id") String userId) {
        List<Post> list = postService.getAllPostsByUser(userId);
        return new ResponseEntity<List<Post>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}/{userId}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") String id,@PathVariable("userId") String userId)
            throws NotFoundException {
        Post entity = postService.getPostById(id, userId);
        return new ResponseEntity<Post>(entity, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<List<Post>> createNewPost(@RequestBody NewPost post)
            throws NotFoundException {
        List<Post> posts =  postService.createNewPost(post);
        return new ResponseEntity<List<Post>>(posts, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Post> updatePost(@RequestBody UpdatePost entity)
            throws NotFoundException {
        Post updatedPost = postService.updatePost(entity);
        return new ResponseEntity<Post>(updatedPost, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/addComment")
    public ResponseEntity<Post> addCommentToPost(@RequestBody NewComment entity)
            throws NotFoundException {
        Post updatedPost = postService.addCommentToPost(entity);
        return new ResponseEntity<Post>(updatedPost, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<Post>> deletePost(@PathVariable("id") String id, @RequestParam String userId) throws NotFoundException {
        List<Post> post = postService.deletePostById(id, userId);
        return new ResponseEntity<List<Post>>(post, new HttpHeaders(), HttpStatus.OK);
    }
}
