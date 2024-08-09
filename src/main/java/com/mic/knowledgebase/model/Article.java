package com.mic.knowledgebase.model;

import lombok.Data;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute
    private LocalDateTime createdAt;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
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

    // LocalDateTime converter
    public static class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {
        private static final DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };

        @Override
        public String convert(LocalDateTime time) {
            return time != null ? time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        }

        @Override
        public LocalDateTime unconvert(String stringValue) {
            if (stringValue == null) {
                return null;
            }
            for (DateTimeFormatter formatter : formatters) {
                try {
                    return LocalDateTime.parse(stringValue, formatter);
                } catch (DateTimeParseException e) {
                    // Try next formatter
                }
            }
            throw new IllegalArgumentException("Unable to parse date time value: " + stringValue);
        }
    }
}