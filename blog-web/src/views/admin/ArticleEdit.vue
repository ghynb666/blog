<template>
  <div class="article-edit">
    <el-card>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="请输入标题" /></el-form-item>
        <el-form-item label="分类" prop="categoryId"><el-select v-model="form.categoryId" placeholder="选择分类"><el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="标签"><el-select v-model="form.tagIds" multiple placeholder="选择标签"><el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" /></el-select></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="3" placeholder="文章摘要" /></el-form-item>
        <el-form-item label="内容" prop="content"><MarkdownEditor v-model="form.content" @upload-image="handleUploadImage" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :value="0">草稿</el-radio><el-radio :value="1">发布</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
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
const formRef = ref()
const loading = ref(false)
const categories = ref([])
const tags = ref([])

const form = reactive({ title: '', categoryId: '', tagIds: [], summary: '', content: '', status: 0 })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }], content: [{ required: true, message: '请输入内容', trigger: 'blur' }] }

const loadData = async () => {
  if (route.params.id) {
    const res = await articleApi.detail(route.params.id)
    Object.assign(form, res)
  }
}

const loadCategories = async () => { categories.value = await categoryApi.list() }
const loadTags = async () => { tags.value = await tagApi.list() }

const handleUploadImage = async (file, callback) => {
  const res = await uploadApi.image(file)
  callback(res.url)
}

const handleSubmit = async () => {
  await formRef.value.validate()
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
