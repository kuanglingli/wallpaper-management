package com.wallpaper.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wallpaper.management.common.Result;
import com.wallpaper.management.entity.WpTag;
import com.wallpaper.management.entity.WpWallpaper;
import com.wallpaper.management.service.WpCategoryService;
import com.wallpaper.management.service.WpTagService;
import com.wallpaper.management.service.WpWallpaperService;
import com.wallpaper.management.service.WpWallpaperTagService;
import com.wallpaper.management.util.ImageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 壁纸控制器
 */
@Tag(name = "壁纸管理")
@RestController
@RequestMapping("/wallpaper")
@RequiredArgsConstructor
@Validated
public class WpWallpaperController {

    private final WpWallpaperService wallpaperService;
    private final WpCategoryService categoryService;
    private final WpTagService tagService;
    private final WpWallpaperTagService wallpaperTagService;
    
    @Value("${file.upload.base-url}")
    private String baseUrl;

    /**
     * 分页查询壁纸列表
     *
     * @param page       页码
     * @param pageSize   页大小
     * @param categoryId 分类ID
     * @param tagId      标签ID
     * @param keyword    关键词
     * @return 结果
     */
    @Operation(summary = "分页查询壁纸列表")
    @GetMapping("/page")
    public Result<IPage<WpWallpaper>> page(
            @Parameter(description = "页码", required = true) @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "页大小", required = true) @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "标签ID") @RequestParam(required = false) Long tagId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        IPage<WpWallpaper> pageResult = wallpaperService.pageWallpapers(page, pageSize, categoryId, tagId, keyword);
        
        // 填充附加信息
        enhanceWallpaperList(pageResult.getRecords());
        
        return Result.success(pageResult);
    }

    /**
     * 获取壁纸详情
     *
     * @param id 壁纸ID
     * @return 结果
     */
    @Operation(summary = "获取壁纸详情")
    @GetMapping("/{id}")
    public Result<WpWallpaper> getById(@Parameter(description = "壁纸ID", required = true) @PathVariable Long id) {
        // 增加浏览次数
        wallpaperService.incrementViews(id);
        WpWallpaper wallpaper = wallpaperService.getById(id);
        
        if (wallpaper != null) {
            enhanceWallpaper(wallpaper);
        }
        
        return Result.success(wallpaper);
    }

    /**
     * 上传壁纸
     *
     * @param file       壁纸文件
     * @param wallpaper  壁纸信息
     * @param tagIds     标签ID列表
     * @param uploadUserId 上传用户ID
     * @return 结果
     */
    @Operation(summary = "上传壁纸")
    @PostMapping("/upload")
    @RequiresPermissions("wallpaper:upload")
    public Result<WpWallpaper> upload(
            @Parameter(description = "壁纸文件", required = true) @RequestParam MultipartFile file,
            @Parameter(description = "壁纸信息", required = true) @Valid WpWallpaper wallpaper,
            @Parameter(description = "标签ID列表") @RequestParam(required = false) List<Long> tagIds,
            @Parameter(description = "上传用户ID", required = true) @RequestParam Long uploadUserId) {
        WpWallpaper uploadedWallpaper = wallpaperService.uploadWallpaper(file, wallpaper, tagIds, uploadUserId);
        return Result.success(uploadedWallpaper, "上传成功");
    }

    /**
     * 删除壁纸
     *
     * @param id 壁纸ID
     * @return 结果
     */
    @Operation(summary = "删除壁纸")
    @DeleteMapping("/{id}")
    @RequiresPermissions("wallpaper:edit")
    public Result<Boolean> delete(@Parameter(description = "壁纸ID", required = true) @PathVariable Long id) {
        boolean result = wallpaperService.removeById(id);
        return Result.success(result, "删除成功");
    }

    /**
     * 修改壁纸信息
     *
     * @param wallpaper 壁纸信息
     * @return 结果
     */
    @Operation(summary = "修改壁纸信息")
    @PutMapping
    @RequiresPermissions("wallpaper:edit")
    public Result<Boolean> update(@Parameter(description = "壁纸信息", required = true) @RequestBody @Valid WpWallpaper wallpaper) {
        boolean result = wallpaperService.updateById(wallpaper);
        return Result.success(result, "修改成功");
    }

    /**
     * 审核壁纸
     *
     * @param id     壁纸ID
     * @param status 状态（0-未审核，1-已审核）
     * @return 结果
     */
    @Operation(summary = "审核壁纸")
    @PutMapping("/audit")
    @RequiresPermissions("wallpaper:audit")
    public Result<Boolean> audit(
            @Parameter(description = "壁纸ID", required = true) @RequestParam Long id,
            @Parameter(description = "状态（0-未审核，1-已审核）", required = true) @RequestParam Integer status) {
        boolean result = wallpaperService.auditWallpaper(id, status);
        return Result.success(result, "审核成功");
    }

    /**
     * 下载壁纸
     *
     * @param id     壁纸ID
     * @param userId 用户ID（可选）
     * @return 结果
     */
    @Operation(summary = "下载壁纸")
    @GetMapping("/download/{id}")
    @RequiresPermissions("wallpaper:download")
    public Result<String> download(
            @Parameter(description = "壁纸ID", required = true) @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {
        // 增加下载次数
        wallpaperService.incrementDownloads(id);
        
        // 获取壁纸信息
        WpWallpaper wallpaper = wallpaperService.getById(id);
        if (wallpaper == null) {
            return Result.error("壁纸不存在");
        }
        
        // 返回下载路径
        return Result.success(wallpaper.getFilePath(), "获取下载路径成功");
    }

    /**
     * 获取推荐壁纸
     *
     * @param limit 数量限制
     * @return 结果
     */
    @Operation(summary = "获取推荐壁纸")
    @GetMapping("/recommend")
    public Result<List<WpWallpaper>> recommend(
            @Parameter(description = "数量限制", required = true) @RequestParam(defaultValue = "10") Integer limit) {
        List<WpWallpaper> recommendWallpapers = wallpaperService.getRecommendWallpapers(limit);
        return Result.success(recommendWallpapers);
    }

    /**
     * 获取最新壁纸
     *
     * @param limit 数量限制
     * @return 结果
     */
    @Operation(summary = "获取最新壁纸")
    @GetMapping("/latest")
    public Result<List<WpWallpaper>> latest(
            @Parameter(description = "数量限制", required = true) @RequestParam(defaultValue = "10") Integer limit) {
        List<WpWallpaper> latestWallpapers = wallpaperService.getLatestWallpapers(limit);
        return Result.success(latestWallpapers);
    }

    /**
     * 获取热门壁纸
     *
     * @param limit 数量限制
     * @return 结果
     */
    @Operation(summary = "获取热门壁纸")
    @GetMapping("/hot")
    public Result<List<WpWallpaper>> hot(
            @Parameter(description = "数量限制", required = true) @RequestParam(defaultValue = "10") Integer limit) {
        List<WpWallpaper> hotWallpapers = wallpaperService.getHotWallpapers(limit);
        return Result.success(hotWallpapers);
    }

    /**
     * 增强壁纸列表，添加URL和关联信息
     *
     * @param wallpapers 壁纸列表
     */
    private void enhanceWallpaperList(List<WpWallpaper> wallpapers) {
        if (wallpapers == null || wallpapers.isEmpty()) {
            return;
        }

        // 获取所有壁纸ID
        List<Long> wallpaperIds = wallpapers.stream()
                .map(WpWallpaper::getId)
                .collect(Collectors.toList());
        
        // 批量获取分类信息
        List<Long> categoryIds = wallpapers.stream()
                .map(WpWallpaper::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> categoryNameMap = categoryService.getCategoryNameMap(categoryIds);
        
        // 批量获取标签关联信息
        Map<Long, List<Long>> wallpaperTagMap = new java.util.HashMap<>();
        for (Long wallpaperId : wallpaperIds) {
            List<Long> tagIdsForWallpaper = wallpaperTagService.getTagIdsByWallpaperId(wallpaperId);
            wallpaperTagMap.put(wallpaperId, tagIdsForWallpaper);
        }
        
        // 获取所有相关标签
        List<Long> allTagIds = wallpaperTagMap.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, WpTag> tagMap = tagService.listByIds(allTagIds).stream()
                .collect(Collectors.toMap(WpTag::getId, Function.identity()));
        
        // 填充壁纸信息
        for (WpWallpaper wallpaper : wallpapers) {
            // 设置URL
            wallpaper.setImageUrl(ImageUtils.buildFullUrl(wallpaper.getFilePath(), baseUrl));
            wallpaper.setThumbnailUrl(ImageUtils.buildFullUrl(wallpaper.getThumbnailPath(), baseUrl));
            
            // 设置分类名称
            if (categoryNameMap.containsKey(wallpaper.getCategoryId())) {
                wallpaper.setCategoryName(categoryNameMap.get(wallpaper.getCategoryId()));
            }
            
            // 设置标签列表
            List<Long> tagIds = wallpaperTagMap.getOrDefault(wallpaper.getId(), new ArrayList<>());
            List<WpTag> tags = tagIds.stream()
                    .map(tagMap::get)
                    .filter(tag -> tag != null)
                    .collect(Collectors.toList());
            wallpaper.setTags(tags);
        }
    }
    
    /**
     * 增强单个壁纸，添加URL和关联信息
     *
     * @param wallpaper 壁纸
     */
    private void enhanceWallpaper(WpWallpaper wallpaper) {
        if (wallpaper == null) {
            return;
        }
        
        List<WpWallpaper> list = new ArrayList<>();
        list.add(wallpaper);
        enhanceWallpaperList(list);
    }
} 