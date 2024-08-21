package com.mic.knowledgebase.repository;

import com.mic.knowledgebase.model.Article;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.stereotype.Repository;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;

@Repository
@EnableScan
public interface ArticleRepository extends DynamoDBCrudRepository<Article, String> {
}
