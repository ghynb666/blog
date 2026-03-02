<template>
  <div class="tag-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>标签管理</span>
          <el-button type="primary" @click="handleAdd">新建标签</el-button>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.id)"><template #reference><el-button link type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑标签' : '新建标签'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" placeholder="标签名称" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button></template>
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
const editId = ref(null)
const formRef = ref()
const form = reactive({ name: '' })
const rules = { name: [{ required: true, message: '请输入名称', trigger: 'blur' }] }

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

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editId.value) await api.update(editId.value, form)
    else await api.create(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const handleDelete = async id => {
  await api.remove(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
