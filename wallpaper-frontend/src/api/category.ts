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
  console.log('请求所有分类');
  // 首先尝试使用tree接口
  return request.get<any, ApiResponse<Category[]>>('/category/tree')
    .then((res: ApiResponse<Category[]>) => {
      console.log('获取到分类树数据:', res);
      // 如果成功获取数据并且是数组，直接返回
      if (res && res.data && Array.isArray(res.data)) {
        return res;
      }
      // 如果tree接口没有返回预期的数据，尝试使用分页接口
      return request.get<any, ApiResponse<PageResult<Category>>>('/category/page', { 
        params: { page: 1, pageSize: 1000 } 
      }).then((pageRes: ApiResponse<PageResult<Category>>) => {
        console.log('获取到分类分页数据:', pageRes);
        if (pageRes && pageRes.data) {
          // 兼容不同的返回数据结构
          if (pageRes.data.list && Array.isArray(pageRes.data.list)) {
            return {
              ...pageRes,
              data: pageRes.data.list
            } as ApiResponse<Category[]>;
          } else if (pageRes.data.records && Array.isArray(pageRes.data.records)) {
            return {
              ...pageRes,
              data: pageRes.data.records
            } as ApiResponse<Category[]>;
          }
        }
        // 如果上面的条件都不满足，返回空数组
        return {
          code: pageRes.code,
          message: pageRes.message,
          data: [] as Category[]
        };
      });
    })
    .catch((error: any) => {
      console.error('获取分类数据失败:', error);
      // 如果tree接口请求失败，尝试使用分页接口
      return request.get<any, ApiResponse<PageResult<Category>>>('/category/page', { 
        params: { page: 1, pageSize: 1000 } 
      }).then((pageRes: ApiResponse<PageResult<Category>>) => {
        console.log('获取到分类分页数据:', pageRes);
        if (pageRes && pageRes.data) {
          // 兼容不同的返回数据结构
          if (pageRes.data.list && Array.isArray(pageRes.data.list)) {
            return {
              ...pageRes,
              data: pageRes.data.list
            } as ApiResponse<Category[]>;
          } else if (pageRes.data.records && Array.isArray(pageRes.data.records)) {
            return {
              ...pageRes,
              data: pageRes.data.records
            } as ApiResponse<Category[]>;
          }
        }
        // 如果上面的条件都不满足，返回空数组
        return {
          code: pageRes.code,
          message: pageRes.message,
          data: [] as Category[]
        };
      });
    });
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