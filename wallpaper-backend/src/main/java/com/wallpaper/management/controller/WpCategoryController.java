package com.wallpaper.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wallpaper.management.common.Result;
import com.wallpaper.management.entity.WpCategory;
import com.wallpaper.management.service.WpCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 壁纸分类控制器
 */
@Tag(name = "壁纸分类管理")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Validated
public class WpCategoryController {

    private final WpCategoryService categoryService;

    /**
     * 获取分类列表（分页）
     *
     * @param page     页码
     * @param pageSize 页大小
     * @param keyword  关键词
     * @return 结果
     */
    @Operation(summary = "获取分类列表（分页）")
    @GetMapping("/page")
    public Result<Page<WpCategory>> page(
            @Parameter(description = "页码", required = true) @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "页大小", required = true) @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        Page<WpCategory> pageResult = categoryService.page(new Page<>(page, pageSize),
                new LambdaQueryWrapper<WpCategory>()
                        .like(keyword != null, WpCategory::getCategoryName, keyword)
                        .orderByAsc(WpCategory::getSort));
        return Result.success(pageResult);
    }

    /**
     * 获取分类树
     *
     * @return 结果
     */
    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<WpCategory>> tree() {
        List<WpCategory> categoryTree = categoryService.getCategoryTree();
        return Result.success(categoryTree);
    }

    /**
     * 获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 结果
     */
    @Operation(summary = "获取子分类列表")
    @GetMapping("/children")
    public Result<List<WpCategory>> children(
            @Parameter(description = "父分类ID", required = true) @RequestParam(defaultValue = "0") Long parentId) {
        List<WpCategory> children = categoryService.getChildrenByParentId(parentId);
        return Result.success(children);
    }

    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 结果
     */
    @Operation(summary = "添加分类")
    @PostMapping
    @RequiresPermissions("wallpaper:manage")
    public Result<Boolean> add(@Parameter(description = "分类信息", required = true) @RequestBody @Valid WpCategory category) {
        boolean result = categoryService.save(category);
        return Result.success(result, "添加成功");
    }

    /**
     * 修改分类
     *
     * @param category 分类信息
     * @return 结果
     */
    @Operation(summary = "修改分类")
    @PutMapping
    @RequiresPermissions("wallpaper:manage")
    public Result<Boolean> update(@Parameter(description = "分类信息", required = true) @RequestBody @Valid WpCategory category) {
        boolean result = categoryService.updateById(category);
        return Result.success(result, "修改成功");
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 结果
     */
    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @RequiresPermissions("wallpaper:manage")
    public Result<Boolean> delete(@Parameter(description = "分类ID", required = true) @PathVariable Long id) {
        boolean result = categoryService.removeById(id);
        return Result.success(result, "删除成功");
    }

    /**
     * 更新分类状态
     *
     * @param id     分类ID
     * @param status 状态（0-禁用，1-正常）
     * @return 结果
     */
    @Operation(summary = "更新分类状态")
    @PutMapping("/status")
    @RequiresPermissions("wallpaper:manage")
    public Result<Boolean> updateStatus(
            @Parameter(description = "分类ID", required = true) @RequestParam Long id,
            @Parameter(description = "状态（0-禁用，1-正常）", required = true) @RequestParam Integer status) {
        boolean result = categoryService.updateStatus(id, status);
        return Result.success(result, "状态更新成功");
    }
} 