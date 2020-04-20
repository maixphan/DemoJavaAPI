package com.example.demo.repository;

import com.example.demo.database.Connect;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

public interface UserRepository {
    User getUserById(String id);

    void createNewUser(User user);

    void updateUser(User user);

    void addFriendToListRequest(String id, String friendId);

    void removeFriendFormListRequest(String id, String friendId);

    void addFriendToListFriends(String id, String friendId);
}

@Repository
class UserRepositoryImpl implements UserRepository {
    @Autowired
    private Connect client;

    @Override
    public User getUserById(String id) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        keyToGet.put("id", AttributeValue.builder().s(id).build());
        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName("User")
                .build();

        Map<String, AttributeValue> returnedItem = db.getItem(request)
                .join()
                .item();

        User user = new User();
        if (returnedItem != null) {
            user = mapUser(returnedItem);
        }
        return user;
    }

    @Override
    public void createNewUser(User entity) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> itemValues = new HashMap<String, AttributeValue>();
        itemValues.put("id", AttributeValue.builder().s(entity.getId()).build());
        itemValues.put("username", AttributeValue.builder().s(entity.getUsername()).build());
        itemValues.put("email", AttributeValue.builder().s(entity.getEmail()).build());
        itemValues.put("password", AttributeValue.builder().s(entity.getPassword()).build());
        itemValues.put("friendsId", null);
        itemValues.put("request", null);

        PutItemRequest request = PutItemRequest.builder()
                .tableName("User")
                .item(itemValues)
                .build();
        db.putItem(request).join();
    }

    @Override
    public void updateUser(User entity) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        keyToGet.put("id", AttributeValue.builder().s(entity.getId()).build());
        HashMap<String, AttributeValueUpdate> updatedValues =
                new HashMap<String, AttributeValueUpdate>();

        if (!entity.getUsername().isEmpty()) {
            handleUpdateValue(updatedValues, "username", entity.getUsername());
        }
        if (!entity.getEmail().isEmpty()) {
            handleUpdateValue(updatedValues, "email", entity.getEmail());
        }
        if (!entity.getPassword().isEmpty()) {
            handleUpdateValue(updatedValues, "password", entity.getPassword());
        }

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("User")
                .key(keyToGet)
                .attributeUpdates(updatedValues)
                .build();
        db.updateItem(request).join();
    }

    @Override
    public void addFriendToListRequest(String id, String friendId) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        keyToGet.put("id", AttributeValue.builder().s(friendId).build());
        User user = getUserById(friendId);
        HashMap<String, AttributeValueUpdate> updatedValues =
                new HashMap<String, AttributeValueUpdate>();

        if (user != null) {
            List<String> newRequestList = new ArrayList<>(user.getRequest());
            newRequestList.add(id);
            handleUpdateListValue(updatedValues, "request", newRequestList);
            UpdateItemRequest requestUpdate = UpdateItemRequest.builder()
                    .tableName("User")
                    .key(keyToGet)
                    .attributeUpdates(updatedValues)
                    .build();
            db.updateItem(requestUpdate).join();
        } else {
            System.out.format("No item found with the key %s!\n", id);
        }
    }

    @Override
    public void removeFriendFormListRequest(String id, String friendId) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        keyToGet.put("id", AttributeValue.builder().s(id).build());
        User user = getUserById(id);
        HashMap<String, AttributeValueUpdate> updatedValues =
                new HashMap<String, AttributeValueUpdate>();

        if (user != null) {
            List<String> newRequestList = new ArrayList<>(user.getRequest());
            newRequestList.remove(friendId);
            handleUpdateListValue(updatedValues, "request", newRequestList);
            UpdateItemRequest requestUpdate = UpdateItemRequest.builder()
                    .tableName("User")
                    .key(keyToGet)
                    .attributeUpdates(updatedValues)
                    .build();
            db.updateItem(requestUpdate).join();
        } else {
            System.out.format("No item found with the key %s!\n", id);
        }
    }

    @Override
    public void addFriendToListFriends(String id, String friendId) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        keyToGet.put("id", AttributeValue.builder().s(id).build());
        HashMap<String, AttributeValueUpdate> updatedValues =
                new HashMap<String, AttributeValueUpdate>();
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (friend != null) {
            List<String> newFriendList = new ArrayList<>(user.getFriendsId());
            newFriendList.add(friendId);
            handleUpdateListValue(updatedValues, "friendsId", newFriendList);
            UpdateItemRequest requestUpdate = UpdateItemRequest.builder()
                    .tableName("User")
                    .key(keyToGet)
                    .attributeUpdates(updatedValues)
                    .build();
            db.updateItem(requestUpdate).join();
        } else {
            System.out.format("No item found with the key %s!\n", id);
        }
    }

    private void handleUpdateValue(HashMap<String, AttributeValueUpdate>
                                           updatedValues, String key, String keyValue) {
        updatedValues.put(key, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(keyValue).build())
                .action(AttributeAction.PUT)
                .build());
    }

    private void handleUpdateListValue(HashMap<String, AttributeValueUpdate>
                                               updatedValues, String key, List<String> keyValue) {
        updatedValues.put(key, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().ss(keyValue).build())
                .action(AttributeAction.PUT)
                .build());
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