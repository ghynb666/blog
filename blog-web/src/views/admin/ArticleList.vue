<template>
  <div class="article-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-form :inline="true" :model="search">
            <el-form-item><el-input v-model="search.keyword" placeholder="搜索文章" clearable @keyup.enter="loadData" /></el-form-item>
            <el-form-item><el-select v-model="search.category" placeholder="分类" clearable @change="loadData"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
            <el-form-item><el-select v-model="search.status" placeholder="状态" clearable @change="loadData"><el-option label="草稿" :value="0" /><el-option label="已发布" :value="1" /></el-select></el-form-item>
            <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
          </el-form>
          <el-button type="primary" @click="$router.push('/admin/article/edit')">新建文章</el-button>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="status" label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status ? 'success' : 'info'">{{ row.status ? '已发布' : '草稿' }}</el-tag></template></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/admin/article/edit/${row.id}`)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.id)"><template #reference><el-button link type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
    </el-card>
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

const loadData = async () => {
  loading.value = true
  try {
    const res = await articleApi.list({ page: page.value, pageSize: pageSize.value, ...search })
    list.value = res.list || res.data || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  categories.value = await categoryApi.list()
}

const handleDelete = async id => {
  await articleApi.remove(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
