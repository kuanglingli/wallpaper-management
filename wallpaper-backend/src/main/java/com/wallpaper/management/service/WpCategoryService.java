package com.wallpaper.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wallpaper.management.entity.WpCategory;

import java.util.List;

/**
 * 壁纸分类服务接口
 */
public interface WpCategoryService extends IService<WpCategory> {

    /**
     * 获取所有分类（树形结构）
     *
     * @return 分类树形列表
     */
    List<WpCategory> getCategoryTree();

    /**
     * 获取指定父级分类下的子分类列表
     *
     * @param parentId 父级分类ID
     * @return 子分类列表
     */
    List<WpCategory> getChildrenByParentId(Long parentId);

    /**
     * 获取指定父级分类下的所有子分类ID列表（包括子分类的子分类）
     *
     * @param parentId 父级分类ID
     * @return 子分类ID列表
     */
    List<Long> getAllChildrenIds(Long parentId);

    /**
     * 更新分类状态
     *
     * @param id     分类ID
     * @param status 状态（0-禁用，1-正常）
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
} 