<template>
  <div class="category-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">分类管理</h1>
        <p class="page-subtitle">管理文章分类，组织您的内容结构</p>
      </div>
      <button class="create-btn" @click="handleAdd">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        <span>新建分类</span>
      </button>
    </div>
    <div class="categories-container" v-loading="loading">
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
        </svg>
        <p>暂无分类</p>
        <button class="empty-btn" @click="handleAdd">创建第一个分类</button>
      </div>
      <div v-else class="category-grid">
        <div class="category-card" v-for="item in list" :key="item.id">
          <div class="category-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
            </svg>
          </div>
          <div class="category-content">
            <h3 class="category-name">{{ item.name }}</h3>
            <p class="category-desc">{{ item.description || '暂无描述' }}</p>
          </div>
          <div class="category-actions">
            <button class="action-btn edit" @click="handleEdit(item)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </button>
            <button class="action-btn delete" @click="confirmDelete(item.id)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑分类' : '新建分类'" width="480px" class="form-dialog">
      <div class="form-content">
        <div class="form-group">
          <label class="form-label">分类名称</label>
          <input v-model="form.name" type="text" class="form-input" placeholder="输入分类名称" />
        </div>
        <div class="form-group">
          <label class="form-label">分类描述</label>
          <textarea v-model="form.description" class="form-textarea" placeholder="输入分类描述（可选）" rows="3"></textarea>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <button class="dialog-btn cancel" @click="dialogVisible = false">取消</button>
          <button class="dialog-btn confirm" :loading="submitting" @click="handleSubmit">保存</button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px" class="delete-dialog">
      <p class="delete-message">确定要删除这个分类吗？此操作不可恢复。</p>
      <template #footer>
        <div class="dialog-footer">
          <button class="dialog-btn cancel" @click="deleteDialogVisible = false">取消</button>
          <button class="dialog-btn delete" @click="handleDelete">删除</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as api from '@/api/category'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const editId = ref(null)
const deleteTargetId = ref(null)
const form = reactive({ name: '', description: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await api.list()
    list.value = res.data || []
  } finally { loading.value = false }
}

const handleAdd = () => {
  editId.value = null
  Object.assign(form, { name: '', description: '' })
  dialogVisible.value = true
}

const handleEdit = row => {
  editId.value = row.id
  Object.assign(form, { name: row.name, description: row.description })
  dialogVisible.value = true
}

const confirmDelete = (id) => {
  deleteTargetId.value = id
  deleteDialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  submitting.value = true
  try {
    if (editId.value) await api.update(editId.value, form)
    else await api.create(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const handleDelete = async () => {
  await api.remove(deleteTargetId.value)
  ElMessage.success('删除成功')
  deleteDialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

.category-page {
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
  border: none;
  cursor: pointer;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.create-btn svg {
  width: 18px;
  height: 18px;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(196, 93, 62, 0.3);
}

.categories-container {
  min-height: 300px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid #e8e0d5;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  color: #b8a99a;
  margin-bottom: 1rem;
}

.empty-state p {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 1rem;
  color: #8b7355;
  margin: 0 0 1.5rem;
}

.empty-btn {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #c45d3e 0%, #a84a2e 100%);
  color: #fff;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.empty-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(196, 93, 62, 0.3);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
}

.category-card {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1.25rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  border: 1px solid #e8e0d5;
  transition: all 0.3s ease;
}

.category-card:hover {
  border-color: #d4a574;
  box-shadow: 0 8px 16px rgba(139, 115, 85, 0.08);
}

.category-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: rgba(212, 165, 116, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.category-icon svg {
  width: 22px;
  height: 22px;
  color: #d4a574;
}

.category-content {
  flex: 1;
  min-width: 0;
}

.category-name {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 1rem;
  font-weight: 600;
  color: #2d2a26;
  margin: 0 0 0.25rem;
}

.category-desc {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.8rem;
  color: #8b7355;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-actions {
  display: flex;
  gap: 0.5rem;
  flex-shrink: 0;
}

.action-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid #e8e0d5;
  background: transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn svg {
  width: 14px;
  height: 14px;
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

.form-dialog :deep(.el-dialog__header) {
  font-family: 'Playfair Display', serif;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
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

.form-input, .form-textarea {
  padding: 0.875rem 1rem;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e8e0d5;
  border-radius: 10px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: #2d2a26;
  transition: all 0.3s ease;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #c45d3e;
  box-shadow: 0 0 0 3px rgba(196, 93, 62, 0.1);
}

.form-input::placeholder, .form-textarea::placeholder {
  color: #b8a99a;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
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
  background: linear-gradient(135deg, #c45d3e 0%, #a84a2e 100%);
  color: #fff;
}

.dialog-btn.confirm:hover {
  box-shadow: 0 4px 12px rgba(196, 93, 62, 0.3);
}

.dialog-btn.delete {
  background: #f44336;
  color: #fff;
}

.dialog-btn.delete:hover {
  background: #d32f2f;
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

@media (max-width: 640px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
  }
  
  .category-grid {
    grid-template-columns: 1fr;
  }
}
</style>
