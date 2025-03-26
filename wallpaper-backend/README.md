# 壁纸管理系统后端

## 项目概述

壁纸管理系统后端，基于Spring Boot + MyBatis Plus + MySQL开发。

## 功能特性

- 用户管理：支持用户注册、登录、信息修改等功能
- 权限控制：基于Shiro实现角色和权限管理
- 壁纸管理：包括壁纸上传、下载、分类、标签、收藏等功能
- 评论管理：支持壁纸评论和评分功能
- 数据统计：提供壁纸浏览量、下载量等数据统计功能

## 项目结构

```
wallpaper-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/wallpaper/management/
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器
│   │   │       ├── entity/          # 实体类
│   │   │       ├── mapper/          # Mapper接口
│   │   │       ├── service/         # 服务接口
│   │   │       │   └── impl/        # 服务实现类
│   │   │       ├── dto/             # 数据传输对象
│   │   │       ├── vo/              # 视图对象
│   │   │       ├── common/          # 通用类
│   │   │       ├── utils/           # 工具类
│   │   │       ├── exception/       # 异常处理
│   │   │       └── WallpaperManagementApplication.java # 启动类
│   │   └── resources/
│   │       ├── mapper/              # Mapper XML文件
│   │       ├── db/                  # 数据库脚本
│   │       └── application.yml      # 配置文件
│   └── test/                        # 测试代码
├── pom.xml                          # Maven配置
└── README.md                        # 项目说明
```

## 开发环境

- JDK 11+
- Maven 3.6+
- MySQL 8.0+

## 安装与运行

### 数据库准备

1. 创建数据库：wallpaper_management
2. 执行数据库脚本：`src/main/resources/db/schema.sql`

### 修改配置

编辑 `src/main/resources/application.yml` 文件，修改数据库连接信息和文件上传路径等配置。

### 编译与运行

```bash
# 编译
mvn clean package

# 运行
java -jar target/wallpaper-management-0.0.1-SNAPSHOT.jar
```

## 接口文档

启动应用后，访问 http://localhost:8080/api/doc.html 查看Swagger接口文档。

## 技术栈

- Spring Boot：应用框架
- MyBatis Plus：ORM框架
- MySQL：数据库
- Shiro：权限管理
- JWT：身份认证
- HuTool：工具库
- Knife4j：接口文档 