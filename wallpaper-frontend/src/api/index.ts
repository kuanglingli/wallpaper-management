import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const instance: AxiosInstance = axios.create({
  baseURL: (import.meta as any).env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求队列，用于存储401状态下的请求
let requestQueue: Array<{ config: AxiosRequestConfig; resolve: Function; reject: Function }> = []

// 是否正在刷新token
let isRefreshing = false

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么，比如获取token
    const token = localStorage.getItem('token')
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 重定向到登录页的函数
const redirectToLogin = () => {
  // 清除认证相关信息
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('refreshToken')
  // 记录当前路由，以便登录后返回
  const currentPath = router.currentRoute.value.fullPath
  const loginPath = `/login${currentPath !== '/login' ? `?redirect=${encodeURIComponent(currentPath)}` : ''}`
  
  // 避免重复跳转到登录页
  if (window.location.pathname !== '/login') {
    router.replace(loginPath)
  }
}

// 刷新token的函数
const refreshToken = async () => {
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) {
    return Promise.reject(new Error('没有刷新令牌'))
  }
  
  try {
    const response = await axios.post('/api/user/refresh-token', { refreshToken })
    if (response.data.code === 200) {
      const { token, refreshToken: newRefreshToken } = response.data.data
      localStorage.setItem('token', token)
      localStorage.setItem('refreshToken', newRefreshToken)
      return token
    } else {
      throw new Error(response.data.message || '刷新令牌失败')
    }
  } catch (error) {
    console.error('刷新token失败:', error)
    return Promise.reject(error)
  }
}

// 处理认证失败
const handleAuthFailure = async (config: AxiosRequestConfig) => {
  if (!isRefreshing) {
    isRefreshing = true
    
    try {
      // 尝试刷新token
      const newToken = await refreshToken()
      
      // 更新当前请求的token
      if (config.headers) {
        config.headers.Authorization = `Bearer ${newToken}`
      }
      
      // 恢复队列中的请求
      requestQueue.forEach(({ config, resolve }) => {
        if (config.headers) {
          config.headers.Authorization = `Bearer ${newToken}`
        }
        resolve(instance(config))
      })
      
      // 清空队列
      requestQueue = []
      return instance(config)
    } catch (error) {
      console.error('Token刷新失败，需要重新登录:', error)
      ElMessage.error('登录已过期，请重新登录')
      redirectToLogin()
      requestQueue.forEach(({ reject }) => {
        reject(error)
      })
      requestQueue = []
      return Promise.reject(error)
    } finally {
      isRefreshing = false
    }
  } else {
    // 正在刷新token，将请求加入队列
    return new Promise((resolve, reject) => {
      requestQueue.push({ config, resolve, reject })
    })
  }
}

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    const res = response.data
    
    // 如果是退出登录接口，直接返回结果，不进行统一错误处理
    if (response.config.url?.includes('/user/logout')) {
      return res
    }
    
    if (res.code !== 200) {
      // 处理各种错误情况，如token过期等
      
      // 处理401未授权的情况
      if (res.code === 401) {
        return handleAuthFailure(response.config)
      } else {
        // 其他错误状态码，显示错误信息
        ElMessage.error(res.message || '请求失败')
      }
      
      return Promise.reject(new Error(res.message || '未知错误'))
    }
    return res
  },
  (error) => {
    // 对响应错误做点什么
    
    // 如果是退出登录请求发生错误，直接返回错误，允许上层代码处理
    if (error.config?.url?.includes('/user/logout')) {
      return Promise.reject(error)
    }
    
    if (error.response) {
      const { status } = error.response
      
      // 处理401未授权的情况
      if (status === 401) {
        return handleAuthFailure(error.config)
      } else if (status === 403) {
        ElMessage.error('没有权限访问')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器内部错误')
      } else {
        ElMessage.error('请求失败，请稍后重试')
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      // 请求配置出错
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default instance 