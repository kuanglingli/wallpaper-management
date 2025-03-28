package com.wallpaper.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wallpaper.management.entity.WpTag;
import com.wallpaper.management.mapper.WpTagMapper;
import com.wallpaper.management.service.WpTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
public class WpTagServiceImpl extends ServiceImpl<WpTagMapper, WpTag> implements WpTagService {

    @Autowired
    private WpTagMapper tagMapper;

    @Override
    public boolean incrementWallpaperCount(Long tagId, int increment) {
        log.info("增加标签关联的壁纸数量，标签ID：{}，增量：{}", tagId, increment);
        
        if (tagId == null) {
            log.warn("标签ID为空，无法增加关联壁纸数量");
            return false;
        }
        
        try {
            // 调用Mapper的方法更新壁纸数量
            int rows = tagMapper.incrementWallpaperCount(tagId, increment);
            return rows > 0;
        } catch (Exception e) {
            log.error("增加标签关联的壁纸数量失败", e);
            return false;
        }
    }
} 