<template>
  <div class="mb-10">
    <h3 class="font-semibold text-apple-text text-sm mb-4">评论</h3>

    <!-- 分段控件 -->
    <div class="flex bg-gray-100 rounded-xl p-1 mb-4">
      <button
        @click="activeTab = 'REVIEW'"
        :class="['flex-1 py-1.5 text-xs font-medium rounded-lg transition-all', activeTab === 'REVIEW' ? 'bg-white text-apple-text shadow-sm' : 'text-apple-gray']"
      >
        整体评论
      </button>
      <button
        @click="activeTab = 'THOUGHT'"
        :class="['flex-1 py-1.5 text-xs font-medium rounded-lg transition-all', activeTab === 'THOUGHT' ? 'bg-white text-apple-text shadow-sm' : 'text-apple-gray']"
      >
        集/章感想
      </button>
    </div>

    <!-- 发表框 -->
    <div class="bg-white rounded-2xl p-4 shadow-sm mb-4">
      <div class="flex gap-3">
        <div class="w-8 h-8 bg-apple-blue/10 rounded-full flex-shrink-0 flex items-center justify-center text-apple-blue text-xs font-semibold">
          我
        </div>
        <div class="flex-1">
          <textarea v-model="newComment" :placeholder="activeTab === 'THOUGHT' ? '写下这集的感想...' : '写下你的评论...'" rows="2"
            class="w-full bg-transparent text-sm text-apple-text resize-none focus:outline-none placeholder-apple-gray"></textarea>
          <div class="flex items-center justify-between mt-2">
            <div v-if="activeTab === 'THOUGHT'" class="flex items-center gap-2">
              <span class="text-xs text-apple-gray">EP</span>
              <input v-model="episodeNum" type="number" min="1" placeholder="集数"
                class="w-16 bg-apple-bg border border-gray-200 rounded-lg px-2 py-1 text-xs text-apple-text focus:outline-none focus:ring-1 focus:ring-apple-blue/30" />
            </div>
            <button @click="submitComment" :disabled="!newComment.trim()"
              class="ml-auto px-4 py-1.5 bg-apple-blue text-white rounded-lg text-xs font-medium hover:bg-blue-600 disabled:opacity-40 transition-colors">
              发送
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="space-y-3">
      <div
        v-for="(comment, i) in filteredComments"
        :key="comment.id"
        class="bg-white rounded-2xl p-4 shadow-sm"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: i * 100 } }"
      >
        <div class="flex gap-3">
          <div class="w-8 h-8 bg-apple-blue/10 rounded-full flex-shrink-0 flex items-center justify-center text-apple-blue text-xs font-semibold">
            {{ comment.username?.charAt(0)?.toUpperCase() }}
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-1">
              <span class="text-sm font-medium text-apple-text">{{ comment.username }}</span>
              <span class="text-xs text-apple-gray">{{ comment.createdAt }}</span>
            </div>

            <!-- 集感想标签 -->
            <div v-if="comment.episodeNum" class="flex items-center gap-1 mb-2">
              <span class="px-1.5 py-0.5 bg-apple-orange/10 text-apple-orange text-xs font-medium rounded border-l-2 border-apple-orange">
                EP{{ comment.episodeNum }}
              </span>
            </div>

            <p class="text-sm text-apple-text leading-relaxed">{{ comment.content }}</p>

            <!-- 操作 -->
            <div class="flex items-center gap-4 mt-2">
              <button @click="toggleLike(comment)" class="flex items-center gap-1 text-xs transition-colors"
                :class="comment.likedByMe ? 'text-apple-red' : 'text-apple-gray hover:text-apple-red'">
                <svg class="w-3.5 h-3.5" :fill="comment.likedByMe ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                {{ comment.likeCount || '' }}
              </button>
              <button @click="replyingTo = comment.id" class="text-xs text-apple-gray hover:text-apple-blue transition-colors">
                回复
              </button>
            </div>

            <!-- 回复框 -->
            <div v-if="replyingTo === comment.id" class="mt-3 flex gap-2">
              <input v-model="replyContent" placeholder="回复..." class="flex-1 bg-apple-bg border border-gray-200 rounded-lg px-3 py-1.5 text-xs focus:outline-none focus:ring-1 focus:ring-apple-blue/30" />
              <button @click="submitReply(comment.id)" class="px-3 py-1.5 bg-apple-blue text-white rounded-lg text-xs font-medium hover:bg-blue-600">发送</button>
            </div>

            <!-- 回复列表 -->
            <div v-if="comment.replies?.length" class="mt-3 pl-4 border-l-2 border-gray-100 space-y-2">
              <div v-for="reply in comment.replies" :key="reply.id" class="flex gap-2">
                <div class="w-6 h-6 bg-gray-100 rounded-full flex-shrink-0 flex items-center justify-center text-xs font-medium text-apple-gray">
                  {{ reply.username?.charAt(0)?.toUpperCase() }}
                </div>
                <div>
                  <span class="text-xs font-medium text-apple-text">{{ reply.username }}</span>
                  <span class="text-xs text-apple-text ml-1">{{ reply.content }}</span>
                  <span class="text-xs text-apple-gray ml-1">{{ reply.createdAt }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空评论 -->
    <div v-if="!filteredComments.length" class="text-center py-10">
      <p class="text-apple-gray text-sm">还没有评论，来写第一条吧</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import commentsApi from '../api/comments'

const props = defineProps({
  workId: [String, Number],
  comments: { type: Array, default: () => [] },
})

const emit = defineEmits(['refresh'])

const activeTab = ref('REVIEW')
const newComment = ref('')
const episodeNum = ref('')
const replyingTo = ref(null)
const replyContent = ref('')

const filteredComments = computed(() => {
  return props.comments.filter(c => c.type === activeTab.value)
})

async function submitComment() {
  if (!newComment.value.trim()) return
  try {
    await commentsApi.create(props.workId, {
      content: newComment.value,
      type: activeTab.value,
      episodeNum: activeTab.value === 'THOUGHT' && episodeNum.value ? parseInt(episodeNum.value) : null,
    })
    newComment.value = ''
    episodeNum.value = ''
    emit('refresh')
  } catch (e) {
    console.error('发表评论失败', e)
  }
}

async function submitReply(commentId) {
  if (!replyContent.value.trim()) return
  try {
    await commentsApi.reply(commentId, { content: replyContent.value })
    replyContent.value = ''
    replyingTo.value = null
    emit('refresh')
  } catch (e) {
    console.error('回复失败', e)
  }
}

async function toggleLike(comment) {
  try {
    const res = await commentsApi.toggleLike(comment.id)
    comment.likedByMe = res.data.liked
    comment.likeCount += res.data.liked ? 1 : -1
  } catch (e) {
    console.error('点赞失败', e)
  }
}
</script>
