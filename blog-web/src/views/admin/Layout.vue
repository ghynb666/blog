<template>
  <div class="admin-layout">
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <svg class="logo-icon" viewBox="0 0 40 40" fill="none">
            <rect x="2" y="2" width="36" height="36" rx="8" stroke="currentColor" stroke-width="2"/>
            <path d="M12 28V12h4l6 10 6-10h4v16h-4V18l-6 10-6-10v10h-4z" fill="currentColor"/>
          </svg>
          <span v-if="!sidebarCollapsed" class="logo-text">Blog Admin</span>
        </div>
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M15 18l-6-6 6-6"/>
          </svg>
        </button>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin/dashboard" class="nav-item" :class="{ active: $route.path === '/admin/dashboard' }">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="7" height="7"/>
            <rect x="14" y="3" width="7" height="7"/>
            <rect x="14" y="14" width="7" height="7"/>
            <rect x="3" y="14" width="7" height="7"/>
          </svg>
          <span v-if="!sidebarCollapsed" class="nav-text">仪表盘</span>
        </router-link>
        <router-link to="/admin/article" class="nav-item" :class="{ active: $route.path.startsWith('/admin/article') }">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
          </svg>
          <span v-if="!sidebarCollapsed" class="nav-text">文章管理</span>
        </router-link>
        <router-link to="/admin/category" class="nav-item" :class="{ active: $route.path === '/admin/category' }">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
          </svg>
          <span v-if="!sidebarCollapsed" class="nav-text">分类管理</span>
        </router-link>
        <router-link to="/admin/tag" class="nav-item" :class="{ active: $route.path === '/admin/tag' }">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
            <line x1="7" y1="7" x2="7.01" y2="7"/>
          </svg>
          <span v-if="!sidebarCollapsed" class="nav-text">标签管理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer" v-if="!sidebarCollapsed">
        <div class="version">v1.0.0</div>
      </div>
    </aside>
    <div class="main-container">
      <header class="top-header">
        <div class="header-left">
          <div class="breadcrumb">
            <span class="breadcrumb-item">{{ pageTitle }}</span>
          </div>
        </div>
        <div class="header-right">
          <div class="header-actions">
            <a href="/" target="_blank" class="action-btn" title="访问前台">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
                <polyline points="15 3 21 3 21 9"/>
                <line x1="10" y1="14" x2="21" y2="3"/>
              </svg>
            </a>
          </div>
          <div class="user-dropdown">
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-info">
                <div class="user-avatar">
                  <span>{{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() || 'A' }}</span>
                </div>
                <span class="user-name">{{ userStore.userInfo?.username || '管理员' }}</span>
                <svg class="dropdown-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="6 9 12 15 18 9"/>
                </svg>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="dropdown-icon">
                      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                      <polyline points="16 17 21 12 16 7"/>
                      <line x1="21" y1="12" x2="9" y2="12"/>
                    </svg>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </header>
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const sidebarCollapsed = ref(false)

const pageTitle = computed(() => {
  const titles = {
    '/admin/dashboard': '仪表盘',
    '/admin/article': '文章管理',
    '/admin/category': '分类管理',
    '/admin/tag': '标签管理'
  }
  if (route.path.startsWith('/admin/article/edit')) return '编辑文章'
  return titles[route.path] || '管理后台'
})

onMounted(async () => {
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.getInfo()
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }
})

const handleCommand = cmd => {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/admin/login')
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

.admin-layout {
  min-height: 100vh;
  display: flex;
  background: #faf8f5;
  font-family: 'Source Sans 3', sans-serif;
}

.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #2d2a26 0%, #1a1816 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-icon {
  width: 36px;
  height: 36px;
  color: #c45d3e;
  flex-shrink: 0;
}

.logo-text {
  font-family: 'Playfair Display', serif;
  font-size: 1.25rem;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.collapse-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.collapse-btn svg {
  width: 16px;
  height: 16px;
  color: #a89b8c;
  transition: transform 0.3s ease;
}

.sidebar.collapsed .collapse-btn svg {
  transform: rotate(180deg);
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

.sidebar-nav {
  flex: 1;
  padding: 1rem 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  padding: 0.875rem 1rem;
  border-radius: 10px;
  text-decoration: none;
  color: #a89b8c;
  transition: all 0.2s ease;
  position: relative;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #d4a574;
}

.nav-item.active {
  background: rgba(196, 93, 62, 0.15);
  color: #c45d3e;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 24px;
  background: #c45d3e;
  border-radius: 0 2px 2px 0;
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-text {
  font-size: 0.95rem;
  font-weight: 500;
  white-space: nowrap;
}

.sidebar-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.version {
  font-size: 0.75rem;
  color: #5c5246;
  text-align: center;
}

.main-container {
  flex: 1;
  margin-left: 260px;
  transition: margin-left 0.3s ease;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.sidebar.collapsed + .main-container {
  margin-left: 80px;
}

.top-header {
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e8e0d5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 2rem;
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb-item {
  font-family: 'Playfair Display', serif;
  font-size: 1.25rem;
  font-weight: 600;
  color: #2d2a26;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: transparent;
  border: 1px solid #e8e0d5;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
  color: #5c5246;
}

.action-btn svg {
  width: 18px;
  height: 18px;
}

.action-btn:hover {
  background: #f5f0e8;
  border-color: #d4a574;
  color: #c45d3e;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0.75rem 0.5rem 0.5rem;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.user-info:hover {
  background: #f5f0e8;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #c45d3e 0%, #d4a574 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  font-size: 0.9rem;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 500;
  color: #2d2a26;
}

.dropdown-arrow {
  width: 16px;
  height: 16px;
  color: #8b7355;
}

.dropdown-icon {
  width: 16px;
  height: 16px;
  margin-right: 0.5rem;
}

.main-content {
  flex: 1;
  padding: 2rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  font-family: 'Source Sans 3', sans-serif;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  
  .sidebar.collapsed {
    transform: translateX(0);
    width: 260px;
  }
  
  .main-container {
    margin-left: 0;
  }
  
  .sidebar.collapsed + .main-container {
    margin-left: 0;
  }
}
</style>
