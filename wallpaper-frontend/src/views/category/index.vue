<template>
  <div class="category-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加分类
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="categoryList"
      style="width: 100%"
      border
    >
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="分类名称">
        <template #default="{ row }">
          {{ row.name || row.categoryName }}
        </template>
      </el-table-column>
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
  getCategoryList, 
  addCategory, 
  updateCategory, 
  deleteCategory 
} from '@/api/category'
import type { Category, PaginationParams } from '@/types'

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
const categoryList = ref<Category[]>([])

// 对话框状态
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const form = reactive<Partial<Category>>({
  name: '',
  categoryName: '',
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' }
  ]
}

// 初始化
onMounted(() => {
  fetchCategories()
})

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true;
  try {
    console.log('开始获取分类列表...');
    const res = await getCategoryList(pagination);
    console.log('获取到分类数据:', res);
    
    // 处理不同的数据格式
    if (res.data && res.data.list && Array.isArray(res.data.list)) {
      categoryList.value = res.data.list;
      total.value = res.data.total || res.data.list.length;
      console.log('成功加载分类列表:', categoryList.value);
    } else if (res.data && res.data.records && Array.isArray(res.data.records)) {
      categoryList.value = res.data.records;
      total.value = res.data.total || res.data.records.length;
      console.log('成功加载分类列表(records格式):', categoryList.value);
    } else if (res.data && Array.isArray(res.data)) {
      // 如果返回的不是分页格式而是直接的数组
      categoryList.value = res.data;
      total.value = res.data.length;
      console.log('成功加载分类列表(直接数组格式):', categoryList.value);
    } else {
      console.error('分类数据格式异常:', res);
      ElMessage.warning('分类数据格式异常，请联系管理员');
      categoryList.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取分类列表失败:', error);
    ElMessage.error('获取分类列表失败，请检查网络连接');
    categoryList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

// 添加分类
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(form, {
    id: undefined,
    name: '',
    categoryName: '',
    description: ''
  })
}

// 编辑分类
const handleEdit = (row: Category) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  
  Object.assign(form, {
    id: row.id,
    name: row.name || row.categoryName,
    categoryName: row.categoryName || row.name,
    description: row.description
  })
}

// 删除分类
const handleDelete = (row: Category) => {
  ElMessageBox.confirm(
    `确认要删除分类 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteCategory(row.id)
      ElMessage.success('删除成功')
      fetchCategories()
    } catch (error) {
      console.error('删除分类失败:', error)
      ElMessage.error('删除分类失败')
    }
  }).catch(() => {
    // 用户取消删除，不做处理
  })
}

// 分页变化
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  fetchCategories()
}

const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  fetchCategories()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      // 确保两个字段都有值
      if (form.name && !form.categoryName) {
        form.categoryName = form.name;
      } else if (form.categoryName && !form.name) {
        form.name = form.categoryName;
      }
      
      if (dialogType.value === 'add') {
        await addCategory(form)
        ElMessage.success('添加成功')
      } else {
        await updateCategory(form)
        ElMessage.success('更新成功')
      }
      
      dialogVisible.value = false
      fetchCategories()
    } catch (error) {
      console.error('保存分类失败:', error)
      ElMessage.error('保存分类失败')
    } finally {
      submitLoading.value = false
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 