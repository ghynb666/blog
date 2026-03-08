<template>
  <div class="dashboard">
    <section class="hero">
      <div>
        <p class="eyebrow">Growth Overview</p>
        <h1>P0 Analytics Dashboard</h1>
        <p class="intro">围绕注册、登录、评论、点赞、订阅五条链路，展示最近 7 天的增长基础数据。</p>
      </div>
      <div class="hero-actions">
        <button class="ghost-btn" @click="loadOverview" :disabled="loading">{{ loading ? 'Refreshing...' : 'Refresh' }}</button>
      </div>
    </section>

    <section class="summary-grid">
      <article v-for="item in summaryCards" :key="item.key" class="summary-card">
        <span class="summary-label">{{ item.label }}</span>
        <strong class="summary-value">{{ item.value }}</strong>
      </article>
    </section>

    <section class="detail-grid">
      <div class="panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">Daily Metrics</p>
            <h2>Recent 7 days</h2>
          </div>
        </div>
        <div class="metric-list">
          <div class="metric-row metric-row--head">
            <span>Date</span>
            <span>Register</span>
            <span>Login</span>
            <span>Comment</span>
            <span>Like</span>
            <span>Subscribe</span>
          </div>
          <div v-for="row in overview.dailyMetrics" :key="row.date" class="metric-row">
            <span>{{ row.date }}</span>
            <span>{{ row.registrations }}</span>
            <span>{{ row.logins }}</span>
            <span>{{ row.comments }}</span>
            <span>{{ row.likes }}</span>
            <span>{{ row.subscriptions }}</span>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">Execution Focus</p>
            <h2>P0 status</h2>
          </div>
        </div>
        <ul class="focus-list">
          <li>注册链路已接入统一返回体和事件记录。</li>
          <li>评论、点赞走登录校验，未登录不会跳到后台登录页。</li>
          <li>订阅默认以留资入库作为最小交付，并记录来源页。</li>
          <li>后台概览直接基于增长事件表聚合，便于周复盘。</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { analyticsApi } from '@/api/analytics'

const loading = ref(false)
const overview = reactive({
  summary: { registrations: 0, logins: 0, comments: 0, likes: 0, subscriptions: 0 },
  dailyMetrics: []
})

const summaryCards = computed(() => [
  { key: 'registrations', label: 'Registrations', value: overview.summary.registrations },
  { key: 'logins', label: 'Logins', value: overview.summary.logins },
  { key: 'comments', label: 'Comments', value: overview.summary.comments },
  { key: 'likes', label: 'Likes', value: overview.summary.likes },
  { key: 'subscriptions', label: 'Subscriptions', value: overview.summary.subscriptions }
])

const loadOverview = async () => {
  loading.value = true
  try {
    const res = await analyticsApi.overview({ days: 7 })
    overview.summary = res.data?.summary || overview.summary
    overview.dailyMetrics = res.data?.dailyMetrics || []
  } finally {
    loading.value = false
  }
}

onMounted(loadOverview)
</script>

<style scoped>
.dashboard { max-width: 1200px; }
.hero, .summary-card, .panel {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  box-shadow: 0 12px 24px var(--shadow);
}
.hero {
  padding: 28px 32px;
  display: flex;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 20px;
}
.eyebrow {
  margin: 0 0 8px;
  color: var(--accent);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  font-weight: 700;
}
h1, h2 {
  margin: 0;
  font-family: 'Playfair Display', serif;
  color: var(--text-primary);
}
h1 { font-size: 36px; margin-bottom: 10px; }
h2 { font-size: 26px; }
.intro { margin: 0; color: var(--text-muted); max-width: 760px; }
.ghost-btn {
  border: 1px solid var(--border-color);
  background: transparent;
  color: var(--text-primary);
  padding: 12px 18px;
  border-radius: 12px;
  cursor: pointer;
}
.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}
.summary-card {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.summary-label { color: var(--text-muted); font-weight: 600; }
.summary-value {
  font-size: 32px;
  color: var(--text-primary);
  font-family: 'Playfair Display', serif;
}
.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.8fr);
  gap: 20px;
}
.panel { padding: 24px; }
.panel-header { margin-bottom: 18px; }
.metric-list { display: grid; gap: 10px; }
.metric-row {
  display: grid;
  grid-template-columns: 1.4fr repeat(5, 1fr);
  gap: 12px;
  align-items: center;
  padding: 12px 14px;
  border-radius: 14px;
  background: var(--bg-tertiary);
}
.metric-row--head {
  font-weight: 700;
  color: var(--text-primary);
  background: transparent;
  padding: 0 14px 4px;
}
.focus-list {
  margin: 0;
  padding-left: 18px;
  color: var(--text-secondary);
  display: grid;
  gap: 12px;
}
@media (max-width: 1100px) {
  .summary-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
  .detail-grid { grid-template-columns: 1fr; }
}
@media (max-width: 720px) {
  .hero { flex-direction: column; padding: 24px 20px; }
  .summary-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .metric-list { overflow-x: auto; }
  .metric-row { min-width: 720px; }
}
</style>
