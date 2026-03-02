<template>
  <div class="tag-page">
    <header class="page-header">
      <span class="label">标签</span>
      <h1 class="title"># {{ tag?.name }}</h1>
      <p class="count">{{ total }} 篇文章</p>
    </header>
    <div class="articles">
      <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    </div>
    <Pagination :page="page" :page-size="pageSize" :total="total" @change="loadArticles" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { frontApi } from '@/api/front'
import ArticleCard from '@/components/ArticleCard.vue'
import Pagination from '@/components/Pagination.vue'
const route = useRoute()
const tag = ref(null)
const articles = ref([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loadArticles = async p => {
  page.value = p || 1
  const res = await frontApi.getArticlesByTag(route.params.id, { page: page.value, pageSize })
  articles.value = res.data?.records || []
  total.value = res.data?.total || 0
}
const loadTag = async () => {
  const res = await frontApi.getTags()
  tag.value = (res.data || []).find(t => t.id === Number(route.params.id))
}
watch(() => route.params.id, () => { loadArticles(); loadTag() }, { immediate: true })
</script>

<style scoped>
.tag-page { min-height: 400px; }
.page-header { margin-bottom: 32px; }
.label {
  display: inline-block;
  background: var(--accent-light);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
}
.title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 700;
  color: var(--fg);
  margin: 0 0 8px;
}
.count { color: var(--muted); font-size: 14px; margin: 0; }
.articles { display: flex; flex-direction: column; gap: 24px; }
</style>
