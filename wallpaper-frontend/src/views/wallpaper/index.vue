<template>
  <div class="wallpaper-container">
    <div class="toolbar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="searchForm.tags"
            multiple
            placeholder="请选择标签"
          >
            <el-option
              v-for="item in tags"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>上传壁纸
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="wallpapers"
      style="width: 100%"
      border
    >
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="预览图" width="120">
        <template #default="{ row }">
          <el-image
            :src="row.thumbnail"
            :preview-src-list="[row.url]"
            fit="cover"
            class="wallpaper-thumbnail"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column label="标签" width="200">
        <template #default="{ row }">
          <el-tag
            v-for="tag in row.tags"
            :key="tag"
            size="small"
            class="tag-item"
          >
            {{ tag }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="uploadTime" label="上传时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="primary" link @click="handlePreview(row)">
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
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
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
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="form.tags"
            multiple
            placeholder="请选择标签"
          >
            <el-option
              v-for="item in tags"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="壁纸" prop="file">
          <el-upload
            v-if="dialogType === 'add'"
            class="wallpaper-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <el-icon v-if="!form.url" class="wallpaper-uploader-icon">
              <Plus />
            </el-icon>
            <img v-else :src="form.url" class="wallpaper-preview" />
          </el-upload>
          <el-image
            v-else
            :src="form.url"
            class="wallpaper-preview"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  title: '',
  category: '',
  tags: []
})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const loading = ref(false)
const wallpapers = ref([])

// 分类和标签选项
const categories = ref([
  { value: '1', label: '风景' },
  { value: '2', label: '动漫' },
  { value: '3', label: '人物' }
])

const tags = ref([
  { value: '1', label: '自然' },
  { value: '2', label: '城市' },
  { value: '3', label: '动物' },
  { value: '4', label: '植物' }
])

// 对话框
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const form = reactive({
  title: '',
  category: '',
  tags: [],
  url: ''
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  tags: [
    { required: true, message: '请选择标签', trigger: 'change' }
  ],
  file: [
    { required: true, message: '请上传壁纸', trigger: 'change' }
  ]
}

// 方法
const handleSearch = () => {
  // TODO: 实现搜索逻辑
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.category = ''
  searchForm.tags = []
  handleSearch()
}

const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  form.title = ''
  form.category = ''
  form.tags = []
  form.url = ''
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

const handlePreview = (row: any) => {
  // TODO: 实现预览逻辑
}

const handleDelete = (row: any) => {
  // TODO: 实现删除逻辑
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  handleSearch()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  handleSearch()
}

const handleUploadSuccess = (response: any) => {
  form.url = response.url
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      // TODO: 实现提交逻辑
      dialogVisible.value = false
      handleSearch()
    }
  })
}
</script>

<style scoped>
.wallpaper-container {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wallpaper-thumbnail {
  width: 100px;
  height: 60px;
  border-radius: 4px;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.wallpaper-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 120px;
}

.wallpaper-uploader:hover {
  border-color: var(--el-color-primary);
}

.wallpaper-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.wallpaper-preview {
  width: 200px;
  height: 120px;
  object-fit: cover;
}
</style> 