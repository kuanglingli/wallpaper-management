package com.wallpaper.management.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wallpaper.management.common.ResultCode;
import com.wallpaper.management.entity.WpWallpaper;
import com.wallpaper.management.exception.BusinessException;
import com.wallpaper.management.mapper.WpWallpaperMapper;
import com.wallpaper.management.service.WpCategoryService;
import com.wallpaper.management.service.WpWallpaperService;
import com.wallpaper.management.service.WpWallpaperTagService;
import com.wallpaper.management.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 壁纸服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WpWallpaperServiceImpl extends ServiceImpl<WpWallpaperMapper, WpWallpaper> implements WpWallpaperService {

    private final WpCategoryService categoryService;
    private final WpWallpaperTagService wallpaperTagService;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    @Value("${file.upload.max-size}")
    private long maxSize;
    
    @Value("${file.upload.base-url}")
    private String baseUrl;
    
    @Value("${file.upload.thumbnail.width}")
    private int thumbnailWidth;
    
    @Value("${file.upload.thumbnail.height}")
    private int thumbnailHeight;

    /**
     * 上传壁纸
     *
     * @param file         壁纸文件
     * @param wallpaper    壁纸信息
     * @param tagIds       标签ID列表
     * @param uploadUserId 上传用户ID
     * @return 壁纸信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WpWallpaper uploadWallpaper(MultipartFile file, WpWallpaper wallpaper, List<Long> tagIds, Long uploadUserId) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException(ResultCode.FILE_SIZE_ERROR);
        }

        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "文件名不能为空");
        }

        // 获取文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        if (!isAllowedFileType(suffix)) {
            throw new BusinessException(ResultCode.FILE_TYPE_ERROR);
        }

        try {
            // 生成文件保存路径
            String relativePath = generateRelativePath(suffix);
            String absolutePath = Paths.get(uploadPath, relativePath).toString();

            // 创建目录
            File directory = new File(FileUtil.getParent(absolutePath, 1));
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 保存文件
            file.transferTo(new File(absolutePath));

            // 读取图片信息
            BufferedImage image = ImageIO.read(new File(absolutePath));
            int width = image.getWidth();
            int height = image.getHeight();
            long fileSize = file.getSize();

            // 生成缩略图
            String thumbnailPath = ImageUtils.generateThumbnail(relativePath, uploadPath, thumbnailWidth, thumbnailHeight);
            if (StrUtil.isBlank(thumbnailPath)) {
                log.warn("缩略图生成失败，使用原图路径");
                thumbnailPath = relativePath;
            }

            // 设置壁纸信息
            wallpaper.setFilePath(relativePath);
            wallpaper.setThumbnailPath(thumbnailPath);
            wallpaper.setFileSize(fileSize);
            wallpaper.setWidth(width);
            wallpaper.setHeight(height);
            wallpaper.setFileType(suffix);
            wallpaper.setUploadUserId(uploadUserId);
            wallpaper.setDownloads(0);
            wallpaper.setViews(0);
            wallpaper.setLikes(0);
            wallpaper.setStatus(0); // 默认未审核

            // 保存壁纸信息
            save(wallpaper);

            // 处理标签关联
            if (!CollectionUtils.isEmpty(tagIds)) {
                wallpaperTagService.saveWallpaperTags(wallpaper.getId(), tagIds);
            }
            
            // 构建URL，用于前端访问
            String imageUrl = ImageUtils.buildFullUrl(relativePath, baseUrl);
            String thumbnailUrl = ImageUtils.buildFullUrl(thumbnailPath, baseUrl);
            
            // 设置URL（不保存到数据库，只用于返回给前端）
            wallpaper.setImageUrl(imageUrl);
            wallpaper.setThumbnailUrl(thumbnailUrl);

            return wallpaper;
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR.getCode(), e.getMessage());
        }
    }

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
    @Override
    public IPage<WpWallpaper> pageWallpapers(Integer page, Integer pageSize, Long categoryId, Long tagId, String keyword) {
        LambdaQueryWrapper<WpWallpaper> queryWrapper = new LambdaQueryWrapper<>();

        // 分类条件
        if (categoryId != null) {
            // 获取当前分类及其所有子分类ID
            List<Long> categoryIds = categoryService.getAllChildrenIds(categoryId);
            categoryIds.add(categoryId);
            queryWrapper.in(WpWallpaper::getCategoryId, categoryIds);
        }

        // 标签条件
        if (tagId != null) {
            // 查询含有该标签的壁纸ID列表
            List<Long> wallpaperIds = wallpaperTagService.getWallpaperIdsByTagId(tagId);
            if (!CollectionUtils.isEmpty(wallpaperIds)) {
                queryWrapper.in(WpWallpaper::getId, wallpaperIds);
            } else {
                // 如果没有找到匹配的壁纸，返回空结果
                return new Page<>(page, pageSize);
            }
        }

        // 关键词条件
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper ->
                    wrapper.like(WpWallpaper::getTitle, keyword)
                            .or()
                            .like(WpWallpaper::getDescription, keyword)
            );
        }

        // 按创建时间倒序排序
        queryWrapper.orderByDesc(WpWallpaper::getCreateTime);

        return page(new Page<>(page, pageSize), queryWrapper);
    }

    /**
     * 获取推荐壁纸
     *
     * @param limit 数量限制
     * @return 壁纸列表
     */
    @Override
    public List<WpWallpaper> getRecommendWallpapers(Integer limit) {
        // 简单实现：根据下载量、浏览量和点赞数综合排序
        LambdaQueryWrapper<WpWallpaper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(WpWallpaper::getDownloads)
                .orderByDesc(WpWallpaper::getViews)
                .orderByDesc(WpWallpaper::getLikes)
                .last("LIMIT " + limit);
        return list(queryWrapper);
    }

    /**
     * 获取最新壁纸
     *
     * @param limit 数量限制
     * @return 壁纸列表
     */
    @Override
    public List<WpWallpaper> getLatestWallpapers(Integer limit) {
        LambdaQueryWrapper<WpWallpaper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(WpWallpaper::getCreateTime)
                .last("LIMIT " + limit);
        return list(queryWrapper);
    }

    /**
     * 获取热门壁纸
     *
     * @param limit 数量限制
     * @return 壁纸列表
     */
    @Override
    public List<WpWallpaper> getHotWallpapers(Integer limit) {
        LambdaQueryWrapper<WpWallpaper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(WpWallpaper::getDownloads)
                .last("LIMIT " + limit);
        return list(queryWrapper);
    }

    /**
     * 增加下载次数
     *
     * @param id 壁纸ID
     * @return 是否成功
     */
    @Override
    public boolean incrementDownloads(Long id) {
        return lambdaUpdate()
                .eq(WpWallpaper::getId, id)
                .setSql("downloads = downloads + 1")
                .update();
    }

    /**
     * 增加浏览次数
     *
     * @param id 壁纸ID
     * @return 是否成功
     */
    @Override
    public boolean incrementViews(Long id) {
        return lambdaUpdate()
                .eq(WpWallpaper::getId, id)
                .setSql("views = views + 1")
                .update();
    }

    /**
     * 审核壁纸
     *
     * @param id     壁纸ID
     * @param status 状态（0-未审核，1-已审核）
     * @return 是否成功
     */
    @Override
    public boolean auditWallpaper(Long id, Integer status) {
        WpWallpaper wallpaper = new WpWallpaper();
        wallpaper.setId(id);
        wallpaper.setStatus(status);
        return updateById(wallpaper);
    }

    /**
     * 生成文件相对路径
     *
     * @param suffix 文件后缀
     * @return 文件相对路径
     */
    private String generateRelativePath(String suffix) {
        // 按日期生成子目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 生成唯一文件名
        String fileName = IdUtil.fastSimpleUUID() + "." + suffix;
        return String.format("%s/%s", dateDir, fileName);
    }

    /**
     * 检查文件类型是否允许
     *
     * @param suffix 文件后缀
     * @return 是否允许
     */
    private boolean isAllowedFileType(String suffix) {
        if (StrUtil.isBlank(suffix)) {
            return false;
        }
        List<String> types = Arrays.asList(allowedTypes.split(","));
        return types.contains(suffix.toLowerCase());
    }
} 