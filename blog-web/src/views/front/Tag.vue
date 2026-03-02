<template>
  <div class="tag-page">
    <h2>标签：{{ tag?.name }}</h2>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
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
.tag-page { background: #fff; border-radius: 8px; padding: 24px; }
h2 { margin: 0 0 24px; font-size: 20px; }
</style>
