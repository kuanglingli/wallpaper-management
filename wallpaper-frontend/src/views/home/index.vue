<template>
  <div class="home-container">
    <!-- 统计卡片 -->
    <div class="statistics">
      <el-card class="statistic-card">
        <template #header>
          <div class="card-header">
            <span>壁纸总数</span>
          </div>
        </template>
        <div class="card-content">
          <div class="statistic-value">{{ statistics.totalWallpapers }}</div>
          <div class="statistic-text">张壁纸</div>
        </div>
      </el-card>
      
      <el-card class="statistic-card">
        <template #header>
          <div class="card-header">
            <span>分类总数</span>
          </div>
        </template>
        <div class="card-content">
          <div class="statistic-value">{{ statistics.totalCategories }}</div>
          <div class="statistic-text">个分类</div>
        </div>
      </el-card>
      
      <el-card class="statistic-card">
        <template #header>
          <div class="card-header">
            <span>标签总数</span>
          </div>
        </template>
        <div class="card-content">
          <div class="statistic-value">{{ statistics.totalTags }}</div>
          <div class="statistic-text">个标签</div>
        </div>
      </el-card>
      
      <el-card class="statistic-card">
        <template #header>
          <div class="card-header">
            <span>今日上传</span>
          </div>
        </template>
        <div class="card-content">
          <div class="statistic-value">{{ statistics.todayUploads }}</div>
          <div class="statistic-text">张壁纸</div>
        </div>
      </el-card>
    </div>

    <!-- 最近上传 -->
    <el-card class="recent-uploads-card">
      <template #header>
        <div class="card-header">
          <span>最近上传</span>
          <el-button class="more-button" text @click="goToWallpaperPage">
            查看更多
          </el-button>
        </div>
      </template>
      <div v-loading="loading.recentUploads">
        <el-empty v-if="recentUploads.length === 0" description="暂无数据" />
        <el-table v-else :data="recentUploads" style="width: 100%">
          <el-table-column label="预览" width="100">
            <template #default="{ row }">
              <el-image
                class="thumbnail"
                :src="row.thumbnailUrl"
                :preview-src-list="[row.imageUrl]"
                fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" />
          <el-table-column label="分类" width="120">
            <template #default="{ row }">
              {{ row.categoryName }}
            </template>
          </el-table-column>
          <el-table-column label="标签" width="220">
            <template #default="{ row }">
              <el-tag
                v-for="tag in row.tags"
                :key="tag.id"
                class="tag-item"
              >
                {{ tag.name }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="上传时间" width="180" />
        </el-table>
      </div>
    </el-card>

    <!-- 热门标签 -->
    <el-card class="hot-tags-card">
      <template #header>
        <div class="card-header">
          <span>热门标签</span>
          <el-button class="more-button" text @click="goToTagPage">
            查看更多
          </el-button>
        </div>
      </template>
      <div v-loading="loading.hotTags">
        <el-empty v-if="hotTags.length === 0" description="暂无数据" />
        <div v-else class="hot-tags">
          <el-tag
            v-for="tag in hotTags"
            :key="tag.id"
            class="hot-tag-item"
            :type="getRandomTagType()"
          >
            {{ tag.name }} ({{ tag.wallpaperCount }})
          </el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWallpaperList } from '@/api/wallpaper'
import { getAllCategories } from '@/api/category'
import { getHotTags } from '@/api/tag'
import type { Wallpaper, Tag } from '@/types'

const router = useRouter()

// 加载状态
const loading = reactive({
  recentUploads: false,
  hotTags: false,
  statistics: false
})

// 统计数据
const statistics = reactive({
  totalWallpapers: 0,
  totalCategories: 0,
  totalTags: 0,
  todayUploads: 0
})

// 最近上传
const recentUploads = ref<Wallpaper[]>([])

// 热门标签
const hotTags = ref<Tag[]>([])

// 初始化
onMounted(() => {
  fetchStatistics()
  fetchRecentUploads()
  fetchHotTags()
})

// 获取统计数据
const fetchStatistics = async () => {
  loading.statistics = true
  try {
    // 这里假设后端有一个统计接口，如果没有，可以分别调用各个接口获取数据
    const [wallpaperRes, categoryRes, tagRes] = await Promise.all([
      getWallpaperList({ pageNum: 1, pageSize: 1 }),
      getAllCategories(),
      getHotTags()
    ])
    
    statistics.totalWallpapers = wallpaperRes.data.total || 0
    statistics.totalCategories = categoryRes.data.length || 0
    statistics.totalTags = tagRes.data.length || 0
    
    // 计算今日上传，实际项目中应该由后端提供
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    
    if (wallpaperRes.data.list && wallpaperRes.data.list.length > 0) {
      statistics.todayUploads = wallpaperRes.data.list.filter((item: Wallpaper) => {
        const uploadDate = new Date(item.createTime)
        return uploadDate >= today
      }).length
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.statistics = false
  }
}

// 获取最近上传
const fetchRecentUploads = async () => {
  loading.recentUploads = true
  try {
    const res = await getWallpaperList({
      pageNum: 1,
      pageSize: 5
    })
    recentUploads.value = res.data.list
  } catch (error) {
    console.error('获取最近上传失败:', error)
    ElMessage.error('获取最近上传失败')
  } finally {
    loading.recentUploads = false
  }
}

// 获取热门标签
const fetchHotTags = async () => {
  loading.hotTags = true
  try {
    const res = await getHotTags(10)
    hotTags.value = res.data
  } catch (error) {
    console.error('获取热门标签失败:', error)
    ElMessage.error('获取热门标签失败')
  } finally {
    loading.hotTags = false
  }
}

// 随机获取标签类型
const getRandomTagType = () => {
  const types = ['', 'success', 'warning', 'info', 'danger']
  return types[Math.floor(Math.random() * types.length)]
}

// 页面跳转
const goToWallpaperPage = () => {
  router.push('/wallpaper')
}

const goToTagPage = () => {
  router.push('/tag')
}
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.statistics {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.statistic-card {
  flex: 1;
  min-width: 200px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
}

.statistic-value {
  font-size: 32px;
  font-weight: bold;
  color: var(--el-color-primary);
}

.statistic-text {
  margin-top: 10px;
  color: #909399;
}

.more-button {
  padding: 0;
  margin: 0;
}

.recent-uploads-card {
  margin-bottom: 20px;
}

.thumbnail {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.tag-item {
  margin-right: 5px;
  margin-bottom: 5px;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tag-item {
  font-size: 14px;
  padding: 10px 15px;
}
</style> 