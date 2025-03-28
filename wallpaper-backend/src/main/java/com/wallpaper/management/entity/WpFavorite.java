package com.wallpaper.management.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 壁纸收藏实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wp_favorite")
public class WpFavorite extends BaseEntity {
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 壁纸ID
     */
    @TableField("wallpaper_id")
    private Long wallpaperId;
} 