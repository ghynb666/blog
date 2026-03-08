<template>
  <div class="admin-layout" :class="{ dark: themeStore.isDark }">
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
            <button class="action-btn theme-toggle" @click="themeStore.toggle()" :title="themeStore.isDark ? '切换到亮色模式' : '切换到暗色模式'">
              <svg v-if="themeStore.isDark" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="5"/>
                <line x1="12" y1="1" x2="12" y2="3"/>
                <line x1="12" y1="21" x2="12" y2="23"/>
                <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
                <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
                <line x1="1" y1="12" x2="3" y2="12"/>
                <line x1="21" y1="12" x2="23" y2="12"/>
                <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
                <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
              </svg>
            </button>
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
import { useThemeStore } from '@/store/theme'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const themeStore = useThemeStore()
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

const handleCommand = async cmd => {
  if (cmd === 'logout') {
    await userStore.logout(true, true)
    router.push('/admin/login')
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

.admin-layout {
  --bg-primary: #faf8f5;
  --bg-secondary: #ffffff;
  --bg-tertiary: #f5f0e8;
  --text-primary: #2d2a26;
  --text-secondary: #5c5246;
  --text-muted: #8b7355;
  --border-color: #e8e0d5;
  --accent: #c45d3e;
  --accent-light: rgba(196, 93, 62, 0.1);
  --shadow: rgba(45, 42, 38, 0.04);
  
  min-height: 100vh;
  display: flex;
  background: var(--bg-primary);
  font-family: 'Source Sans 3', sans-serif;
  transition: background 0.3s ease, color 0.3s ease;
}

.admin-layout.dark {
  --bg-primary: #0f0f0f;
  --bg-secondary: #1a1a1a;
  --bg-tertiary: #252525;
  --text-primary: #f5f5f5;
  --text-secondary: #b8b8b8;
  --text-muted: #888888;
  --border-color: #333333;
  --accent: #e07a5f;
  --accent-light: rgba(224, 122, 95, 0.15);
  --shadow: rgba(0, 0, 0, 0.3);
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

.admin-layout.dark .sidebar {
  background: linear-gradient(180deg, #1a1a1a 0%, #0f0f0f 100%);
  border-right: 1px solid #222;
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
  color: var(--accent);
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
  background: var(--accent-light);
  color: var(--accent);
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 24px;
  background: var(--accent);
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
  background: var(--bg-secondary);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 2rem;
  position: sticky;
  top: 0;
  z-index: 50;
  transition: background 0.3s ease, border-color 0.3s ease;
}

.admin-layout.dark .top-header {
  backdrop-filter: blur(20px);
  background: rgba(26, 26, 26, 0.9);
}

.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb-item {
  font-family: 'Playfair Display', serif;
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  transition: color 0.3s ease;
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
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
  color: var(--text-secondary);
}

.action-btn svg {
  width: 18px;
  height: 18px;
}

.action-btn:hover {
  background: var(--bg-tertiary);
  border-color: var(--accent);
  color: var(--accent);
}

.theme-toggle {
  position: relative;
  overflow: hidden;
}

.theme-toggle::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, var(--accent-light) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.theme-toggle:hover::after {
  opacity: 1;
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
  background: var(--bg-tertiary);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--accent) 0%, #d4a574 100%);
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
  color: var(--text-primary);
  transition: color 0.3s ease;
}

.dropdown-arrow {
  width: 16px;
  height: 16px;
  color: var(--text-muted);
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

.admin-layout.dark :deep(.el-dropdown-menu) {
  background: var(--bg-secondary);
  border-color: var(--border-color);
}

.admin-layout.dark :deep(.el-dropdown-menu__item) {
  color: var(--text-primary);
}

.admin-layout.dark :deep(.el-dropdown-menu__item:hover) {
  background: var(--bg-tertiary);
  color: var(--accent);
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
