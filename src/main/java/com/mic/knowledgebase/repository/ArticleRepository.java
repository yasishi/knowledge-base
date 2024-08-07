package com.mic.knowledgebase.repository;

import com.mic.knowledgebase.model.Article;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;

@EnableScan
public interface ArticleRepository extends DynamoDBCrudRepository<Article, String> {
}
