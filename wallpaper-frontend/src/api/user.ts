import request from './index'
import type { ApiResponse, LoginParams, LoginResult, UserInfo } from '../types'

/**
 * 用户登录
 */
export function login(data: LoginParams) {
  console.log('发送登录请求:', data)
  return request.post<any, any>('/user/login', data)
    .then((response: any) => {
      console.log('原始登录响应:', response)
      return response
    })
    .catch((error: any) => {
      console.error('登录请求错误:', error)
      throw error
    })
}

/**
 * 用户注册
 */
export function register(data: LoginParams) {
  return request.post<any, ApiResponse<any>>('/user/register', data)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request.get<any, ApiResponse<UserInfo>>('/user/info')
}

/**
 * 用户登出
 */
export function logout() {
  console.log('发送登出请求')
  return request.post<any, ApiResponse<any>>('/user/logout')
    .then((response: any) => {
      console.log('登出响应:', response)
      return response
    })
    .catch((error: any) => {
      console.error('登出请求错误:', error)
      throw error
    })
} 