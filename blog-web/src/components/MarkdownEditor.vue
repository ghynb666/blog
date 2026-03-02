<template>
  <div class="markdown-editor">
    <div class="toolbar">
      <el-button-group>
        <el-button size="small" @click="insertText('**', '**')" title="加粗">B</el-button>
        <el-button size="small" @click="insertText('*', '*')" title="斜体"><i>I</i></el-button>
        <el-button size="small" @click="insertText('## ')" title="标题">H</el-button>
        <el-button size="small" @click="insertText('[', '](url)')" title="链接">链</el-button>
        <el-button size="small" @click="triggerUpload" title="图片">图</el-button>
        <el-button size="small" @click="insertText('```\n', '\n```')" title="代码块">码</el-button>
      </el-button-group>
    </div>
    <div class="editor-body">
      <div class="edit-area">
        <textarea ref="textareaRef" v-model="content" @input="handleInput" @paste="handlePaste" @drop="handleDrop" @dragover.prevent placeholder="请输入内容..." />
      </div>
      <div class="preview-area" v-html="html" />
    </div>
    <input ref="fileInputRef" type="file" accept="image/*" style="display: none" @change="handleFileChange" />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { marked } from 'marked'
import 'highlight.js/styles/github.css'
import hljs from 'highlight.js'

marked.setOptions({
  highlight: code => hljs.highlightAuto(code).value,
  breaks: true
})

const props = defineProps({ modelValue: { type: String, default: '' } })
const emit = defineEmits(['update:modelValue', 'upload-image'])

const textareaRef = ref()
const fileInputRef = ref()
const content = ref(props.modelValue)

const html = computed(() => marked(content.value || ''))

watch(() => props.modelValue, val => { content.value = val })

const handleInput = () => emit('update:modelValue', content.value)

const insertText = (before, after = '') => {
  const ta = textareaRef.value
  const start = ta.selectionStart
  const end = ta.selectionEnd
  const selected = content.value.substring(start, end)
  content.value = content.value.substring(0, start) + before + selected + after + content.value.substring(end)
  ta.focus()
  ta.selectionStart = ta.selectionEnd = start + before.length
  handleInput()
}

const triggerUpload = () => fileInputRef.value.click()

const handleFileChange = e => {
  const file = e.target.files[0]
  if (file) uploadImage(file)
  e.target.value = ''
}

const handlePaste = e => {
  const file = e.clipboardData.files[0]
  if (file && file.type.startsWith('image/')) {
    e.preventDefault()
    uploadImage(file)
  }
}

const handleDrop = e => {
  const file = e.dataTransfer.files[0]
  if (file && file.type.startsWith('image/')) {
    e.preventDefault()
    uploadImage(file)
  }
}

const uploadImage = file => {
  emit('upload-image', file, url => {
    insertText(`![image](${url})`)
  })
}
</script>

<style scoped>
.markdown-editor { border: 1px solid #dcdfe6; border-radius: 4px; }
.toolbar { padding: 8px; border-bottom: 1px solid #dcdfe6; background: #f5f7fa; }
.editor-body { display: flex; height: 500px; }
.edit-area, .preview-area { flex: 1; overflow: auto; }
.edit-area { border-right: 1px solid #dcdfe6; }
.edit-area textarea { width: 100%; height: 100%; border: none; resize: none; padding: 10px; font-family: monospace; font-size: 14px; outline: none; }
.preview-area { padding: 10px; background: #fff; }
.preview-area :deep(pre) { background: #f6f8fa; padding: 10px; border-radius: 4px; overflow-x: auto; }
.preview-area :deep(code) { font-family: monospace; }
.preview-area :deep(img) { max-width: 100%; }
</style>
