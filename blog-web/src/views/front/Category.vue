<template>
  <div class="category-page">
    <header class="page-header">
      <span class="label">分类</span>
      <h1 class="title">{{ category?.name }}</h1>
      <p class="count">{{ total }} 篇文章</p>
    </header>
    <div class="articles">
      <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    </div>
    <Pagination :page="page" :page-size="pageSize" :total="total" @change="loadArticles" />
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { frontApi } from '@/api/front'
import ArticleCard from '@/components/ArticleCard.vue'
import Pagination from '@/components/Pagination.vue'
const route = useRoute()
const category = ref(null)
const articles = ref([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const tagIds = computed(() => {
  const ids = route.query.tagIds
  return ids ? ids.split(',').map(Number) : null
})
const loadArticles = async p => {
  page.value = p || 1
  const params = { page: page.value, pageSize }
  const res = await frontApi.getArticlesByCategory(route.params.id, params, tagIds.value)
  articles.value = res.data?.records || []
  total.value = res.data?.total || 0
}
const loadCategory = async () => {
  const res = await frontApi.getCategories()
  category.value = (res.data || []).find(c => c.id === Number(route.params.id))
}
watch(() => route.params.id, () => { loadArticles(); loadCategory() }, { immediate: true })
watch(() => route.query.tagIds, () => loadArticles())
</script>

<style scoped>
.category-page { min-height: 400px; }
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
