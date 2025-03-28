package com.wallpaper.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wallpaper.management.entity.WpTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 标签Mapper接口
 */
@Mapper
public interface WpTagMapper extends BaseMapper<WpTag> {
    
    /**
     * 增加标签关联的壁纸数量
     *
     * @param tagId     标签ID
     * @param increment 增量
     * @return 影响的行数
     */
    int incrementWallpaperCount(@Param("tagId") Long tagId, @Param("increment") int increment);
} 