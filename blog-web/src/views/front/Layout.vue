<template>
  <div class="layout">
    <header class="header">
      <div class="header-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">B</span>
          <span class="logo-text">Blog</span>
        </router-link>
        <nav class="nav">
          <router-link to="/" class="nav-link">
            <span class="nav-text">首页</span>
            <span class="nav-line"></span>
          </router-link>
          <router-link to="/archives" class="nav-link">
            <span class="nav-text">归档</span>
            <span class="nav-line"></span>
          </router-link>
          <a class="nav-link" @click="toggleTheme">
            <span class="nav-text">{{ isDark ? '☀' : '☾' }}</span>
          </a>
        </nav>
      </div>
    </header>
    <main class="main">
      <div class="main-inner">
        <div class="content"><router-view /></div>
        <aside class="sidebar">
          <div class="widget about-widget">
            <div class="avatar"></div>
            <h3 class="widget-title">关于博主</h3>
            <p class="about-text">热爱技术，热爱生活。记录学习与成长的点滴。</p>
          </div>
          <div class="widget">
            <h3 class="widget-title">
              <span class="title-icon">#</span>分类
            </h3>
            <ul class="category-list">
              <li v-for="c in categories" :key="c.id" @click="$router.push(`/category/${c.id}`)">
                <span class="cat-name">{{ c.name }}</span>
                <span class="cat-count">{{ c.articleCount || 0 }}</span>
              </li>
            </ul>
          </div>
          <div class="widget">
            <h3 class="widget-title">
              <span class="title-icon">♦</span>标签
            </h3>
            <TagCloud :tags="tags" v-model="selectedTagIds" @change="onTagChange" />
          </div>
        </aside>
      </div>
    </main>
    <footer class="footer">
      <div class="footer-inner">
        <p>© 2024 Blog. Made with ♥</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { frontApi } from '@/api/front'
import TagCloud from '@/components/TagCloud.vue'
const router = useRouter()
const categories = ref([])
const tags = ref([])
const selectedTagIds = ref([])
const isDark = ref(false)
onMounted(async () => {
  const [cRes, tRes] = await Promise.all([frontApi.getCategories(), frontApi.getTags()])
  categories.value = cRes.data || []
  tags.value = tRes.data || []
})
const onTagChange = ids => {
  selectedTagIds.value = ids
  if (ids.length > 0) {
    router.push(`/tags/${ids.join(',')}`)
  } else {
    router.push('/')
  }
}
const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');
:root {
  --bg: #faf9f7;
  --fg: #1a1a1a;
  --muted: #6b6b6b;
  --accent: #c45d3e;
  --accent-light: #e8d5cf;
  --card: #ffffff;
  --border: #e8e6e3;
  --shadow: rgba(0,0,0,0.06);
  --font-display: 'Playfair Display', Georgia, serif;
  --font-body: 'Source Sans 3', -apple-system, sans-serif;
}
.dark {
  --bg: #141414;
  --fg: #f5f5f5;
  --muted: #9a9a9a;
  --accent: #e07a5f;
  --accent-light: #3a2a25;
  --card: #1e1e1e;
  --border: #2a2a2a;
  --shadow: rgba(0,0,0,0.3);
}
html { scroll-behavior: smooth; }
body {
  font-family: var(--font-body);
  background: var(--bg);
  color: var(--fg);
  line-height: 1.6;
  transition: background 0.4s, color 0.4s;
}
</style>

<style scoped>
.layout { min-height: 100vh; display: flex; flex-direction: column; }
.header {
  background: var(--card);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
  background: rgba(255,255,255,0.9);
}
.dark .header { background: rgba(30,30,30,0.9); }
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  height: 72px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo { display: flex; align-items: center; gap: 10px; text-decoration: none; }
.logo-icon {
  width: 36px;
  height: 36px;
  background: var(--accent);
  color: #fff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
}
.logo-text {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--fg);
  letter-spacing: -0.5px;
}
.nav { display: flex; gap: 8px; }
.nav-link {
  position: relative;
  padding: 8px 16px;
  color: var(--muted);
  text-decoration: none;
  font-weight: 500;
  font-size: 15px;
  cursor: pointer;
  transition: color 0.3s;
}
.nav-link:hover { color: var(--fg); }
.nav-link.router-link-active { color: var(--accent); }
.nav-line {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: var(--accent);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateX(-50%);
}
.nav-link:hover .nav-line,
.nav-link.router-link-active .nav-line { width: 24px; }
.main { flex: 1; padding: 48px 0; }
.main-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  display: flex;
  gap: 48px;
}
.content { flex: 1; min-width: 0; }
.sidebar { width: 300px; flex-shrink: 0; }
.widget {
  background: var(--card);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  border: 1px solid var(--border);
  box-shadow: 0 4px 20px var(--shadow);
}
.widget-title {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  color: var(--fg);
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.title-icon { color: var(--accent); font-size: 14px; }
.about-widget { text-align: center; }
.avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, var(--accent), var(--accent-light));
  border-radius: 50%;
  margin: 0 auto 16px;
}
.about-text { color: var(--muted); font-size: 14px; margin: 0; }
.category-list { list-style: none; padding: 0; margin: 0; }
.category-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
  cursor: pointer;
  transition: all 0.3s;
}
.category-list li:last-child { border-bottom: none; }
.category-list li:hover { padding-left: 8px; }
.category-list li:hover .cat-name { color: var(--accent); }
.cat-name { color: var(--fg); font-size: 14px; transition: color 0.3s; }
.cat-count {
  background: var(--accent-light);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 12px;
}
.footer {
  background: var(--card);
  border-top: 1px solid var(--border);
  padding: 32px;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}
.footer p { color: var(--muted); font-size: 14px; margin: 0; }
@media (max-width: 900px) {
  .main-inner { flex-direction: column; }
  .sidebar { width: 100%; }
}
@media (max-width: 600px) {
  .header-inner { padding: 0 16px; height: 60px; }
  .main-inner { padding: 0 16px; gap: 24px; }
  .nav-link { padding: 8px 12px; font-size: 14px; }
}
</style>
