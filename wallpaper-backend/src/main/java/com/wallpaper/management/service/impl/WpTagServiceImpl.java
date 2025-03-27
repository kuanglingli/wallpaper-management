package com.wallpaper.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wallpaper.management.entity.WpTag;
import com.wallpaper.management.mapper.WpTagMapper;
import com.wallpaper.management.service.WpTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
public class WpTagServiceImpl extends ServiceImpl<WpTagMapper, WpTag> implements WpTagService {

    @Override
    public boolean incrementWallpaperCount(Long tagId, int increment) {
        if (tagId == null) {
            return false;
        }
        
        return lambdaUpdate()
                .eq(WpTag::getId, tagId)
                .setSql("wallpaper_count = wallpaper_count + " + increment)
                .update();
    }
} 