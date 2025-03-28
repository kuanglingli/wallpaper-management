//package com.wallpaper.management.config;
//
//import org.flywaydb.core.Flyway;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
///**
// * Flyway数据库版本控制配置
// */
////@Configuration
//public class FlywayConfig {
//
//    @Value("${spring.flyway.enabled:true}")
//    private boolean flywayEnabled;
//
//    @Value("${spring.flyway.baseline-on-migrate:true}")
//    private boolean baselineOnMigrate;
//
//    @Value("${spring.flyway.locations:classpath:db/migration}")
//    private String[] locations;
//
//    /**
//     * 初始化Flyway配置
//     */
////    @Bean
//    public Flyway flyway(DataSource dataSource) {
//        if (!flywayEnabled) {
//            return null;
//        }
//
//        Flyway flyway = Flyway.configure()
//                .dataSource(dataSource)
//                .baselineOnMigrate(baselineOnMigrate)
//                .locations(locations)
//                .load();
//
//        // 执行数据库迁移
//        flyway.migrate();
//
//        return flyway;
//    }
//}