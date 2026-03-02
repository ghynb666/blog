<template>
  <div class="pagination" v-if="total > 0">
    <button :disabled="current <= 1" @click="changePage(current - 1)">上一页</button>
    <span v-for="p in pages" :key="p" :class="{ active: p === current }" @click="changePage(p)">{{ p }}</span>
    <button :disabled="current >= pageCount" @click="changePage(current + 1)">下一页</button>
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

const pages = computed(() => {
  const arr = []
  for (let i = 1; i <= pageCount.value; i++) arr.push(i)
  return arr
})

const changePage = (p) => {
  if (p >= 1 && p <= pageCount.value) emit('change', p)
}
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
  margin: 20px 0;
}
.pagination button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}
.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.pagination span {
  padding: 6px 12px;
  border: 1px solid #ddd;
  cursor: pointer;
}
.pagination span.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
</style>
