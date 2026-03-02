<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px">
        <div class="logo">博客管理</div>
        <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
          <el-menu-item index="/admin/dashboard">
            <el-icon><Odometer /></el-icon>仪表盘
          </el-menu-item>
          <el-menu-item index="/admin/article">
            <el-icon><Document /></el-icon>文章管理
          </el-menu-item>
          <el-menu-item index="/admin/category">
            <el-icon><Folder /></el-icon>分类管理
          </el-menu-item>
          <el-menu-item index="/admin/tag">
            <el-icon><PriceTag /></el-icon>标签管理
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">{{ userStore.userInfo?.username || '管理员' }}<el-icon><ArrowDown /></el-icon></span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main><router-view /></el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  if (userStore.token && !userStore.userInfo) {
    userStore.getInfo()
  }
})

const handleCommand = cmd => {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout { height: 100vh; }
.el-aside { background-color: #304156; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 18px; font-weight: bold; }
.el-header { background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.1); display: flex; align-items: center; justify-content: flex-end; padding: 0 20px; }
.header-right { display: flex; align-items: center; }
.el-dropdown-link { cursor: pointer; display: flex; align-items: center; }
.el-menu { border-right: none; }
</style>
