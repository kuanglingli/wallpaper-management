package com.wallpaper.management.controller;

import com.wallpaper.management.common.Result;
import com.wallpaper.management.entity.WpTag;
import com.wallpaper.management.service.WpTagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 壁纸控制器
 */
@Tag(name = "标签管理")
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
@Validated
public class WpTagController {

    private final WpTagService tagService;

    /**
     * 获取所有标签（不分页）
     *
     * @return 标签列表
     */
    @GetMapping("/all")
    public Result<List<WpTag>> getAllTags() {
        List<WpTag> list = tagService.getAllTags();
        return Result.success(list);
    }

    @GetMapping("/hot")
    public Result<List<WpTag>> getHotTags() {
        List<WpTag> list = tagService.getHotTags();
        return Result.success(list);
    }

}