package com.mic.knowledgebase.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.mic.knowledgebase.exception.ArticleNotFoundException;
import com.mic.knowledgebase.exception.DatabaseOperationException;
import com.mic.knowledgebase.model.Article;
import com.mic.knowledgebase.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> getAllArticles() {
        logger.info("Fetching all articles");
        List<Article> articles = new ArrayList<>();
        try {
            articleRepository.findAll().forEach(articles::add);
            logger.info("Fetched {} articles successfully", articles.size());
            return articles;
        } catch (AmazonServiceException e) {
            logger.error("Error occurred while fetching all articles: {}", e.getMessage());
            throw new DatabaseOperationException("Failed to fetch articles", e);
        }
    }

    public Article getArticleById(String id) {
        logger.info("Fetching article with ID: {}", id);
        try {
            Optional<Article> article = articleRepository.findById(id);
            if (article.isPresent()) {
                logger.info("Article found: {}", article.get().getTitle());
                return article.get();
            } else {
                logger.warn("Article not found with ID: {}", id);
                throw new ArticleNotFoundException("Article not found with ID: " + id);
            }
        } catch (ResourceNotFoundException e) {
            logger.error("Error occurred while fetching article: {}", e.getMessage());
            throw new ArticleNotFoundException("Article not found with ID: " + id, e);
        } catch (AmazonServiceException e) {
            logger.error("Error occurred while fetching article: {}", e.getMessage());
            throw new DatabaseOperationException("Failed to fetch article", e);
        }
    }

    public Article createArticle(Article article) {
        logger.info("Creating new article: {}", article.getTitle());
        try {
            article.setId(UUID.randomUUID().toString());
            article.setCreatedAt(LocalDateTime.now());
            article.setUpdatedAt(LocalDateTime.now());
            Article savedArticle = articleRepository.save(article);
            logger.info("Article created successfully with ID: {}", savedArticle.getId());
            return savedArticle;
        } catch (AmazonServiceException e) {
            logger.error("Error occurred while creating article: {}", e.getMessage());
            throw new DatabaseOperationException("Failed to create article", e);
        }
    }

    public Article updateArticle(String id, Article articleDetails) {
        logger.info("Updating article with ID: {}", id);
        try {
            Optional<Article> articleOptional = articleRepository.findById(id);
            if (articleOptional.isPresent()) {
                Article existingArticle = articleOptional.get();
                existingArticle.setTitle(articleDetails.getTitle());
                existingArticle.setContent(articleDetails.getContent());
                existingArticle.setUpdatedAt(LocalDateTime.now());
                Article updatedArticle = articleRepository.save(existingArticle);
                logger.info("Article updated successfully: {}", updatedArticle.getTitle());
                return updatedArticle;
            } else {
                logger.warn("Article not found for update with ID: {}", id);
                throw new ArticleNotFoundException("Article not found with ID: " + id);
            }
        } catch (ResourceNotFoundException e) {
            logger.error("Error occurred while updating article: {}", e.getMessage());
            throw new ArticleNotFoundException("Article not found with ID: " + id, e);
        } catch (AmazonServiceException e) {
            logger.error("Error occurred while updating article: {}", e.getMessage());
            throw new DatabaseOperationException("Failed to update article", e);
        }
    }

    public void deleteArticle(String id) {
        logger.info("Deleting article with ID: {}", id);
        try {
            if (articleRepository.existsById(id)) {
                articleRepository.deleteById(id);
                logger.info("Article deleted successfully with ID: {}", id);
            } else {
                logger.warn("Article not found for deletion with ID: {}", id);
                throw new ArticleNotFoundException("Article not found with ID: " + id);
            }
        } catch (ResourceNotFoundException e) {
            logger.error("Error occurred while deleting article: {}", e.getMessage());
            throw new ArticleNotFoundException("Article not found with ID: " + id, e);
        } catch (AmazonServiceException e) {
            logger.error("Error occurred while deleting article: {}", e.getMessage());
            throw new DatabaseOperationException("Failed to delete article", e);
        }
    }
}
