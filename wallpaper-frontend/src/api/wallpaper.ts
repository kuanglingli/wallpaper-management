import request from './index'
import type { ApiResponse, Wallpaper, WallpaperQueryParams, PageResult } from '../types'

/**
 * 获取壁纸列表
 */
export function getWallpaperList(params: WallpaperQueryParams) {
  return request.get<any, ApiResponse<PageResult<Wallpaper>>>('/wallpaper/list', { params })
}

/**
 * 获取壁纸详情
 */
export function getWallpaperDetail(id: number) {
  return request.get<any, ApiResponse<Wallpaper>>(`/wallpaper/${id}`)
}

/**
 * 添加壁纸
 */
export function addWallpaper(data: Partial<Wallpaper>) {
  return request.post<any, ApiResponse<any>>('/wallpaper', data)
}

/**
 * 更新壁纸
 */
export function updateWallpaper(data: Partial<Wallpaper>) {
  return request.put<any, ApiResponse<any>>(`/wallpaper/${data.id}`, data)
}

/**
 * 删除壁纸
 */
export function deleteWallpaper(id: number) {
  return request.delete<any, ApiResponse<any>>(`/wallpaper/${id}`)
}

/**
 * 上传壁纸图片
 */
export function uploadWallpaperImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, ApiResponse<{ url: string, thumbnailUrl: string }>>(
    '/wallpaper/upload',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }
  )
} 