package com.mic.knowledgebase.service;

import com.amazonaws.AmazonServiceException;
import com.mic.knowledgebase.exception.ArticleNotFoundException;
import com.mic.knowledgebase.exception.DatabaseOperationException;
import com.mic.knowledgebase.model.Article;
import com.mic.knowledgebase.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllArticles() {
        List<Article> articles = Arrays.asList(
                new Article("1", "Title 1", "Content 1", LocalDateTime.now(), LocalDateTime.now()),
                new Article("2", "Title 2", "Content 2", LocalDateTime.now(), LocalDateTime.now()));
        when(articleRepository.findAll()).thenReturn(articles);

        List<Article> result = articleService.getAllArticles();

        assertEquals(2, result.size());
        verify(articleRepository, times(1)).findAll();
    }

    @Test
    void getAllArticles_DatabaseOperationException() {
        when(articleRepository.findAll()).thenThrow(new AmazonServiceException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> articleService.getAllArticles());
    }

    @Test
    void getArticleById() {
        Article article = new Article("1", "Title", "Content", LocalDateTime.now(), LocalDateTime.now());
        when(articleRepository.findById("1")).thenReturn(Optional.of(article));

        Article result = articleService.getArticleById("1");

        assertEquals("1", result.getId());
    }

    @Test
    void getArticleById_NotFound() {
        when(articleRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ArticleNotFoundException.class, () -> articleService.getArticleById("1"));
    }

    @Test
    void createArticle() {
        Article article = new Article(null, "New Title", "New Content", null, null);
        Article savedArticle = new Article("1", "New Title", "New Content", LocalDateTime.now(), LocalDateTime.now());
        when(articleRepository.save(any(Article.class))).thenReturn(savedArticle);

        Article result = articleService.createArticle(article);

        assertNotNull(result.getId());
        assertEquals("New Title", result.getTitle());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void createArticle_DatabaseOperationException() {
        Article article = new Article(null, "New Title", "New Content", null, null);
        when(articleRepository.save(any(Article.class))).thenThrow(new AmazonServiceException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> articleService.createArticle(article));
    }

    @Test
    void updateArticle() {
        Article existingArticle = new Article("1", "Old Title", "Old Content", LocalDateTime.now(),
                LocalDateTime.now());
        Article updatedArticle = new Article("1", "Updated Title", "Updated Content", existingArticle.getCreatedAt(),
                LocalDateTime.now());
        when(articleRepository.findById("1")).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);

        Article result = articleService.updateArticle("1", updatedArticle);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
    }

    @Test
    void updateArticle_NotFound() {
        Article updatedArticle = new Article("1", "Updated Title", "Updated Content", LocalDateTime.now(),
                LocalDateTime.now());
        when(articleRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ArticleNotFoundException.class, () -> articleService.updateArticle("1", updatedArticle));
    }

    @Test
    void deleteArticle() {
        when(articleRepository.existsById("1")).thenReturn(true);
        doNothing().when(articleRepository).deleteById("1");

        assertDoesNotThrow(() -> articleService.deleteArticle("1"));
        verify(articleRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteArticle_NotFound() {
        when(articleRepository.existsById("1")).thenReturn(false);

        assertThrows(ArticleNotFoundException.class, () -> articleService.deleteArticle("1"));
    }
}
