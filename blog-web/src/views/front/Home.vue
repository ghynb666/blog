<template>
  <div class="home">
    <transition-group name="fade" tag="div" class="articles">
      <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    </transition-group>
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

<style scoped>
.home { min-height: 400px; }
.articles { display: flex; flex-direction: column; gap: 24px; }
.fade-enter-active, .fade-leave-active { transition: all 0.4s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(20px); }
</style>
