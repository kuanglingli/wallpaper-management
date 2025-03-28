//package com.wallpaper.management.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class MvcConfig implements WebMvcConfigurer {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // 添加额外的静态资源路径映射
//        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/my-static-files/");
//        // 保留默认的静态资源映射
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/", "classpath:/public/", "classpath:/resources/", "classpath:/META-INF/resources/");
//    }
//}
