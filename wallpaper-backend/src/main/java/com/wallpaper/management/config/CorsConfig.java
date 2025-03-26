package com.wallpaper.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    /**
     * 配置跨域过滤器
     *
     * @return 跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        // 跨域配置
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 使用allowedOriginPatterns替代allowedOrigin
        corsConfiguration.addAllowedOriginPattern("*");
        // 允许所有请求头
        corsConfiguration.addAllowedHeader("*");
        // 允许所有方法
        corsConfiguration.addAllowedMethod("*");
        // 允许Cookie
        corsConfiguration.setAllowCredentials(true);
        // 允许发送凭证
        corsConfiguration.setMaxAge(3600L);
        
        // 添加映射路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsFilter(source);
    }
} 