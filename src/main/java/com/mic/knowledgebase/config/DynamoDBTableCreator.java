package com.mic.knowledgebase.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DynamoDBTableCreator {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @PostConstruct
    public void createTables() {
        try {
            // Article テーブルの作成
            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withAttributeDefinitions(new AttributeDefinition("id", ScalarAttributeType.S))
                    .withKeySchema(new KeySchemaElement("id", KeyType.HASH))
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))
                    .withTableName("Article");

            amazonDynamoDB.createTable(createTableRequest);

            System.out.println("Article テーブルが作成されました");
        } catch (ResourceInUseException e) {
            System.out.println("Article テーブルは既に存在します");
        }
    }
}