-- 修改wp_wallpaper表，重命名downloads列为download_count，并添加favorite_count列
ALTER TABLE `wp_wallpaper` 
CHANGE COLUMN `downloads` `download_count` int(11) DEFAULT 0 COMMENT '下载次数',
ADD COLUMN `favorite_count` int(11) DEFAULT 0 COMMENT '收藏次数' AFTER `download_count`;

-- 重命名wp_user_collection表为wp_favorite
RENAME TABLE `wp_user_collection` TO `wp_favorite`; 