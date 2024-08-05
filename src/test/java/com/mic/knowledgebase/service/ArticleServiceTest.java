package com.mic.knowledgebase.service;

import com.mic.knowledgebase.model.Article;
import com.mic.knowledgebase.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
                new Article(), new Article());
        Page<Article> pagedResponse = new PageImpl(articles);
        Pageable pageable = PageRequest.of(0, 10);

        when(articleRepository.findAll(pageable)).thenReturn(pagedResponse);

        Page<Article> result = articleService.getAllArticles(pageable);

        assertEquals(2, result.getContent().size());
        verify(articleRepository, times(1)).findAll(pageable);
    }

    @Test
    void getArticleById() {
        Article article = new Article();
        article.setId(1L);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        Optional<Article> result = articleService.getArticleById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    // 他のテストメソッドも同様に実装
}