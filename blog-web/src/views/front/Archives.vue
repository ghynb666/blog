<template>
  <div class="archives">
    <header class="page-header">
      <h1 class="page-title">归档</h1>
      <p class="page-desc">共 {{ totalCount }} 篇文章</p>
    </header>
    <div class="timeline">
      <div v-for="(group, year) in archives" :key="year" class="year-group">
        <div class="year-header">
          <span class="year">{{ year }}</span>
          <span class="count">{{ group.length }} 篇</span>
        </div>
        <div class="items">
          <div v-for="item in group" :key="item.id" class="item" @click="$router.push(`/article/${item.id}`)">
            <time class="item-date">{{ formatDate(item.createdAt) }}</time>
            <h3 class="item-title">{{ item.title }}</h3>
            <span v-if="item.category?.name" class="item-category">{{ item.category.name }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { frontApi } from '@/api/front'
const articles = ref([])
const totalCount = computed(() => articles.value.length)
const archives = computed(() => {
  const groups = {}
  articles.value.forEach(a => {
    const year = new Date(a.createdAt).getFullYear()
    if (!groups[year]) groups[year] = []
    groups[year].push(a)
  })
  return Object.keys(groups).sort((a, b) => b - a).reduce((obj, key) => { obj[key] = groups[key]; return obj }, {})
})
const formatDate = d => {
  const date = new Date(d)
  return `${date.getMonth()+1}.${date.getDate()}`
}
onMounted(async () => {
  const res = await frontApi.getArticles({ pageSize: 1000 })
  articles.value = res.data?.records || []
})
</script>

<style scoped>
.archives { min-height: 400px; }
.page-header { margin-bottom: 40px; }
.page-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 700;
  color: var(--fg);
  margin: 0 0 8px;
}
.page-desc { color: var(--muted); font-size: 15px; margin: 0; }
.year-group { margin-bottom: 48px; }
.year-header {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--border);
}
.year {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--accent);
}
.count { color: var(--muted); font-size: 14px; }
.items { display: flex; flex-direction: column; gap: 4px; }
.item {
  display: grid;
  grid-template-columns: 60px 1fr auto;
  gap: 20px;
  align-items: center;
  padding: 16px 20px;
  background: var(--card);
  border-radius: 12px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.3s;
}
.item:hover {
  border-color: var(--accent);
  transform: translateX(8px);
}
.item-date {
  font-size: 14px;
  font-weight: 500;
  color: var(--muted);
  font-variant-numeric: tabular-nums;
}
.item-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--fg);
  margin: 0;
  transition: color 0.3s;
}
.item:hover .item-title { color: var(--accent); }
.item-category {
  font-size: 12px;
  color: var(--accent);
  background: var(--accent-light);
  padding: 4px 12px;
  border-radius: 20px;
}
@media (max-width: 600px) {
  .page-title { font-size: 26px; }
  .year { font-size: 22px; }
  .item {
    grid-template-columns: 1fr;
    gap: 8px;
    padding: 14px 16px;
  }
  .item-category { justify-self: start; }
}
</style>
