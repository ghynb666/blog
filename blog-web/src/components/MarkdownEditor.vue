<template>
  <div class="markdown-editor">
    <div class="toolbar">
      <div class="toolbar-group">
        <button class="tool-btn" @click="insertText('**', '**')" title="加粗">
          <span class="tool-icon bold">B</span>
        </button>
        <button class="tool-btn" @click="insertText('*', '*')" title="斜体">
          <span class="tool-icon italic">I</span>
        </button>
        <button class="tool-btn" @click="insertText('## ')" title="标题">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 12h8M4 18V6M12 18V6M17 12h3m0 0v6m0-6l-4-4"/>
          </svg>
        </button>
        <button class="tool-btn" @click="insertText('[', '](url)')" title="链接">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
            <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
          </svg>
        </button>
        <button class="tool-btn" @click="triggerUpload" title="图片">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
            <circle cx="8.5" cy="8.5" r="1.5"/>
            <polyline points="21 15 16 10 5 21"/>
          </svg>
        </button>
        <button class="tool-btn" @click="insertText('```\n', '\n```')" title="代码块">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="16 18 22 12 16 6"/>
            <polyline points="8 6 2 12 8 18"/>
          </svg>
        </button>
      </div>
    </div>
    <div class="editor-body">
      <div class="edit-area">
        <textarea ref="textareaRef" v-model="content" @input="handleInput" @paste="handlePaste" @drop="handleDrop" @dragover.prevent placeholder="请输入内容..." />
      </div>
      <div class="divider"></div>
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
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600&display=swap');

.markdown-editor {
  border: 1px solid #e8e0d5;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(45, 42, 38, 0.04);
}

.toolbar {
  padding: 12px 16px;
  border-bottom: 1px solid #e8e0d5;
  background: linear-gradient(135deg, #faf8f5 0%, #f5f0e8 100%);
}

.toolbar-group {
  display: flex;
  gap: 6px;
}

.tool-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid #e8e0d5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tool-btn:hover {
  background: #fff;
  border-color: #c45d3e;
  color: #c45d3e;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(196, 93, 62, 0.15);
}

.tool-btn svg {
  width: 18px;
  height: 18px;
}

.tool-icon {
  font-family: 'Playfair Display', serif;
  font-size: 14px;
  font-weight: 600;
}

.tool-icon.italic {
  font-style: italic;
}

.editor-body {
  display: flex;
  height: 500px;
  position: relative;
}

.edit-area, .preview-area {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.edit-area textarea {
  width: 100%;
  height: 100%;
  border: none;
  resize: none;
  padding: 20px;
  font-family: 'Source Sans 3', sans-serif;
  font-size: 15px;
  line-height: 1.7;
  color: #2d2a26;
  background: transparent;
  outline: none;
}

.edit-area textarea::placeholder {
  color: #b8a99a;
}

.divider {
  width: 1px;
  background: linear-gradient(180deg, transparent 0%, #e8e0d5 20%, #e8e0d5 80%, transparent 100%);
  flex-shrink: 0;
}

.preview-area {
  padding: 20px;
  background: #fff;
  overflow-y: auto;
  overflow-x: hidden;
  font-family: 'Source Sans 3', sans-serif;
  line-height: 1.8;
  color: #2d2a26;
}

.preview-area::-webkit-scrollbar,
.edit-area textarea::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.preview-area::-webkit-scrollbar-track,
.edit-area textarea::-webkit-scrollbar-track {
  background: transparent;
}

.preview-area::-webkit-scrollbar-thumb,
.edit-area textarea::-webkit-scrollbar-thumb {
  background: #d4c4b0;
  border-radius: 3px;
}

.preview-area::-webkit-scrollbar-thumb:hover,
.edit-area textarea::-webkit-scrollbar-thumb:hover {
  background: #c45d3e;
}

.preview-area :deep(h1),
.preview-area :deep(h2),
.preview-area :deep(h3),
.preview-area :deep(h4),
.preview-area :deep(h5),
.preview-area :deep(h6) {
  font-family: 'Playfair Display', serif;
  color: #2d2a26;
  margin: 1.5em 0 0.75em;
  font-weight: 600;
}

.preview-area :deep(h1) { font-size: 1.8em; }
.preview-area :deep(h2) { font-size: 1.5em; }
.preview-area :deep(h3) { font-size: 1.25em; }

.preview-area :deep(p) {
  margin: 0.75em 0;
}

.preview-area :deep(pre) {
  background: #f5f0e8;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 1em 0;
  border: 1px solid #e8e0d5;
}

.preview-area :deep(pre::-webkit-scrollbar) {
  height: 4px;
}

.preview-area :deep(pre::-webkit-scrollbar-thumb) {
  background: #d4c4b0;
  border-radius: 2px;
}

.preview-area :deep(code) {
  font-family: 'Fira Code', monospace;
  font-size: 0.9em;
  background: #f5f0e8;
  padding: 2px 6px;
  border-radius: 4px;
  color: #c45d3e;
}

.preview-area :deep(pre code) {
  background: transparent;
  padding: 0;
  color: #2d2a26;
}

.preview-area :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 1em 0;
  box-shadow: 0 2px 8px rgba(45, 42, 38, 0.1);
}

.preview-area :deep(blockquote) {
  border-left: 3px solid #c45d3e;
  padding-left: 16px;
  margin: 1em 0;
  color: #5c5246;
  font-style: italic;
}

.preview-area :deep(a) {
  color: #c45d3e;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.2s;
}

.preview-area :deep(a:hover) {
  border-bottom-color: #c45d3e;
}

.preview-area :deep(ul),
.preview-area :deep(ol) {
  padding-left: 1.5em;
  margin: 0.75em 0;
}

.preview-area :deep(li) {
  margin: 0.35em 0;
}

.preview-area :deep(hr) {
  border: none;
  height: 1px;
  background: linear-gradient(90deg, transparent, #e8e0d5, transparent);
  margin: 1.5em 0;
}
</style>