package com.wallpaper.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wp_tag")
public class WpTag extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String name;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 关联壁纸数量
     */
    private Integer wallpaperCount;
} 