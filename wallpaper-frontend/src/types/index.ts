// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页请求参数
export interface PaginationParams {
  pageNum: number
  pageSize: number
}

// 分页响应结果
export interface PageResult<T> {
  total: number
  list: T[]
}

// 壁纸相关类型
export interface Wallpaper {
  id: number
  title: string
  description?: string
  imageUrl: string
  thumbnailUrl: string
  categoryId: number
  categoryName?: string
  tags?: Tag[]
  tagIds?: number[]
  downloadCount?: number
  createTime: string
  updateTime?: string
}

export interface WallpaperQueryParams extends PaginationParams {
  title?: string
  categoryId?: number
  tagIds?: number[]
}

// 类别相关类型
export interface Category {
  id: number
  name: string
  description?: string
  wallpaperCount?: number
  createTime?: string
  updateTime?: string
}

// 标签相关类型
export interface Tag {
  id: number
  name: string
  description?: string
  wallpaperCount?: number
  createTime?: string
  updateTime?: string
}

// 登录相关类型
export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userInfo: UserInfo
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  roles: string[]
} 