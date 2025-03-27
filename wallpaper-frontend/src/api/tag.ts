import request from './index'
import type { ApiResponse, Tag, PageResult, PaginationParams } from '../types'

/**
 * 获取标签列表
 */
export function getTagList(params?: PaginationParams) {
  console.log('请求标签列表，参数:', params)
  return request.get<any, ApiResponse<PageResult<Tag>>>('/tag/page', { params })
}

/**
 * 获取所有标签（不分页）
 */
export function getAllTags() {
  console.log('请求所有标签')
  return request.get<any, ApiResponse<Tag[]>>('/tag/all')
}

/**
 * 获取热门标签
 */
export function getHotTags(limit: number = 10) {
  console.log('请求热门标签，限制:', limit)
  return request.get<any, ApiResponse<Tag[]>>('/tag/hot', { params: { limit } })
}

/**
 * 获取标签详情
 */
export function getTagDetail(id: number) {
  return request.get<any, ApiResponse<Tag>>(`/tag/${id}`)
}

/**
 * 添加标签
 */
export function addTag(data: Partial<Tag>) {
  return request.post<any, ApiResponse<any>>('/tag', data)
}

/**
 * 更新标签
 */
export function updateTag(data: Partial<Tag>) {
  return request.put<any, ApiResponse<any>>('/tag', data)
}

/**
 * 删除标签
 */
export function deleteTag(id: number) {
  return request.delete<any, ApiResponse<any>>(`/tag/${id}`)
} 