# 壁纸管理系统前端

## 项目概述

壁纸管理系统前端，基于Vue3 + Arco Design UI + TypeScript开发。

## 功能特性

- 壁纸浏览：支持按分类、标签、热门程度等多维度浏览壁纸
- 壁纸上传：支持上传本地壁纸，并添加标题、描述、分类、标签等信息
- 壁纸下载：支持一键下载壁纸到本地
- 用户管理：包括登录、注册、个人信息管理等功能
- 壁纸收藏：用户可收藏喜欢的壁纸，方便后续查看
- 壁纸点赞：用户可对壁纸进行点赞，提高壁纸热度
- 评论功能：用户可对壁纸发表评论，分享感受

## 项目结构

```
wallpaper-frontend/
├── public/              # 静态资源
├── src/                 # 源代码
│   ├── api/             # API接口
│   ├── assets/          # 资源文件
│   ├── components/      # 通用组件
│   ├── layout/          # 布局组件
│   ├── router/          # 路由配置
│   ├── store/           # 状态管理
│   ├── types/           # TypeScript类型定义
│   ├── utils/           # 工具函数
│   ├── views/           # 页面组件
│   ├── App.vue          # 根组件
│   └── main.ts          # 入口文件
├── .gitignore           # Git忽略文件
├── index.html           # HTML模板
├── package.json         # 依赖配置
├── tsconfig.json        # TypeScript配置
└── vite.config.ts       # Vite配置
```

## 开发环境

- Node.js 16+
- npm 8+

## 安装与运行

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

### 构建生产环境

```bash
npm run build
```

## 技术栈

- Vue3：前端框架
- Arco Design：UI组件库
- TypeScript：静态类型检查
- Vite：构建工具
- Vue Router：路由管理
- Pinia：状态管理
- Axios：HTTP请求 