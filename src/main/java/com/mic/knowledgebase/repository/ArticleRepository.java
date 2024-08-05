package com.mic.knowledgebase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mic.knowledgebase.model.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
