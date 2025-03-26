import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layout/index.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/home/index.vue'),
          meta: {
            title: '首页'
          }
        },
        {
          path: 'wallpaper',
          name: 'wallpaper',
          component: () => import('@/views/wallpaper/index.vue'),
          meta: {
            title: '壁纸管理'
          }
        },
        {
          path: 'category',
          name: 'category',
          component: () => import('@/views/category/index.vue'),
          meta: {
            title: '分类管理'
          }
        },
        {
          path: 'tag',
          name: 'tag',
          component: () => import('@/views/tag/index.vue'),
          meta: {
            title: '标签管理'
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
    }
  ]
})

export default router 