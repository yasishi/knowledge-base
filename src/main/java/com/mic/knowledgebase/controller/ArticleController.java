package com.mic.knowledgebase.controller;

import com.mic.knowledgebase.model.Article;
import com.mic.knowledgebase.service.ArticleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Article", description = "記事の管理API")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @Operation(summary = "全ての記事を取得", description = "データベースに保存されている全ての記事のリストを取得します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "記事リストの取得に成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Article.class))),
            @ApiResponse(responseCode = "500", description = "サーバーエラー", content = @Content)
    })
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

    @Operation(summary = "IDで記事を取得", description = "指定されたIDの記事を取得します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "記事の取得に成功", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Article.class)) }),
            @ApiResponse(responseCode = "404", description = "指定されたIDの記事が見つかりません", content = @Content),
            @ApiResponse(responseCode = "500", description = "サーバーエラー", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        try {
            logger.debug("Fetching article id: {}", id);
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

    @Operation(summary = "新しい記事を作成", description = "新しい記事をデータベースに作成します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "記事の作成に成功", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Article.class)) }),
            @ApiResponse(responseCode = "500", description = "サーバーエラー", content = @Content)
    })
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

    @Operation(summary = "記事を更新", description = "指定されたIDの記事を更新します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "記事の更新に成功", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Article.class)) }),
            @ApiResponse(responseCode = "404", description = "指定されたIDの記事が見つかりません", content = @Content),
            @ApiResponse(responseCode = "500", description = "サーバーエラー", content = @Content)
    })
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

    @Operation(summary = "記事を削除", description = "指定されたIDの記事を削除します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "記事の削除に成功"),
            @ApiResponse(responseCode = "404", description = "指定されたIDの記事が見つかりません", content = @Content),
            @ApiResponse(responseCode = "500", description = "サーバーエラー", content = @Content)
    })
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
