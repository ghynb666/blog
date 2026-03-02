<template>
  <div class="tags-page">
    <header class="page-header">
      <span class="label">多标签筛选</span>
      <h1 class="title">标签组合</h1>
    </header>
    <div class="selected-tags" v-if="selectedTags.length">
      <span v-for="tag in selectedTags" :key="tag.id" class="selected-tag"># {{ tag.name }}</span>
    </div>
    <div class="articles">
      <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    </div>
    <Pagination :page="page" :page-size="pageSize" :total="total" @change="loadArticles" />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { frontApi } from '@/api/front'
import ArticleCard from '@/components/ArticleCard.vue'
import Pagination from '@/components/Pagination.vue'
const route = useRoute()
const articles = ref([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const allTags = ref([])
const tagIds = computed(() => route.params.ids ? route.params.ids.split(',').map(Number) : [])
const categoryId = computed(() => route.query.categoryId ? Number(route.query.categoryId) : null)
const selectedTags = computed(() => allTags.value.filter(t => tagIds.value.includes(t.id)))
const loadArticles = async p => {
  page.value = p || 1
  if (tagIds.value.length === 0) { articles.value = []; total.value = 0; return }
  const res = await frontApi.getArticlesByTags(tagIds.value, { page: page.value, pageSize }, categoryId.value)
  articles.value = res.data?.records || []
  total.value = res.data?.total || 0
}
const loadTags = async () => { const res = await frontApi.getTags(); allTags.value = res.data || [] }
watch(() => route.params.ids, () => { loadArticles() }, { immediate: true })
watch(() => route.query.categoryId, () => loadArticles())
loadTags()
</script>

<style scoped>
.tags-page { min-height: 400px; }
.page-header { margin-bottom: 24px; }
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
  margin: 0;
}
.selected-tags { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 32px; }
.selected-tag {
  padding: 8px 16px;
  background: var(--accent);
  color: #fff;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}
.articles { display: flex; flex-direction: column; gap: 24px; }
</style>
