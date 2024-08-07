package com.mic.knowledgebase.service;

import com.mic.knowledgebase.model.Article;
import com.mic.knowledgebase.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        articleRepository.findAll().forEach(articles::add);
        return articles;
    }

    public Optional<Article> getArticleById(String id) {
        return articleRepository.findById(id);
    }

    public Article createArticle(Article article) {
        article.setId(UUID.randomUUID().toString());
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public Optional<Article> updateArticle(String id, Article articleDetails) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (articleOptional.isPresent()) {
            Article existingArticle = articleOptional.get();
            existingArticle.setTitle(articleDetails.getTitle());
            existingArticle.setContent(articleDetails.getContent());
            existingArticle.setUpdatedAt(LocalDateTime.now());
            return Optional.of(articleRepository.save(existingArticle));
        }
        return Optional.empty();
    }

    public void deleteArticle(String id) {
        articleRepository.deleteById(id);
    }
}
