<template>
  <div class="tag-cloud">
    <span
      v-for="tag in tags"
      :key="tag.id"
      class="tag"
      :class="{ active: selectedIds.includes(tag.id) }"
      @click="toggleTag(tag.id)"
    >
      {{ tag.name }}
    </span>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
const props = defineProps({
  tags: { type: Array, default: () => [] },
  modelValue: { type: Array, default: () => [] }
})
const emit = defineEmits(['update:modelValue', 'change'])
const selectedIds = ref([...props.modelValue])
watch(() => props.modelValue, val => { selectedIds.value = [...val] })
const toggleTag = id => {
  const idx = selectedIds.value.indexOf(id)
  if (idx > -1) {
    selectedIds.value.splice(idx, 1)
  } else {
    selectedIds.value.push(id)
  }
  emit('update:modelValue', [...selectedIds.value])
  emit('change', [...selectedIds.value])
}
</script>

<style scoped>
.tag-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.tag {
  padding: 6px 14px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 20px;
  font-size: 13px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
}
.tag:hover {
  border-color: var(--accent);
  color: var(--accent);
  transform: translateY(-2px);
}
.tag.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
  box-shadow: 0 4px 12px rgba(196, 93, 62, 0.3);
}
.tag.active:hover {
  background: var(--accent);
  transform: translateY(-2px);
}
</style>
