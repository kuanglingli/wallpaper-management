package com.wallpaper.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wallpaper.management.entity.WpWallpaper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 壁纸服务接口
 */
public interface WpWallpaperService extends IService<WpWallpaper> {

    /**
     * 上传壁纸
     *
     * @param file       壁纸文件
     * @param wallpaper  壁纸信息
     * @param tagIds     标签ID列表
     * @param uploadUserId 上传用户ID
     * @return 壁纸信息
     */
    WpWallpaper uploadWallpaper(MultipartFile file, WpWallpaper wallpaper, List<Long> tagIds, Long uploadUserId);

    /**
     * 分页查询壁纸列表
     *
     * @param page       页码
     * @param pageSize   页大小
     * @param categoryId 分类ID
     * @param tagId      标签ID
     * @param keyword    关键词
     * @return 壁纸列表
     */
    IPage<WpWallpaper> pageWallpapers(Integer page, Integer pageSize, Long categoryId, Long tagId, String keyword);

    /**
     * 获取推荐壁纸
     *
     * @param limit 数量限制
     * @return 壁纸列表
     */
    List<WpWallpaper> getRecommendWallpapers(Integer limit);

    /**
     * 获取最新壁纸
     *
     * @param limit 数量限制
     * @return 壁纸列表
     */
    List<WpWallpaper> getLatestWallpapers(Integer limit);

    /**
     * 获取热门壁纸
     *
     * @param limit 数量限制
     * @return 壁纸列表
     */
    List<WpWallpaper> getHotWallpapers(Integer limit);

    /**
     * 增加下载次数
     *
     * @param id 壁纸ID
     * @return 是否成功
     */
    boolean incrementDownloads(Long id);

    /**
     * 增加浏览次数
     *
     * @param id 壁纸ID
     * @return 是否成功
     */
    boolean incrementViews(Long id);

    /**
     * 审核壁纸
     *
     * @param id     壁纸ID
     * @param status 状态（0-未审核，1-已审核）
     * @return 是否成功
     */
    boolean auditWallpaper(Long id, Integer status);
} 