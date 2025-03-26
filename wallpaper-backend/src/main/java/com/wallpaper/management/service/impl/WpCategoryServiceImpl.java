package com.wallpaper.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wallpaper.management.entity.WpCategory;
import com.wallpaper.management.mapper.WpCategoryMapper;
import com.wallpaper.management.service.WpCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 壁纸分类服务实现类
 */
@Service
@RequiredArgsConstructor
public class WpCategoryServiceImpl extends ServiceImpl<WpCategoryMapper, WpCategory> implements WpCategoryService {

    /**
     * 获取所有分类（树形结构）
     *
     * @return 分类树形列表
     */
    @Override
    public List<WpCategory> getCategoryTree() {
        // 获取所有分类
        List<WpCategory> allCategories = list(new LambdaQueryWrapper<WpCategory>()
                .eq(WpCategory::getStatus, 1)
                .orderByAsc(WpCategory::getSort));

        // 按照父ID分组
        Map<Long, List<WpCategory>> categoryMap = allCategories.stream()
                .collect(Collectors.groupingBy(WpCategory::getParentId));

        // 获取顶级分类
        List<WpCategory> rootCategories = categoryMap.get(0L);
        if (rootCategories == null) {
            return new ArrayList<>();
        }

        // 递归设置子分类
        rootCategories.forEach(category -> setChildren(category, categoryMap));

        return rootCategories;
    }

    /**
     * 获取指定父级分类下的子分类列表
     *
     * @param parentId 父级分类ID
     * @return 子分类列表
     */
    @Override
    public List<WpCategory> getChildrenByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<WpCategory>()
                .eq(WpCategory::getParentId, parentId)
                .eq(WpCategory::getStatus, 1)
                .orderByAsc(WpCategory::getSort));
    }

    /**
     * 获取指定父级分类下的所有子分类ID列表（包括子分类的子分类）
     *
     * @param parentId 父级分类ID
     * @return 子分类ID列表
     */
    @Override
    public List<Long> getAllChildrenIds(Long parentId) {
        List<Long> result = new ArrayList<>();
        
        // 获取直接子分类
        List<WpCategory> children = getChildrenByParentId(parentId);
        
        // 递归获取子分类的子分类
        for (WpCategory child : children) {
            result.add(child.getId());
            result.addAll(getAllChildrenIds(child.getId()));
        }
        
        return result;
    }

    /**
     * 更新分类状态
     *
     * @param id     分类ID
     * @param status 状态（0-禁用，1-正常）
     * @return 是否成功
     */
    @Override
    public boolean updateStatus(Long id, Integer status) {
        WpCategory category = new WpCategory();
        category.setId(id);
        category.setStatus(status);
        return updateById(category);
    }

    /**
     * 递归设置子分类
     *
     * @param parent      父分类
     * @param categoryMap 分类Map
     */
    private void setChildren(WpCategory parent, Map<Long, List<WpCategory>> categoryMap) {
        List<WpCategory> children = categoryMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            children.forEach(child -> setChildren(child, categoryMap));
            parent.setChildren(children);
        }
    }
} 