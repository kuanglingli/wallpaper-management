<template>
  <div class="category-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加分类
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="categories"
      style="width: 100%"
      border
    >
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="wallpaperCount" label="壁纸数量" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加分类' : '编辑分类'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入描述"
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

// 表格数据
const loading = ref(false)
const categories = ref([
  {
    id: 1,
    name: '风景',
    description: '自然风景壁纸',
    wallpaperCount: 100,
    createTime: '2024-03-25 10:00:00'
  },
  {
    id: 2,
    name: '动漫',
    description: '动漫壁纸',
    wallpaperCount: 80,
    createTime: '2024-03-25 09:30:00'
  }
])

// 对话框
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const form = reactive({
  name: '',
  description: ''
})

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' }
  ]
}

// 方法
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  form.name = ''
  form.description = ''
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

const handleDelete = (row: any) => {
  // TODO: 实现删除逻辑
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      // TODO: 实现提交逻辑
      dialogVisible.value = false
    }
  })
}
</script>

<style scoped>
.category-container {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style> 