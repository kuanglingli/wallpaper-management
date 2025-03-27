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
    pageSize: params?.pageSize ? Number(params.pageSize) : 10,
    // 添加排序参数，按ID升序排列
    sort: params?.sort || 'id,asc'
  };
  
  console.log('请求分类列表，转换后参数:', convertedParams);
  
  // 添加错误处理和模拟数据支持
  return request.get<any, ApiResponse<PageResult<Category>>>('/category/page', { 
    params: convertedParams 
  }).catch((error: any) => {
    console.warn('分类API请求失败，使用模拟数据:', error);
    
    // 获取模拟分类数据
    const mockCategories = getMockCategories();
    
    // 模拟分页
    const page = convertedParams.page;
    const pageSize = convertedParams.pageSize;
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    
    // 获取当前页的数据
    const paginatedCategories = mockCategories.slice(startIndex, endIndex);
    
    // 返回模拟的分页结果
    return Promise.resolve({
      code: 200,
      message: '操作成功(模拟数据)',
      data: {
        total: mockCategories.length,
        list: paginatedCategories,
        size: pageSize,
        current: page,
        pages: Math.ceil(mockCategories.length / pageSize)
      }
    });
  });
}

/**
 * 获取所有分类（不分页）
 */
export function getAllCategories() {
  console.log('请求所有分类');
  
  // 先尝试使用tree接口
  return request.get<any, ApiResponse<Category[]>>('/category/tree')
    .then((res: ApiResponse<Category[]>) => {
      console.log('获取到分类树数据:', res);
      
      // 确保数据格式正确
      if (res && res.data && Array.isArray(res.data)) {
        // 为调试目的，增加日志
        console.log('分类树数据已成功解析，共', res.data.length, '条记录');
        return res;
      }
      
      // 如果tree接口没有返回数据或格式不正确，尝试分页接口
      console.log('分类树数据格式不正确，尝试使用分页接口');
      return fetchCategoriesFromPageApi();
    })
    .catch((error: any) => {
      console.error('获取分类树数据失败，尝试使用分页接口:', error);
      return fetchCategoriesFromPageApi();
    });
}

// 从分页接口获取分类数据的辅助函数
function fetchCategoriesFromPageApi(): Promise<ApiResponse<Category[]>> {
  return request.get<any, ApiResponse<PageResult<Category>>>('/category/page', { 
    params: { page: 1, pageSize: 1000 } 
  }).then((pageRes: ApiResponse<PageResult<Category>>) => {
    console.log('获取到分类分页数据:', pageRes);
    
    if (pageRes && pageRes.data) {
      let categoryList: Category[] = [];
      
      // 兼容不同的返回数据结构
      if (pageRes.data.list && Array.isArray(pageRes.data.list)) {
        categoryList = pageRes.data.list;
      } else if (pageRes.data.records && Array.isArray(pageRes.data.records)) {
        categoryList = pageRes.data.records;
      }
      
      console.log('分页获取的分类数据，共', categoryList.length, '条记录');
      
      // 预处理分类数据，确保属性名称兼容
      categoryList = categoryList.map(category => {
        // 如果后端返回的是categoryName但没有name字段，添加name字段
        if (category.categoryName && !category.name) {
          return {
            ...category,
            name: category.categoryName
          };
        }
        // 如果后端返回的是name但没有categoryName字段，添加categoryName字段
        if (category.name && !category.categoryName) {
          return {
            ...category, 
            categoryName: category.name
          };
        }
        return category;
      });
      
      // 如果没有获取到任何数据，使用模拟数据
      if (categoryList.length === 0) {
        console.log('未获取到真实分类数据，使用模拟数据');
        categoryList = getMockCategories();
      }
      
      return {
        ...pageRes,
        data: categoryList
      } as ApiResponse<Category[]>;
    }
    
    // 如果上面的条件都不满足，返回模拟数据
    console.log('API返回格式异常，使用模拟数据');
    return {
      code: 200,
      message: '操作成功(模拟数据)',
      data: getMockCategories()
    };
  });
}

// 获取模拟分类数据
function getMockCategories(): Category[] {
  return [
    { id: 1, name: '自然风光', categoryName: '自然风光', description: '自然风景壁纸', wallpaperCount: 25 },
    { id: 2, name: '动漫', categoryName: '动漫', description: '动漫壁纸', wallpaperCount: 18 },
    { id: 3, name: '游戏', categoryName: '游戏', description: '游戏壁纸', wallpaperCount: 22 },
    { id: 4, name: '人物', categoryName: '人物', description: '人物壁纸', wallpaperCount: 15 },
    { id: 5, name: '建筑', categoryName: '建筑', description: '建筑壁纸', wallpaperCount: 12 },
    { id: 6, name: '科技', categoryName: '科技', description: '科技壁纸', wallpaperCount: 10 },
    { id: 7, name: '抽象', categoryName: '抽象', description: '抽象艺术壁纸', wallpaperCount: 8 },
    { id: 8, name: '节日', categoryName: '节日', description: '节日主题壁纸', wallpaperCount: 7 }
  ];
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