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
        
        // 调试输出完整响应
        console.log('完整登录响应:', JSON.stringify(res))
        
        // 尝试检查不同位置的token
        console.log('res.token:', res.token)
        console.log('res.data?.token:', res.data?.token)
        
        let token = null
        let userInfo = null
        
        // 检查token可能存在的位置
        if (res.token) {
          // token直接在响应根级别
          token = res.token
          userInfo = res.userInfo
          console.log('找到根级别的token')
        } else if (res.data && res.data.token) {
          // token在data字段中
          token = res.data.token
          userInfo = res.data.userInfo
          console.log('找到data中的token')
        } else if (typeof res === 'string') {
          // 如果响应直接是字符串token
          token = res
          console.log('响应直接是字符串token')
        }
        
        if (!token) {
          console.error('无法从响应中提取token:', res)
          ElMessage.error('登录响应中缺少token，请检查后端返回')
          return
        }
        
        console.log('最终提取的token:', token)
        
        // 确保token不是"undefined"字符串
        if (token === 'undefined' || token === undefined) {
          console.error('提取的token无效:', token)
          ElMessage.error('获取到的token无效')
          return
        }
        
        // 保存token到本地存储并立即验证
        localStorage.setItem('token', token)
        const storedToken = localStorage.getItem('token')
        console.log('存储后立即读取的token:', storedToken)
        
        // 保存用户信息
        if (userInfo) {
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
        }
        
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