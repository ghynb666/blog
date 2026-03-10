<template>
  <div class="toc" v-if="headings.length">
    <ul class="toc-list">
      <li v-for="h in headings" :key="h.id">
        <button type="button" class="toc-link" :class="`level-${h.level}`" @click="scrollTo(h.id)">
          <span class="toc-dot"></span>
          <span class="toc-text">{{ h.text }}</span>
        </button>
      </li>
    </ul>
  </div>
</template>

<script setup>
defineProps({ headings: { type: Array, default: () => [] } })

const scrollTo = id => {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
</script>

<style scoped>
.toc {
  border: 1px solid color-mix(in srgb, var(--border) 82%, #fff 18%);
  border-radius: 16px;
  padding: 10px;
  background: color-mix(in srgb, var(--bg) 90%, #fff 10%);
}

.toc-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.toc-link {
  width: 100%;
  border: none;
  background: transparent;
  cursor: pointer;
  display: grid;
  grid-template-columns: 10px minmax(0, 1fr);
  align-items: start;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 10px;
  text-align: left;
  color: var(--muted);
  transition: color 0.2s ease, background 0.2s ease;
}

.toc-link:hover {
  color: var(--accent);
  background: rgba(196, 93, 62, 0.1);
}

.toc-dot {
  margin-top: 0.45em;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentcolor;
  opacity: 0.5;
}

.toc-text {
  font-size: 13px;
  line-height: 1.45;
  word-break: break-word;
}

.toc-link.level-1 {
  font-weight: 700;
  color: var(--fg);
}

.toc-link.level-2 {
  padding-left: 18px;
}

.toc-link.level-3 {
  padding-left: 26px;
}

.toc-link.level-4 {
  padding-left: 34px;
  opacity: 0.9;
}
</style>
