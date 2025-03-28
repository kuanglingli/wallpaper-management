package com.wallpaper.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wallpaper.management.entity.WpFavorite;
import com.wallpaper.management.entity.WpWallpaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 壁纸收藏Mapper
 */
@Mapper
public interface WpFavoriteMapper extends BaseMapper<WpFavorite> {
    
    /**
     * 分页查询用户收藏的壁纸
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    @Select("SELECT w.* FROM wp_wallpaper w " +
            "JOIN wp_favorite f ON w.id = f.wallpaper_id " +
            "WHERE f.user_id = #{userId} " +
            "ORDER BY f.create_time DESC")
    IPage<WpWallpaper> selectFavoriteWallpapers(Page<WpWallpaper> page, @Param("userId") Long userId);
    
    /**
     * 检查壁纸是否被用户收藏
     *
     * @param userId 用户ID
     * @param wallpaperId 壁纸ID
     * @return 收藏数量
     */
    @Select("SELECT COUNT(*) FROM wp_favorite " +
            "WHERE user_id = #{userId} AND wallpaper_id = #{wallpaperId}")
    int checkFavorite(@Param("userId") Long userId, @Param("wallpaperId") Long wallpaperId);
} 