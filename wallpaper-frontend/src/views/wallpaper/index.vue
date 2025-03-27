<template>
  <div class="wallpaper-container">
    <div class="toolbar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入壁纸标题" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select style="width: 200px;" v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name || item.categoryName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select style="width: 200px;"
            v-model="searchForm.tagIds"
            placeholder="请选择标签"
            multiple
            clearable
            collapse-tags
          >
            <el-option
              v-for="item in tags"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetForm">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>上传壁纸
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="wallpaperList"
      style="width: 100%"
      border
    >
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
      <el-table-column label="标签" width="200">
        <template #default="{ row }">
          <el-tag v-for="tag in row.tags" :key="tag.id" class="tag-item">
            {{ tag.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="上传时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="success" link @click="handlePreview(row)">
            预览
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 上传/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '上传壁纸' : '编辑壁纸'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="壁纸图片" prop="imageUrl">
          <el-upload
            v-if="dialogType === 'add' || !form.imageUrl"
            class="wallpaper-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
          >
            <el-icon v-if="!uploadImageUrl" class="uploader-icon"><Plus /></el-icon>
            <img v-else :src="uploadImageUrl" class="upload-image" />
          </el-upload>
          <div v-else class="existing-image">
            <el-image :src="form.thumbnailUrl" fit="cover" class="upload-image" />
            <el-button type="danger" size="small" @click="handleReupload">重新上传</el-button>
          </div>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入壁纸标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入壁纸描述"
          />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name || item.categoryName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tagIds">
          <el-select
            v-model="form.tagIds"
            placeholder="请选择标签"
            multiple
            collapse-tags
          >
            <el-option
              v-for="item in tags"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="壁纸预览"
      width="800px"
      class="preview-dialog"
    >
      <div class="preview-container">
        <el-image :src="previewImageUrl" fit="contain" class="preview-image" />
      </div>
      <div class="preview-info">
        <h3>{{ previewWallpaper?.title }}</h3>
        <p>分类：{{ previewWallpaper?.categoryName }}</p>
        <p>标签：
          <el-tag
            v-for="tag in previewWallpaper?.tags"
            :key="tag.id"
            class="tag-item"
          >
            {{ tag.name }}
          </el-tag>
        </p>
        <p>描述：{{ previewWallpaper?.description }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { 
  getWallpaperList, 
  addWallpaper, 
  updateWallpaper, 
  deleteWallpaper,
  uploadWallpaperImage
} from '@/api/wallpaper'
import { getAllCategories } from '@/api/category'
import { getAllTags } from '@/api/tag'
import type { Wallpaper, Category, Tag, WallpaperQueryParams } from '@/types'

// 表单引用
const formRef = ref<FormInstance>()

// 分页和搜索
const searchForm = reactive<{
  title?: string
  categoryId?: number
  tagIds?: number[]
}>({
  title: undefined,
  categoryId: undefined,
  tagIds: []
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

// 状态
const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const wallpaperList = ref<Wallpaper[]>([])
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])

// 对话框状态
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const form = reactive<Partial<Wallpaper>>({
  title: '',
  description: '',
  imageUrl: '',
  thumbnailUrl: '',
  categoryId: undefined,
  tagIds: []
})

// 上传状态
const uploadImageUrl = ref('')
const uploadFile = ref<File | null>(null)

// 预览状态
const previewVisible = ref(false)
const previewWallpaper = ref<Wallpaper | null>(null)
const previewImageUrl = computed(() => previewWallpaper.value?.imageUrl || '')

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入壁纸标题', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  imageUrl: [
    { 
      required: true, 
      message: '请上传壁纸图片', 
      trigger: 'change',
      validator: (rule: any, value: string, callback: Function) => {
        // 如果是编辑模式且已有图片，或者有上传的新图片
        if ((dialogType.value === 'edit' && value) || uploadFile.value) {
          callback(); // 验证通过
        } else {
          callback(new Error('请上传壁纸图片'));
        }
      }
    }
  ]
}

// 初始化
onMounted(async () => {
  fetchCategoriesAndTags()
  fetchWallpapers()
})

// 获取分类和标签
const fetchCategoriesAndTags = async () => {
  try {
    console.log('开始获取分类和标签数据');
    
    // 分别获取分类和标签数据，避免一个失败影响另一个
    console.log('开始获取分类数据...');
    try {
      const categoryRes = await getAllCategories();
      console.log('分类数据获取成功:', categoryRes);
      
      // 确保分类数据正确填充
      if (categoryRes?.data && Array.isArray(categoryRes.data)) {
        console.log('分类数据是数组，长度:', categoryRes.data.length);
        categories.value = categoryRes.data;
        
        // 检查分类数据内容
        if (categories.value.length > 0) {
          console.log('分类数据示例:', categories.value[0]);
        }
      } else {
        console.warn('分类数据结构不符合预期:', categoryRes);
        categories.value = [];
      }
    } catch (categoryError) {
      console.error('获取分类数据失败:', categoryError);
      categories.value = [];
    }
    
    console.log('开始获取标签数据...');
    try {
      const tagRes = await getAllTags();
      console.log('标签数据获取成功:', tagRes);
      
      // 确保标签数据正确填充
      if (tagRes?.data && Array.isArray(tagRes.data)) {
        console.log('标签数据是数组，长度:', tagRes.data.length);
        tags.value = tagRes.data;
        
        // 检查标签数据内容
        if (tags.value.length > 0) {
          console.log('标签数据示例:', tags.value[0]);
        }
      } else {
        console.warn('标签数据结构不符合预期:', tagRes);
        tags.value = [];
      }
    } catch (tagError) {
      console.error('获取标签数据失败:', tagError);
      tags.value = [];
    }
    
    console.log('分类和标签数据处理完成');
    console.log('分类数量:', categories.value.length);
    console.log('标签数量:', tags.value.length);
  } catch (error) {
    console.error('获取分类或标签总体失败:', error);
    ElMessage.error('获取分类或标签失败，请检查网络连接');
    // 设置为空数组以避免页面错误
    categories.value = [];
    tags.value = [];
  }
}

// 获取壁纸列表
const fetchWallpapers = async () => {
  loading.value = true
  try {
    const params: WallpaperQueryParams = {
      ...pagination,
      ...searchForm
    }
    const res = await getWallpaperList(params)
    wallpaperList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取壁纸列表失败:', error)
    ElMessage.error('获取壁纸列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchWallpapers()
}

// 重置表单
const resetForm = () => {
  searchForm.title = undefined
  searchForm.categoryId = undefined
  searchForm.tagIds = []
  handleSearch()
}

// 添加壁纸
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  
  // 确保清空所有图片相关状态
  uploadImageUrl.value = ''
  uploadFile.value = null
  
  // 重置表单数据
  Object.assign(form, {
    id: undefined,
    title: '',
    description: '',
    imageUrl: '',
    thumbnailUrl: '',
    categoryId: undefined,
    tagIds: []
  })
  
  // 延迟一下再重置表单验证状态
  setTimeout(() => {
    if (formRef.value) {
      formRef.value.resetFields()
    }
  }, 0)
}

// 编辑壁纸
const handleEdit = (row: Wallpaper) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  uploadImageUrl.value = ''
  uploadFile.value = null
  
  Object.assign(form, {
    id: row.id,
    title: row.title,
    description: row.description,
    imageUrl: row.imageUrl,
    thumbnailUrl: row.thumbnailUrl,
    categoryId: row.categoryId,
    tagIds: row.tags?.map(tag => tag.id) || []
  })
}

// 预览壁纸
const handlePreview = (row: Wallpaper) => {
  previewWallpaper.value = row
  previewVisible.value = true
}

// 删除壁纸
const handleDelete = (row: Wallpaper) => {
  ElMessageBox.confirm(
    `确认要删除壁纸 "${row.title}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteWallpaper(row.id)
      ElMessage.success('删除成功')
      fetchWallpapers()
    } catch (error) {
      console.error('删除壁纸失败:', error)
      ElMessage.error('删除壁纸失败')
    }
  }).catch(() => {
    // 用户取消删除，不做处理
  })
}

// 分页变化
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  fetchWallpapers()
}

const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  fetchWallpapers()
}

// 文件上传处理
const handleFileChange = (file: any) => {
  const isImage = file.raw.type.startsWith('image/')
  const isLt10M = file.raw.size / 1024 / 1024 < 10
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return
  }
  
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return
  }
  
  uploadImageUrl.value = URL.createObjectURL(file.raw)
  uploadFile.value = file.raw
  
  // 设置临时URL到表单的imageUrl字段，通过验证
  // 实际URL会在提交时由uploadWallpaperImage替换
  form.imageUrl = uploadImageUrl.value
}

// 重新上传
const handleReupload = () => {
  form.imageUrl = ''
  form.thumbnailUrl = ''
  uploadImageUrl.value = ''
  uploadFile.value = null
  
  // 如果在编辑模式下重新上传，需要手动触发表单验证
  if (dialogType.value === 'edit' && formRef.value) {
    formRef.value.validateField('imageUrl')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 再次检查是否有图片上传
  if (dialogType.value === 'add' && !uploadFile.value) {
    ElMessage.warning('请上传壁纸图片')
    return
  }
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      // 如果有上传新文件，先上传图片
      if (uploadFile.value) {
        console.log('开始上传图片文件...')
        const uploadRes = await uploadWallpaperImage(uploadFile.value)
        console.log('图片上传成功:', uploadRes)
        form.imageUrl = uploadRes.data.url
        form.thumbnailUrl = uploadRes.data.thumbnailUrl
      } else if (dialogType.value === 'add') {
        // 如果是添加模式且没有上传文件，停止提交
        ElMessage.warning('请上传壁纸图片')
        submitLoading.value = false
        return
      }
      
      console.log('保存壁纸信息:', form)
      
      // 保存壁纸信息
      if (dialogType.value === 'add') {
        await addWallpaper(form)
        ElMessage.success('添加成功')
      } else {
        await updateWallpaper(form)
        ElMessage.success('更新成功')
      }
      
      dialogVisible.value = false
      fetchWallpapers()
    } catch (error) {
      console.error('保存壁纸失败:', error)
      ElMessage.error('保存壁纸失败')
    } finally {
      submitLoading.value = false
    }
  })
}
</script>

<style scoped>
.wallpaper-container {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-form {
  flex: 1;
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.wallpaper-uploader {
  width: 150px;
  height: 150px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

.upload-image {
  width: 150px;
  height: 150px;
  display: block;
  object-fit: cover;
}

.existing-image {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  width: 150px;
}

.preview-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 500px;
}

.preview-info {
  padding: 0 20px;
}
</style> 