<template>
  <div class="front-layout">
    <header class="header">
      <div class="container">
        <router-link to="/" class="logo">My Blog</router-link>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/archives">归档</router-link>
        </nav>
      </div>
    </header>
    <main class="main">
      <div class="container">
        <div class="content"><router-view /></div>
        <aside class="sidebar">
          <div class="widget">
            <h3>分类</h3>
            <ul>
              <li v-for="c in categories" :key="c.id" @click="$router.push(`/category/${c.id}`)">{{ c.name }}</li>
            </ul>
          </div>
          <div class="widget">
            <h3>标签</h3>
            <TagCloud :tags="tags" v-model="selectedTagIds" @change="onTagChange" />
          </div>
        </aside>
      </div>
    </main>
    <footer class="footer">Blog &copy; 2024</footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { frontApi } from '@/api/front'
import TagCloud from '@/components/TagCloud.vue'
const router = useRouter()
const categories = ref([])
const tags = ref([])
const selectedTagIds = ref([])
onMounted(async () => {
  const [cRes, tRes] = await Promise.all([frontApi.getCategories(), frontApi.getTags()])
  categories.value = cRes.data || []
  tags.value = tRes.data || []
})
const onTagChange = ids => {
  selectedTagIds.value = ids
  if (ids.length > 0) {
    router.push(`/tags/${ids.join(',')}`)
  } else {
    router.push('/')
  }
}
</script>

<style scoped>
.front-layout { min-height: 100vh; display: flex; flex-direction: column; }
.header { background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.08); position: sticky; top: 0; z-index: 100; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.header .container { display: flex; justify-content: space-between; align-items: center; height: 60px; }
.logo { font-size: 20px; font-weight: bold; color: #333; text-decoration: none; }
.nav { display: flex; gap: 24px; }
.nav a { color: #666; text-decoration: none; transition: color 0.3s; }
.nav a:hover, .nav a.router-link-active { color: #1890ff; }
.main { flex: 1; background: #f5f5f5; padding: 24px 0; }
.main .container { display: flex; gap: 24px; }
.content { flex: 1; min-width: 0; }
.sidebar { width: 280px; flex-shrink: 0; }
.widget { background: #fff; border-radius: 8px; padding: 16px; margin-bottom: 16px; }
.widget h3 { margin: 0 0 12px; font-size: 16px; color: #333; }
.widget ul { list-style: none; padding: 0; margin: 0; }
.widget li { padding: 8px 0; color: #666; cursor: pointer; transition: color 0.3s; }
.widget li:hover { color: #1890ff; }
.footer { background: #fff; padding: 24px; text-align: center; color: #999; }
</style>
