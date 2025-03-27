import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么，比如获取token
    const token = localStorage.getItem('token')
    console.log('请求拦截器中的token:', token)
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('添加到请求头的Authorization:', `Bearer ${token}`)
    } else {
      console.warn('token为空，未添加Authorization请求头')
    }
    return config
  },
  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 是否正在刷新的标记
let isRefreshing = false
// 重定向到登录页的函数
const redirectToLogin = () => {
  // 清除认证相关信息
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  // 记录当前路由，以便登录后返回
  const currentPath = router.currentRoute.value.fullPath
  const loginPath = `/login${currentPath !== '/login' ? `?redirect=${encodeURIComponent(currentPath)}` : ''}`
  
  // 避免重复跳转到登录页
  if (window.location.pathname !== '/login') {
    console.log('重定向到登录页')
    router.replace(loginPath)
  }
}

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    const res = response.data
    console.log('响应拦截器收到响应:', res)
    
    if (res.code !== 200) {
      // 处理各种错误情况，如token过期等
      console.error('响应状态码错误:', res.code, res.message)
      
      // 处理401未授权的情况
      if (res.code === 401) {
        // 避免多个请求同时触发重定向
        if (!isRefreshing) {
          isRefreshing = true
          
          ElMessage.error('登录已过期，请重新登录')
          // 重定向到登录页
          redirectToLogin()
          
          // 重置标记
          setTimeout(() => {
            isRefreshing = false
          }, 1000)
        }
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
    console.error('请求发生错误:', error)
    
    if (error.response) {
      const { status } = error.response
      
      // 处理401未授权的情况
      if (status === 401) {
        // 避免多个请求同时触发重定向
        if (!isRefreshing) {
          isRefreshing = true
          
          ElMessage.error('登录已过期，请重新登录')
          // 重定向到登录页
          redirectToLogin()
          
          // 重置标记
          setTimeout(() => {
            isRefreshing = false
          }, 1000)
        }
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