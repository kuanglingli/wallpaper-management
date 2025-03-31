<template>
  <div class="layout-container">
    <el-container>
      <el-aside width="200px">
        <div class="logo">
          <h3>共享壁纸</h3>
        </div>
        <el-menu
          router
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/home">
            <el-icon><Monitor /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/wallpaper-showcase">
            <el-icon><PictureRounded /></el-icon>
            <span>壁纸展示</span>
          </el-menu-item>
          <el-menu-item index="/wallpaper">
            <el-icon><Picture /></el-icon>
            <span>壁纸管理</span>
          </el-menu-item>
          <el-menu-item index="/category">
            <el-icon><Folder /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/tag">
            <el-icon><Collection /></el-icon>
            <span>标签管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="username-dropdown">
                {{ userInfo.nickname || userInfo.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import { 
  Monitor, 
  Picture, 
  PictureRounded,
  Folder, 
  Collection, 
  ArrowDown 
} from '@element-plus/icons-vue';
import { logout } from '@/api/user';
import type { UserInfo } from '@/types';

const router = useRouter();
const route = useRoute();

const activeMenu = computed(() => {
  return route.path;
});

// 获取存储的用户信息
const userInfo = ref<UserInfo>({
  id: 0,
  username: '',
  nickname: '',
  roles: []
});

onMounted(() => {
  // 从本地存储获取用户信息
  const storedUserInfo = localStorage.getItem('userInfo');
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo);
  }
});

// 处理下拉菜单命令
const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm(
        '确定要退出登录吗？',
        '退出登录',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      );
      
      // 显示退出登录中的加载状态
      const loading = ElMessage.info({
        message: '正在退出登录...',
        duration: 0
      });
      
      try {
        // 调用退出接口
        const res = await logout();
        console.log('退出登录成功:', res);
        
        // 关闭加载提示
        loading.close();
        
        // 清除本地存储的用户信息和token
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        
        // 显示退出成功提示
        ElMessage.success('已退出登录');
        
        // 跳转到登录页
        router.push('/login');
      } catch (error) {
        console.error('退出登录失败:', error);
        
        // 关闭加载提示
        loading.close();
        
        // 即使API调用失败，也清除本地存储并跳转到登录页
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        
        ElMessage.warning('退出登录请求失败，已强制登出');
        router.push('/login');
      }
    } catch {
      // 用户取消退出登录
      console.log('用户取消了退出登录');
    }
  } else if (command === 'profile') {
    // 处理个人信息页面跳转
    ElMessage.info('功能开发中...');
  }
};
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
}

.el-header {
  background-color: #fff;
  line-height: 60px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-right {
  margin-right: 20px;
}

.username-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.username-dropdown .el-icon {
  margin-left: 5px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.el-dropdown {
  vertical-align:middle !important;
}
</style> 