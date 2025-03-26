package com.wallpaper.management;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 壁纸管理系统启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.wallpaper.management.mapper")
public class WallpaperManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WallpaperManagementApplication.class, args);
    }
} 