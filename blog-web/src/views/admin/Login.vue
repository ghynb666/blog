<template>
  <div class="login-container" :class="{ dark: themeStore.isDark }">
    <div class="login-bg">
      <div class="grain-overlay"></div>
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    <div class="login-content">
      <div class="login-card">
        <div class="login-header">
          <div class="logo-mark">
            <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="2" y="2" width="36" height="36" rx="8" stroke="currentColor" stroke-width="2"/>
              <path d="M12 28V12h4l6 10 6-10h4v16h-4V18l-6 10-6-10v10h-4z" fill="currentColor"/>
            </svg>
          </div>
          <h1 class="login-title">Blog Admin</h1>
          <p class="login-subtitle">管理你的博客内容</p>
        </div>
        <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
          <el-form-item prop="username">
            <div class="input-wrapper">
              <label class="input-label">用户名</label>
              <el-input v-model="form.username" placeholder="请输入用户名" size="large" @keyup.enter="handleLogin">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                </template>
              </el-input>
            </div>
          </el-form-item>
          <el-form-item prop="password">
            <div class="input-wrapper">
              <label class="input-label">密码</label>
              <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password @keyup.enter="handleLogin">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                  </svg>
                </template>
              </el-input>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleLogin" class="login-btn">
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <button class="theme-toggle" @click="themeStore.toggle()">
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
            <span>{{ themeStore.isDark ? '切换亮色' : '切换暗色' }}</span>
          </button>
          <p class="copyright">© 2024 Blog Admin</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useThemeStore } from '@/store/theme'
import { encryptPassword } from '@/utils/rsa'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const encryptedPassword = await encryptPassword(form.password)
    await userStore.login({
      username: form.username,
      password: encryptedPassword
    })
    ElMessage.success('登录成功')
    router.push('/admin')
  } catch (e) {
    console.error('登录失败', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

.login-container {
  --bg-primary: #faf8f5;
  --bg-secondary: #ffffff;
  --bg-tertiary: #f5f0e8;
  --text-primary: #2d2a26;
  --text-secondary: #5c5246;
  --text-muted: #8b7355;
  --border-color: #e8e0d5;
  --accent: #c45d3e;
  --accent-light: rgba(196, 93, 62, 0.1);
  
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: var(--bg-primary);
  transition: background 0.3s ease;
}

.login-container.dark {
  --bg-primary: #0f0f0f;
  --bg-secondary: #1a1a1a;
  --bg-tertiary: #252525;
  --text-primary: #f5f5f5;
  --text-secondary: #b8b8b8;
  --text-muted: #888888;
  --border-color: #333333;
  --accent: #e07a5f;
  --accent-light: rgba(224, 122, 95, 0.15);
}

.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, var(--bg-tertiary) 0%, var(--border-color) 50%, var(--bg-tertiary) 100%);
  transition: background 0.3s ease;
}

.grain-overlay {
  position: absolute;
  inset: 0;
  opacity: 0.4;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  pointer-events: none;
}

.floating-shapes {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
  animation: float 20s ease-in-out infinite;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, var(--accent) 0%, #d4a574 100%);
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, var(--text-muted) 0%, var(--accent) 100%);
  bottom: -50px;
  left: -50px;
  animation-delay: -7s;
}

.shape-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #d4a574 0%, var(--text-muted) 100%);
  top: 50%;
  left: 30%;
  animation-delay: -14s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(30px, -30px) rotate(5deg); }
  66% { transform: translate(-20px, 20px) rotate(-5deg); }
}

.login-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  z-index: 1;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--bg-secondary);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 3rem 2.5rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--border-color);
  animation: cardEntrance 0.6s ease-out;
  transition: background 0.3s ease, border-color 0.3s ease;
}

@keyframes cardEntrance {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.logo-mark {
  width: 56px;
  height: 56px;
  margin: 0 auto 1.25rem;
  color: var(--accent);
  animation: logoFloat 3s ease-in-out infinite;
  transition: color 0.3s ease;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.login-title {
  font-family: 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 0.5rem;
  letter-spacing: -0.02em;
  transition: color 0.3s ease;
}

.login-subtitle {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: var(--text-muted);
  margin: 0;
  font-weight: 400;
  transition: color 0.3s ease;
}

.login-form {
  margin-bottom: 1.5rem;
}

.input-wrapper {
  width: 100%;
}

.input-label {
  display: block;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
  letter-spacing: 0.02em;
  transition: color 0.3s ease;
}

.login-form :deep(.el-input__wrapper) {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: none;
  padding: 0.25rem 1rem;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: var(--accent);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-light);
}

.login-form :deep(.el-input__inner) {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: var(--text-primary);
  height: 44px;
  background: transparent;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: var(--text-muted);
}

.input-icon {
  width: 18px;
  height: 18px;
  color: var(--text-muted);
}

.login-btn {
  width: 100%;
  height: 50px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  background: linear-gradient(135deg, var(--accent) 0%, #a84a2e 100%);
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
  margin-top: 0.5rem;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(196, 93, 62, 0.3);
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: center;
}

.theme-toggle {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.8rem;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.theme-toggle svg {
  width: 16px;
  height: 16px;
}

.theme-toggle:hover {
  background: var(--accent-light);
  border-color: var(--accent);
  color: var(--accent);
}

.copyright {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.8rem;
  color: var(--text-muted);
  margin: 0;
  transition: color 0.3s ease;
}

@media (max-width: 480px) {
  .login-card {
    padding: 2rem 1.5rem;
  }
  
  .login-title {
    font-size: 1.75rem;
  }
}
</style>