package com.mic.knowledgebase.service;

import com.mic.knowledgebase.model.Article;
import com.mic.knowledgebase.repository.ArticleRepository;
import com.mic.knowledgebase.util.LogUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public Page<Article> getAllArticles(Pageable pageable) {
        LogUtil.info("Fetching articles page: " + pageable.getPageNumber() + ", size: " + pageable.getPageSize());
        return articleRepository.findAll(pageable);
    }

    public List<Article> getAllArticles() {
        LogUtil.info("Fetching all articles");
        return articleRepository.findAll();
    }

    public Optional<Article> getArticleById(Long id) {
        LogUtil.info("Fetching article by id: " + id);
        return articleRepository.findById(id);
    }

    public Article createArticle(Article article) {
        LogUtil.info("Creating a new article");
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        Article savedArticle = articleRepository.save(article);
        LogUtil.info("Article created with id: " + savedArticle.getId());
        return articleRepository.save(article);
    }

    public Optional<Article> updateArticle(Long id, Article articleDetails) {
        Optional<Article> article = articleRepository.findById(id);
        if (article.isPresent()) {
            Article updatedArticle = article.get();
            updatedArticle.setTitle(articleDetails.getTitle());
            updatedArticle.setContent(articleDetails.getContent());
            updatedArticle.setUpdatedAt(LocalDateTime.now());
            return Optional.of(articleRepository.save(updatedArticle));
        }
        return Optional.empty();
    }

    public boolean deleteArticle(Long id) {
        if (articleRepository.existsById(id)) {
            articleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}