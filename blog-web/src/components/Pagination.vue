<template>
  <div class="pagination" v-if="total > 0">
    <button class="page-btn" :disabled="current <= 1" @click="changePage(current - 1)">
      <span class="arrow">←</span> 上一页
    </button>
    <div class="page-numbers">
      <span v-for="p in displayPages" :key="p" :class="{ active: p === current, ellipsis: p === '...' }" @click="p !== '...' && changePage(p)">{{ p }}</span>
    </div>
    <button class="page-btn" :disabled="current >= pageCount" @click="changePage(current + 1)">
      下一页 <span class="arrow">→</span>
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  total: { type: Number, default: 0 },
  pageSize: { type: Number, default: 10 },
  current: { type: Number, default: 1 }
})
const emit = defineEmits(['change'])
const pageCount = computed(() => Math.ceil(props.total / props.pageSize))
const displayPages = computed(() => {
  const total = pageCount.value
  const current = props.current
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1)
  const pages = []
  pages.push(1)
  if (current > 3) pages.push('...')
  const start = Math.max(2, current - 1)
  const end = Math.min(total - 1, current + 1)
  for (let i = start; i <= end; i++) pages.push(i)
  if (current < total - 2) pages.push('...')
  pages.push(total)
  return pages
})
const changePage = p => { if (p >= 1 && p <= pageCount.value) emit('change', p) }
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin: 48px 0;
}
.page-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border: 1px solid var(--border);
  background: var(--card);
  border-radius: 8px;
  font-size: 14px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.3s;
}
.page-btn:hover:not(:disabled) {
  border-color: var(--accent);
  color: var(--accent);
}
.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.arrow { font-size: 12px; }
.page-numbers { display: flex; gap: 4px; }
.page-numbers span {
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 14px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.3s;
}
.page-numbers span:hover:not(.ellipsis) {
  background: var(--bg);
  color: var(--fg);
}
.page-numbers span.active {
  background: var(--accent);
  color: #fff;
  font-weight: 600;
}
.page-numbers span.ellipsis {
  cursor: default;
  color: var(--muted);
}
@media (max-width: 600px) {
  .pagination { flex-wrap: wrap; gap: 12px; }
  .page-btn { padding: 8px 14px; font-size: 13px; }
  .page-numbers span { min-width: 36px; height: 36px; font-size: 13px; }
}
</style>
