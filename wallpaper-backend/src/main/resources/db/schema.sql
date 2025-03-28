-- 创建数据库
CREATE DATABASE IF NOT EXISTS wallpaper_management CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE wallpaper_management;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
  `permission_code` varchar(50) NOT NULL COMMENT '权限编码',
  `description` varchar(200) DEFAULT NULL COMMENT '权限描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 壁纸分类表
CREATE TABLE IF NOT EXISTS `wp_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父分类ID（0表示顶级分类）',
  `icon` varchar(200) DEFAULT NULL COMMENT '分类图标',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='壁纸分类表';

-- 壁纸标签表
CREATE TABLE IF NOT EXISTS `wp_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `description` varchar(200) DEFAULT NULL COMMENT '标签描述',
  `wallpaper_count` int(11) DEFAULT 0 COMMENT '关联壁纸数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='壁纸标签表';

-- 壁纸表
CREATE TABLE IF NOT EXISTS `wp_wallpaper` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '壁纸ID',
  `title` varchar(100) NOT NULL COMMENT '壁纸标题',
  `description` varchar(500) DEFAULT NULL COMMENT '壁纸描述',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `file_path` varchar(200) NOT NULL COMMENT '文件路径',
  `thumbnail_path` varchar(200) DEFAULT NULL COMMENT '缩略图路径',
  `file_size` bigint(20) DEFAULT 0 COMMENT '文件大小（字节）',
  `width` int(11) DEFAULT 0 COMMENT '宽度（像素）',
  `height` int(11) DEFAULT 0 COMMENT '高度（像素）',
  `file_type` varchar(20) DEFAULT NULL COMMENT '文件类型',
  `downloads` int(11) DEFAULT 0 COMMENT '下载次数',
  `views` int(11) DEFAULT 0 COMMENT '浏览次数',
  `likes` int(11) DEFAULT 0 COMMENT '点赞次数',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（0-未审核，1-已审核）',
  `upload_user_id` bigint(20) DEFAULT NULL COMMENT '上传用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='壁纸表';

-- 壁纸标签关联表
CREATE TABLE IF NOT EXISTS `wp_wallpaper_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `wallpaper_id` bigint(20) NOT NULL COMMENT '壁纸ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wallpaper_tag` (`wallpaper_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='壁纸标签关联表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS `wp_user_collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `wallpaper_id` bigint(20) NOT NULL COMMENT '壁纸ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_wallpaper` (`user_id`,`wallpaper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 评论表
CREATE TABLE IF NOT EXISTS `wp_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `wallpaper_id` bigint(20) NOT NULL COMMENT '壁纸ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `rating` tinyint(1) DEFAULT 5 COMMENT '评分（1-5）',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父评论ID（0表示顶级评论）',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（0-待审核，1-已审核）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 下载记录表
CREATE TABLE IF NOT EXISTS `wp_download_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID（未登录用户为NULL）',
  `wallpaper_id` bigint(20) NOT NULL COMMENT '壁纸ID',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `download_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下载时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='下载记录表';

-- 初始化数据
-- 插入管理员角色
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`) VALUES ('管理员', 'ROLE_ADMIN', '系统管理员');
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`) VALUES ('普通用户', 'ROLE_USER', '普通用户');

-- 插入初始权限
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`) VALUES ('用户管理', 'user:manage', '用户管理权限');
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`) VALUES ('角色管理', 'role:manage', '角色管理权限');
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`) VALUES ('壁纸上传', 'wallpaper:upload', '壁纸上传权限');
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`) VALUES ('壁纸编辑', 'wallpaper:edit', '壁纸编辑权限');
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`) VALUES ('壁纸审核', 'wallpaper:audit', '壁纸审核权限');
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`) VALUES ('壁纸下载', 'wallpaper:download', '壁纸下载权限');
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`) VALUES ('评论管理', 'comment:manage', '评论管理权限');

-- 为角色分配权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 1);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 2);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 3);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 4);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 5);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 6);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (1, 7);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (2, 3);
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES (2, 6);

-- 插入管理员账号（密码为加密后的123456）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`) 
VALUES ('admin', 'd35b4f021dea22a29651810361e84848', '系统管理员', 'admin@example.com', 1);

-- 为管理员分配角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 插入初始壁纸分类
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('自然风景', 0, 1);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('动物', 0, 2);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('建筑', 0, 3);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('抽象', 0, 4);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('科技', 0, 5);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('动漫', 0, 6);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('游戏', 0, 7);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('艺术', 0, 8);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('节日', 0, 9);
INSERT INTO `wp_category` (`category_name`, `parent_id`, `sort`) VALUES ('其他', 0, 10); 