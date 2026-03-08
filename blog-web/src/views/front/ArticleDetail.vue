<template>
  <div class="article-detail" v-if="article">
    <article class="article">
      <header class="article-header">
        <div class="header-meta">
          <span v-if="article.categoryName" class="category">{{ article.categoryName }}</span>
          <time class="date">{{ formatDate(article.createdAt) }}</time>
          <span class="metric">{{ interaction.likeCount }} likes</span>
          <span class="metric">{{ interaction.commentCount }} comments</span>
        </div>
        <h1 class="title">{{ article.title }}</h1>
        <p class="summary" v-if="article.summary">{{ article.summary }}</p>
      </header>

      <div class="interaction-bar">
        <button class="interaction-btn" :class="{ active: interaction.liked }" @click="handleToggleLike">
          <span>{{ interaction.liked ? 'Liked' : 'Like' }}</span>
          <strong>{{ interaction.likeCount }}</strong>
        </button>
        <button class="interaction-btn" @click="scrollToComments">
          <span>Comment</span>
          <strong>{{ interaction.commentCount }}</strong>
        </button>
      </div>

      <div class="content" v-html="htmlContent"></div>

      <footer class="article-footer">
        <div class="tags" v-if="article.tags?.length">
          <span v-for="tag in article.tags" :key="tag.id" class="tag"># {{ tag.name }}</span>
        </div>
      </footer>
    </article>

    <section class="subscribe-card">
      <div>
        <p class="eyebrow">Subscribe</p>
        <h2>Get the next post in your inbox</h2>
        <p class="subscribe-copy">P0 版本先提供留资订阅，提交后会记录来源页并进入后台统计。</p>
      </div>
      <form class="subscribe-form" @submit.prevent="handleSubscribe">
        <input v-model.trim="subscription.email" type="email" placeholder="you@example.com" />
        <button type="submit" :disabled="submitting.subscribe">{{ submitting.subscribe ? 'Submitting...' : 'Subscribe' }}</button>
      </form>
    </section>

    <section id="comments" class="comments-section">
      <div class="section-header">
        <div>
          <p class="eyebrow">Comments</p>
          <h2>{{ comments.length }} conversations</h2>
        </div>
        <router-link v-if="!userStore.token" to="/login" class="login-link">Login to join</router-link>
      </div>

      <form class="comment-form" @submit.prevent="handleComment">
        <textarea
          v-model.trim="commentForm.content"
          :placeholder="userStore.token ? 'Write something useful...' : 'Login first to comment...'"
          :disabled="!userStore.token || submitting.comment"
          maxlength="500"
        />
        <div class="comment-actions">
          <span>{{ commentForm.content.length }}/500</span>
          <button type="submit" :disabled="!userStore.token || !commentForm.content || submitting.comment">
            {{ submitting.comment ? 'Posting...' : 'Post Comment' }}
          </button>
        </div>
      </form>

      <div class="comment-list">
        <article v-for="comment in comments" :key="comment.id" class="comment-card">
          <div class="comment-avatar">{{ avatarText(comment.user) }}</div>
          <div class="comment-body">
            <div class="comment-meta">
              <strong>{{ comment.user?.nickname || comment.user?.username || 'User' }}</strong>
              <span>{{ formatDateTime(comment.createdAt) }}</span>
            </div>
            <p>{{ comment.content }}</p>
          </div>
        </article>
        <div v-if="comments.length === 0" class="empty-comments">No comments yet. Be the first one.</div>
      </div>
    </section>

    <div class="toc-wrapper" v-if="headings.length">
      <Toc :headings="headings" />
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import { frontApi } from '@/api/front'
import { useUserStore } from '@/store/user'
import Toc from '@/components/Toc.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const article = ref(null)
const comments = ref([])
const headings = ref([])
const interaction = reactive({ commentCount: 0, likeCount: 0, liked: false })
const commentForm = reactive({ content: '' })
const subscription = reactive({ email: userStore.userInfo?.email || '' })
const submitting = reactive({ comment: false, subscribe: false, like: false })

marked.setOptions({
  highlight: (code, lang) => (lang && hljs.getLanguage(lang) ? hljs.highlight(code, { language: lang }).value : code),
  gfm: true
})

const htmlContent = computed(() => {
  if (!article.value?.content) return ''
  return marked.parse(article.value.content)
})

watch(htmlContent, async () => {
  await nextTick()
  extractHeadings()
  applySeo()
})

watch(
  () => userStore.userInfo?.email,
  value => {
    if (value && !subscription.email) {
      subscription.email = value
    }
  },
  { immediate: true }
)

watch(
  () => route.params.id,
  () => {
    loadDetail()
  }
)

const loadDetail = async () => {
  const [articleRes, commentsRes, interactionRes] = await Promise.all([
    frontApi.getArticle(route.params.id),
    frontApi.getComments(route.params.id),
    frontApi.getArticleInteraction(route.params.id)
  ])
  article.value = articleRes.data
  comments.value = commentsRes.data || []
  Object.assign(interaction, interactionRes.data || {})
}

const handleToggleLike = async () => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  submitting.like = true
  try {
    const res = await frontApi.toggleLike(route.params.id)
    Object.assign(interaction, res.data || {})
  } finally {
    submitting.like = false
  }
}

const handleComment = async () => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  submitting.comment = true
  try {
    const res = await frontApi.createComment(route.params.id, { content: commentForm.content })
    comments.value.unshift(res.data)
    commentForm.content = ''
    interaction.commentCount = comments.value.length
    ElMessage.success('Comment posted')
  } finally {
    submitting.comment = false
  }
}

const handleSubscribe = async () => {
  submitting.subscribe = true
  try {
    await frontApi.subscribe({
      email: subscription.email,
      sourcePage: `/article/${route.params.id}`
    })
    ElMessage.success('Subscription saved')
  } finally {
    submitting.subscribe = false
  }
}

const extractHeadings = () => {
  const els = document.querySelectorAll('.content h1, .content h2, .content h3, .content h4')
  headings.value = Array.from(els).map((el, index) => {
    const id = `heading-${index}`
    el.id = id
    return { id, level: Number(el.tagName.slice(1)), text: el.textContent }
  })
}

const ensureMeta = (name, attribute = 'name') => {
  let meta = document.head.querySelector(`meta[${attribute}="${name}"]`)
  if (!meta) {
    meta = document.createElement('meta')
    meta.setAttribute(attribute, name)
    document.head.appendChild(meta)
  }
  return meta
}

const applySeo = () => {
  if (!article.value) return
  document.title = `${article.value.title} | Blog`
  ensureMeta('description').setAttribute('content', article.value.summary || article.value.title)
  ensureMeta('keywords').setAttribute('content', (article.value.tags || []).map(tag => tag.name).join(','))
  let link = document.head.querySelector('link[rel="canonical"]')
  if (!link) {
    link = document.createElement('link')
    link.setAttribute('rel', 'canonical')
    document.head.appendChild(link)
  }
  link.setAttribute('href', window.location.href)
}

const scrollToComments = () => {
  document.getElementById('comments')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const formatDate = value => {
  if (!value) return ''
  return new Intl.DateTimeFormat('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date(value))
}

const formatDateTime = value => {
  if (!value) return ''
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value))
}

const avatarText = user => (user?.nickname || user?.username || 'U').slice(0, 1).toUpperCase()

onMounted(loadDetail)
</script>

<style scoped>
.article-detail { display: grid; grid-template-columns: minmax(0, 1fr) 260px; gap: 32px; }
.article, .subscribe-card, .comments-section {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 20px;
  box-shadow: 0 10px 30px var(--shadow);
}
.article-header { padding: 36px 40px 28px; border-bottom: 1px solid var(--border); }
.header-meta { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; margin-bottom: 12px; }
.category, .metric {
  padding: 6px 12px;
  border-radius: 999px;
  background: var(--accent-light);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
}
.date { color: var(--muted); font-size: 14px; }
.title { margin: 0; font-family: var(--font-display); font-size: 40px; line-height: 1.2; }
.summary { margin: 14px 0 0; color: var(--muted); font-size: 16px; }
.interaction-bar {
  display: flex;
  gap: 12px;
  padding: 20px 40px;
  border-bottom: 1px solid var(--border);
}
.interaction-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--fg);
  border-radius: 999px;
  padding: 10px 16px;
  cursor: pointer;
}
.interaction-btn.active {
  background: var(--accent);
  color: #fff;
  border-color: var(--accent);
}
.content { padding: 36px 40px; line-height: 1.9; font-size: 16px; }
.content :deep(h1), .content :deep(h2), .content :deep(h3), .content :deep(h4) {
  margin: 40px 0 16px;
  font-family: var(--font-display);
}
.content :deep(h2) { border-bottom: 1px solid var(--border); padding-bottom: 10px; }
.content :deep(pre) {
  background: var(--bg);
  padding: 18px 20px;
  border-radius: 14px;
  overflow-x: auto;
  border: 1px solid var(--border);
}
.content :deep(code) { font-family: 'JetBrains Mono', monospace; }
.content :deep(img) { max-width: 100%; border-radius: 12px; }
.article-footer { padding: 24px 40px 36px; border-top: 1px solid var(--border); }
.tags { display: flex; flex-wrap: wrap; gap: 10px; }
.tag {
  padding: 8px 12px;
  border-radius: 999px;
  background: var(--accent-light);
  color: var(--accent);
  font-size: 13px;
}
.subscribe-card, .comments-section {
  grid-column: 1 / 2;
  padding: 28px 32px;
}
.eyebrow {
  margin: 0 0 8px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
}
.subscribe-card h2, .comments-section h2 {
  margin: 0 0 8px;
  font-family: var(--font-display);
  font-size: 28px;
}
.subscribe-copy { margin: 0; color: var(--muted); }
.subscribe-form {
  margin-top: 18px;
  display: flex;
  gap: 12px;
}
.subscribe-form input, .comment-form textarea {
  width: 100%;
  border: 1px solid var(--border);
  border-radius: 14px;
  background: var(--bg);
  color: var(--fg);
}
.subscribe-form input { padding: 14px 16px; }
.subscribe-form button, .comment-actions button {
  border: none;
  border-radius: 14px;
  background: var(--accent);
  color: #fff;
  padding: 0 18px;
  font-weight: 600;
  cursor: pointer;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.login-link { color: var(--accent); text-decoration: none; font-weight: 600; }
.comment-form { margin-top: 20px; }
.comment-form textarea {
  min-height: 120px;
  resize: vertical;
  padding: 16px;
}
.comment-actions {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--muted);
  font-size: 13px;
}
.comment-list { margin-top: 20px; display: flex; flex-direction: column; gap: 14px; }
.comment-card {
  display: flex;
  gap: 14px;
  padding: 18px;
  background: var(--bg);
  border-radius: 16px;
  border: 1px solid var(--border);
}
.comment-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: var(--accent);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}
.comment-meta {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 8px;
}
.comment-meta span { color: var(--muted); font-size: 13px; }
.comment-body p { margin: 0; white-space: pre-wrap; }
.empty-comments {
  padding: 24px;
  border: 1px dashed var(--border);
  border-radius: 16px;
  color: var(--muted);
  text-align: center;
}
.toc-wrapper {
  position: sticky;
  top: 96px;
  align-self: start;
}
@media (max-width: 980px) {
  .article-detail { grid-template-columns: 1fr; }
  .toc-wrapper { display: none; }
}
@media (max-width: 640px) {
  .article-header, .content, .article-footer, .interaction-bar, .subscribe-card, .comments-section { padding-left: 20px; padding-right: 20px; }
  .title { font-size: 30px; }
  .subscribe-form { flex-direction: column; }
}
</style>
