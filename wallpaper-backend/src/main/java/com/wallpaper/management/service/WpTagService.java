package com.wallpaper.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wallpaper.management.entity.WpTag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface WpTagService extends IService<WpTag> {

    /**
     * 增加标签关联的壁纸数量
     *
     * @param tagId 标签ID
     * @param increment 增量
     * @return 是否成功
     */
    boolean incrementWallpaperCount(Long tagId, int increment);

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<WpTag> getAllTags();

    List<WpTag> getHotTags();
} 