import request from './index'
import type { ApiResponse, Tag, PageResult, PaginationParams } from '../types'

/**
 * 获取标签列表
 */
export function getTagList(params?: PaginationParams) {
  // 将分页参数转换为正确的格式
  const convertedParams = {
    // 确保page和pageSize是数字类型
    page: params?.pageNum ? Number(params.pageNum) : 1,
    pageSize: params?.pageSize ? Number(params.pageSize) : 10
  };
  
  console.log('请求标签列表，转换后参数:', convertedParams);
  return request.get<any, ApiResponse<PageResult<Tag>>>('/tag/page', { 
    params: convertedParams 
  });
}

/**
 * 获取所有标签（不分页）
 */
export function getAllTags() {
  console.log('请求所有标签');
  // 由于/tag/all可能不存在，尝试使用/tag/list或/tag/page?pageSize=1000代替
  return request.get<any, ApiResponse<Tag[]>>('/tag/page', { 
    params: { page: 1, pageSize: 1000 } 
  }).then((res: ApiResponse<PageResult<Tag>>) => {
    if (res && res.data && res.data.list) {
      // 如果返回是分页格式，则提取list作为返回值
      return {
        ...res,
        data: res.data.list
      } as ApiResponse<Tag[]>;
    }
    return res as unknown as ApiResponse<Tag[]>;
  });
}

/**
 * 获取热门标签
 */
export function getHotTags(limit: number = 10) {
  console.log('请求热门标签，限制:', limit);
  // 确保limit是数字类型
  const numLimit = Number(limit);
  // 热门标签接口可能不存在，使用分页接口并按照热度排序
  return request.get<any, ApiResponse<PageResult<Tag>>>('/tag/page', { 
    params: { 
      page: 1, 
      pageSize: numLimit,
      sort: 'wallpaperCount,desc' 
    } 
  }).then((res: ApiResponse<PageResult<Tag>>) => {
    if (res && res.data && res.data.list) {
      // 如果返回是分页格式，则提取list作为返回值
      return {
        ...res,
        data: res.data.list
      } as ApiResponse<Tag[]>;
    }
    return res as unknown as ApiResponse<Tag[]>;
  });
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