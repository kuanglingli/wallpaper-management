import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'

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

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    const res = response.data
    console.log('响应拦截器收到响应:', res)
    
    if (res.code !== 200) {
      // 处理各种错误情况，如token过期等
      console.error('响应状态码错误:', res.code, res.message)
      if (res.code === 401) {
        // token过期处理
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || '未知错误'))
    }
    return res
  },
  (error) => {
    // 对响应错误做点什么
    console.error('请求发生错误:', error)
    return Promise.reject(error)
  }
)

export default instance 