<template>
  <div class="archives">
    <h2>归档</h2>
    <div class="timeline">
      <div v-for="(group, year) in archives" :key="year" class="year-group">
        <h3>{{ year }}</h3>
        <div v-for="item in group" :key="item.id" class="item" @click="$router.push(`/article/${item.id}`)">
          <span class="date">{{ formatDate(item.createdAt) }}</span>
          <span class="title">{{ item.title }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { frontApi } from '@/api/front'
const articles = ref([])
const archives = computed(() => {
  const groups = {}
  articles.value.forEach(a => {
    const year = new Date(a.createdAt).getFullYear()
    if (!groups[year]) groups[year] = []
    groups[year].push(a)
  })
  return Object.keys(groups).sort((a, b) => b - a).reduce((obj, key) => { obj[key] = groups[key]; return obj }, {})
})
const formatDate = d => new Date(d).toLocaleDateString('zh-CN')
onMounted(async () => {
  const res = await frontApi.getArticles({ pageSize: 1000 })
  articles.value = res.data?.list || []
})
</script>

<style scoped>
.archives { background: #fff; border-radius: 8px; padding: 24px; }
h2 { margin: 0 0 24px; font-size: 20px; }
.year-group { margin-bottom: 24px; }
.year-group h3 { font-size: 18px; color: #1890ff; margin-bottom: 12px; }
.item { display: flex; gap: 16px; padding: 12px 0; border-bottom: 1px solid #f0f0f0; cursor: pointer; }
.item:hover .title { color: #1890ff; }
.date { color: #999; font-size: 14px; min-width: 100px; }
.title { color: #333; transition: color 0.3s; }
</style>
