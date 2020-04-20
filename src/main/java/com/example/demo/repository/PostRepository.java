package com.example.demo.repository;

import com.example.demo.database.Connect;
import com.example.demo.entity.NewComment;
import com.example.demo.entity.Post;
import com.example.demo.entity.UpdatePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface PostRepository {
    List<Post> getAllPost();

    Post getPostById(String id, String userId);

    void createNewPost(Post entity);

    void updatePost(UpdatePost entity);

    void addCommentToPost(NewComment entity, List<String> newCommentList);

    void deletePost(String id, String userId);
}

@Repository
class PostRepositoryImpl implements PostRepository {
    @Autowired
    private Connect client;

    @Override
    public List<Post> getAllPost() {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Post")
                .build();
        ScanResponse scanResponse = Mono.fromCompletionStage(db.scan(scanRequest)).block();
        List<Post> allPost = scanResponse.items().stream()
                .map(valueMap -> mapPost(valueMap))
                .collect(Collectors.toList());
        return allPost;
    }

    @Override
    public Post getPostById(String id, String userId) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        keyToGet.put("id", AttributeValue.builder().s(id).build());
        keyToGet.put("userId", AttributeValue.builder().s(userId).build());
        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName("Post")
                .build();

        Map<String, AttributeValue> returnedItem = db.getItem(request)
                .join()
                .item();

        Post post = new Post();

        if (returnedItem != null) {
            post = mapPost(returnedItem);
        }
        return post;
    }

    @Override
    public void createNewPost(Post entity) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> itemValues = new HashMap<String, AttributeValue>();
        itemValues.put("id", AttributeValue.builder().s(entity.getId()).build());
        itemValues.put("txt", AttributeValue.builder().s(entity.getTxt()).build());
        itemValues.put("time", AttributeValue.builder().s(entity.getTime()).build());
        itemValues.put("userId", AttributeValue.builder().s(entity.getUserId()).build());
        itemValues.put("tagsList", !entity.getTagsList().isEmpty()
                ? AttributeValue.builder().ss(entity.getTagsList()).build()
                : null);
        itemValues.put("commentsList", null);
        itemValues.put("likeList", null);

        PutItemRequest request = PutItemRequest.builder()
                .tableName("Post")
                .item(itemValues)
                .build();
        db.putItem(request).join();
    }

    @Override
    public void updatePost(UpdatePost entity) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<String, AttributeValueUpdate>();
        keyToGet.put("id", AttributeValue.builder().s(entity.getId()).build());
        keyToGet.put("userId", AttributeValue.builder().s(entity.getUserId()).build());

        handleUpdateValue(updatedValues, "txt", entity.getTxt());
        handleUpdateListValue(updatedValues, "tagsList", entity.getTagsList());

        UpdateItemRequest requestUpdate = UpdateItemRequest.builder()
                .tableName("Post")
                .key(keyToGet)
                .attributeUpdates(updatedValues)
                .build();
        db.updateItem(requestUpdate).join();
    }

    @Override
    public void addCommentToPost(NewComment entity, List<String> newCommentList) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<String, AttributeValueUpdate>();
        keyToGet.put("id", AttributeValue.builder().s(entity.getPostId()).build());
        keyToGet.put("userId", AttributeValue.builder().s(entity.getUserId()).build());
        handleUpdateListValue(updatedValues, "commentsList", newCommentList);

        UpdateItemRequest requestUpdate = UpdateItemRequest.builder()
                .tableName("Post")
                .key(keyToGet)
                .attributeUpdates(updatedValues)
                .build();
        db.updateItem(requestUpdate).join();
    }

    @Override
    public void deletePost(String id, String userId) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> keyToGet =
                new HashMap<String, AttributeValue>();

        keyToGet.put("id", AttributeValue.builder().s(id).build());
        keyToGet.put("userId", AttributeValue.builder().s(userId).build());
        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName("Post")
                .key(keyToGet)
                .build();
        db.deleteItem(deleteReq);
    }

    private Post mapPost(Map<String, AttributeValue> map) {
        return new Post(
                map.get("id").s(),
                map.get("txt").s(),
                map.get("time").s(),
                map.get("userId").s(),
                map.get("commentsList") != null ?
                        map.get("commentsList").ss() :
                        Collections.EMPTY_LIST,
                map.get("likeList") != null ?
                        map.get("likeList").ss() :
                        Collections.EMPTY_LIST,
                map.get("tagsList") != null ?
                        map.get("tagsList").ss() :
                        Collections.EMPTY_LIST
        );
    }

    private void handleUpdateListValue(HashMap<String, AttributeValueUpdate>
                                               updatedValues, String key, List<String> keyValue) {
        updatedValues.put(key, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().ss(keyValue).build())
                .action(AttributeAction.PUT)
                .build());
    }

    private void handleUpdateValue(HashMap<String, AttributeValueUpdate>
                                           updatedValues, String key, String keyValue) {
        updatedValues.put(key, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(keyValue).build())
                .action(AttributeAction.PUT)
                .build());
    }
}