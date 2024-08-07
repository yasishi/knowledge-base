package com.mic.knowledgebase.model;

import lombok.Data;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@DynamoDBTable(tableName = "Article")
public class Article {

    @Id
    @DynamoDBHashKey
    private String id;

    @DynamoDBAttribute
    private String title;

    @DynamoDBAttribute
    private String content;

    @DynamoDBAttribute
    private LocalDateTime createdAt;

    @DynamoDBAttribute
    private LocalDateTime updatedAt;

    // デフォルトコンストラクタ
    public Article() {
    }

    // 全フィールドを含むコンストラクタ
    public Article(String id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ゲッターとセッター
    // ... (省略)
}
