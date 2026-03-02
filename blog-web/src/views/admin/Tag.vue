<template>
  <div class="tag-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">标签管理</h1>
        <p class="page-subtitle">管理文章标签，帮助读者发现内容</p>
      </div>
      <button class="create-btn" @click="handleAdd">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        <span>新建标签</span>
      </button>
    </div>
    <div class="tags-container" v-loading="loading">
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
          <line x1="7" y1="7" x2="7.01" y2="7"/>
        </svg>
        <p>暂无标签</p>
        <button class="empty-btn" @click="handleAdd">创建第一个标签</button>
      </div>
      <div v-else class="tags-grid">
        <div class="tag-card" v-for="item in list" :key="item.id">
          <div class="tag-content">
            <div class="tag-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
                <line x1="7" y1="7" x2="7.01" y2="7"/>
              </svg>
            </div>
            <span class="tag-name">{{ item.name }}</span>
          </div>
          <div class="tag-actions">
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
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑标签' : '新建标签'" width="420px" class="form-dialog">
      <div class="form-content">
        <div class="form-group">
          <label class="form-label">标签名称</label>
          <input v-model="form.name" type="text" class="form-input" placeholder="输入标签名称" />
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
      <p class="delete-message">确定要删除这个标签吗？此操作不可恢复。</p>
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
import * as api from '@/api/tag'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const editId = ref(null)
const deleteTargetId = ref(null)
const form = reactive({ name: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await api.list()
    list.value = res.data || []
  } finally { loading.value = false }
}

const handleAdd = () => {
  editId.value = null
  form.name = ''
  dialogVisible.value = true
}

const handleEdit = row => {
  editId.value = row.id
  form.name = row.name
  dialogVisible.value = true
}

const confirmDelete = (id) => {
  deleteTargetId.value = id
  deleteDialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.name) {
    ElMessage.warning('请输入标签名称')
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

.tag-page {
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

.tags-container {
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

.tags-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.tag-card {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid #e8e0d5;
  transition: all 0.3s ease;
}

.tag-card:hover {
  border-color: #d4a574;
  box-shadow: 0 4px 12px rgba(139, 115, 85, 0.08);
}

.tag-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.tag-icon {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: rgba(196, 93, 62, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.tag-icon svg {
  width: 14px;
  height: 14px;
  color: #c45d3e;
}

.tag-name {
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.9rem;
  font-weight: 500;
  color: #2d2a26;
}

.tag-actions {
  display: flex;
  gap: 0.375rem;
  margin-left: 0.25rem;
}

.action-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  border: 1px solid transparent;
  background: transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  opacity: 0.6;
}

.action-btn svg {
  width: 14px;
  height: 14px;
}

.tag-card:hover .action-btn {
  opacity: 1;
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

.form-input {
  padding: 0.875rem 1rem;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e8e0d5;
  border-radius: 10px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 0.95rem;
  color: #2d2a26;
  transition: all 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #c45d3e;
  box-shadow: 0 0 0 3px rgba(196, 93, 62, 0.1);
}

.form-input::placeholder {
  color: #b8a99a;
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
  
  .tags-grid {
    flex-direction: column;
  }
  
  .tag-card {
    width: 100%;
  }
}
</style>
