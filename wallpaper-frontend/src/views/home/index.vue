<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>壁纸总数</span>
              <el-icon><Picture /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.totalWallpapers }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>分类总数</span>
              <el-icon><Folder /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.totalCategories }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>标签总数</span>
              <el-icon><Collection /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.totalTags }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>今日上传</span>
              <el-icon><Upload /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.todayUploads }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近上传</span>
              <el-button type="primary" link>查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentUploads" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="uploadTime" label="上传时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>热门标签</span>
              <el-button type="primary" link>查看更多</el-button>
            </div>
          </template>
          <div class="tag-cloud">
            <el-tag
              v-for="tag in hotTags"
              :key="tag.name"
              :type="tag.type"
              class="tag-item"
              effect="plain"
            >
              {{ tag.name }}
              <span class="tag-count">({{ tag.count }})</span>
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Picture,
  Folder,
  Collection,
  Upload
} from '@element-plus/icons-vue'

const statistics = ref({
  totalWallpapers: 0,
  totalCategories: 0,
  totalTags: 0,
  todayUploads: 0
})

const recentUploads = ref([])
const hotTags = ref([])

onMounted(() => {
  // TODO: 获取统计数据
  statistics.value = {
    totalWallpapers: 100,
    totalCategories: 10,
    totalTags: 50,
    todayUploads: 5
  }

  // TODO: 获取最近上传
  recentUploads.value = [
    {
      title: '示例壁纸1',
      category: '风景',
      uploadTime: '2024-03-25 10:00:00'
    },
    {
      title: '示例壁纸2',
      category: '动漫',
      uploadTime: '2024-03-25 09:30:00'
    }
  ]

  // TODO: 获取热门标签
  hotTags.value = [
    { name: '风景', count: 100, type: 'success' },
    { name: '动漫', count: 80, type: 'warning' },
    { name: '人物', count: 60, type: 'danger' },
    { name: '动物', count: 40, type: 'info' }
  ]
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.stat-card {
  height: 120px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.number {
  font-size: 24px;
  font-weight: bold;
  color: var(--el-color-primary);
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  cursor: pointer;
}

.tag-count {
  margin-left: 4px;
  font-size: 12px;
  opacity: 0.8;
}
</style> 