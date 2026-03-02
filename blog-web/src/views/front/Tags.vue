<template>
  <div class="tags-page">
    <h2>标签筛选</h2>
    <div class="selected-tags">
      <span v-for="tag in selectedTags" :key="tag.id" class="selected-tag">{{ tag.name }}</span>
    </div>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
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

const tagIds = computed(() => {
  return route.params.ids ? route.params.ids.split(',').map(Number) : []
})

const selectedTags = computed(() => {
  return allTags.value.filter(t => tagIds.value.includes(t.id))
})

const loadArticles = async p => {
  page.value = p || 1
  if (tagIds.value.length === 0) {
    articles.value = []
    total.value = 0
    return
  }
  const res = await frontApi.getArticlesByTags(tagIds.value, { page: page.value, pageSize })
  articles.value = res.data?.records || []
  total.value = res.data?.total || 0
}

const loadTags = async () => {
  const res = await frontApi.getTags()
  allTags.value = res.data || []
}

watch(() => route.params.ids, () => { loadArticles() }, { immediate: true })
loadTags()
</script>

<style scoped>
.tags-page { background: #fff; border-radius: 8px; padding: 24px; }
h2 { margin: 0 0 16px; font-size: 20px; }
.selected-tags { margin-bottom: 24px; }
.selected-tag {
  display: inline-block;
  padding: 4px 12px;
  background: #1890ff;
  color: #fff;
  border-radius: 16px;
  font-size: 13px;
  margin-right: 8px;
}
</style>
