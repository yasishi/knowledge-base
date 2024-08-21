package com.mic.knowledgebase.controller;

import com.mic.knowledgebase.model.Article;
import com.mic.knowledgebase.service.ArticleService;
import com.mic.knowledgebase.exception.ArticleNotFoundException;
import com.mic.knowledgebase.exception.DatabaseOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        logger.debug("Fetching all articles");
        try {
            List<Article> articles = articleService.getAllArticles();
            logger.debug("Found {} articles", articles.size());
            return ResponseEntity.ok(articles);
        } catch (DatabaseOperationException e) {
            logger.error("Error fetching articles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        try {
            Article article = articleService.getArticleById(id);
            return ResponseEntity.ok(article);
        } catch (ArticleNotFoundException e) {
            logger.warn("Article not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (DatabaseOperationException e) {
            logger.error("Error fetching article with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        try {
            Article createdArticle = articleService.createArticle(article);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        } catch (DatabaseOperationException e) {
            logger.error("Error creating article", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable String id, @RequestBody Article articleDetails) {
        try {
            Article updatedArticle = articleService.updateArticle(id, articleDetails);
            return ResponseEntity.ok(updatedArticle);
        } catch (ArticleNotFoundException e) {
            logger.warn("Article not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (DatabaseOperationException e) {
            logger.error("Error updating article with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.noContent().build();
        } catch (ArticleNotFoundException e) {
            logger.warn("Article not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (DatabaseOperationException e) {
            logger.error("Error deleting article with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
