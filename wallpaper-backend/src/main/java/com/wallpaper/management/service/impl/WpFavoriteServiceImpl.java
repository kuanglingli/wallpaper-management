package com.wallpaper.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wallpaper.management.entity.WpFavorite;
import com.wallpaper.management.entity.WpWallpaper;
import com.wallpaper.management.mapper.WpFavoriteMapper;
import com.wallpaper.management.service.WpFavoriteService;
import com.wallpaper.management.service.WpWallpaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 壁纸收藏Service实现
 */
@Service
@RequiredArgsConstructor
public class WpFavoriteServiceImpl extends ServiceImpl<WpFavoriteMapper, WpFavorite> implements WpFavoriteService {

    private final WpWallpaperService wallpaperService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favorite(Long userId, Long wallpaperId) {
        // 检查壁纸是否存在
        WpWallpaper wallpaper = wallpaperService.getById(wallpaperId);
        if (wallpaper == null) {
            return false;
        }
        
        // 检查是否已收藏
        if (isFavorite(userId, wallpaperId)) {
            return true;
        }
        
        // 创建收藏记录
        WpFavorite favorite = new WpFavorite();
        favorite.setUserId(userId);
        favorite.setWallpaperId(wallpaperId);
        
        // 增加壁纸的收藏数
        wallpaper.setFavoriteCount(wallpaper.getFavoriteCount() + 1);
        wallpaperService.updateById(wallpaper);
        
        return save(favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfavorite(Long userId, Long wallpaperId) {
        // 检查壁纸是否存在
        WpWallpaper wallpaper = wallpaperService.getById(wallpaperId);
        if (wallpaper == null) {
            return false;
        }
        
        // 构建条件
        LambdaQueryWrapper<WpFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WpFavorite::getUserId, userId)
                .eq(WpFavorite::getWallpaperId, wallpaperId);
        
        // 删除收藏记录
        boolean result = remove(wrapper);
        
        // 如果成功删除，减少壁纸的收藏数
        if (result && wallpaper.getFavoriteCount() > 0) {
            wallpaper.setFavoriteCount(wallpaper.getFavoriteCount() - 1);
            wallpaperService.updateById(wallpaper);
        }
        
        return result;
    }

    @Override
    public IPage<WpWallpaper> getFavoriteWallpapers(Page<WpWallpaper> page, Long userId) {
        return baseMapper.selectFavoriteWallpapers(page, userId);
    }

    @Override
    public boolean isFavorite(Long userId, Long wallpaperId) {
        return baseMapper.checkFavorite(userId, wallpaperId) > 0;
    }
} 