package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping(path = "/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping(path = "/",  produces = "application/json")
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> list = commentService.getAllComments();
        return new ResponseEntity<List<Comment>>(list, new HttpHeaders(), HttpStatus.OK);
    }
}
