import { createRouter, createWebHistory } from 'vue-router'

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
        }
      ]
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

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 壁纸管理系统` : '壁纸管理系统'
  
  // 检查该路由是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 获取token
    const token = localStorage.getItem('token')
    if (!token) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 将要访问的路径作为参数，以便登录成功后跳转
      })
    } else {
      next() // 已登录，正常跳转
    }
  } else {
    next() // 不需要登录权限的页面，直接跳转
  }
})

export default router 