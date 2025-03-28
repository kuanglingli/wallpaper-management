<template>
  <div class="showcase-container">
    <!-- 顶部搜索与分类过滤区域 -->
    <div class="showcase-filter-bar">
      <div class="filter-title">
        <el-icon><Picture /></el-icon>
        <span>壁纸展示</span>
      </div>
      <div class="filter-controls">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索壁纸" 
          clearable 
          prefix-icon="Search"
          @input="debounceSearch"
          class="search-input"
        />
        <el-select 
          v-model="selectedCategory" 
          placeholder="选择分类" 
          clearable
          @change="handleCategoryChange"
          class="category-select"
        >
          <el-option 
            v-for="category in categories" 
            :key="category.id" 
            :label="category.name || category.categoryName" 
            :value="category.id"
          />
        </el-select>
      </div>
    </div>

    <!-- 分类标签选择区域 -->
    <div class="category-tags">
      <el-scrollbar>
        <div class="tag-list">
          <el-tag 
            :effect="selectedCategory === null ? 'dark' : 'plain'"
            @click="selectedCategory = null; fetchWallpapers()"
            class="category-tag"
          >
            全部
          </el-tag>
          <el-tag 
            v-for="category in categories" 
            :key="category.id"
            :effect="selectedCategory === category.id ? 'dark' : 'plain'"
            @click="selectedCategory = category.id; fetchWallpapers()"
            class="category-tag"
          >
            {{ category.name || category.categoryName }}
          </el-tag>
        </div>
      </el-scrollbar>
    </div>
    
    <!-- 壁纸展示区域 -->
    <div class="wallpaper-grid" v-loading="loading">
      <div v-if="wallpapers.length === 0 && !loading" class="no-wallpapers">
        <el-empty description="暂无壁纸" />
      </div>
      <div v-else class="masonry-grid">
        <div 
          v-for="wallpaper in wallpapers" 
          :key="wallpaper.id" 
          class="wallpaper-card"
          @click="previewWallpaper(wallpaper)"
        >
          <div class="card-content">
            <div class="image-container">
              <el-image 
                :src="wallpaper.thumbnailUrl" 
                :alt="wallpaper.title"
                fit="cover"
                loading="lazy"
                :preview-src-list="[wallpaper.imageUrl]"
                hide-on-click-modal
                class="wallpaper-image"
              />
              <div class="wallpaper-overlay">
                <div class="wallpaper-title">{{ wallpaper.title }}</div>
                <div class="wallpaper-actions">
                  <el-tooltip content="收藏" placement="top">
                    <el-button 
                      circle 
                      :type="wallpaper.isFavorite ? 'danger' : 'default'" 
                      :icon="wallpaper.isFavorite ? 'Star' : 'StarFilled'" 
                      @click.stop="toggleFavorite(wallpaper)"
                      size="small"
                    />
                  </el-tooltip>
                  <el-tooltip content="点赞" placement="top">
                    <el-button 
                      circle 
                      :type="wallpaper.isLiked ? 'danger' : 'default'" 
                      :icon="wallpaper.isLiked ? 'Goods' : 'GoodsFilled'" 
                      @click.stop="toggleLike(wallpaper)"
                      size="small"
                    />
                  </el-tooltip>
                  <el-tooltip content="下载" placement="top">
                    <el-button 
                      circle 
                      type="default" 
                      icon="Download" 
                      @click.stop="downloadWallpaper(wallpaper)"
                      size="small"
                    />
                  </el-tooltip>
                </div>
              </div>
              <div class="wallpaper-meta">
                <div class="category-info">
                  <el-tag size="small" type="info">{{ wallpaper.categoryName }}</el-tag>
                </div>
                <div class="stats">
                  <span><el-icon><Download /></el-icon> {{ wallpaper.downloadCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
      />
    </div>
    
    <!-- 壁纸预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      :title="currentWallpaper?.title || '壁纸预览'"
      width="80%"
      class="wallpaper-preview-dialog"
      destroy-on-close
    >
      <div class="preview-content">
        <div class="preview-image-container">
          <el-image
            :src="currentWallpaper?.imageUrl"
            fit="contain"
            class="preview-image"
          />
        </div>
        <div class="preview-info">
          <h3>{{ currentWallpaper?.title }}</h3>
          <p class="description">{{ currentWallpaper?.description || '暂无描述' }}</p>
          
          <div class="info-section">
            <h4>分类</h4>
            <el-tag>{{ currentWallpaper?.categoryName }}</el-tag>
          </div>
          
          <div class="info-section" v-if="currentWallpaper?.tags && currentWallpaper.tags.length > 0">
            <h4>标签</h4>
            <div class="tags-container">
              <el-tag 
                v-for="tag in currentWallpaper.tags" 
                :key="tag.id"
                size="small"
                class="tag-item"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </div>
          
          <div class="info-section">
            <h4>统计</h4>
            <p><el-icon><Download /></el-icon> 下载次数: {{ currentWallpaper?.downloadCount || 0 }}</p>
            <p><el-icon><Timer /></el-icon> 上传时间: {{ formatDate(currentWallpaper?.createTime) }}</p>
          </div>
          
          <div class="action-buttons">
            <el-button 
              type="primary" 
              icon="Download" 
              @click="downloadWallpaper(currentWallpaper)"
              :loading="downloading"
            >
              下载壁纸
            </el-button>
            <el-button 
              :type="currentWallpaper?.isFavorite ? 'danger' : 'default'" 
              :icon="currentWallpaper?.isFavorite ? 'Star' : 'StarFilled'" 
              @click="toggleFavorite(currentWallpaper)"
            >
              {{ currentWallpaper?.isFavorite ? '取消收藏' : '收藏壁纸' }}
            </el-button>
            <el-button 
              :type="currentWallpaper?.isLiked ? 'danger' : 'default'" 
              :icon="currentWallpaper?.isLiked ? 'Goods' : 'GoodsFilled'" 
              @click="toggleLike(currentWallpaper)"
            >
              {{ currentWallpaper?.isLiked ? '取消点赞' : '点赞壁纸' }}
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, Download, Star, Timer, Search, Goods, GoodsFilled } from '@element-plus/icons-vue'
import { getWallpaperList, getWallpaperDetail, favoriteWallpaper, unfavoriteWallpaper } from '@/api/wallpaper'
import { getAllCategories } from '@/api/category'
import type { Wallpaper, Category } from '@/types'

// 状态管理
const loading = ref(false)
const downloading = ref(false)
const searchKeyword = ref('')
const selectedCategory = ref(null)
const wallpapers = ref<any[]>([])
const categories = ref<Category[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

// 预览相关
const previewDialogVisible = ref(false)
const currentWallpaper = ref<any | null>(null)

// 搜索防抖
let searchTimer: any = null
const debounceSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    fetchWallpapers()
  }, 500)
}

// 初始化加载数据
onMounted(async () => {
  await Promise.all([
    fetchCategories(),
    fetchWallpapers()
  ])
})

// 获取所有分类
const fetchCategories = async () => {
  try {
    const response = await getAllCategories()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.error('获取分类失败')
  }
}

// 获取壁纸列表
const fetchWallpapers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      title: searchKeyword.value || undefined,
      categoryId: selectedCategory.value || undefined
    }
    
    const response = await getWallpaperList(params)
    if (response.code === 200) {
      const data = response.data
      const wallpaperList = data.list || data.records || []
      wallpapers.value = wallpaperList
      total.value = data.total || 0
      
      // 请求每个壁纸的收藏状态（假设后端有此接口）
      // 这里模拟一下，实际项目中应该调用真实接口
      wallpapers.value = wallpapers.value.map(wallpaper => ({
        ...wallpaper,
        isFavorite: Math.random() > 0.5, // 模拟随机收藏状态
        isLiked: Math.random() > 0.5     // 模拟随机点赞状态
      }))
    }
  } catch (error) {
    console.error('获取壁纸列表失败:', error)
    ElMessage.error('获取壁纸列表失败')
  } finally {
    loading.value = false
  }
}

// 切换收藏状态
const toggleFavorite = async (wallpaper: Wallpaper | null) => {
  if (!wallpaper) return
  
  try {
    if (wallpaper.isFavorite) {
      await unfavoriteWallpaper(wallpaper.id)
      wallpaper.isFavorite = false
      ElMessage.success('已取消收藏')
    } else {
      await favoriteWallpaper(wallpaper.id)
      wallpaper.isFavorite = true
      ElMessage.success('收藏成功')
    }
    
    // 更新当前预览壁纸的状态
    if (currentWallpaper.value && currentWallpaper.value.id === wallpaper.id) {
      currentWallpaper.value.isFavorite = wallpaper.isFavorite
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    ElMessage.error('操作收藏失败，请稍后重试')
  }
}

// 切换点赞状态
const toggleLike = async (wallpaper: Wallpaper | null) => {
  if (!wallpaper) return
  
  try {
    // 假设这里有like和unlike的API
    wallpaper.isLiked = !wallpaper.isLiked
    ElMessage.success(wallpaper.isLiked ? '点赞成功' : '已取消点赞')
    
    // 更新当前预览壁纸的状态
    if (currentWallpaper.value && currentWallpaper.value.id === wallpaper.id) {
      currentWallpaper.value.isLiked = wallpaper.isLiked
    }
  } catch (error) {
    console.error('操作点赞失败:', error)
    ElMessage.error('操作点赞失败，请稍后重试')
  }
}

// 下载壁纸
const downloadWallpaper = (wallpaper: Wallpaper | null) => {
  if (!wallpaper) return
  
  downloading.value = true
  
  // 创建一个新的a标签
  const link = document.createElement('a')
  link.href = wallpaper.imageUrl
  link.target = '_blank'
  // 提取文件名
  const filename = wallpaper.imageUrl.substring(wallpaper.imageUrl.lastIndexOf('/') + 1)
  link.download = `${wallpaper.title || 'wallpaper'}_${filename}`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  setTimeout(() => {
    downloading.value = false
    ElMessage.success('壁纸已开始下载')
  }, 1000)
}

// 预览壁纸
const previewWallpaper = async (wallpaper: Wallpaper) => {
  try {
    // 获取壁纸详情
    const response = await getWallpaperDetail(wallpaper.id)
    if (response.code === 200) {
      currentWallpaper.value = {
        ...response.data,
        isFavorite: wallpaper.isFavorite,
        isLiked: wallpaper.isLiked
      }
      previewDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取壁纸详情失败:', error)
    // 如果获取详情失败，使用列表中的数据
    currentWallpaper.value = wallpaper
    previewDialogVisible.value = true
  }
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  fetchWallpapers()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchWallpapers()
}

// 分类变更
const handleCategoryChange = () => {
  currentPage.value = 1
  fetchWallpapers()
}

// 格式化日期
const formatDate = (dateString?: string) => {
  if (!dateString) return '未知时间'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.showcase-container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
}

.showcase-filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.filter-title {
  display: flex;
  align-items: center;
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.filter-title .el-icon {
  margin-right: 10px;
  font-size: 1.8rem;
  color: #409EFF;
}

.filter-controls {
  display: flex;
  gap: 15px;
}

.search-input {
  width: 250px;
}

.category-select {
  width: 180px;
}

.category-tags {
  margin-bottom: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 10px;
}

.tag-list {
  display: flex;
  gap: 10px;
  padding: 5px;
}

.category-tag {
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.category-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.wallpaper-grid {
  margin-bottom: 30px;
  min-height: 200px;
}

.masonry-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  grid-gap: 20px;
}

.wallpaper-card {
  break-inside: avoid;
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: white;
}

.wallpaper-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.card-content {
  position: relative;
}

.image-container {
  position: relative;
  overflow: hidden;
  aspect-ratio: 16 / 9;
}

.wallpaper-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.wallpaper-card:hover .wallpaper-image {
  transform: scale(1.05);
}

.wallpaper-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0) 50%, rgba(0, 0, 0, 0.7) 100%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 15px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.wallpaper-card:hover .wallpaper-overlay {
  opacity: 1;
}

.wallpaper-title {
  color: white;
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
}

.wallpaper-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.wallpaper-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: white;
  border-top: 1px solid #f0f0f0;
}

.category-info {
  display: flex;
  align-items: center;
}

.stats {
  display: flex;
  gap: 15px;
  color: #666;
  font-size: 0.9rem;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

.no-wallpapers {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

/* 预览对话框样式 */
.wallpaper-preview-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.preview-content {
  display: flex;
  flex-direction: column;
}

@media (min-width: 768px) {
  .preview-content {
    flex-direction: row;
  }
}

.preview-image-container {
  flex: 2;
  background-color: #f5f5f5;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 350px;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.preview-info {
  flex: 1;
  padding: 20px;
  max-width: 100%;
  overflow-y: auto;
  background-color: white;
}

@media (min-width: 768px) {
  .preview-info {
    max-height: 70vh;
    max-width: 400px;
  }
}

.preview-info h3 {
  margin-top: 0;
  font-size: 1.5rem;
  color: #333;
}

.description {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
}

.info-section {
  margin-bottom: 20px;
}

.info-section h4 {
  margin-bottom: 10px;
  color: #333;
  font-size: 1.1rem;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: default;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .showcase-filter-bar {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .filter-controls {
    width: 100%;
  }
  
  .search-input,
  .category-select {
    width: 100%;
  }
  
  .masonry-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons button {
    width: 100%;
  }
}
</style> 