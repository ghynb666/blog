<template>
  <div class="article-edit">
    <div class="page-header">
      <button class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        <span>返回</span>
      </button>
      <h1 class="page-title">{{ route.params.id ? '编辑文章' : '新建文章' }}</h1>
    </div>
    <div class="edit-container">
      <div class="main-content">
        <div class="form-section">
          <div class="form-group">
            <label class="form-label">文章标题</label>
            <input v-model="form.title" type="text" class="form-input title-input" placeholder="输入文章标题..." />
          </div>
          <div class="form-group">
            <label class="form-label">文章摘要</label>
            <textarea v-model="form.summary" class="form-textarea" placeholder="输入文章摘要..." rows="3"></textarea>
          </div>
          <div class="form-group">
            <label class="form-label">文章内容</label>
            <div class="editor-wrapper">
              <MarkdownEditor v-model="form.content" @upload-image="handleUploadImage" />
            </div>
          </div>
        </div>
      </div>
      <div class="sidebar">
        <div class="sidebar-card">
          <h3 class="sidebar-title">发布设置</h3>
          <div class="form-group">
            <label class="form-label">状态</label>
            <div class="status-options">
              <label class="status-option" :class="{ active: form.status === 0 }">
                <input type="radio" v-model="form.status" :value="0" />
                <span class="status-icon draft">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                    <polyline points="14 2 14 8 20 8"/>
                  </svg>
                </span>
                <span class="status-text">草稿</span>
              </label>
              <label class="status-option" :class="{ active: form.status === 1 }">
                <input type="radio" v-model="form.status" :value="1" />
                <span class="status-icon published">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                  </svg>
                </span>
                <span class="status-text">发布</span>
              </label>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">分类</label>
            <select v-model="form.categoryId" class="form-select">
              <option value="">选择分类</option>
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">标签</label>
            <div class="tags-selector">
              <label v-for="t in tags" :key="t.id" class="tag-checkbox" :class="{ checked: form.tagIds.includes(t.id) }">
                <input type="checkbox" :value="t.id" v-model="form.tagIds" />
                <span class="tag-label">{{ t.name }}</span>
              </label>
            </div>
          </div>
          <div class="action-buttons">
            <button class="save-btn" :loading="loading" @click="handleSubmit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                <polyline points="17 21 17 13 7 13 7 21"/>
                <polyline points="7 3 7 8 15 8"/>
              </svg>
              <span>保存文章</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MarkdownEditor from '@/components/MarkdownEditor.vue'
import * as articleApi from '@/api/article'
import * as categoryApi from '@/api/category'
import * as tagApi from '@/api/tag'
import * as uploadApi from '@/api/upload'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const categories = ref([])
const tags = ref([])

const form = reactive({ title: '', categoryId: '', tagIds: [], summary: '', content: '', status: 0 })

const loadData = async () => {
  if (route.params.id) {
    const res = await articleApi.detail(route.params.id)
    Object.assign(form, res)
  }
}

const loadCategories = async () => { 
  const res = await categoryApi.list()
  categories.value = res.data || []
}

const loadTags = async () => { 
  const res = await tagApi.list()
  tags.value = res.data || []
}

const handleUploadImage = async (file, callback) => {
  try {
    const res = await uploadApi.image(file)
    let url = res.data?.url || res.url
    if (url) {
      const token = localStorage.getItem('token')
      if (token) {
        url += (url.includes('?') ? '&' : '?') + 'token=' + encodeURIComponent(token)
      }
      callback(url)
    } else {
      ElMessage.error('上传失败：未获取到图片地址')
    }
  } catch (error) {
    ElMessage.error('图片上传失败')
  }
}

const handleSubmit = async () => {
  if (!form.title) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!form.content) {
    ElMessage.warning('请输入文章内容')
    return
  }
  loading.value = true
  try {
    if (route.params.id) {
      await articleApi.update(route.params.id, form)
    } else {
      await articleApi.create(form)
    }
    ElMessage.success('保存成功')
    router.push('/admin/article')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
  loadCategories()
  loadTags()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

.article-edit {
  max-width: 1400px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: transparent;
  border: 1px solid #e8e0d5;
  border-radius: 8px;
  cursor: pointer;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.875rem;
  color: #5c5246;
  transition: all 0.2s ease;
}

.back-btn svg {
  width: 16px;
  height: 16px;
}

.back-btn:hover {
  background: #f5f0e8;
  border-color: #d4a574;
}

.page-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.75rem;
  font-weight: 600;
  color: #2d2a26;
  margin: 0;
  letter-spacing: -0.02em;
}

.edit-container {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 2rem;
}

.main-content {
  min-width: 0;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  color: #2d2a26;
}

.form-input, .form-textarea, .form-select {
  padding: 0.875rem 1rem;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e8e0d5;
  border-radius: 10px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: #2d2a26;
  transition: all 0.3s ease;
}

.form-input:focus, .form-textarea:focus, .form-select:focus {
  outline: none;
  border-color: #c45d3e;
  box-shadow: 0 0 0 3px rgba(196, 93, 62, 0.1);
}

.form-input::placeholder, .form-textarea::placeholder {
  color: #b8a99a;
}

.title-input {
  font-family: 'Playfair Display', serif;
  font-size: 1.25rem;
  font-weight: 500;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%238b7355' stroke-width='2'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
}

.editor-wrapper {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e8e0d5;
  border-radius: 12px;
  overflow: hidden;
}

.sidebar {
  position: sticky;
  top: 96px;
  height: fit-content;
}

.sidebar-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid #e8e0d5;
  padding: 1.5rem;
}

.sidebar-title {
  font-family: 'Playfair Display', serif;
  font-size: 1.125rem;
  font-weight: 600;
  color: #2d2a26;
  margin: 0 0 1.25rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e8e0d5;
}

.status-options {
  display: flex;
  gap: 0.75rem;
}

.status-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  background: #f5f0e8;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent;
}

.status-option input {
  display: none;
}

.status-option.active {
  background: rgba(196, 93, 62, 0.1);
  border-color: #c45d3e;
}

.status-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.status-icon svg {
  width: 18px;
  height: 18px;
}

.status-icon.draft {
  background: rgba(139, 115, 85, 0.15);
  color: #8b7355;
}

.status-icon.published {
  background: rgba(76, 175, 80, 0.15);
  color: #4caf50;
}

.status-text {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.8rem;
  font-weight: 500;
  color: #5c5246;
}

.status-option.active .status-text {
  color: #c45d3e;
}

.tags-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-checkbox {
  display: flex;
  align-items: center;
  padding: 0.375rem 0.75rem;
  background: #f5f0e8;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.tag-checkbox input {
  display: none;
}

.tag-checkbox.checked {
  background: rgba(196, 93, 62, 0.1);
  border-color: #c45d3e;
}

.tag-label {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.8rem;
  font-weight: 500;
  color: #5c5246;
}

.tag-checkbox.checked .tag-label {
  color: #c45d3e;
}

.action-buttons {
  margin-top: 1.5rem;
  padding-top: 1.25rem;
  border-top: 1px solid #e8e0d5;
}

.save-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.875rem 1.5rem;
  background: linear-gradient(135deg, #c45d3e 0%, #a84a2e 100%);
  color: #fff;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.save-btn svg {
  width: 18px;
  height: 18px;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(196, 93, 62, 0.3);
}

@media (max-width: 1024px) {
  .edit-container {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    position: static;
  }
}
</style>
