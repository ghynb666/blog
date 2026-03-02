<template>
  <article class="article-card" @click="$router.push(`/article/${article.id}`)">
    <div class="card-content">
      <div class="card-header">
        <span v-if="article.category?.name" class="category">{{ article.category.name }}</span>
        <time class="date">{{ formatDate(article.createdAt) }}</time>
      </div>
      <h2 class="title">{{ article.title }}</h2>
      <p class="summary">{{ article.summary }}</p>
      <div class="card-footer">
        <div class="tags">
          <span v-for="tag in article.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
        </div>
        <span class="read-more">阅读全文 →</span>
      </div>
    </div>
  </article>
</template>

<script setup>
defineProps({ article: Object })
const formatDate = d => {
  const date = new Date(d)
  return `${date.getFullYear()}.${String(date.getMonth()+1).padStart(2,'0')}.${String(date.getDate()).padStart(2,'0')}`
}
</script>

<style scoped>
.article-card {
  background: var(--card);
  border-radius: 16px;
  border: 1px solid var(--border);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}
.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px var(--shadow);
  border-color: var(--accent);
}
.card-content { padding: 28px 32px; }
.card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.category {
  background: var(--accent-light);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.date { color: var(--muted); font-size: 13px; font-weight: 400; }
.title {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  color: var(--fg);
  margin: 0 0 12px;
  line-height: 1.4;
  transition: color 0.3s;
}
.article-card:hover .title { color: var(--accent); }
.summary {
  color: var(--muted);
  font-size: 15px;
  line-height: 1.7;
  margin: 0 0 20px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.tags { display: flex; gap: 8px; flex-wrap: wrap; }
.tag {
  font-size: 12px;
  color: var(--muted);
  padding: 4px 10px;
  background: var(--bg);
  border-radius: 6px;
  transition: all 0.3s;
}
.article-card:hover .tag { background: var(--accent-light); color: var(--accent); }
.read-more {
  font-size: 13px;
  font-weight: 500;
  color: var(--accent);
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s;
}
.article-card:hover .read-more { opacity: 1; transform: translateX(0); }
@media (max-width: 600px) {
  .card-content { padding: 20px; }
  .title { font-size: 20px; }
  .read-more { display: none; }
}
</style>
