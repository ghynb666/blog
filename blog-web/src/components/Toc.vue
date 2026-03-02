<template>
  <div class="toc" v-if="headings.length">
    <h4 class="toc-title">目录</h4>
    <ul>
      <li v-for="h in headings" :key="h.id" :class="`level-${h.level}`" @click="scrollTo(h.id)">{{ h.text }}</li>
    </ul>
  </div>
</template>

<script setup>
defineProps({ headings: { type: Array, default: () => [] } })
const scrollTo = id => {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({ behavior: 'smooth' })
}
</script>

<style scoped>
.toc {
  background: var(--card);
  border: 1px solid var(--border);
  padding: 20px;
  border-radius: 12px;
}
.toc-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--fg);
  margin: 0 0 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}
.toc ul { list-style: none; padding: 0; margin: 0; }
.toc li {
  padding: 8px 0;
  font-size: 13px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.3s;
  border-left: 2px solid transparent;
  padding-left: 12px;
}
.toc li:hover {
  color: var(--accent);
  border-left-color: var(--accent);
}
.toc li.level-2 { padding-left: 20px; }
.toc li.level-3 { padding-left: 28px; }
.toc li.level-4 { padding-left: 36px; }
</style>
