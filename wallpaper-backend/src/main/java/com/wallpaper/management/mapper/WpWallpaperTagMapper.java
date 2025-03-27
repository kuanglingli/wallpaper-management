package com.wallpaper.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wallpaper.management.entity.WpWallpaperTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 壁纸标签关联Mapper接口
 */
@Mapper
public interface WpWallpaperTagMapper extends BaseMapper<WpWallpaperTag> {

    /**
     * 批量保存壁纸标签关联
     *
     * @param wallpaperId 壁纸ID
     * @param tagIds 标签ID列表
     * @return 影响的行数
     */
    int batchSave(@Param("wallpaperId") Long wallpaperId, @Param("tagIds") List<Long> tagIds);

    /**
     * 根据壁纸ID删除关联
     *
     * @param wallpaperId 壁纸ID
     * @return 影响的行数
     */
    int deleteByWallpaperId(@Param("wallpaperId") Long wallpaperId);

    /**
     * 根据标签ID删除关联
     *
     * @param tagId 标签ID
     * @return 影响的行数
     */
    int deleteByTagId(@Param("tagId") Long tagId);
} 