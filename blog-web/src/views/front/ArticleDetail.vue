<template>
  <div class="article-detail" v-if="article">
    <div class="article-main">
      <article class="article-shell">
        <header class="article-hero">
          <p class="hero-kicker">Article</p>
          <div class="hero-meta">
            <span v-if="article.categoryName" class="meta-pill">{{ article.categoryName }}</span>
            <span class="meta-text">Published {{ formatDate(article.createdAt) }}</span>
            <span class="meta-text">{{ readingMinutes }} min read</span>
          </div>

          <h1 class="hero-title">{{ article.title }}</h1>
          <p v-if="article.summary" class="hero-summary">{{ article.summary }}</p>

          <div class="hero-stats">
            <div class="stat-card">
              <span class="stat-label">Likes</span>
              <strong>{{ interaction.likeCount }}</strong>
            </div>
            <div class="stat-card">
              <span class="stat-label">Comments</span>
              <strong>{{ totalCommentCount }}</strong>
            </div>
            <div class="stat-card">
              <span class="stat-label">Views</span>
              <strong>{{ article.viewCount || 0 }}</strong>
            </div>
          </div>
        </header>

        <div class="article-toolbar">
          <button class="toolbar-btn primary" :class="{ active: interaction.liked }" @click="handleToggleLike">
            <span>{{ interaction.liked ? 'Liked' : 'Like this post' }}</span>
            <strong>{{ interaction.likeCount }}</strong>
          </button>
          <button class="toolbar-btn" @click="scrollToComments">
            <span>Jump to comments</span>
            <strong>{{ totalCommentCount }}</strong>
          </button>
        </div>

        <div class="article-content" v-html="htmlContent"></div>

        <footer class="article-footer">
          <div class="footer-group">
            <span class="footer-label">Filed under</span>
            <div class="tags" v-if="article.tags?.length">
              <span v-for="tag in article.tags" :key="tag.id" class="tag"># {{ tag.name }}</span>
            </div>
            <span v-else class="footer-note">No tags attached yet.</span>
          </div>
        </footer>
      </article>

      <section v-if="hasToc" class="mobile-outline">
        <div class="section-head">
          <div>
            <p class="section-kicker">Outline</p>
            <h2>In this post</h2>
          </div>
        </div>
        <Toc :headings="headings" />
      </section>

      <section class="subscribe-card">
        <div class="section-head">
          <div>
            <p class="section-kicker">Subscribe</p>
            <h2>Get the next post in your inbox</h2>
          </div>
          <p class="section-copy">Save future articles to your inbox and keep the reading loop going.</p>
        </div>
        <form class="subscribe-form" @submit.prevent="handleSubscribe">
          <input v-model.trim="subscription.email" type="email" placeholder="you@example.com" />
          <button type="submit" :disabled="submitting.subscribe">{{ submitting.subscribe ? 'Submitting...' : 'Subscribe' }}</button>
        </form>
      </section>

      <section id="comments" class="comments-section">
        <div class="section-head comments-head">
          <div>
            <p class="section-kicker">Comments</p>
            <h2>{{ totalCommentCount }} comments · {{ comments.length }} conversations</h2>
          </div>
          <router-link v-if="!userStore.token" to="/login" class="login-link">Login to join</router-link>
        </div>

        <form class="comment-form" @submit.prevent="handleComment">
          <div v-if="replyingTo" class="reply-banner">
            <span>Replying to {{ displayName(replyingTo.user) }}</span>
            <button type="button" class="reply-cancel" @click="cancelReply">Cancel</button>
          </div>
          <textarea
            ref="commentInput"
            v-model.trim="commentForm.content"
            :placeholder="commentPlaceholder"
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
          <CommentThread v-for="comment in comments" :key="comment.id" :comment="comment" @reply="startReply" />
          <div v-if="comments.length === 0" class="empty-comments">No comments yet. Be the first one.</div>
        </div>
      </section>
    </div>

    <aside class="article-rail">
      <section class="rail-card article-info-card">
        <p class="section-kicker">Article Info</p>
        <h2>Reading details</h2>
        <dl class="article-facts">
          <div>
            <dt>Published</dt>
            <dd>{{ formatDate(article.createdAt) }}</dd>
          </div>
          <div v-if="article.updatedAt">
            <dt>Updated</dt>
            <dd>{{ formatDate(article.updatedAt) }}</dd>
          </div>
          <div>
            <dt>Reading time</dt>
            <dd>{{ readingMinutes }} min</dd>
          </div>
        </dl>

        <div class="rail-tags" v-if="article.tags?.length">
          <span v-for="tag in article.tags" :key="tag.id" class="tag muted"># {{ tag.name }}</span>
        </div>

        <div class="rail-actions">
          <router-link to="/archives" class="text-link">Back to archives</router-link>
          <button class="text-link button-link" type="button" @click="scrollToComments">Open discussion</button>
        </div>
      </section>

      <section v-if="hasToc" class="rail-card toc-card">
        <div class="toc-head">
          <p class="section-kicker">Outline</p>
          <h2>On this page</h2>
        </div>
        <Toc :headings="headings" />
      </section>
    </aside>
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
import CommentThread from '@/components/CommentThread.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const article = ref(null)
const comments = ref([])
const headings = ref([])
const commentInput = ref(null)
const interaction = reactive({ commentCount: 0, likeCount: 0, liked: false })
const commentForm = reactive({ content: '', parentId: null })
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

const replyingTo = computed(() => findCommentById(comments.value, commentForm.parentId))
const totalCommentCount = computed(() => countComments(comments.value))
const hasToc = computed(() => headings.value.length > 1)
const readingMinutes = computed(() => {
  const content = article.value?.content || ''
  if (!content) return 1

  const plainText = content
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/`[^`]*`/g, ' ')
    .replace(/!\[[^\]]*]\([^)]*\)/g, ' ')
    .replace(/\[[^\]]*]\([^)]*\)/g, ' ')
    .replace(/[#>*_~-]/g, ' ')

  const latinWords = plainText.match(/[A-Za-z0-9]+/g)?.length || 0
  const cjkChars = plainText.match(/[\u4e00-\u9fff]/g)?.length || 0
  const units = latinWords + Math.ceil(cjkChars / 2)
  return Math.max(1, Math.ceil(units / 220))
})

const commentPlaceholder = computed(() => {
  if (!userStore.token) return 'Login first to comment...'
  if (replyingTo.value) {
    return `Reply to ${displayName(replyingTo.value.user)}...`
  }
  return 'Write something useful...'
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
  window.scrollTo({ top: 0, behavior: 'auto' })
}

const loadComments = async () => {
  const res = await frontApi.getComments(route.params.id)
  comments.value = res.data || []
}

const loadInteraction = async () => {
  const res = await frontApi.getArticleInteraction(route.params.id)
  Object.assign(interaction, res.data || {})
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
    await frontApi.createComment(route.params.id, {
      content: commentForm.content,
      parentId: commentForm.parentId
    })
    await Promise.all([loadComments(), loadInteraction()])
    commentForm.content = ''
    commentForm.parentId = null
    ElMessage.success('Comment posted')
  } finally {
    submitting.comment = false
  }
}

const startReply = async comment => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  commentForm.parentId = comment.id
  await nextTick()
  commentInput.value?.focus()
}

const cancelReply = () => {
  commentForm.parentId = null
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
  const els = document.querySelectorAll('.article-content h1, .article-content h2, .article-content h3, .article-content h4')
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

const displayName = user => user?.nickname || user?.username || 'User'

const countComments = items => items.reduce((total, item) => total + 1 + countComments(item.children || []), 0)

const findCommentById = (items, id) => {
  if (!id) return null
  for (const item of items) {
    if (item.id === id) return item
    const child = findCommentById(item.children || [], id)
    if (child) return child
  }
  return null
}

onMounted(loadDetail)
</script>

<style scoped>
.article-detail {
  display: grid;
  grid-template-columns: minmax(0, 820px) 280px;
  justify-content: center;
  gap: 32px;
  width: 100%;
}

.article-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.article-shell,
.subscribe-card,
.comments-section,
.rail-card,
.mobile-outline {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 26px;
  box-shadow: 0 18px 46px var(--shadow);
}

.article-hero {
  padding: 44px 48px 30px;
  background:
    linear-gradient(180deg, rgba(196, 93, 62, 0.1), rgba(196, 93, 62, 0.02) 42%, transparent 100%),
    var(--card);
  border-bottom: 1px solid rgba(196, 93, 62, 0.12);
}

.hero-kicker,
.section-kicker {
  margin: 0 0 12px;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 14px;
  align-items: center;
}

.meta-pill {
  padding: 7px 14px;
  border-radius: 999px;
  background: var(--accent-light);
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
}

.meta-text {
  color: var(--muted);
  font-size: 14px;
}

.hero-title {
  margin: 18px 0 0;
  font-family: var(--font-display);
  font-size: clamp(2.8rem, 4vw, 4.4rem);
  line-height: 1.02;
  letter-spacing: -0.03em;
  max-width: 12ch;
}

.hero-summary {
  max-width: 62ch;
  margin: 18px 0 0;
  color: var(--muted);
  font-size: 18px;
  line-height: 1.8;
}

.hero-stats {
  margin-top: 28px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.stat-card {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.56);
  border: 1px solid rgba(196, 93, 62, 0.12);
}

.dark .stat-card {
  background: rgba(20, 18, 17, 0.4);
}

.stat-card strong {
  display: block;
  margin-top: 8px;
  font-size: 28px;
  line-height: 1;
  font-family: var(--font-display);
}

.stat-label {
  color: var(--muted);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.article-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 18px 48px 0;
}

.toolbar-btn {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-width: 190px;
  padding: 13px 18px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.5);
  color: var(--fg);
  cursor: pointer;
  transition: transform 0.25s ease, border-color 0.25s ease, background 0.25s ease;
}

.toolbar-btn:hover {
  transform: translateY(-1px);
  border-color: rgba(196, 93, 62, 0.38);
}

.toolbar-btn.primary.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
}

.article-content {
  padding: 26px 48px 52px;
  font-size: 18px;
  line-height: 1.95;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3),
.article-content :deep(h4) {
  margin: 52px 0 18px;
  font-family: var(--font-display);
  line-height: 1.15;
  scroll-margin-top: 110px;
}

.article-content :deep(h1) {
  font-size: 2.4rem;
}

.article-content :deep(h2) {
  font-size: 2rem;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}

.article-content :deep(h3) {
  font-size: 1.55rem;
}

.article-content :deep(h4) {
  font-size: 1.2rem;
}

.article-content :deep(p),
.article-content :deep(ul),
.article-content :deep(ol),
.article-content :deep(blockquote) {
  margin: 0 0 1.2em;
}

.article-content :deep(ul),
.article-content :deep(ol) {
  padding-left: 1.4em;
}

.article-content :deep(blockquote) {
  padding: 18px 20px;
  border-left: 3px solid var(--accent);
  background: rgba(196, 93, 62, 0.07);
  border-radius: 0 18px 18px 0;
  color: var(--muted);
}

.article-content :deep(pre) {
  margin: 28px 0;
  padding: 20px 22px;
  border-radius: 20px;
  overflow-x: auto;
  border: 1px solid var(--border);
  background: color-mix(in srgb, var(--bg) 78%, white 22%);
}

.dark .article-content :deep(pre) {
  background: rgba(18, 17, 16, 0.92);
}

.article-content :deep(code) {
  font-family: 'JetBrains Mono', monospace;
}

.article-content :deep(img) {
  display: block;
  width: 100%;
  max-width: 100%;
  margin: 24px 0;
  border-radius: 18px;
}

.article-footer {
  padding: 0 48px 38px;
}

.footer-group {
  padding-top: 24px;
  border-top: 1px solid var(--border);
}

.footer-label {
  display: block;
  margin-bottom: 14px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--muted);
}

.footer-note,
.section-copy {
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
}

.tags,
.rail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: var(--accent-light);
  color: var(--accent);
  font-size: 13px;
}

.tag.muted {
  background: rgba(196, 93, 62, 0.08);
}

.section-head {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: end;
}

.section-head h2,
.toc-head h2,
.article-info-card h2 {
  margin: 0;
  font-family: var(--font-display);
  font-size: 30px;
  line-height: 1.08;
}

.subscribe-card,
.comments-section,
.mobile-outline {
  padding: 30px 32px;
}

.subscribe-form {
  margin-top: 22px;
  display: flex;
  gap: 12px;
}

.subscribe-form input,
.comment-form textarea {
  width: 100%;
  border: 1px solid var(--border);
  border-radius: 16px;
  background: color-mix(in srgb, var(--bg) 80%, white 20%);
  color: var(--fg);
}

.subscribe-form input {
  padding: 15px 16px;
}

.subscribe-form button,
.comment-actions button {
  border: none;
  border-radius: 16px;
  background: var(--accent);
  color: #fff;
  padding: 0 20px;
  font-weight: 700;
  cursor: pointer;
}

.comments-head {
  align-items: center;
}

.login-link,
.text-link {
  color: var(--accent);
  text-decoration: none;
  font-weight: 700;
}

.button-link {
  padding: 0;
  border: none;
  background: transparent;
  font: inherit;
  text-align: left;
  cursor: pointer;
}

.comment-form {
  margin-top: 22px;
}

.reply-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding: 10px 14px;
  border: 1px solid rgba(196, 93, 62, 0.18);
  border-radius: 14px;
  background: linear-gradient(90deg, rgba(196, 93, 62, 0.12), rgba(196, 93, 62, 0.04));
  color: var(--accent);
  font-size: 13px;
  font-weight: 600;
}

.reply-cancel {
  padding: 0;
  border: none;
  background: transparent;
  color: inherit;
  cursor: pointer;
  font-weight: 700;
}

.comment-form textarea {
  min-height: 140px;
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

.comment-list {
  margin-top: 22px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.empty-comments {
  padding: 28px;
  border: 1px dashed var(--border);
  border-radius: 18px;
  color: var(--muted);
  text-align: center;
}

.article-rail {
  position: sticky;
  top: 96px;
  align-self: start;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.rail-card {
  padding: 22px;
}

.article-info-card h2,
.toc-head h2 {
  font-size: 24px;
}

.article-facts {
  margin: 20px 0 0;
}

.article-facts div {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}

.article-facts div:last-child {
  border-bottom: none;
}

.article-facts dt,
.article-facts dd {
  margin: 0;
}

.article-facts dt {
  color: var(--muted);
}

.article-facts dd {
  font-weight: 700;
}

.rail-actions {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rail-tags {
  margin-top: 18px;
}

.toc-head {
  margin-bottom: 16px;
}

.mobile-outline {
  display: none;
}

@media (max-width: 980px) {
  .article-detail {
    grid-template-columns: 1fr;
  }

  .article-rail {
    position: static;
    display: none;
  }

  .mobile-outline {
    display: block;
  }
}

@media (max-width: 640px) {
  .article-main {
    gap: 22px;
  }

  .article-hero {
    padding: 32px 22px 24px;
  }

  .hero-title {
    font-size: 2.4rem;
    max-width: none;
  }

  .hero-summary,
  .article-content {
    font-size: 16px;
  }

  .hero-stats {
    grid-template-columns: 1fr;
  }

  .article-toolbar,
  .article-content,
  .article-footer,
  .subscribe-card,
  .comments-section,
  .mobile-outline {
    padding-left: 22px;
    padding-right: 22px;
  }

  .article-content {
    padding-top: 24px;
    padding-bottom: 34px;
  }

  .article-footer {
    padding-bottom: 28px;
  }

  .toolbar-btn,
  .subscribe-form button {
    width: 100%;
  }

  .subscribe-form,
  .section-head,
  .comments-head {
    flex-direction: column;
    align-items: stretch;
  }

  .comment-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
