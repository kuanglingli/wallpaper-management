package com.wallpaper.management.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Shiro工具类
 */
@Slf4j
public class ShiroUtil {

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        try {
            Subject subject = SecurityUtils.getSubject();
            String token = (String) subject.getPrincipal();
            if (token == null) {
                log.warn("获取用户ID失败：未登录（token为null）");
                return null;
            }
            
            Long userId = JwtUtil.getUserId(token);
            log.debug("获取用户ID: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("获取用户ID异常", e);
            return null;
        }
    }
    
    /**
     * 获取当前登录用户ID（非空，如果为空会抛出异常）
     *
     * @return 用户ID
     * @throws IllegalStateException 如果用户未登录或获取ID失败
     */
    public static Long getCurrentUserId() {
        Long userId = getUserId();
        if (userId == null) {
            log.error("获取当前用户ID失败：用户未登录或ID为空");
            throw new IllegalStateException("用户未登录或获取ID失败");
        }
        return userId;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        try {
            Subject subject = SecurityUtils.getSubject();
            String token = (String) subject.getPrincipal();
            if (token == null) {
                log.warn("获取用户名失败：未登录（token为null）");
                return null;
            }
            
            String username = JwtUtil.getUsername(token);
            log.debug("获取用户名: {}", username);
            return username;
        } catch (Exception e) {
            log.error("获取用户名异常", e);
            return null;
        }
    }

    /**
     * 是否已登录
     *
     * @return 是否已登录
     */
    public static boolean isLogin() {
        try {
            Subject subject = SecurityUtils.getSubject();
            return subject.getPrincipal() != null;
        } catch (Exception e) {
            log.error("检查是否登录异常", e);
            return false;
        }
    }

    /**
     * 登出
     */
    public static void logout() {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                subject.logout();
                log.info("用户登出成功");
            }
        } catch (Exception e) {
            log.error("用户登出异常", e);
        }
    }
} 