package com.example.demo.repository;
import com.example.demo.database.Connect;
import com.example.demo.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;

public interface CommentRepository {
    void createNewComment(Comment entity);
}

@Repository
class CommentRepositoryImpl implements CommentRepository {
    @Autowired
    private Connect client;

    @Override
    public void createNewComment(Comment entity) {
        DynamoDbAsyncClient db = client.connectDynamoDb();
        HashMap<String, AttributeValue> itemValues = new HashMap<String, AttributeValue>();
        itemValues.put("id", AttributeValue.builder().s(entity.getId()).build());
        itemValues.put("txt", AttributeValue.builder().s(entity.getTxt()).build());
        itemValues.put("time", AttributeValue.builder().s(entity.getTime()).build());
        itemValues.put("userId", AttributeValue.builder().s(entity.getUserId()).build());
        itemValues.put("comments", null);
        itemValues.put("likes", null);

        PutItemRequest request = PutItemRequest.builder()
                .tableName("Comment")
                .item(itemValues)
                .build();
        db.putItem(request).join();
    }

}