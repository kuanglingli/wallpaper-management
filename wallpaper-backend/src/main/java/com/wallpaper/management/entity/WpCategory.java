package com.wallpaper.management.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 壁纸分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wp_category")
public class WpCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类ID（0表示顶级分类）
     */
    private Long parentId;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用，1-正常）
     */
    private Integer status;
    
    /**
     * 子分类列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<WpCategory> children;
} 