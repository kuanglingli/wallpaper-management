import request from './index'
import type { ApiResponse, Category, PageResult, PaginationParams } from '../types'

/**
 * 获取分类列表
 */
export function getCategoryList(params?: PaginationParams) {
  // 将分页参数转换为正确的格式
  const convertedParams = {
    // 确保page和pageSize是数字类型
    page: params?.pageNum ? Number(params.pageNum) : 1,
    pageSize: params?.pageSize ? Number(params.pageSize) : 10
  };
  
  console.log('请求分类列表，转换后参数:', convertedParams);
  return request.get<any, ApiResponse<PageResult<Category>>>('/category/page', { 
    params: convertedParams 
  });
}

/**
 * 获取所有分类（不分页）
 */
export function getAllCategories() {
  console.log('请求所有分类')
  return request.get<any, ApiResponse<Category[]>>('/category/tree')
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
  return request.put<any, ApiResponse<any>>(`/category`, data)
}

/**
 * 删除分类
 */
export function deleteCategory(id: number) {
  return request.delete<any, ApiResponse<any>>(`/category/${id}`)
}

/**
 * 获取分类树
 */
export function getCategoryTree() {
  return request.get<any, ApiResponse<Category[]>>('/category/tree')
}

/**
 * 获取子分类
 */
export function getCategoryChildren(parentId: number = 0) {
  return request.get<any, ApiResponse<Category[]>>('/category/children', { params: { parentId } })
} 