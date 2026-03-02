<template>
  <div class="login-container">
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
          <p>© 2024 Blog Admin</p>
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

const router = useRouter()
const userStore = useUserStore()
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
    await userStore.login(form)
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
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: #faf8f5;
}

.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #f5f0e8 0%, #e8e0d5 50%, #f0ebe3 100%);
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
  background: linear-gradient(135deg, #c45d3e 0%, #d4a574 100%);
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #8b7355 0%, #c45d3e 100%);
  bottom: -50px;
  left: -50px;
  animation-delay: -7s;
}

.shape-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #d4a574 0%, #8b7355 100%);
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
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 3rem 2.5rem;
  box-shadow: 
    0 4px 6px -1px rgba(139, 115, 85, 0.05),
    0 10px 15px -3px rgba(139, 115, 85, 0.08),
    0 20px 25px -5px rgba(139, 115, 85, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.8);
  animation: cardEntrance 0.6s ease-out;
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
  color: #c45d3e;
  animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.login-title {
  font-family: 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 600;
  color: #2d2a26;
  margin: 0 0 0.5rem;
  letter-spacing: -0.02em;
}

.login-subtitle {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: #8b7355;
  margin: 0;
  font-weight: 400;
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
  color: #5c5246;
  margin-bottom: 0.5rem;
  letter-spacing: 0.02em;
}

.login-form :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e8e0d5;
  border-radius: 12px;
  box-shadow: none;
  padding: 0.25rem 1rem;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #d4a574;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #c45d3e;
  box-shadow: 0 0 0 3px rgba(196, 93, 62, 0.1);
}

.login-form :deep(.el-input__inner) {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: #2d2a26;
  height: 44px;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #b8a99a;
}

.input-icon {
  width: 18px;
  height: 18px;
  color: #8b7355;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  background: linear-gradient(135deg, #c45d3e 0%, #a84a2e 100%);
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
  margin-top: 0.5rem;
}

.login-btn:hover {
  background: linear-gradient(135deg, #d46a4a 0%, #c45d3e 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(196, 93, 62, 0.3);
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid #e8e0d5;
}

.login-footer p {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.8rem;
  color: #a89b8c;
  margin: 0;
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
