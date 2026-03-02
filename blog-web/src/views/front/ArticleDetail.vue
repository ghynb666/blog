<template>
  <div class="article-detail" v-if="article">
    <article class="article">
      <header class="article-header">
        <div class="header-meta">
          <span v-if="article.category?.name" class="category">{{ article.category.name }}</span>
          <time class="date">{{ formatDate(article.createdAt) }}</time>
        </div>
        <h1 class="title">{{ article.title }}</h1>
      </header>
      <div class="content" v-html="htmlContent"></div>
      <footer class="article-footer">
        <div class="tags" v-if="article.tags?.length">
          <span v-for="tag in article.tags" :key="tag.id" class="tag"># {{ tag.name }}</span>
        </div>
      </footer>
    </article>
    <div class="toc-wrapper" v-if="headings.length">
      <Toc :headings="headings" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import { frontApi } from '@/api/front'
import Toc from '@/components/Toc.vue'
const route = useRoute()
const article = ref(null)
const headings = ref([])
marked.setOptions({
  highlight: (code, lang) => lang && hljs.getLanguage(lang) ? hljs.highlight(code, { language: lang }).value : code,
  gfm: true
})
const htmlContent = computed(() => {
  if (!article.value?.content) return ''
  const html = marked.parse(article.value.content)
  setTimeout(extractHeadings, 0)
  return html
})
const extractHeadings = () => {
  const els = document.querySelectorAll('.content h1, .content h2, .content h3, .content h4, .content h5, .content h6')
  headings.value = Array.from(els).map((el, i) => {
    const id = `heading-${i}`
    el.id = id
    return { id, level: parseInt(el.tagName[1]), text: el.textContent }
  })
}
const formatDate = d => {
  const date = new Date(d)
  return `${date.getFullYear()}年${date.getMonth()+1}月${date.getDate()}日`
}
onMounted(async () => {
  const res = await frontApi.getArticle(route.params.id)
  article.value = res.data
})
</script>

<style scoped>
.article-detail { display: flex; gap: 48px; }
.article {
  flex: 1;
  background: var(--card);
  border-radius: 16px;
  border: 1px solid var(--border);
  overflow: hidden;
}
.article-header { padding: 40px 48px 32px; border-bottom: 1px solid var(--border); }
.header-meta { display: flex; align-items: center; gap: 16px; margin-bottom: 16px; }
.category {
  background: var(--accent-light);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
  padding: 4px 14px;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.date { color: var(--muted); font-size: 14px; }
.title {
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 700;
  color: var(--fg);
  margin: 0;
  line-height: 1.3;
  letter-spacing: -0.5px;
}
.content {
  padding: 40px 48px;
  line-height: 1.9;
  color: var(--fg);
  font-size: 16px;
}
.content :deep(h1), .content :deep(h2), .content :deep(h3),
.content :deep(h4), .content :deep(h5), .content :deep(h6) {
  font-family: var(--font-display);
  color: var(--fg);
  margin: 48px 0 24px;
  line-height: 1.4;
}
.content :deep(h2) { font-size: 28px; border-bottom: 1px solid var(--border); padding-bottom: 12px; }
.content :deep(h3) { font-size: 22px; }
.content :deep(p) { margin: 0 0 24px; }
.content :deep(a) { color: var(--accent); text-decoration: none; border-bottom: 1px solid transparent; transition: border-color 0.3s; }
.content :deep(a:hover) { border-bottom-color: var(--accent); }
.content :deep(pre) {
  background: var(--bg);
  border: 1px solid var(--border);
  padding: 20px 24px;
  border-radius: 12px;
  overflow-x: auto;
  margin: 24px 0;
}
.content :deep(code) {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 14px;
  background: var(--bg);
  padding: 2px 6px;
  border-radius: 4px;
}
.content :deep(pre code) { background: none; padding: 0; }
.content :deep(blockquote) {
  border-left: 4px solid var(--accent);
  padding: 16px 24px;
  margin: 24px 0;
  background: var(--bg);
  border-radius: 0 8px 8px 0;
  color: var(--muted);
  font-style: italic;
}
.content :deep(ul), .content :deep(ol) { padding-left: 24px; margin: 16px 0; }
.content :deep(li) { margin: 8px 0; }
.content :deep(img) { max-width: 100%; border-radius: 8px; margin: 24px 0; }
.content :deep(hr) { border: none; border-top: 1px solid var(--border); margin: 48px 0; }
.content :deep(table) { width: 100%; border-collapse: collapse; margin: 24px 0; }
.content :deep(th), .content :deep(td) { border: 1px solid var(--border); padding: 12px 16px; text-align: left; }
.content :deep(th) { background: var(--bg); font-weight: 600; }
.article-footer { padding: 24px 48px; border-top: 1px solid var(--border); }
.tags { display: flex; gap: 12px; flex-wrap: wrap; }
.tag {
  color: var(--accent);
  font-size: 14px;
  padding: 6px 14px;
  background: var(--accent-light);
  border-radius: 20px;
  transition: all 0.3s;
}
.tag:hover { background: var(--accent); color: #fff; }
.toc-wrapper { width: 260px; flex-shrink: 0; position: sticky; top: 96px; height: fit-content; }
@media (max-width: 900px) {
  .article-detail { flex-direction: column; }
  .toc-wrapper { display: none; }
}
@media (max-width: 600px) {
  .article-header { padding: 24px; }
  .content { padding: 24px; }
  .title { font-size: 28px; }
  .article-footer { padding: 20px 24px; }
}
</style>
