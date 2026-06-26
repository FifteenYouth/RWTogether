<template>
  <div class="space-y-6">
    <!-- 标题 -->
    <h3 class="text-2xl font-bold text-white">评论区</h3>

    <!-- 分段控件 - 二次元风格 -->
    <div class="flex bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-1.5">
      <button
        @click="activeTab = 'REVIEW'"
        :class="[
          'flex-1 py-3 text-sm font-semibold rounded-xl transition-all duration-300',
          activeTab === 'REVIEW'
            ? 'bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep text-white shadow-glow-purple'
            : 'text-white/60 hover:text-white hover:bg-white/5'
        ]"
      >
        整体评论
      </button>
      <button
        @click="activeTab = 'THOUGHT'"
        :class="[
          'flex-1 py-3 text-sm font-semibold rounded-xl transition-all duration-300',
          activeTab === 'THOUGHT'
            ? 'bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep text-white shadow-glow-purple'
            : 'text-white/60 hover:text-white hover:bg-white/5'
        ]"
      >
        分集感想
      </button>
    </div>

    <!-- 集数筛选 (仅分集感想) -->
    <div v-if="activeTab === 'THOUGHT' && episodeChips.length" class="flex flex-wrap gap-2">
      <button
        @click="episodeFilter = null"
        :class="[
          'px-3 py-1.5 rounded-xl text-xs font-semibold transition-all duration-200',
          episodeFilter === null
            ? 'bg-anime-purple/30 border border-anime-purple-light/50 text-anime-purple-light'
            : 'bg-white/5 border border-white/10 text-white/50 hover:text-white hover:bg-white/10'
        ]"
      >
        全部
      </button>
      <button
        v-for="ep in episodeChips"
        :key="ep"
        @click="episodeFilter = ep"
        :class="[
          'px-3 py-1.5 rounded-xl text-xs font-semibold transition-all duration-200',
          episodeFilter === ep
            ? 'bg-anime-purple/30 border border-anime-purple-light/50 text-anime-purple-light'
            : 'bg-white/5 border border-white/10 text-white/50 hover:text-white hover:bg-white/10'
        ]"
      >
        第 {{ ep }} 集
      </button>
    </div>

    <!-- 发表框 -->
    <div class="bg-slate-900/60 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
      <div class="flex gap-4">
        <!-- 用户头像 -->
        <div class="w-10 h-10 bg-gradient-to-br from-anime-cyan to-anime-purple rounded-xl flex-shrink-0 flex items-center justify-center text-white text-sm font-bold shadow-anime">
          {{ currentUserInitial }}
        </div>

        <!-- 输入区域 -->
        <div class="flex-1">
          <textarea
            v-model="newComment"
            :placeholder="activeTab === 'THOUGHT' ? '写下这集的感想...' : '写下你的评论...'"
            rows="3"
            class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white text-sm resize-none focus:outline-none focus:border-anime-purple-light/50 placeholder-white/40 transition-all"
          ></textarea>

          <div class="flex items-center justify-between mt-3">
            <!-- 集数选择 (仅分集感想) -->
            <div v-if="activeTab === 'THOUGHT'" class="flex items-center gap-3">
              <span class="text-sm font-medium text-white/60">集数</span>
              <input
                v-model="episodeNum"
                type="number"
                min="1"
                :max="totalEpisodes"
                placeholder="EP"
                class="w-20 bg-white/5 border border-white/10 rounded-xl px-3 py-2 text-white text-sm text-center focus:outline-none focus:border-anime-purple-light/50 placeholder-white/40"
              />
              <span class="text-xs text-white/40">/ {{ totalEpisodes || '?' }}</span>
            </div>

            <div v-else class="flex-1"></div>

            <!-- 发送按钮 -->
            <AnimatedButton
              variant="primary"
              @click="submitComment"
              :disabled="!canSubmit"
              size="sm"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
              </svg>
              发送
            </AnimatedButton>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="space-y-4">
      <div
        v-for="(comment, i) in filteredComments"
        :key="comment.id"
        class="group bg-slate-900/60 backdrop-blur-xl border border-white/10 hover:border-anime-purple-light/30 rounded-2xl p-6 transition-all duration-300"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: i * 50 } }"
      >
        <div class="flex gap-4">
          <!-- 用户头像 -->
          <div class="w-10 h-10 bg-gradient-to-br from-anime-cyan to-anime-purple rounded-xl flex-shrink-0 flex items-center justify-center text-white text-sm font-bold shadow-anime">
            {{ comment.username?.charAt(0)?.toUpperCase() || 'U' }}
          </div>

          <!-- 评论内容 -->
          <div class="flex-1 min-w-0">
            <!-- 用户名和时间 -->
            <div class="flex items-center gap-3 mb-2">
              <span class="text-sm font-semibold text-white">{{ comment.username || '匿名' }}</span>
              <span class="text-xs text-white/40">{{ formatDate(comment.createdAt) }}</span>
            </div>

            <!-- 集数标签 (分集感想) -->
            <div v-if="comment.episodeNum" class="mb-3">
              <span class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-anime-purple/20 border border-anime-purple-light/30 rounded-xl text-xs font-bold text-anime-purple-light">
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 4v16M17 4v16M3 8h4m10 0h4M3 12h18M3 16h4m10 0h4M4 20h16a1 1 0 001-1V5a1 1 0 00-1-1H4a1 1 0 00-1 1v14a1 1 0 001 1z" />
                </svg>
                第 {{ comment.episodeNum }} 集
              </span>
            </div>

            <!-- 评论文本 -->
            <p class="text-sm text-white/80 leading-relaxed mb-3">{{ comment.content }}</p>

            <!-- 操作按钮 -->
            <div class="flex items-center gap-4">
              <!-- 点赞 -->
              <button
                @click="toggleLike(comment)"
                class="group/like flex items-center gap-1.5 transition-all duration-200"
                :class="comment.likedByMe ? 'text-anime-pink' : 'text-white/40 hover:text-anime-pink'"
              >
                <svg
                  class="w-4 h-4 transition-transform group-hover/like:scale-125"
                  :fill="comment.likedByMe ? 'currentColor' : 'none'"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                <span class="text-xs font-medium">{{ comment.likeCount || 0 }}</span>
              </button>

              <!-- 回复 -->
              <button
                @click="replyingTo = replyingTo === comment.id ? null : comment.id"
                class="flex items-center gap-1.5 text-white/40 hover:text-anime-cyan transition-colors text-xs font-medium"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
                </svg>
                回复
              </button>
            </div>

            <!-- 回复输入框 -->
            <div v-if="replyingTo === comment.id" class="mt-4 flex gap-3">
              <input
                v-model="replyContent"
                placeholder="写下你的回复..."
                class="flex-1 bg-white/5 border border-white/10 rounded-xl px-4 py-2 text-white text-sm focus:outline-none focus:border-anime-purple-light/50 placeholder-white/40"
                @keyup.enter="submitReply(comment.id)"
              />
              <AnimatedButton variant="secondary" @click="submitReply(comment.id)" size="sm">
                发送
              </AnimatedButton>
            </div>

            <!-- 回复列表 -->
            <div v-if="comment.replies?.length" class="mt-4 pl-6 border-l-2 border-anime-purple/20 space-y-3">
              <div
                v-for="reply in comment.replies"
                :key="reply.id"
                class="flex gap-3"
              >
                <div class="w-8 h-8 bg-gradient-to-br from-anime-cyan to-anime-purple rounded-lg flex-shrink-0 flex items-center justify-center text-white text-xs font-bold">
                  {{ reply.username?.charAt(0)?.toUpperCase() || 'U' }}
                </div>
                <div class="flex-1">
                  <div class="flex items-center gap-2 mb-1">
                    <span class="text-xs font-semibold text-white">{{ reply.username || '匿名' }}</span>
                    <span class="text-xs text-white/30">{{ formatDate(reply.createdAt) }}</span>
                  </div>
                  <p class="text-sm text-white/70">{{ reply.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredComments.length === 0" class="text-center py-12">
        <div class="text-6xl mb-4 opacity-50">💬</div>
        <p class="text-white/40 text-sm">
          {{ activeTab === 'REVIEW' ? '还没有评论，快来发表第一条吧' : '还没有分集感想' }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import commentsApi from '../api/comments'
import AnimatedButton from './AnimatedButton.vue'

const props = defineProps({
  workId: {
    type: Number,
    required: true,
  },
  totalEpisodes: {
    type: Number,
    default: 0,
  },
  comments: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['refresh'])

const activeTab = ref('REVIEW')
const newComment = ref('')
const episodeNum = ref(null)
const episodeFilter = ref(null)
const replyingTo = ref(null)
const replyContent = ref('')

const currentUserInitial = computed(() => {
  // 从 localStorage 或 store 获取当前用户信息
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.username?.charAt(0)?.toUpperCase() || '我'
})

// 分集感想中出现过的集数（升序，去重）
const episodeChips = computed(() => {
  if (!props.comments) return []
  const nums = props.comments
    .filter(c => c.type === 'THOUGHT' && c.episodeNum != null)
    .map(c => c.episodeNum)
  return [...new Set(nums)].sort((a, b) => a - b)
})

const filteredComments = computed(() => {
  if (!props.comments) return []
  let list = props.comments.filter(c => c.type === activeTab.value)
  if (activeTab.value === 'THOUGHT' && episodeFilter.value != null) {
    list = list.filter(c => c.episodeNum === episodeFilter.value)
  }
  return list
})

const canSubmit = computed(() => {
  if (!newComment.value.trim()) return false
  if (activeTab.value === 'THOUGHT' && (!episodeNum.value || episodeNum.value < 1)) return false
  return true
})

async function submitComment() {
  if (!canSubmit.value) return

  try {
    const data = {
      content: newComment.value.trim(),
      type: activeTab.value,
    }

    if (activeTab.value === 'THOUGHT') {
      data.episodeNum = parseInt(episodeNum.value)
    }

    await commentsApi.create(props.workId, data)

    newComment.value = ''
    episodeNum.value = null
    emit('refresh')
  } catch (e) {
    console.error('发表评论失败', e)
    alert('发表评论失败，请重试')
  }
}

async function submitReply(commentId) {
  if (!replyContent.value.trim()) return

  try {
    await commentsApi.reply(commentId, { content: replyContent.value.trim() })
    replyContent.value = ''
    replyingTo.value = null
    emit('refresh')
  } catch (e) {
    console.error('回复失败', e)
    alert('回复失败，请重试')
  }
}

async function toggleLike(comment) {
  try {
    await commentsApi.toggleLike(comment.id)
    emit('refresh')
  } catch (e) {
    console.error('点赞失败', e)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = Math.floor((now - date) / 1000)

  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)} 分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)} 小时前`
  if (diff < 2592000) return `${Math.floor(diff / 86400)} 天前`

  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

// 由父组件调用：从分集列表点击"评论"时，切换到分集感想并定位集数
function focusEpisode(num) {
  activeTab.value = 'THOUGHT'
  if (num != null) {
    episodeNum.value = num
    episodeFilter.value = num
  }
}

defineExpose({ focusEpisode })

</script>
