import request from './index'
import type { ApiResponse, Wallpaper, WallpaperQueryParams, PageResult } from '../types'

/**
 * 获取壁纸列表
 */
export function getWallpaperList(params: WallpaperQueryParams) {
  // 将分页参数转换为正确的格式
  const convertedParams = {
    ...params,
    // 确保pageNum和pageSize是数字类型
    page: params.pageNum ? Number(params.pageNum) : 1,
    pageSize: params.pageSize ? Number(params.pageSize) : 10
  };
  
  console.log('请求壁纸列表，转换后参数:', convertedParams);
  return request.get<any, ApiResponse<PageResult<Wallpaper>>>('/wallpaper/page', { 
    params: convertedParams 
  });
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

/**
 * 获取最新壁纸
 */
export function getLatestWallpapers(limit: number = 5) {
  return request.get<any, ApiResponse<Wallpaper[]>>('/wallpaper/latest', { params: { limit }})
}

/**
 * 获取热门壁纸
 */
export function getHotWallpapers(limit: number = 5) {
  return request.get<any, ApiResponse<Wallpaper[]>>('/wallpaper/hot', { params: { limit }})
} 