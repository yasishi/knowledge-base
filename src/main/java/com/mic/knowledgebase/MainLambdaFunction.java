package com.mic.knowledgebase;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mic.knowledgebase.controller.ArticleController;
import com.mic.knowledgebase.model.Article;
import com.mic.knowledgebase.service.ArticleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * メインLambda関数ハンドラー。
 * API GatewayからのHTTPリクエストを処理し、適切なArticleServiceメソッドを呼び出します。
 * このクラスは、AWS LambdaとSpring Cloud Functionを統合するためのエントリーポイントとして機能します。
 * Spring Cloud Function の使用: Function インターフェースを実装することで、Spring
 * の依存性注入やその他の機能を利用しながら、Lambda 関数として動作します。
 * API Gateway との統合: APIGatewayProxyRequestEvent と APIGatewayProxyResponseEvent
 * を使用することで、API Gateway からのリクエストを適切に処理できます。
 */
@Component
public class MainLambdaFunction implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MainLambdaFunction.class);

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ObjectMapper objectMapper;

    public MainLambdaFunction() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private String serialize(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Lambda関数のメインハンドラーメソッド。
     * API Gatewayからのリクエストを処理し、適切なレスポンスを返します。
     *
     * @param input API Gatewayからのリクエストイベント
     * @return API Gatewayに返すレスポンスイベント
     */
    @Override
    public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent input) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        // CORS ヘッダーを追加
        response.setHeaders(createCorsHeaders());

        try {
            logger.info("Received event: {}", objectMapper.writeValueAsString(input));
            // HTTPメソッドに基づいて適切な操作を実行
            switch (input.getHttpMethod()) {
                case "GET":
                    handleGetRequest(input, response);
                    break;
                case "POST":
                    handlePostRequest(input, response);
                    break;
                case "PUT":
                    handlePutRequest(input, response);
                    break;
                case "DELETE":
                    handleDeleteRequest(input, response);
                    break;
                case "OPTIONS":
                    response.setStatusCode(200);
                    break;
                default:
                    // サポートされていないHTTPメソッドの場合
                    response.setStatusCode(405);
                    response.setBody("Method Not Allowed");
            }
        } catch (Exception e) {
            // 予期しないエラーが発生した場合の処理
            logger.error("Error processing request", e);
            response.setStatusCode(500);
            response.setBody("Internal Server Error: " + e.getMessage());
        }
        return response;
    }

    private Map<String, String> createCorsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "https://d2vr3lhn3cliio.cloudfront.net");
        headers.put("Access-Control-Allow-Headers",
                "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        return headers;
    }

    // private void handleGetRequest(APIGatewayProxyRequestEvent input,
    // APIGatewayProxyResponseEvent response)
    // throws Exception {
    // logger.info("### input.path: {} ###", input.getPath());
    // if (input.getPath().matches("/articles/\\w+")) {
    // // 特定の記事を取得
    // String id = input.getPath().split("/")[2];
    // Article article = articleService.getArticleById(id);
    // response.setStatusCode(200);
    // response.setBody(objectMapper.writeValueAsString(article));
    // } else {
    // // 全記事を取得
    // List<Article> articles = articleService.getAllArticles();
    // response.setStatusCode(200);
    // response.setBody(objectMapper.writeValueAsString(articles));
    // }
    // }

    /**
     * GETリクエストを処理します。
     * 特定の記事IDが指定されている場合は単一の記事を、そうでない場合は全記事のリストを返します。
     */
    private void handleGetRequest(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response)
            throws Exception {
        logger.info("### input.path: {} ###", input.getPath());
        logger.info("### input.pathParameters: {} ###", input.getPathParameters());

        Map<String, String> pathParameters = input.getPathParameters();
        if (pathParameters != null && pathParameters.containsKey("proxy")) {
            String[] pathParts = pathParameters.get("proxy").split("/");
            logger.info("### pathParts: {} ###", Arrays.toString(pathParts));

            if (pathParts.length > 2 && "articles".equals(pathParts[1])) {
                // 特定の記事を取得
                String id = pathParts[2];
                logger.info("Fetching article with ID: {}", id);
                Article article = articleService.getArticleById(id);
                response.setStatusCode(200);
                response.setBody(objectMapper.writeValueAsString(article));
            } else {
                logger.info("Condition not met for single article fetch, fetching all articles");
                // 全記事を取得
                List<Article> articles = articleService.getAllArticles();
                response.setStatusCode(200);
                response.setBody(objectMapper.writeValueAsString(articles));
            }
        } else {
            logger.info("No path parameters, fetching all articles");
            // パスパラメータがない場合は全記事を取得
            List<Article> articles = articleService.getAllArticles();
            response.setStatusCode(200);
            response.setBody(objectMapper.writeValueAsString(articles));
        }
    }

    /**
     * POSTリクエストを処理します。
     * 新しい記事を作成します。
     */
    private void handlePostRequest(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response)
            throws Exception {
        Article newArticle = objectMapper.readValue(input.getBody(), Article.class);
        Article created = articleService.createArticle(newArticle);
        response.setStatusCode(201);
        response.setBody(objectMapper.writeValueAsString(created));
    }

    // private void handlePutRequest(APIGatewayProxyRequestEvent input,
    // APIGatewayProxyResponseEvent response)
    // throws Exception {
    // String id = input.getPath().split("/")[2];
    // Article updateArticle = objectMapper.readValue(input.getBody(),
    // Article.class);
    // Article updated = articleService.updateArticle(id, updateArticle);
    // response.setStatusCode(200);
    // response.setBody(objectMapper.writeValueAsString(updated));
    // }

    /**
     * PUTリクエストを処理します。
     * 既存の記事を更新します。
     */
    private void handlePutRequest(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response)
            throws Exception {
        logger.info("Handling PUT request");
        Map<String, String> pathParameters = input.getPathParameters();
        if (pathParameters != null && pathParameters.containsKey("proxy")) {
            String[] pathParts = pathParameters.get("proxy").split("/");
            logger.info("Path parts: {}", Arrays.toString(pathParts));
            if (pathParts.length > 2 && "articles".equals(pathParts[1])) {
                String id = pathParts[2];
                logger.info("Updating article with ID: {}", id);
                Article updateArticle = objectMapper.readValue(input.getBody(), Article.class);
                Article updated = articleService.updateArticle(id, updateArticle);
                response.setStatusCode(200);
                response.setBody(objectMapper.writeValueAsString(updated));
            } else {
                logger.warn("Invalid path for article update");
                response.setStatusCode(400);
                response.setBody("{\"error\": \"Invalid article ID\"}");
            }
        } else {
            logger.warn("No path parameters found for article update");
            response.setStatusCode(400);
            response.setBody("{\"error\": \"Missing article ID\"}");
        }
    }

    /**
     * DELETEリクエストを処理します。
     * 指定された記事を削除します。
     */
    private void handleDeleteRequest(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response) {
        String deleteId = input.getPath().split("/")[2];
        articleService.deleteArticle(deleteId);
        response.setStatusCode(204);
    }
}
