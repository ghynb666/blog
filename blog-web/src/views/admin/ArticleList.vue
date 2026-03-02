<template>
  <div class="article-list">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">文章管理</h1>
        <p class="page-subtitle">管理您的所有博客文章</p>
      </div>
      <router-link to="/admin/article/edit" class="create-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        <span>新建文章</span>
      </router-link>
    </div>
    <div class="filter-bar">
      <div class="search-box">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/>
          <path d="M21 21l-4.35-4.35"/>
        </svg>
        <input v-model="search.keyword" type="text" placeholder="搜索文章标题..." @keyup.enter="loadData" />
      </div>
      <div class="filter-group">
        <select v-model="search.category" @change="loadData" class="filter-select">
          <option value="">全部分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <select v-model="search.status" @change="loadData" class="filter-select">
          <option value="">全部状态</option>
          <option :value="0">草稿</option>
          <option :value="1">已发布</option>
        </select>
      </div>
    </div>
    <div class="articles-container" v-loading="loading">
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
        </svg>
        <p>暂无文章</p>
      </div>
      <div v-else class="article-items">
        <div class="article-item" v-for="article in list" :key="article.id">
          <div class="article-main">
            <div class="article-info">
              <div class="article-header">
                <h3 class="article-title">{{ article.title }}</h3>
                <span class="status-badge" :class="article.status ? 'published' : 'draft'">
                  {{ article.status ? '已发布' : '草稿' }}
                </span>
              </div>
              <div class="article-meta">
                <span class="meta-item" v-if="article.categoryName">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
                  </svg>
                  {{ article.categoryName }}
                </span>
                <span class="meta-item">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                    <line x1="16" y1="2" x2="16" y2="6"/>
                    <line x1="8" y1="2" x2="8" y2="6"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                  {{ article.createdAt }}
                </span>
              </div>
            </div>
          </div>
          <div class="article-actions">
            <router-link :to="`/admin/article/edit/${article.id}`" class="action-btn edit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </router-link>
            <button class="action-btn delete" @click="confirmDelete(article.id)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination-container" v-if="total > pageSize">
      <div class="pagination-info">共 {{ total }} 篇文章</div>
      <div class="pagination">
        <button class="page-btn" :disabled="page === 1" @click="page = 1; loadData()">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="11 17 6 12 11 7"/>
            <polyline points="18 17 13 12 18 7"/>
          </svg>
        </button>
        <button class="page-btn" :disabled="page === 1" @click="page--; loadData()">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
        </button>
        <span class="page-current">{{ page }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="page >= Math.ceil(total / pageSize)" @click="page++; loadData()">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"/>
          </svg>
        </button>
        <button class="page-btn" :disabled="page >= Math.ceil(total / pageSize)" @click="page = Math.ceil(total / pageSize); loadData()">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="13 17 18 12 13 7"/>
            <polyline points="6 17 11 12 6 7"/>
          </svg>
        </button>
      </div>
    </div>
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px" class="delete-dialog">
      <p class="delete-message">确定要删除这篇文章吗？此操作不可恢复。</p>
      <template #footer>
        <button class="dialog-btn cancel" @click="deleteDialogVisible = false">取消</button>
        <button class="dialog-btn confirm" @click="handleDelete">删除</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as articleApi from '@/api/article'
import * as categoryApi from '@/api/category'

const loading = ref(false)
const list = ref([])
const categories = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const search = reactive({ keyword: '', category: '', status: '' })
const deleteDialogVisible = ref(false)
const deleteTargetId = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const res = await articleApi.list({ page: page.value, pageSize: pageSize.value, ...search })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const res = await categoryApi.list()
  categories.value = res.data || []
}

const confirmDelete = (id) => {
  deleteTargetId.value = id
  deleteDialogVisible.value = true
}

const handleDelete = async () => {
  await articleApi.remove(deleteTargetId.value)
  ElMessage.success('删除成功')
  deleteDialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

.article-list {
  max-width: 1000px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
}

.page-title {
  font-family: 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 600;
  color: #2d2a26;
  margin: 0 0 0.5rem;
  letter-spacing: -0.02em;
}

.page-subtitle {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: #8b7355;
  margin: 0;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.25rem;
  background: linear-gradient(135deg, #c45d3e 0%, #a84a2e 100%);
  color: #fff;
  border-radius: 10px;
  text-decoration: none;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
}

.create-btn svg {
  width: 18px;
  height: 18px;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(196, 93, 62, 0.3);
}

.filter-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.search-box {
  flex: 1;
  min-width: 250px;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: #8b7355;
}

.search-box input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 2.75rem;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e8e0d5;
  border-radius: 10px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.9rem;
  color: #2d2a26;
  transition: all 0.3s ease;
}

.search-box input:focus {
  outline: none;
  border-color: #c45d3e;
  box-shadow: 0 0 0 3px rgba(196, 93, 62, 0.1);
}

.filter-group {
  display: flex;
  gap: 0.75rem;
}

.filter-select {
  padding: 0.875rem 2.5rem 0.875rem 1rem;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e8e0d5;
  border-radius: 10px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.9rem;
  color: #2d2a26;
  cursor: pointer;
  transition: all 0.3s ease;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%238b7355' stroke-width='2'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
}

.filter-select:focus {
  outline: none;
  border-color: #c45d3e;
}

.articles-container {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid #e8e0d5;
  overflow: hidden;
  min-height: 300px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: #8b7355;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-state p {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 1rem;
  margin: 0;
}

.article-items {
  divide-y: 1px solid #e8e0d5;
}

.article-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #e8e0d5;
  transition: all 0.2s ease;
}

.article-item:last-child {
  border-bottom: none;
}

.article-item:hover {
  background: rgba(245, 240, 232, 0.5);
}

.article-main {
  flex: 1;
  min-width: 0;
}

.article-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.article-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.article-title {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 1.05rem;
  font-weight: 600;
  color: #2d2a26;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.status-badge {
  padding: 0.25rem 0.625rem;
  border-radius: 6px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.75rem;
  font-weight: 500;
  flex-shrink: 0;
}

.status-badge.published {
  background: rgba(76, 175, 80, 0.1);
  color: #4caf50;
}

.status-badge.draft {
  background: rgba(139, 115, 85, 0.1);
  color: #8b7355;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.8rem;
  color: #8b7355;
}

.meta-item svg {
  width: 14px;
  height: 14px;
}

.article-actions {
  display: flex;
  gap: 0.5rem;
  margin-left: 1rem;
}

.action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid #e8e0d5;
  background: transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.edit {
  color: #5c5246;
}

.action-btn.edit:hover {
  background: rgba(196, 93, 62, 0.1);
  border-color: #c45d3e;
  color: #c45d3e;
}

.action-btn.delete {
  color: #5c5246;
}

.action-btn.delete:hover {
  background: rgba(244, 67, 54, 0.1);
  border-color: #f44336;
  color: #f44336;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding: 1rem 0;
}

.pagination-info {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.875rem;
  color: #8b7355;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.page-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid #e8e0d5;
  background: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn svg {
  width: 16px;
  height: 16px;
  color: #5c5246;
}

.page-btn:hover:not(:disabled) {
  border-color: #c45d3e;
  color: #c45d3e;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-current {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.875rem;
  color: #2d2a26;
  padding: 0 0.75rem;
}

.delete-dialog :deep(.el-dialog__header) {
  font-family: 'Playfair Display', serif;
}

.delete-message {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: #5c5246;
  margin: 0;
}

.dialog-btn {
  padding: 0.625rem 1.25rem;
  border-radius: 8px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.dialog-btn.cancel {
  background: #f5f0e8;
  color: #5c5246;
}

.dialog-btn.cancel:hover {
  background: #e8e0d5;
}

.dialog-btn.confirm {
  background: #f44336;
  color: #fff;
  margin-left: 0.75rem;
}

.dialog-btn.confirm:hover {
  background: #d32f2f;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
  }
  
  .filter-bar {
    flex-direction: column;
  }
  
  .filter-group {
    width: 100%;
  }
  
  .filter-select {
    flex: 1;
  }
  
  .article-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .article-actions {
    margin-left: 0;
    align-self: flex-end;
  }
}
</style>
