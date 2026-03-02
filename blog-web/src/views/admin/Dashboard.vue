<template>
  <div class="dashboard">
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎回来</h1>
      <p class="welcome-subtitle">这是您的博客数据概览</p>
    </div>
    <div class="stats-grid">
      <div class="stat-card" v-for="(stat, index) in statsList" :key="stat.key">
        <div class="stat-icon" :class="stat.key">
          <component :is="stat.icon" />
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ animatedStats[stat.key] || 0 }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
        <div class="stat-decoration"></div>
      </div>
    </div>
    <div class="quick-actions">
      <h2 class="section-title">快捷操作</h2>
      <div class="actions-grid">
        <router-link to="/admin/article/edit" class="action-card">
          <div class="action-icon write">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 19l7-7 3 3-7 7-3-3z"/>
              <path d="M18 13l-1.5-7.5L2 2l3.5 14.5L13 18l5-5z"/>
              <path d="M2 2l7.586 7.586"/>
              <circle cx="11" cy="11" r="2"/>
            </svg>
          </div>
          <div class="action-content">
            <span class="action-title">写文章</span>
            <span class="action-desc">创建新的博客文章</span>
          </div>
          <svg class="action-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </router-link>
        <router-link to="/admin/category" class="action-card">
          <div class="action-icon category">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
            </svg>
          </div>
          <div class="action-content">
            <span class="action-title">分类管理</span>
            <span class="action-desc">整理文章分类</span>
          </div>
          <svg class="action-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </router-link>
        <router-link to="/admin/tag" class="action-card">
          <div class="action-icon tag">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
              <line x1="7" y1="7" x2="7.01" y2="7"/>
            </svg>
          </div>
          <div class="action-content">
            <span class="action-title">标签管理</span>
            <span class="action-desc">管理文章标签</span>
          </div>
          <svg class="action-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </router-link>
        <a href="/" target="_blank" class="action-card">
          <div class="action-icon visit">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="2" y1="12" x2="22" y2="12"/>
              <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>
            </svg>
          </div>
          <div class="action-content">
            <span class="action-title">访问前台</span>
            <span class="action-desc">查看博客主页</span>
          </div>
          <svg class="action-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import * as articleApi from '@/api/article'
import * as categoryApi from '@/api/category'
import * as tagApi from '@/api/tag'

const stats = ref({ articles: 0, categories: 0, tags: 0, views: 0 })
const animatedStats = reactive({ articles: 0, categories: 0, tags: 0, views: 0 })

const statsList = [
  { key: 'articles', label: '文章总数', icon: h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('path', { d: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z' }),
    h('polyline', { points: '14 2 14 8 20 8' }),
    h('line', { x1: '16', y1: '13', x2: '8', y2: '13' }),
    h('line', { x1: '16', y1: '17', x2: '8', y2: '17' })
  ])},
  { key: 'categories', label: '分类数量', icon: h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('path', { d: 'M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z' })
  ])},
  { key: 'tags', label: '标签数量', icon: h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('path', { d: 'M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z' }),
    h('line', { x1: '7', y1: '7', x2: '7.01', y2: '7' })
  ])},
  { key: 'views', label: '总浏览量', icon: h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('path', { d: 'M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z' }),
    h('circle', { cx: '12', cy: '12', r: '3' })
  ])}
]

const animateValue = (key, target) => {
  const duration = 1000
  const start = animatedStats[key]
  const startTime = performance.now()
  
  const update = (currentTime) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    const easeOut = 1 - Math.pow(1 - progress, 3)
    animatedStats[key] = Math.floor(start + (target - start) * easeOut)
    if (progress < 1) requestAnimationFrame(update)
  }
  requestAnimationFrame(update)
}

onMounted(async () => {
  try {
    const [articlesRes, categoriesRes, tagsRes] = await Promise.all([
      articleApi.list({ page: 1, pageSize: 1 }),
      categoryApi.list(),
      tagApi.list()
    ])
    stats.value = {
      articles: articlesRes.data?.total || 0,
      categories: categoriesRes.data?.length || 0,
      tags: tagsRes.data?.length || 0,
      views: 0
    }
    Object.keys(stats.value).forEach(key => {
      setTimeout(() => animateValue(key, stats.value[key]), 200)
    })
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

.dashboard {
  max-width: 1200px;
}

.welcome-section {
  margin-bottom: 2.5rem;
}

.welcome-title {
  font-family: 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 0.5rem;
  letter-spacing: -0.02em;
}

.welcome-subtitle {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 1rem;
  color: var(--text-muted);
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: var(--bg-secondary);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1.5rem;
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px var(--shadow);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-icon.articles {
  background: var(--accent-light);
  color: var(--accent);
}

.stat-icon.categories {
  background: rgba(212, 165, 116, 0.15);
  color: #d4a574;
}

.stat-icon.tags {
  background: rgba(139, 115, 85, 0.1);
  color: #8b7355;
}

.stat-icon.views {
  background: rgba(92, 82, 70, 0.1);
  color: #5c5246;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-family: 'Playfair Display', serif;
  font-size: 2.25rem;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: 0.375rem;
}

.stat-label {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.875rem;
  color: var(--text-muted);
  font-weight: 500;
}

.stat-decoration {
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent-light) 0%, rgba(212, 165, 116, 0.08) 100%);
}

.section-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 1.5rem;
  letter-spacing: -0.01em;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem 1.5rem;
  background: var(--bg-secondary);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  border: 1px solid var(--border-color);
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.action-card:hover {
  border-color: var(--accent);
  transform: translateX(4px);
  box-shadow: 0 8px 16px var(--shadow);
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-icon svg {
  width: 22px;
  height: 22px;
}

.action-icon.write {
  background: linear-gradient(135deg, var(--accent) 0%, #d4a574 100%);
  color: #fff;
}

.action-icon.category {
  background: rgba(212, 165, 116, 0.2);
  color: #d4a574;
}

.action-icon.tag {
  background: rgba(139, 115, 85, 0.15);
  color: #8b7355;
}

.action-icon.visit {
  background: rgba(92, 82, 70, 0.1);
  color: #5c5246;
}

.action-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.action-title {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
}

.action-desc {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.8rem;
  color: var(--text-muted);
}

.action-arrow {
  width: 20px;
  height: 20px;
  color: var(--text-muted);
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.action-card:hover .action-arrow {
  color: var(--accent);
  transform: translateX(4px);
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-title {
    font-size: 1.75rem;
  }
}
</style>