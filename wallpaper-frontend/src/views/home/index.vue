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
import { getHotTags, getAllTags } from '@/api/tag'
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
  loading.statistics = true;
  try {
    // 使用壁纸分页接口、分类列表接口和标签列表接口获取统计数据
    const promises = [
      getWallpaperList({ pageNum: 1, pageSize: 1 }),
      getAllCategories(),
      getAllTags()
    ];
    
    console.log('开始获取统计数据');
    const [wallpaperRes, categoryRes, tagRes] = await Promise.all(promises);
    console.log('获取到壁纸数据:', wallpaperRes);
    console.log('获取到分类数据:', categoryRes);
    console.log('获取到标签数据:', tagRes);
    
    // 注意: 处理可能的数据结构不匹配
    // 壁纸总数
    if (wallpaperRes?.data?.total !== undefined) {
      statistics.totalWallpapers = wallpaperRes.data.total;
    } else if (wallpaperRes?.data?.records && Array.isArray(wallpaperRes.data.records)) {
      statistics.totalWallpapers = wallpaperRes.data.records.length;
    } else {
      statistics.totalWallpapers = 0;
    }
    
    // 分类总数 - 从getAllCategories返回的数组
    if (categoryRes?.data && Array.isArray(categoryRes.data)) {
      statistics.totalCategories = categoryRes.data.length;
    } else {
      statistics.totalCategories = 0;
    }
    
    // 标签总数 - 从getAllTags返回的数组
    if (tagRes?.data && Array.isArray(tagRes.data)) {
      statistics.totalTags = tagRes.data.length;
    } else {
      statistics.totalTags = 0;
    }
    
    // 计算今日上传，尝试使用分页接口获取最新壁纸
    try {
      // 使用page接口而不是latest
      const latestRes = await getWallpaperList({ 
        pageNum: 1, 
        pageSize: 20,
        sort: 'createTime,desc' // 按创建时间降序排序
      });
      console.log('获取到最新壁纸:', latestRes);
      
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      
      // 从分页结果中获取壁纸列表
      let wallpapers = [];
      if (latestRes?.data?.list && Array.isArray(latestRes.data.list)) {
        wallpapers = latestRes.data.list;
      } else if (latestRes?.data?.records && Array.isArray(latestRes.data.records)) {
        wallpapers = latestRes.data.records;
      }
      
      // 计算今日上传数量
      statistics.todayUploads = wallpapers.filter((item: Wallpaper) => {
        if (!item.createTime) return false;
        try {
          const uploadDate = new Date(item.createTime);
          return uploadDate >= today;
        } catch (e) {
          console.error('日期解析错误:', e);
          return false;
        }
      }).length;
    } catch (error) {
      console.error('获取最新壁纸失败:', error);
      statistics.todayUploads = 0;
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
    ElMessage.error('获取统计数据失败');
  } finally {
    loading.statistics = false;
  }
};

// 获取最近上传
const fetchRecentUploads = async () => {
  loading.recentUploads = true;
  try {
    console.log('开始获取最新壁纸');
    // 使用分页接口获取最新壁纸，而不是专门的latest接口
    const res = await getWallpaperList({ 
      pageNum: 1, 
      pageSize: 5,
      sort: 'createTime,desc' // 按创建时间降序排序
    });
    console.log('获取到最新壁纸:', res);
    
    // 兼容不同的返回数据结构
    if (res?.data?.list && Array.isArray(res.data.list)) {
      recentUploads.value = res.data.list;
    } else if (res?.data?.records && Array.isArray(res.data.records)) {
      recentUploads.value = res.data.records;
    } else {
      console.warn('最新壁纸数据格式不正确:', res);
      recentUploads.value = [];
    }
  } catch (error) {
    console.error('获取最近上传失败:', error);
    ElMessage.error('获取最近上传失败');
    recentUploads.value = [];
  } finally {
    loading.recentUploads = false;
  }
};

// 获取热门标签
const fetchHotTags = async () => {
  loading.hotTags = true;
  try {
    console.log('开始获取热门标签');
    // 使用getHotTags API获取热门标签
    const res = await getHotTags(10);
    console.log('获取到热门标签:', res);
    
    // 从返回的数据中获取标签
    if (res?.data && Array.isArray(res.data)) {
      hotTags.value = res.data;
    } else {
      console.warn('热门标签数据格式不正确:', res);
      hotTags.value = [];
    }
  } catch (error) {
    console.error('获取热门标签失败:', error);
    ElMessage.error('获取热门标签失败');
    hotTags.value = [];
  } finally {
    loading.hotTags = false;
  }
};

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