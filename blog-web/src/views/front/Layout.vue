<template>
  <div class="layout">
    <header class="header">
      <div class="header-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">B</span>
          <span class="logo-text">Blog</span>
        </router-link>
        <nav class="nav">
          <router-link to="/" class="nav-link">Home</router-link>
          <router-link to="/archives" class="nav-link">Archives</router-link>
          <a class="nav-link" @click="toggleTheme">{{ isDark ? 'Light' : 'Dark' }}</a>
        </nav>
        <div class="auth-actions">
          <template v-if="userStore.userInfo">
            <div class="user-pill">
              <span class="user-name">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</span>
              <span class="user-role">{{ userStore.userInfo.role }}</span>
            </div>
            <router-link v-if="userStore.userInfo.role === 'ADMIN'" to="/admin/dashboard" class="ghost-btn">Dashboard</router-link>
            <button class="ghost-btn" @click="handleLogout">Logout</button>
          </template>
          <template v-else>
            <router-link to="/login" class="ghost-btn">Login</router-link>
            <router-link to="/register" class="solid-btn">Register</router-link>
          </template>
        </div>
      </div>
    </header>

    <main class="main">
      <div v-if="showHero" class="hero">
        <div class="hero-copy">
          <p class="hero-kicker">P0 Growth Loop</p>
          <h1>Publish, interact, subscribe, measure.</h1>
          <p>在现有博客之上补齐用户沉淀和增长闭环，让内容不再只是被浏览，而是能够被互动、订阅和复盘。</p>
        </div>
        <form class="hero-subscribe" @submit.prevent="handleSubscribe">
          <label>Subscribe for updates</label>
          <div class="subscribe-row">
            <input v-model.trim="heroEmail" type="email" placeholder="you@example.com" />
            <button type="submit">Join</button>
          </div>
          <span class="subscribe-note">来源页会记录为首页，用于 P0 转化统计。</span>
        </form>
      </div>

      <div class="main-inner" :class="{ 'detail-main': isDetailLayout }">
        <div class="content" :class="{ 'detail-content': isDetailLayout }"><router-view /></div>
        <aside v-if="showSidebar" class="sidebar">
          <div class="widget about-widget">
            <div class="avatar"></div>
            <h3 class="widget-title">About This Blog</h3>
            <p class="about-text">聚焦工程实践、前端体验和内容产品演进，用最小投入验证真实增长。</p>
          </div>
          <div class="widget">
            <h3 class="widget-title"><span class="title-icon">#</span>Categories</h3>
            <ul class="category-list">
              <li v-for="c in categories" :key="c.id" @click="onCategoryClick(c.id)">
                <span class="cat-name">{{ c.name }}</span>
                <span class="cat-count">{{ c.articleCount || 0 }}</span>
              </li>
            </ul>
          </div>
          <div class="widget">
            <h3 class="widget-title"><span class="title-icon">+</span>Tags</h3>
            <TagCloud :tags="tags" v-model="selectedTagIds" @change="onTagChange" />
          </div>
        </aside>
      </div>
    </main>

    <footer class="footer">
      <div class="footer-inner">
        <p>2026 Blog | <a href="/sitemap.xml" target="_blank">Sitemap</a> | <a href="/rss.xml" target="_blank">RSS</a></p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { frontApi } from '@/api/front'
import { useUserStore } from '@/store/user'
import TagCloud from '@/components/TagCloud.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const categories = ref([])
const tags = ref([])
const selectedTagIds = ref([])
const heroEmail = ref('')
const isDark = ref(false)
const frontLayout = computed(() => route.meta?.frontLayout || {})
const isDetailLayout = computed(() => frontLayout.value.variant === 'detail')
const showHero = computed(() => !frontLayout.value.hideHero)
const showSidebar = computed(() => !frontLayout.value.hideSidebar)

const currentCategoryId = computed(() => {
  const match = route.path.match(/^\/category\/(\d+)/)
  return match ? Number(match[1]) : null
})

watch(
  () => route.query.tagIds,
  val => {
    selectedTagIds.value = val ? val.split(',').map(Number) : []
  },
  { immediate: true }
)

onMounted(async () => {
  const [cRes, tRes] = await Promise.all([frontApi.getCategories(), frontApi.getTags()])
  categories.value = cRes.data || []
  tags.value = tRes.data || []
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.getProfile()
    } catch (e) {
      await userStore.logout()
    }
  }
  if (userStore.userInfo?.email) {
    heroEmail.value = userStore.userInfo.email
  }
})

const onTagChange = ids => {
  selectedTagIds.value = ids
  if (ids.length > 0) {
    router.push(currentCategoryId.value ? `/category/${currentCategoryId.value}?tagIds=${ids.join(',')}` : `/tags/${ids.join(',')}`)
  } else {
    router.push(currentCategoryId.value ? `/category/${currentCategoryId.value}` : '/')
  }
}

const onCategoryClick = id => {
  if (selectedTagIds.value.length > 0) {
    router.push(`/category/${id}?tagIds=${selectedTagIds.value.join(',')}`)
  } else {
    router.push(`/category/${id}`)
  }
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
}

const handleSubscribe = async () => {
  await frontApi.subscribe({ email: heroEmail.value, sourcePage: '/' })
  ElMessage.success('Subscription saved')
}

const handleLogout = async () => {
  await userStore.logout(true, false)
  router.push('/')
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600;700&family=Source+Sans+3:wght@300;400;500;600;700&display=swap');
:root {
  --bg: #faf7f1;
  --fg: #1f1b18;
  --muted: #71655a;
  --accent: #c45d3e;
  --accent-light: #f1ddd6;
  --card: rgba(255,255,255,0.88);
  --border: #eadfd6;
  --shadow: rgba(61, 41, 23, 0.08);
  --font-display: 'Playfair Display', serif;
  --font-body: 'Source Sans 3', sans-serif;
}
.dark {
  --bg: #141211;
  --fg: #f5ede7;
  --muted: #b3a59a;
  --accent: #e07a5f;
  --accent-light: #34241f;
  --card: rgba(27,25,24,0.9);
  --border: #2d2724;
  --shadow: rgba(0,0,0,0.28);
}
html { scroll-behavior: smooth; }
body {
  margin: 0;
  font-family: var(--font-body);
  background:
    radial-gradient(circle at top left, rgba(196, 93, 62, 0.08), transparent 30%),
    linear-gradient(180deg, var(--bg), #f4efe8 50%, var(--bg));
  color: var(--fg);
}
.dark body {
  background:
    radial-gradient(circle at top left, rgba(224, 122, 95, 0.12), transparent 24%),
    linear-gradient(180deg, var(--bg), #171513 50%, var(--bg));
}
</style>

<style scoped>
.layout { min-height: 100vh; display: flex; flex-direction: column; }
.header {
  position: sticky;
  top: 0;
  z-index: 50;
  backdrop-filter: blur(14px);
  background: rgba(250, 247, 241, 0.82);
  border-bottom: 1px solid var(--border);
}
.dark .header { background: rgba(20, 18, 17, 0.84); }
.header-inner, .hero, .main-inner, .footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding-left: 24px;
  padding-right: 24px;
}
.header-inner {
  min-height: 76px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}
.logo { display: inline-flex; align-items: center; gap: 10px; color: var(--fg); text-decoration: none; }
.logo-icon {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--accent), #d89a71);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-weight: 700;
}
.logo-text { font-family: var(--font-display); font-size: 24px; }
.nav, .auth-actions { display: flex; align-items: center; gap: 10px; }
.nav-link {
  color: var(--muted);
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
}
.nav-link.router-link-active, .nav-link:hover { color: var(--accent); }
.ghost-btn, .solid-btn {
  border-radius: 999px;
  padding: 10px 16px;
  font-weight: 600;
  text-decoration: none;
}
.ghost-btn {
  background: transparent;
  border: 1px solid var(--border);
  color: var(--fg);
}
.solid-btn {
  background: var(--accent);
  color: #fff;
  border: 1px solid var(--accent);
}
.user-pill {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 999px;
}
.user-role {
  font-size: 12px;
  background: var(--accent-light);
  color: var(--accent);
  padding: 4px 8px;
  border-radius: 999px;
}
.main { flex: 1; padding: 28px 0 48px; }
.hero {
  margin-bottom: 28px;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 24px;
}
.hero-copy, .hero-subscribe, .widget {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 24px;
  box-shadow: 0 12px 40px var(--shadow);
}
.hero-copy {
  padding: 32px;
}
.hero-copy h1 {
  margin: 0 0 12px;
  font-family: var(--font-display);
  font-size: 48px;
  line-height: 1.08;
}
.hero-copy p:last-child {
  margin: 0;
  color: var(--muted);
  font-size: 18px;
}
.hero-kicker {
  margin: 0 0 8px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
}
.hero-subscribe {
  padding: 28px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.hero-subscribe label {
  font-family: var(--font-display);
  font-size: 24px;
  margin-bottom: 14px;
}
.subscribe-row {
  display: flex;
  gap: 12px;
}
.subscribe-row input {
  flex: 1;
  padding: 14px 16px;
  border: 1px solid var(--border);
  border-radius: 16px;
  background: var(--bg);
}
.subscribe-row button {
  border: none;
  border-radius: 16px;
  background: var(--accent);
  color: #fff;
  padding: 0 18px;
  font-weight: 700;
}
.subscribe-note {
  margin-top: 10px;
  color: var(--muted);
  font-size: 13px;
}
.main-inner {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 32px;
}
.main-inner.detail-main {
  max-width: 1280px;
  grid-template-columns: minmax(0, 1fr);
}
.content { min-width: 0; }
.content.detail-content { width: 100%; }
.sidebar { display: flex; flex-direction: column; gap: 20px; }
.widget { padding: 24px; }
.about-widget { text-align: center; }
.avatar {
  width: 86px;
  height: 86px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent), #d7b58c);
  margin: 0 auto 16px;
}
.widget-title {
  margin: 0 0 16px;
  font-family: var(--font-display);
  font-size: 22px;
}
.about-text { margin: 0; color: var(--muted); }
.title-icon { color: var(--accent); }
.category-list { list-style: none; padding: 0; margin: 0; }
.category-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
  cursor: pointer;
}
.category-list li:last-child { border-bottom: none; }
.cat-count {
  padding: 4px 8px;
  border-radius: 999px;
  background: var(--accent-light);
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
}
.footer {
  border-top: 1px solid var(--border);
  background: rgba(255,255,255,0.5);
}
.dark .footer { background: rgba(0,0,0,0.16); }
.footer-inner { padding-top: 24px; padding-bottom: 24px; }
.footer p { margin: 0; text-align: center; color: var(--muted); }
.footer a { color: var(--accent); text-decoration: none; }
@media (max-width: 980px) {
  .hero, .main-inner { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .header-inner { flex-wrap: wrap; justify-content: center; padding-top: 16px; padding-bottom: 16px; }
  .hero-copy h1 { font-size: 34px; }
  .subscribe-row { flex-direction: column; }
}
</style>
