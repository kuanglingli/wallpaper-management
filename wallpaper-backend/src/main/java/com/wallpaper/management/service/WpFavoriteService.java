package com.wallpaper.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wallpaper.management.entity.WpFavorite;
import com.wallpaper.management.entity.WpWallpaper;

/**
 * 壁纸收藏Service
 */
public interface WpFavoriteService extends IService<WpFavorite> {
    
    /**
     * 收藏壁纸
     *
     * @param userId 用户ID
     * @param wallpaperId 壁纸ID
     * @return 操作结果
     */
    boolean favorite(Long userId, Long wallpaperId);
    
    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param wallpaperId 壁纸ID
     * @return 操作结果
     */
    boolean unfavorite(Long userId, Long wallpaperId);
    
    /**
     * 分页查询用户收藏的壁纸
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<WpWallpaper> getFavoriteWallpapers(Page<WpWallpaper> page, Long userId);
    
    /**
     * 检查壁纸是否已被收藏
     *
     * @param userId 用户ID
     * @param wallpaperId 壁纸ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long wallpaperId);
} 