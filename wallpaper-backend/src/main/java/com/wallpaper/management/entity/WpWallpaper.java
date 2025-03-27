package com.wallpaper.management.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private Integer downloads;

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
} 