package com.wallpaper.management.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件控制器
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;
    
    private static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};

    /**
     * 获取文件
     *
     * @param date 日期目录
     * @param filename 文件名
     * @param request HTTP请求
     * @return 文件响应
     */
    @Operation(summary = "获取文件")
    @GetMapping("/{date}/{filename}")
    public ResponseEntity<byte[]> getFile(
            @Parameter(description = "日期目录", required = true) @PathVariable String date,
            @Parameter(description = "文件名", required = true) @PathVariable String filename,
            HttpServletRequest request) {
        
        // 构建文件路径
        String filePath = date + "/" + filename;
        Path path = Paths.get(uploadPath, filePath);
        File file = path.toFile();
        
        // 检查文件是否存在
        if (!file.exists()) {
            log.error("文件不存在: {}", path);
            return ResponseEntity.notFound().build();
        }
        
        try {
            // 读取文件内容
            byte[] fileContent = Files.readAllBytes(path);
            
            // 判断是否为图片
            String extension = FilenameUtils.getExtension(filename);
            boolean isImage = StrUtil.isNotBlank(extension) && isImageExtension(extension);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            
            if (isImage) {
                // 图片类型设置Content-Type
                headers.setContentType(getMediaType(extension));
            } else {
                // 下载文件设置Content-Disposition
                String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"");
            }
            
            headers.setContentLength(fileContent.length);
            
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("读取文件失败: {}", path, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 判断是否为图片扩展名
     *
     * @param extension 扩展名
     * @return 是否为图片
     */
    private boolean isImageExtension(String extension) {
        if (StrUtil.isBlank(extension)) {
            return false;
        }
        
        String lowerExt = extension.toLowerCase();
        for (String imageExt : IMAGE_EXTENSIONS) {
            if (imageExt.equals(lowerExt)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 根据扩展名获取媒体类型
     *
     * @param extension 扩展名
     * @return 媒体类型
     */
    private MediaType getMediaType(String extension) {
        if (StrUtil.isBlank(extension)) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "webp":
                // WebP没有标准MediaType，使用通用图片类型
                return MediaType.valueOf("image/webp");
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
} 