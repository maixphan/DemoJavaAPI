package com.example.demo.database;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class Connect {
    @Bean
    public DynamoDbAsyncClient connectDynamoDb() {
        return DynamoDbAsyncClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }
}
