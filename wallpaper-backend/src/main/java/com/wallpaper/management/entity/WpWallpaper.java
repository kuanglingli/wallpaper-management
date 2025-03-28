package com.wallpaper.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 壁纸实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wp_wallpaper")
public class WpWallpaper extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 壁纸标题
     */
    private String title;

    /**
     * 壁纸描述
     */
    private String description;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 缩略图路径
     */
    private String thumbnailPath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 宽度（像素）
     */
    private Integer width;

    /**
     * 高度（像素）
     */
    private Integer height;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 下载次数
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 收藏次数
     */
    @TableField("favorite_count")
    private Integer favoriteCount;

    /**
     * 浏览次数
     */
    private Integer views;

    /**
     * 点赞次数
     */
    private Integer likes;

    /**
     * 状态（0-未审核，1-已审核）
     */
    private Integer status;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;
    
    /**
     * 图片URL (不映射数据库字段)
     */
    @TableField(exist = false)
    private String imageUrl;
    
    /**
     * 缩略图URL (不映射数据库字段)
     */
    @TableField(exist = false)
    private String thumbnailUrl;
    
    /**
     * 分类名称 (不映射数据库字段)
     */
    @TableField(exist = false)
    private String categoryName;
    
    /**
     * 标签列表 (不映射数据库字段)
     */
    @TableField(exist = false)
    private List<WpTag> tags;

    @TableField(exist = false)
    private List<Long> tagIds;
} 