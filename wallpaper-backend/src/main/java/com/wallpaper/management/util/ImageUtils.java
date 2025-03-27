package com.wallpaper.management.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图片处理工具类
 */
@Slf4j
public class ImageUtils {

    /**
     * 生成缩略图
     *
     * @param originalPath 原图路径
     * @param uploadPath 上传根目录
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @return 缩略图相对路径
     */
    public static String generateThumbnail(String originalPath, String uploadPath, int maxWidth, int maxHeight) {
        if (StrUtil.isBlank(originalPath) || StrUtil.isBlank(uploadPath)) {
            return null;
        }
        
        try {
            // 获取原图绝对路径
            Path originalFullPath = Paths.get(uploadPath, originalPath);
            File originalFile = originalFullPath.toFile();
            if (!originalFile.exists()) {
                log.error("原图不存在: {}", originalFullPath);
                return null;
            }
            
            // 读取原图
            BufferedImage originalImage = ImageIO.read(originalFile);
            int originWidth = originalImage.getWidth();
            int originHeight = originalImage.getHeight();
            
            // 计算缩略图尺寸
            int[] dimensions = calculateDimensions(originWidth, originHeight, maxWidth, maxHeight);
            int thumbWidth = dimensions[0];
            int thumbHeight = dimensions[1];
            
            // 创建缩略图
            BufferedImage thumbnailImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = thumbnailImage.createGraphics();
            
            // 设置图像质量
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制缩略图
            graphics.drawImage(originalImage, 0, 0, thumbWidth, thumbHeight, null);
            graphics.dispose();
            
            // 生成缩略图路径
            String suffix = FileUtil.getSuffix(originalPath);
            String thumbnailPath = originalPath.replace("." + suffix, "_thumb." + suffix);
            Path thumbnailFullPath = Paths.get(uploadPath, thumbnailPath);
            
            // 确保目录存在
            FileUtil.mkParentDirs(thumbnailFullPath.toFile());
            
            // 保存缩略图
            ImageIO.write(thumbnailImage, suffix, thumbnailFullPath.toFile());
            
            return thumbnailPath;
        } catch (IOException e) {
            log.error("生成缩略图失败", e);
            return null;
        }
    }
    
    /**
     * 计算缩略图尺寸
     *
     * @param originWidth 原图宽度
     * @param originHeight 原图高度
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @return 缩略图尺寸数组，第一个元素为宽度，第二个元素为高度
     */
    private static int[] calculateDimensions(int originWidth, int originHeight, int maxWidth, int maxHeight) {
        int thumbWidth = originWidth;
        int thumbHeight = originHeight;
        
        // 如果原图尺寸小于等于缩略图尺寸，则不缩放
        if (originWidth <= maxWidth && originHeight <= maxHeight) {
            return new int[] {originWidth, originHeight};
        }
        
        // 计算缩放比例
        double widthRatio = (double) maxWidth / originWidth;
        double heightRatio = (double) maxHeight / originHeight;
        
        // 选择较小的比例进行等比缩放
        double ratio = Math.min(widthRatio, heightRatio);
        
        thumbWidth = (int) (originWidth * ratio);
        thumbHeight = (int) (originHeight * ratio);
        
        return new int[] {thumbWidth, thumbHeight};
    }
    
    /**
     * 构建文件的完整访问URL
     *
     * @param filePath 文件相对路径
     * @param baseUrl 基础URL
     * @return 完整访问URL
     */
    public static String buildFullUrl(String filePath, String baseUrl) {
        if (StrUtil.isBlank(filePath)) {
            return "";
        }
        
        if (StrUtil.isBlank(baseUrl)) {
            return filePath;
        }
        
        // 确保baseUrl以/结尾
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        
        // 确保filePath不以/开头
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        
        return baseUrl + filePath;
    }
} 