package com.wallpaper.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wallpaper.management.entity.WpWallpaperTag;

import java.util.List;

/**
 * 壁纸标签关联服务接口
 */
public interface WpWallpaperTagService extends IService<WpWallpaperTag> {

    /**
     * 保存壁纸标签关联
     *
     * @param wallpaperId 壁纸ID
     * @param tagIds 标签ID列表
     */
    void saveWallpaperTags(Long wallpaperId, List<Long> tagIds);

    /**
     * 根据壁纸ID获取标签ID列表
     *
     * @param wallpaperId 壁纸ID
     * @return 标签ID列表
     */
    List<Long> getTagIdsByWallpaperId(Long wallpaperId);

    /**
     * 根据标签ID获取壁纸ID列表
     *
     * @param tagId 标签ID
     * @return 壁纸ID列表
     */
    List<Long> getWallpaperIdsByTagId(Long tagId);

    /**
     * 根据壁纸ID删除关联
     *
     * @param wallpaperId 壁纸ID
     */
    void deleteByWallpaperId(Long wallpaperId);

    /**
     * 根据标签ID删除关联
     *
     * @param tagId 标签ID
     */
    void deleteByTagId(Long tagId);
} 