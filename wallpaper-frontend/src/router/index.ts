import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layout/index.vue'),
      redirect: '/home',
      children: [
        {
          path: 'home',
          name: 'home',
          component: () => import('@/views/home/index.vue'),
          meta: {
            title: '首页',
            requiresAuth: true
          }
        },
        {
          path: 'wallpaper',
          name: 'wallpaper',
          component: () => import('@/views/wallpaper/index.vue'),
          meta: {
            title: '壁纸管理',
            requiresAuth: true
          }
        },
        {
          path: 'category',
          name: 'category',
          component: () => import('@/views/category/index.vue'),
          meta: {
            title: '分类管理',
            requiresAuth: true
          }
        },
        {
          path: 'tag',
          name: 'tag',
          component: () => import('@/views/tag/index.vue'),
          meta: {
            title: '标签管理',
            requiresAuth: true
          }
        },
        {
          path: 'wallpaper-showcase',
          name: 'wallpaperShowcase',
          component: () => import('@/views/showcase/index.vue'),
          meta: {
            title: '壁纸展示',
            requiresAuth: true // 设置为false，允许未登录用户访问
          }
        }
      ]
    },
    {
      path: '/wallpaper-showcase',
      name: 'wallpaperShowcase',
      component: () => import('@/views/showcase/index.vue'),
      meta: {
        title: '壁纸展示',
        requiresAuth: true // 设置为false，允许未登录用户访问
      }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/index.vue'),
      meta: {
        title: '登录'
      }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/login/register.vue'),
      meta: {
        title: '注册'
      }
    },
    // 将不匹配的路由重定向到登录页
    {
      path: '/:pathMatch(.*)*',
      redirect: '/login'
    }
  ]
})

// 检查token是否有效（简单检查格式和过期时间）
const isTokenValid = (token: string): boolean => {
  try {
    if (!token) return false
    
    // 简单格式检查（JWT通常由三部分组成，用.分隔）
    if (!token.includes('.') || token.split('.').length !== 3) {
      console.warn('Token格式不正确')
      return false
    }
    
    // 简单检查token是否为undefined字符串
    if (token === 'undefined' || token === 'null') {
      console.warn('Token为无效值:', token)
      return false
    }
    
    return true
  } catch (error) {
    console.error('Token验证出错:', error)
    return false
  }
}

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 壁纸管理系统` : '壁纸管理系统'
  
  // 检查该路由是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 获取token
    const token = localStorage.getItem('token')
    
    // 检查token是否存在且有效
    if (!token || !isTokenValid(token)) {
      console.warn('需要登录权限，但token不存在或无效:', token)
      
      // 清除可能存在的无效token
      if (token) {
        console.log('清除无效token')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
      
      // 未登录或token无效，跳转到登录页
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 将要访问的路径作为参数，以便登录成功后跳转
      })
    } else {
      // token存在且格式有效，继续导航
      console.log('token校验通过，允许访问:', to.path)
      next()
    }
  } else {
    // 对于登录页面，如果已经有token，直接跳转到首页
    if (to.path === '/login' || to.path === '/register') {
      const token = localStorage.getItem('token')
      if (token && isTokenValid(token)) {
        console.log('已登录状态，自动跳转到首页')
        next({ path: '/home' })
        return
      }
    }
    
    // 不需要登录权限的页面，直接跳转
    next()
  }
})

export default router 