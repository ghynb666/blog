<template>
  <div class="auth-page">
    <div class="auth-card">
      <p class="eyebrow">{{ isLogin ? 'Welcome Back' : 'Create Account' }}</p>
      <h1>{{ isLogin ? 'Login to continue the loop' : 'Register and join the growth loop' }}</h1>
      <p class="intro">P0 版本使用站内账号体系。注册后会自动进入登录态，并进入可评论、点赞、订阅的完整链路。</p>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label>
          <span>Username</span>
          <input v-model.trim="form.username" type="text" maxlength="50" required />
        </label>
        <label v-if="!isLogin">
          <span>Email</span>
          <input v-model.trim="form.email" type="email" maxlength="100" required />
        </label>
        <label v-if="!isLogin">
          <span>Nickname</span>
          <input v-model.trim="form.nickname" type="text" maxlength="50" />
        </label>
        <label>
          <span>Password</span>
          <input v-model="form.password" type="password" minlength="6" maxlength="100" required />
        </label>
        <button type="submit" :disabled="loading">{{ loading ? 'Submitting...' : (isLogin ? 'Login' : 'Register') }}</button>
      </form>

      <p class="switcher">
        <span>{{ isLogin ? 'Need an account?' : 'Already have an account?' }}</span>
        <router-link :to="isLogin ? '/register' : '/login'">{{ isLogin ? 'Register' : 'Login' }}</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { encryptPassword } from '@/utils/rsa'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', email: '', nickname: '', password: '' })
const isLogin = computed(() => route.path === '/login')

const handleSubmit = async () => {
  loading.value = true
  try {
    const password = await encryptPassword(form.password)
    if (isLogin.value) {
      await userStore.loginFront({ username: form.username, password })
      ElMessage.success('Login successful')
    } else {
      await userStore.registerFront({
        username: form.username,
        email: form.email,
        nickname: form.nickname,
        password
      })
      ElMessage.success('Registration successful')
    }
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-card {
  width: min(100%, 620px);
  padding: 32px;
  border-radius: 24px;
  background: var(--card);
  border: 1px solid var(--border);
  box-shadow: 0 16px 40px var(--shadow);
}
.eyebrow {
  margin: 0 0 8px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--accent);
  font-weight: 700;
  font-size: 12px;
}
h1 {
  margin: 0 0 12px;
  font-family: var(--font-display);
  font-size: 40px;
  line-height: 1.1;
}
.intro {
  margin: 0 0 24px;
  color: var(--muted);
}
.auth-form {
  display: grid;
  gap: 16px;
}
.auth-form label {
  display: grid;
  gap: 8px;
  font-weight: 600;
}
.auth-form input {
  padding: 14px 16px;
  border-radius: 16px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--fg);
}
.auth-form button {
  margin-top: 8px;
  border: none;
  border-radius: 16px;
  background: var(--accent);
  color: #fff;
  padding: 14px 16px;
  font-weight: 700;
  cursor: pointer;
}
.switcher {
  margin: 18px 0 0;
  color: var(--muted);
}
.switcher a {
  color: var(--accent);
  font-weight: 700;
  margin-left: 8px;
  text-decoration: none;
}
@media (max-width: 640px) {
  .auth-card { padding: 24px 20px; }
  h1 { font-size: 30px; }
}
</style>
