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
          <div class="password-field">
            <input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              :minlength="isLogin ? undefined : 6"
              maxlength="100"
              required
            />
            <button
              type="button"
              class="toggle-password"
              :aria-label="showPassword ? 'Hide password' : 'Show password'"
              @click="showPassword = !showPassword"
            >
              <span class="password-icon" :style="{ '--icon-url': `url(${passwordIcon})` }" aria-hidden="true"></span>
            </button>
          </div>
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
import eyeOpenIcon from '@/static/eye-open.svg'
import eyeCloseIcon from '@/static/aye-close.svg'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const showPassword = ref(false)
const form = reactive({ username: '', email: '', nickname: '', password: '' })
const isLogin = computed(() => route.path === '/login')
const passwordIcon = computed(() => (showPassword.value ? eyeOpenIcon : eyeCloseIcon))

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
  width: 100%;
  box-sizing: border-box;
}
.password-field {
  position: relative;
}
.password-field input {
  padding-right: 64px;
}
.toggle-password {
  position: absolute;
  top: 50%;
  right: 18px;
  transform: translateY(-50%);
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--fg);
  z-index: 2;
}
.password-icon {
  width: 20px;
  height: 20px;
  display: block;
  background-color: currentColor;
  -webkit-mask-image: var(--icon-url);
  mask-image: var(--icon-url);
  -webkit-mask-repeat: no-repeat;
  mask-repeat: no-repeat;
  -webkit-mask-position: center;
  mask-position: center;
  -webkit-mask-size: contain;
  mask-size: contain;
}
.toggle-password:hover {
  color: var(--accent);
}
.toggle-password:focus-visible {
  outline: 2px solid var(--accent);
  outline-offset: 4px;
  border-radius: 6px;
}
.auth-form > button {
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
  .password-field input { padding-right: 60px; }
  .toggle-password { right: 16px; }
}
</style>
