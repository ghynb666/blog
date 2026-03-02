<template>
  <div class="toc" v-if="headings.length">
    <h4>目录</h4>
    <ul>
      <li v-for="h in headings" :key="h.id" :class="`level-${h.level}`" @click="scrollTo(h.id)">{{ h.text }}</li>
    </ul>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ content: { type: String, default: '' } })

const headings = computed(() => {
  const regex = /^(#{1,6})\s+(.+)$/gm
  const arr = []
  let m
  while ((m = regex.exec(props.content)) !== null) {
    arr.push({ level: m[1].length, text: m[2], id: `heading-${arr.length}` })
  }
  return arr
})

const scrollTo = id => {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({ behavior: 'smooth' })
}
</script>

<style scoped>
.toc { background: #f9f9f9; padding: 16px; border-radius: 8px; }
.toc h4 { margin: 0 0 12px; font-size: 14px; color: #333; }
.toc ul { list-style: none; padding: 0; margin: 0; }
.toc li { padding: 6px 0; font-size: 13px; color: #666; cursor: pointer; }
.toc li:hover { color: #1890ff; }
.toc li.level-2 { padding-left: 12px; }
.toc li.level-3 { padding-left: 24px; }
.toc li.level-4 { padding-left: 36px; }
</style>
