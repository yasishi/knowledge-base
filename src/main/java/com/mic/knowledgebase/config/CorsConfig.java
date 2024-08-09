package com.mic.knowledgebase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

// Spring Bootの設定クラスであることを示す
@Configuration
public class CorsConfig {

    // CorsFilterをSpringのコンテナに登録するためのメソッド
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // クロスオリジンリクエストでクレデンシャル（クッキーなど）の使用を許可
        config.setAllowCredentials(true);

        // 特定のオリジン（ここではローカルのReactアプリケーション）からのリクエストを許可
        config.addAllowedOrigin("http://localhost:3000");

        // すべてのヘッダーを許可
        config.addAllowedHeader("*");

        // すべてのHTTPメソッド（GET、POST、PUTなど）を許可
        config.addAllowedMethod("*");

        // すべてのエンドポイントにこのCORS設定を適用
        source.registerCorsConfiguration("/**", config);

        // 設定を適用したCorsFilterを返す
        return new CorsFilter(source);
    }
}