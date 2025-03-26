import request from './index'
import type { ApiResponse, Category, PageResult, PaginationParams } from '../types'

/**
 * 获取分类列表
 */
export function getCategoryList(params?: PaginationParams) {
  return request.get<any, ApiResponse<PageResult<Category>>>('/category/list', { params })
}

/**
 * 获取所有分类（不分页）
 */
export function getAllCategories() {
  return request.get<any, ApiResponse<Category[]>>('/category/all')
}

/**
 * 获取分类详情
 */
export function getCategoryDetail(id: number) {
  return request.get<any, ApiResponse<Category>>(`/category/${id}`)
}

/**
 * 添加分类
 */
export function addCategory(data: Partial<Category>) {
  return request.post<any, ApiResponse<any>>('/category', data)
}

/**
 * 更新分类
 */
export function updateCategory(data: Partial<Category>) {
  return request.put<any, ApiResponse<any>>(`/category/${data.id}`, data)
}

/**
 * 删除分类
 */
export function deleteCategory(id: number) {
  return request.delete<any, ApiResponse<any>>(`/category/${id}`)
} 