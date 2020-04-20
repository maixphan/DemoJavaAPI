package com.example.demo.controller;

import com.example.demo.entity.NewUser;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(path = "/",  produces = "application/json")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> list = userService.getAllUsers();
        return new ResponseEntity<List<User> >(list, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id)
            throws NotFoundException {
        User entity = userService.getUserById(id);
        return new ResponseEntity<User>(entity, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<List<User>> CreateUser(@RequestBody NewUser entity)
            throws NotFoundException {
        List<User> users = userService.createUser(entity);
        return new ResponseEntity<List<User>>(users, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> UpdateUser(@PathVariable("id") String id, String username, String password, String email)
            throws NotFoundException {
        User entity = new User(id, username, email, password);
        User user = userService.updateUser(entity);
        return new ResponseEntity<User>(user, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/addFriend/{id}")
    public ResponseEntity<User> requestAddFriend(@PathVariable("id") String id, String friendId)
            throws NotFoundException {
        User user = userService.requestAddFriend(id,friendId);
        return new ResponseEntity<User>(user, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/responseAddFriend/{id}")
    public ResponseEntity<User> responseAddFriend(@PathVariable("id") String id, String requestId, String accept)
            throws NotFoundException {
        User user = userService.responseAddFriend(id, requestId, accept);
        return new ResponseEntity<User>(user, new HttpHeaders(), HttpStatus.OK);
    }
}
