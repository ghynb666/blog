<template>
  <div class="article-detail" v-if="article">
    <article class="article">
      <h1>{{ article.title }}</h1>
      <div class="meta">
        <span>{{ formatDate(article.createdAt) }}</span>
        <span v-if="article.category?.name" class="category">{{ article.category.name }}</span>
      </div>
      <div class="content" v-html="htmlContent"></div>
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
const formatDate = d => new Date(d).toLocaleDateString('zh-CN')
onMounted(async () => {
  const res = await frontApi.getArticle(route.params.id)
  article.value = res.data
})
</script>

<style scoped>
.article-detail { display: flex; gap: 24px; }
.article { flex: 1; background: #fff; border-radius: 8px; padding: 24px; }
.article h1 { margin: 0 0 16px; font-size: 28px; }
.meta { font-size: 14px; color: #999; margin-bottom: 24px; }
.meta span { margin-right: 16px; }
.category { color: #1890ff; }
.content { line-height: 1.8; color: #333; }
.content :deep(pre) { background: #f6f8fa; padding: 16px; border-radius: 6px; overflow-x: auto; }
.content :deep(code) { font-family: Consolas, monospace; font-size: 14px; }
.content :deep(p) { margin: 16px 0; }
.content :deep(h2), .content :deep(h3) { margin: 24px 0 16px; }
.content :deep(ul), .content :deep(ol) { padding-left: 24px; }
.content :deep(blockquote) { border-left: 4px solid #ddd; padding-left: 16px; color: #666; margin: 16px 0; }
.toc-wrapper { width: 240px; flex-shrink: 0; }
</style>
