<template>
  <article class="comment-card" :class="{ child: level > 0 }">
    <div class="comment-avatar">{{ avatarText(comment.user) }}</div>
    <div class="comment-body">
      <div class="comment-meta">
        <strong>{{ displayName(comment.user) }}</strong>
        <span>{{ formatDateTime(comment.createdAt) }}</span>
      </div>
      <p class="comment-content">
        <span v-if="comment.replyToUser" class="reply-target">@{{ displayName(comment.replyToUser) }}</span>
        {{ comment.content }}
      </p>
      <div class="comment-tools">
        <button type="button" class="comment-link" @click="$emit('reply', comment)">Reply</button>
      </div>

      <div v-if="comment.children?.length" class="comment-children">
        <CommentThread
          v-for="child in comment.children"
          :key="child.id"
          :comment="child"
          :level="level + 1"
          @reply="$emit('reply', $event)"
        />
      </div>
    </div>
  </article>
</template>

<script setup>
defineOptions({ name: 'CommentThread' })

defineProps({
  comment: {
    type: Object,
    required: true
  },
  level: {
    type: Number,
    default: 0
  }
})

defineEmits(['reply'])

const formatDateTime = value => {
  if (!value) return ''
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value))
}

const displayName = user => user?.nickname || user?.username || 'User'

const avatarText = user => displayName(user).slice(0, 1).toUpperCase()
</script>

<style scoped>
.comment-card {
  display: flex;
  gap: 14px;
  padding: 18px;
  background:
    linear-gradient(135deg, rgba(196, 93, 62, 0.06), transparent 35%),
    var(--bg);
  border-radius: 18px;
  border: 1px solid var(--border);
}

.comment-card.child {
  margin-top: 14px;
  background:
    linear-gradient(135deg, rgba(196, 93, 62, 0.1), transparent 40%),
    rgba(255, 255, 255, 0.55);
}

.comment-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent), #d98c68);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 10px 24px rgba(196, 93, 62, 0.2);
}

.comment-body {
  min-width: 0;
  flex: 1;
}

.comment-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  margin-bottom: 8px;
}

.comment-meta span {
  color: var(--muted);
  font-size: 13px;
}

.comment-content {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.7;
}

.reply-target {
  color: var(--accent);
  font-weight: 600;
  margin-right: 6px;
}

.comment-tools {
  margin-top: 12px;
}

.comment-link {
  padding: 0;
  border: none;
  background: transparent;
  color: var(--accent);
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
}

.comment-children {
  margin-top: 16px;
  padding-left: 18px;
  border-left: 2px solid rgba(196, 93, 62, 0.18);
}

@media (max-width: 640px) {
  .comment-card {
    padding: 16px;
  }

  .comment-children {
    padding-left: 12px;
  }
}
</style>
