<template>
  <div class="tag-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加标签
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="tagList"
      style="width: 100%"
      border
    >
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="name" label="标签名称" />
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

    <!-- 分页 -->
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加标签' : '编辑标签'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" />
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
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { 
  getTagList, 
  addTag, 
  updateTag, 
  deleteTag 
} from '@/api/tag'
import type { Tag, PaginationParams } from '@/types'

// 表单引用
const formRef = ref<FormInstance>()

// 分页
const pagination = reactive<PaginationParams>({
  pageNum: 1,
  pageSize: 10
})

// 状态
const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const tagList = ref<Tag[]>([])

// 对话框状态
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const form = reactive<Partial<Tag>>({
  name: '',
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' }
  ]
}

// 初始化
onMounted(() => {
  fetchTags()
})

// 获取标签列表
const fetchTags = async () => {
  loading.value = true
  try {
    const res = await getTagList(pagination)
    tagList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取标签列表失败:', error)
    ElMessage.error('获取标签列表失败')
  } finally {
    loading.value = false
  }
}

// 添加标签
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(form, {
    id: undefined,
    name: '',
    description: ''
  })
}

// 编辑标签
const handleEdit = (row: Tag) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  
  Object.assign(form, {
    id: row.id,
    name: row.name,
    description: row.description
  })
}

// 删除标签
const handleDelete = (row: Tag) => {
  ElMessageBox.confirm(
    `确认要删除标签 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteTag(row.id)
      ElMessage.success('删除成功')
      fetchTags()
    } catch (error) {
      console.error('删除标签失败:', error)
      ElMessage.error('删除标签失败')
    }
  }).catch(() => {
    // 用户取消删除，不做处理
  })
}

// 分页变化
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  fetchTags()
}

const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  fetchTags()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (dialogType.value === 'add') {
        await addTag(form)
        ElMessage.success('添加成功')
      } else {
        await updateTag(form)
        ElMessage.success('更新成功')
      }
      
      dialogVisible.value = false
      fetchTags()
    } catch (error) {
      console.error('保存标签失败:', error)
      ElMessage.error('保存标签失败')
    } finally {
      submitLoading.value = false
    }
  })
}
</script>

<style scoped>
.tag-container {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 