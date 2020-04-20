package com.example.demo.service;

import com.example.demo.entity.NewUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.database.Connect;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import com.example.demo.entity.User;

import java.util.*;
import java.util.stream.Collectors;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(String id);

    List<User> createUser(NewUser user);

    User updateUser(User user);

    User requestAddFriend(String id, String friendId);

    User responseAddFriend(String id, String requestId, String accept);
}

@Service
class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private Connect client;

    @Override
    public List<User> getAllUsers() {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("User")
                .build();
        ScanResponse scanResponse = Mono.fromCompletionStage(db.scan(scanRequest)).block();

        return scanResponse.items().stream().map(valueMap -> {
            User user = mapUser(valueMap);
            return user;
        }).collect(Collectors.toList());
    }

    @Override
    public User getUserById(String id) throws NotFoundException {
        return repository.getUserById(id);
    }

    @Override
    public List<User> createUser(NewUser entity) throws NotFoundException {
        User user = new User(
                UUID.randomUUID().toString(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword());
        try {
            repository.createNewUser(user);
        } catch (ResourceNotFoundException e) {
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return getAllUsers();
    }

    @Override
    public User updateUser(User entity) throws NotFoundException {
        try {
            repository.updateUser(entity);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return getUserById(entity.getId());
    }

    @Override
    public User requestAddFriend(String id, String friendId) throws NotFoundException {
        repository.addFriendToListRequest(id, friendId);
        return getUserById(friendId);
    }

    @Override
    public User responseAddFriend(String id, String requestId, String accept) throws NotFoundException {
        handleRequestFriend addFriendsForTheRequested = new handleRequestFriend();
        handleRequestFriend addFriendsToTheSenderOfTheRequest = new handleRequestFriend();
        if (accept.equals("yes")) {
            addFriendsForTheRequested.acceptAddFriend(id, requestId);
            addFriendsToTheSenderOfTheRequest.acceptAddFriend(requestId, id);
        } else {
            addFriendsForTheRequested.rejectAddFriend(id, requestId);
        }

        return getUserById(id);
    }

    private class handleRequestFriend {
        void acceptAddFriend(String id, String friendId) throws NotFoundException {
            repository.addFriendToListFriends(id, friendId);
        }

        void rejectAddFriend(String id, String requestId) throws NotFoundException {
            repository.removeFriendFormListRequest(id, requestId);
        }
    }

    private User mapUser(Map<String, AttributeValue> map) {
        return new User(
                map.get("id").s(),
                map.get("username").s(),
                map.get("email").s(),
                map.get("password").s(),
                map.get("friendsId") != null ?
                        map.get("friendsId").ss() :
                        Collections.EMPTY_LIST,
                map.get("request") != null ?
                        map.get(("request")).ss() :
                        null
        );
    }
}
