<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">壁纸管理系统</h2>
      </template>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="register-link">
          <el-link type="primary" @click="goToRegister">没有账号？点击注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/user'
import type { LoginParams } from '@/types'

const router = useRouter()
const route = useRoute()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive<LoginParams>({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        // 保存token到本地存储
        localStorage.setItem('token', res.data.token)
        // 保存用户信息
        localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
        
        ElMessage.success('登录成功')
        
        // 如果有重定向参数，跳转到指定页面，否则跳转到首页
        const redirect = route.query.redirect as string
        router.push(redirect || '/home')
      } catch (error: any) {
        console.error('登录失败:', error)
        ElMessage.error(error.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
}

.login-title {
  text-align: center;
  margin: 0;
  color: #303133;
}

.login-button {
  width: 100%;
}

.register-link {
  text-align: center;
  margin-top: 15px;
}
</style> 