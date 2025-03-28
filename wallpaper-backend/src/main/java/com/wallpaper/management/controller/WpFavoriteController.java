package com.wallpaper.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wallpaper.management.common.Result;
import com.wallpaper.management.entity.WpWallpaper;
import com.wallpaper.management.service.WpFavoriteService;
import com.wallpaper.management.utils.ShiroUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 壁纸收藏控制器
 */
@Tag(name = "壁纸收藏")
@RestController
@RequestMapping("/wallpaper/favorite")
@RequiredArgsConstructor
public class WpFavoriteController {

    private final WpFavoriteService favoriteService;

    /**
     * 收藏壁纸
     *
     * @param params 参数
     * @return 结果
     */
    @Operation(summary = "收藏壁纸")
    @PostMapping
    @RequiresAuthentication
    public Result<Boolean> favorite(@RequestBody Map<String, Long> params) {
        Long wallpaperId = params.get("wallpaperId");
        if (wallpaperId == null) {
            return Result.error("壁纸ID不能为空");
        }
        
        Long userId = ShiroUtil.getCurrentUserId();
        boolean result = favoriteService.favorite(userId, wallpaperId);
        
        return Result.success(result, "收藏成功");
    }

    /**
     * 取消收藏
     *
     * @param wallpaperId 壁纸ID
     * @return 结果
     */
    @Operation(summary = "取消收藏")
    @DeleteMapping("/{wallpaperId}")
    @RequiresAuthentication
    public Result<Boolean> unfavorite(@Parameter(description = "壁纸ID", required = true) @PathVariable Long wallpaperId) {
        Long userId = ShiroUtil.getCurrentUserId();
        boolean result = favoriteService.unfavorite(userId, wallpaperId);
        
        return Result.success(result, "取消收藏成功");
    }

    /**
     * 获取收藏的壁纸列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "获取收藏的壁纸列表")
    @GetMapping
    @RequiresAuthentication
    public Result<IPage<WpWallpaper>> list(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") long size) {
        Long userId = ShiroUtil.getCurrentUserId();
        Page<WpWallpaper> page = new Page<>(current, size);
        IPage<WpWallpaper> result = favoriteService.getFavoriteWallpapers(page, userId);
        
        return Result.success(result);
    }

    /**
     * 检查壁纸是否已收藏
     *
     * @param wallpaperId 壁纸ID
     * @return 是否已收藏
     */
    @Operation(summary = "检查壁纸是否已收藏")
    @GetMapping("/check/{wallpaperId}")
    @RequiresAuthentication
    public Result<Boolean> checkFavorite(@Parameter(description = "壁纸ID", required = true) @PathVariable Long wallpaperId) {
        Long userId = ShiroUtil.getCurrentUserId();
        boolean result = favoriteService.isFavorite(userId, wallpaperId);
        
        return Result.success(result);
    }
} 