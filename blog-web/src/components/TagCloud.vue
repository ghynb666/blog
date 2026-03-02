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

watch(() => props.modelValue, val => {
  selectedIds.value = [...val]
})

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
.tag-cloud { display: flex; flex-wrap: wrap; gap: 10px; }
.tag {
  padding: 4px 12px;
  background: #f0f0f0;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  user-select: none;
}
.tag:hover { background: #e0e0e0; }
.tag.active { background: #1890ff; color: #fff; }
.tag.active:hover { background: #40a9ff; }
</style>
