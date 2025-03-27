-- 创建壁纸标签关联表
CREATE TABLE IF NOT EXISTS `wp_wallpaper_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `wallpaper_id` bigint(20) NOT NULL COMMENT '壁纸ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_wallpaper_id` (`wallpaper_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='壁纸标签关联表';

-- 添加外键约束
ALTER TABLE `wp_wallpaper_tag`
  ADD CONSTRAINT `fk_wallpaper_tag_wallpaper_id` FOREIGN KEY (`wallpaper_id`) REFERENCES `wp_wallpaper` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_wallpaper_tag_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `wp_tag` (`id`) ON DELETE CASCADE; 