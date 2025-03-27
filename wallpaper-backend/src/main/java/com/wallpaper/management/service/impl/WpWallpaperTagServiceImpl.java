package com.wallpaper.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wallpaper.management.entity.WpWallpaperTag;
import com.wallpaper.management.mapper.WpWallpaperTagMapper;
import com.wallpaper.management.service.WpWallpaperTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 壁纸标签关联服务实现类
 */
@Slf4j
@Service
public class WpWallpaperTagServiceImpl extends ServiceImpl<WpWallpaperTagMapper, WpWallpaperTag> implements WpWallpaperTagService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWallpaperTags(Long wallpaperId, List<Long> tagIds) {
        if (wallpaperId == null || CollectionUtils.isEmpty(tagIds)) {
            log.warn("保存壁纸标签关联失败：wallpaperId或tagIds为空");
            return;
        }
        
        // 先删除原有关联
        this.deleteByWallpaperId(wallpaperId);
        
        // 批量保存新关联
        try {
            baseMapper.batchSave(wallpaperId, tagIds);
            log.info("保存壁纸标签关联成功，wallpaperId={}，tagIds={}", wallpaperId, tagIds);
        } catch (Exception e) {
            log.error("保存壁纸标签关联失败", e);
            throw e;
        }
    }

    @Override
    public List<Long> getTagIdsByWallpaperId(Long wallpaperId) {
        if (wallpaperId == null) {
            return Collections.emptyList();
        }
        
        LambdaQueryWrapper<WpWallpaperTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WpWallpaperTag::getWallpaperId, wallpaperId);
        
        List<WpWallpaperTag> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        
        return list.stream()
                  .map(WpWallpaperTag::getTagId)
                  .collect(Collectors.toList());
    }

    @Override
    public void deleteByWallpaperId(Long wallpaperId) {
        if (wallpaperId == null) {
            return;
        }
        try {
            baseMapper.deleteByWallpaperId(wallpaperId);
            log.info("根据壁纸ID删除标签关联成功，wallpaperId={}", wallpaperId);
        } catch (Exception e) {
            log.error("根据壁纸ID删除标签关联失败", e);
            throw e;
        }
    }

    @Override
    public void deleteByTagId(Long tagId) {
        if (tagId == null) {
            return;
        }
        try {
            baseMapper.deleteByTagId(tagId);
            log.info("根据标签ID删除壁纸关联成功，tagId={}", tagId);
        } catch (Exception e) {
            log.error("根据标签ID删除壁纸关联失败", e);
            throw e;
        }
    }

    @Override
    public List<Long> getWallpaperIdsByTagId(Long tagId) {
        if (tagId == null) {
            return Collections.emptyList();
        }
        
        LambdaQueryWrapper<WpWallpaperTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WpWallpaperTag::getTagId, tagId);
        
        List<WpWallpaperTag> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        
        return list.stream()
                  .map(WpWallpaperTag::getWallpaperId)
                  .collect(Collectors.toList());
    }
} 