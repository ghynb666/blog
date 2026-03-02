<template>
  <div class="article-card" @click="$router.push(`/article/${article.id}`)">
    <h3 class="title">{{ article.title }}</h3>
    <div class="meta">
      <span>{{ formatDate(article.createdAt) }}</span>
      <span v-if="article.category?.name" class="category">{{ article.category.name }}</span>
    </div>
    <p class="summary">{{ article.summary }}</p>
    <div class="tags">
      <span v-for="tag in article.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({ article: Object })
const formatDate = d => new Date(d).toLocaleDateString('zh-CN')
</script>

<style scoped>
.article-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: box-shadow 0.3s;
}
.article-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
.title { margin: 0 0 8px; font-size: 18px; color: #333; }
.meta { font-size: 13px; color: #999; margin-bottom: 12px; }
.meta span { margin-right: 16px; }
.category { color: #1890ff; }
.summary { color: #666; font-size: 14px; line-height: 1.6; margin: 0 0 12px; }
.tags { display: flex; gap: 8px; flex-wrap: wrap; }
.tag {
  font-size: 12px;
  padding: 2px 8px;
  background: #f0f0f0;
  border-radius: 4px;
  color: #666;
}
</style>
