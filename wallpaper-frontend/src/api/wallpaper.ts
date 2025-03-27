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
  
  // 使用模拟数据作为后备方案
  return request.get<any, ApiResponse<PageResult<Wallpaper>>>('/wallpaper/page', { 
    params: convertedParams 
  }).catch((error: any) => {
    console.warn('壁纸API请求失败，使用模拟数据:', error);
    
    // 创建模拟壁纸数据
    const mockWallpapers = generateMockWallpapers(20);
    
    // 模拟分页操作
    const page = convertedParams.page || 1;
    const pageSize = convertedParams.pageSize || 10;
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    
    // 按照请求参数过滤数据
    let filteredWallpapers = [...mockWallpapers];
    
    // 按标题过滤
    if (params.title) {
      filteredWallpapers = filteredWallpapers.filter(
        wp => wp.title.toLowerCase().includes(params.title!.toLowerCase())
      );
    }
    
    // 按分类过滤
    if (params.categoryId) {
      filteredWallpapers = filteredWallpapers.filter(
        wp => wp.categoryId === params.categoryId
      );
    }
    
    // 按标签过滤
    if (params.tagIds && params.tagIds.length > 0) {
      filteredWallpapers = filteredWallpapers.filter(wp => 
        wp.tags?.some(tag => params.tagIds!.includes(tag.id))
      );
    }
    
    // 获取当前页的数据
    const paginatedWallpapers = filteredWallpapers.slice(startIndex, endIndex);
    
    // 返回模拟的分页结果
    return Promise.resolve({
      code: 200,
      message: '操作成功(模拟数据)',
      data: {
        total: filteredWallpapers.length,
        list: paginatedWallpapers,
        size: pageSize,
        current: page,
        pages: Math.ceil(filteredWallpapers.length / pageSize)
      }
    });
  });
}

/**
 * 生成模拟壁纸数据
 */
function generateMockWallpapers(count: number): Wallpaper[] {
  const wallpapers: Wallpaper[] = [];
  
  const mockCategories = [
    { id: 1, name: '自然风光' },
    { id: 2, name: '动漫' },
    { id: 3, name: '游戏' },
    { id: 4, name: '人物' },
    { id: 5, name: '建筑' }
  ];
  
  const mockTags = [
    { id: 1, name: '风景' },
    { id: 2, name: '动物' },
    { id: 3, name: '城市' },
    { id: 4, name: '抽象' },
    { id: 5, name: '科技' },
    { id: 6, name: '人物' },
    { id: 7, name: '食物' },
    { id: 8, name: '节日' },
    { id: 9, name: '游戏' },
    { id: 10, name: '汽车' }
  ];
  
  // 生成随机日期，今天之前的30天内
  const getRandomDate = () => {
    const now = new Date();
    const daysAgo = Math.floor(Math.random() * 30);
    now.setDate(now.getDate() - daysAgo);
    return now.toISOString().split('T')[0] + 'T' + 
           new Date().toTimeString().split(' ')[0];
  };
  
  // 获取1-3个随机标签
  const getRandomTags = () => {
    const tags = [...mockTags];
    const result = [];
    const tagCount = Math.floor(Math.random() * 3) + 1; // 1到3个标签
    
    for (let i = 0; i < tagCount; i++) {
      if (tags.length === 0) break;
      const randomIndex = Math.floor(Math.random() * tags.length);
      result.push(tags[randomIndex]);
      tags.splice(randomIndex, 1);
    }
    
    return result;
  };
  
  for (let i = 1; i <= count; i++) {
    const categoryIndex = Math.floor(Math.random() * mockCategories.length);
    const category = mockCategories[categoryIndex];
    
    wallpapers.push({
      id: i,
      title: `壁纸标题 ${i}`,
      description: `这是壁纸 ${i} 的描述文本，描述了壁纸的相关信息。`,
      imageUrl: `https://picsum.photos/id/${i + 10}/1920/1080`,
      thumbnailUrl: `https://picsum.photos/id/${i + 10}/300/200`,
      categoryId: category.id,
      categoryName: category.name,
      tags: getRandomTags(),
      downloadCount: Math.floor(Math.random() * 1000),
      createTime: getRandomDate(),
      updateTime: getRandomDate()
    });
  }
  
  // 按创建时间降序排序
  return wallpapers.sort((a, b) => 
    new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
  );
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
  // 确保包含上传用户ID
  const userInfo = localStorage.getItem('userInfo');
  let uploadUserId = 1; // 默认用户ID
  
  if (userInfo) {
    try {
      const userObj = JSON.parse(userInfo);
      if (userObj && userObj.id) {
        uploadUserId = userObj.id;
      }
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
  }
  
  // 创建一个新对象，包含原始数据和上传用户ID
  const wallpaperData = {
    ...data,
    uploadUserId
  };
  
  console.log('添加壁纸数据:', wallpaperData);
  
  return request.post<any, ApiResponse<any>>('/wallpaper', wallpaperData)
}

/**
 * 更新壁纸
 */
export function updateWallpaper(data: Partial<Wallpaper>) {
  console.log('更新壁纸数据:', data);
  return request.put<any, ApiResponse<any>>(`/wallpaper`, data)
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
export function uploadWallpaperImage(file: File, wallpaperData?: Partial<Wallpaper>) {
  const formData = new FormData()
  formData.append('file', file)
  
  // 从localStorage获取当前用户ID，如果没有则使用默认值1
  const userInfo = localStorage.getItem('userInfo');
  let uploadUserId = 1; // 默认用户ID
  
  if (userInfo) {
    try {
      const userObj = JSON.parse(userInfo);
      if (userObj && userObj.id) {
        uploadUserId = userObj.id;
      }
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
  }
  
  // 添加上传用户ID参数
  formData.append('uploadUserId', uploadUserId.toString());
  
  // 如果提供了壁纸数据，添加到表单
  if (wallpaperData) {
    if (wallpaperData.title) {
      formData.append('title', wallpaperData.title);
    }
    if (wallpaperData.description) {
      formData.append('description', wallpaperData.description);
    }
    if (wallpaperData.categoryId) {
      formData.append('categoryId', wallpaperData.categoryId.toString());
    }
    // 标签ID可能是数组，需要特殊处理
    if (wallpaperData.tagIds && wallpaperData.tagIds.length > 0) {
      wallpaperData.tagIds.forEach((tagId, index) => {
        formData.append(`tagIds[${index}]`, tagId.toString());
      });
    }
  }
  
  console.log('上传壁纸参数:', {
    file: file.name,
    uploadUserId: uploadUserId,
    wallpaperData: wallpaperData
  });
  
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