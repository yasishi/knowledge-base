package com.mic.knowledgebase.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DynamoDBTableCreator {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBTableCreator.class);

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @PostConstruct
    public void createTables() {
        logger.info("Attempting to create DynamoDB tables...");
        try {
            // Article テーブルの作成
            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withAttributeDefinitions(new AttributeDefinition("id", ScalarAttributeType.S))
                    .withKeySchema(new KeySchemaElement("id", KeyType.HASH))
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))
                    .withTableName("Articles");
            amazonDynamoDB.createTable(createTableRequest);
            logger.info("Article テーブルが作成されました");
        } catch (ResourceInUseException e) {
            logger.info("Article テーブルは既に存在します");
        } catch (Exception e) {
            logger.error("テーブル作成中にエラーが発生しました", e);
        }
    }
}