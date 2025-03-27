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
  
  // 由于后端暂未实现标签API，使用模拟数据
  return new Promise<ApiResponse<Tag[]>>((resolve) => {
    console.log('使用模拟标签数据');
    
    // 模拟标签数据
    const mockTags: Tag[] = [
      { id: 1, name: '风景', description: '自然风景', wallpaperCount: 15 },
      { id: 2, name: '动物', description: '各种动物', wallpaperCount: 8 },
      { id: 3, name: '城市', description: '城市风光', wallpaperCount: 12 },
      { id: 4, name: '抽象', description: '抽象艺术', wallpaperCount: 5 },
      { id: 5, name: '科技', description: '科技相关', wallpaperCount: 7 },
      { id: 6, name: '人物', description: '人物肖像', wallpaperCount: 9 },
      { id: 7, name: '食物', description: '美食图片', wallpaperCount: 6 },
      { id: 8, name: '节日', description: '节日主题', wallpaperCount: 4 },
      { id: 9, name: '游戏', description: '游戏相关', wallpaperCount: 11 },
      { id: 10, name: '汽车', description: '汽车图片', wallpaperCount: 3 }
    ];
    
    setTimeout(() => {
      resolve({
        code: 200,
        message: '操作成功',
        data: mockTags
      });
    }, 300); // 添加短暂延迟模拟网络请求
  });
}

/**
 * 获取热门标签
 */
export function getHotTags(limit: number = 10) {
  console.log('请求热门标签，限制:', limit);
  
  // 由于后端暂未实现标签API，使用模拟数据
  return new Promise<ApiResponse<Tag[]>>((resolve) => {
    console.log('使用模拟热门标签数据');
    
    // 模拟标签数据，按wallpaperCount排序
    const mockTags: Tag[] = [
      { id: 1, name: '风景', description: '自然风景', wallpaperCount: 15 },
      { id: 3, name: '城市', description: '城市风光', wallpaperCount: 12 },
      { id: 9, name: '游戏', description: '游戏相关', wallpaperCount: 11 },
      { id: 6, name: '人物', description: '人物肖像', wallpaperCount: 9 },
      { id: 2, name: '动物', description: '各种动物', wallpaperCount: 8 },
      { id: 5, name: '科技', description: '科技相关', wallpaperCount: 7 },
      { id: 7, name: '食物', description: '美食图片', wallpaperCount: 6 },
      { id: 4, name: '抽象', description: '抽象艺术', wallpaperCount: 5 },
      { id: 8, name: '节日', description: '节日主题', wallpaperCount: 4 },
      { id: 10, name: '汽车', description: '汽车图片', wallpaperCount: 3 }
    ];
    
    // 只返回限制数量的标签
    const limitedTags = mockTags.slice(0, limit);
    
    setTimeout(() => {
      resolve({
        code: 200,
        message: '操作成功',
        data: limitedTags
      });
    }, 300); // 添加短暂延迟模拟网络请求
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