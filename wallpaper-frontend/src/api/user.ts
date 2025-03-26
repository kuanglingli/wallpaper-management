import request from './index'
import type { ApiResponse, LoginParams, LoginResult, UserInfo } from '../types'

/**
 * 用户登录
 */
export function login(data: LoginParams) {
  return request.post<any, ApiResponse<LoginResult>>('/user/login', data)
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
  return request.post<any, ApiResponse<any>>('/user/logout')
} 