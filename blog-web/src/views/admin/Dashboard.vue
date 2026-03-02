<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="文章总数" :value="stats.articles" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="分类数" :value="stats.categories" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="标签数" :value="stats.tags" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="浏览量" :value="stats.views" /></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as articleApi from '@/api/article'
import * as categoryApi from '@/api/category'
import * as tagApi from '@/api/tag'

const stats = ref({ articles: 0, categories: 0, tags: 0, views: 0 })

onMounted(async () => {
  const [articles, categories, tags] = await Promise.all([
    articleApi.list({ page: 1, pageSize: 1 }),
    categoryApi.list(),
    tagApi.list()
  ])
  stats.value = {
    articles: articles.total || 0,
    categories: categories.length || 0,
    tags: tags.length || 0,
    views: 0
  }
})
</script>

<style scoped>
.dashboard .el-card { text-align: center; margin-bottom: 20px; }
</style>
