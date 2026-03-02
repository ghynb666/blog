<template>
  <div class="home">
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <Pagination :page="page" :page-size="pageSize" :total="total" @change="loadArticles" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { frontApi } from '@/api/front'
import ArticleCard from '@/components/ArticleCard.vue'
import Pagination from '@/components/Pagination.vue'
const articles = ref([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loadArticles = async p => {
  page.value = p || 1
  const res = await frontApi.getArticles({ page: page.value, pageSize })
  articles.value = res.data?.records || []
  total.value = res.data?.total || 0
}
onMounted(() => loadArticles())
</script>
