<template>
  <div class="min-h-screen bg-apple-bg" v-if="work">
    <!-- 英雄区域 -->
    <div class="relative bg-gradient-to-b from-[#1d1d1f] via-[#2c2c2e] to-[#3a3a3c] pt-6 pb-20">
      <div class="max-w-4xl mx-auto px-6">
        <!-- 返回按钮 -->
        <button @click="router.back()" class="text-white/70 hover:text-white text-sm mb-6 flex items-center gap-1">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          返回
        </button>

        <div class="flex gap-6">
          <!-- 封面 -->
          <div class="w-32 h-44 rounded-[14px] bg-white/10 flex-shrink-0 overflow-hidden shadow-lg">
            <img v-if="work.coverUrl" :src="work.coverUrl" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center text-3xl">🎬</div>
          </div>

          <div class="flex-1 min-w-0">
            <!-- 评分 -->
            <div v-if="work.myProgress?.rating" class="flex items-center gap-1 mb-2">
              <span class="text-apple-orange text-lg font-bold">{{ work.myProgress.rating }}</span>
              <span class="text-white/50 text-xs">/10</span>
            </div>
            <!-- 标题 -->
            <h1 class="text-[26px] font-bold text-white tracking-tightest leading-tight">{{ work.title }}</h1>
            <!-- 标签 -->
            <div class="flex gap-2 mt-3">
              <span class="px-2.5 py-1 rounded-lg text-xs font-medium text-white/80 bg-white/[0.12] backdrop-blur-sm">
                {{ typeLabel }}
              </span>
              <span v-if="work.totalEpisodes" class="px-2.5 py-1 rounded-lg text-xs font-medium text-white/80 bg-white/[0.12] backdrop-blur-sm">
                {{ work.totalEpisodes }} 集
              </span>
            </div>
            <!-- 简介 -->
            <p class="text-white/75 text-sm mt-4 line-clamp-3">{{ work.description }}</p>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-3 mt-6">
          <button @click="markAs('WATCHING')" class="px-5 py-2 bg-apple-blue text-white rounded-xl text-sm font-semibold hover:bg-blue-600 transition-colors">
            {{ work.myProgress?.status === 'WATCHING' ? '正在追' : '标记在看' }}
          </button>
          <button @click="markAs('WANT')" class="px-5 py-2 bg-white/[0.12] backdrop-blur-sm text-white rounded-xl text-sm font-medium hover:bg-white/20 transition-colors">
            想看
          </button>
          <button @click="showRatingModal = true" class="px-5 py-2 bg-white/[0.12] backdrop-blur-sm text-white rounded-xl text-sm font-medium hover:bg-white/20 transition-colors">
            评分
          </button>
        </div>
      </div>
    </div>

    <div class="max-w-4xl mx-auto px-6 -mt-5">
      <!-- 我的进度卡片 -->
      <div v-if="work.myProgress" class="bg-white rounded-2xl p-5 shadow-sm mb-6">
        <div class="flex items-center justify-between mb-3">
          <h3 class="font-semibold text-apple-text text-sm">我的进度</h3>
          <span class="text-xs px-2 py-0.5 rounded-full font-medium"
            :class="statusBadgeClass">
            {{ statusText }}
          </span>
        </div>
        <div v-if="work.myProgress.progressDetail" class="text-apple-gray text-sm mb-2">
          {{ work.myProgress.progressDetail }}
        </div>
        <div class="flex gap-2 mt-3">
          <button @click="showProgressModal = true" class="text-apple-blue text-xs font-medium hover:underline">
            更新进度
          </button>
          <button @click="removeProgress" class="text-apple-red text-xs font-medium hover:underline">
            删除
          </button>
        </div>
      </div>

      <!-- 朋友在看 -->
      <div v-if="work.friendProgresses?.length" class="mb-6">
        <h3 class="font-semibold text-apple-text text-sm mb-3">朋友在看</h3>
        <div class="flex gap-3 overflow-x-auto pb-2">
          <div v-for="fp in work.friendProgresses" :key="fp.userId"
            class="bg-white rounded-2xl p-4 shadow-sm min-w-[160px] flex-shrink-0">
            <div class="flex items-center gap-2 mb-2">
              <div class="w-7 h-7 bg-apple-blue/10 rounded-full flex items-center justify-center text-apple-blue text-xs font-semibold">
                {{ fp.username.charAt(0).toUpperCase() }}
              </div>
              <span class="text-sm font-medium text-apple-text">{{ fp.username }}</span>
            </div>
            <p class="text-xs text-apple-gray">{{ fp.status }} {{ fp.progressDetail }}</p>
            <p v-if="fp.rating" class="text-xs text-apple-orange mt-1">{{ fp.rating }}/10</p>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <CommentSection :work-id="work.id" :comments="work.comments" @refresh="loadWork" />
    </div>

    <!-- 进度更新弹窗 -->
    <div v-if="showProgressModal" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click.self="showProgressModal = false">
      <div class="bg-white rounded-2xl p-6 w-80 shadow-xl">
        <h3 class="font-semibold text-apple-text mb-4">更新进度</h3>
        <select v-model="progressForm.status" class="w-full bg-apple-bg border border-gray-200 rounded-xl px-3 py-2 text-sm mb-3">
          <option value="WANT">想看</option>
          <option value="WATCHING">在看</option>
          <option value="PAUSED">暂停</option>
          <option value="DROPPED">弃</option>
          <option value="DONE">看完</option>
        </select>
        <input v-model="progressForm.progressDetail" placeholder="如：第5集/第3章" class="w-full bg-apple-bg border border-gray-200 rounded-xl px-3 py-2 text-sm mb-3" />
        <div class="flex gap-2">
          <button @click="showProgressModal = false" class="flex-1 py-2 text-sm text-apple-gray hover:bg-gray-100 rounded-xl">取消</button>
          <button @click="updateProgress" class="flex-1 py-2 text-sm text-white bg-apple-blue rounded-xl font-medium hover:bg-blue-600">保存</button>
        </div>
      </div>
    </div>

    <!-- 评分弹窗 -->
    <div v-if="showRatingModal" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click.self="showRatingModal = false">
      <div class="bg-white rounded-2xl p-6 w-80 shadow-xl">
        <h3 class="font-semibold text-apple-text mb-4">评分</h3>
        <div class="flex justify-center gap-1 mb-4">
          <button v-for="i in 5" :key="i" @click="ratingValue = i * 2" class="text-2xl transition-transform hover:scale-110"
            :class="i * 2 <= ratingValue ? 'text-apple-orange' : 'text-gray-200'">
            ★
          </button>
        </div>
        <p class="text-center text-sm text-apple-gray mb-4">{{ ratingValue }}/10</p>
        <div class="flex gap-2">
          <button @click="showRatingModal = false" class="flex-1 py-2 text-sm text-apple-gray hover:bg-gray-100 rounded-xl">取消</button>
          <button @click="submitRating" class="flex-1 py-2 text-sm text-white bg-apple-blue rounded-xl font-medium hover:bg-blue-600">提交</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import worksApi from '../api/works'
import progressApi from '../api/progress'
import CommentSection from '../components/CommentSection.vue'

const route = useRoute()
const router = useRouter()

const work = ref(null)
const showProgressModal = ref(false)
const showRatingModal = ref(false)
const ratingValue = ref(5)
const progressForm = ref({ status: 'WATCHING', progressDetail: '' })

const typeLabel = computed(() => {
  const map = { ANIME: '番剧', DRAMA: '电视剧', MOVIE: '电影', BOOK: '书籍' }
  return map[work.value?.type] || ''
})

const statusText = computed(() => {
  const map = { WANT: '想看', WATCHING: '在看', PAUSED: '暂停', DROPPED: '弃', DONE: '看完' }
  return map[work.value?.myProgress?.status] || ''
})

const statusBadgeClass = computed(() => {
  const map = {
    WANT: 'bg-apple-orange/10 text-apple-orange',
    WATCHING: 'bg-apple-blue/10 text-apple-blue',
    PAUSED: 'bg-gray-100 text-apple-gray',
    DROPPED: 'bg-apple-red/10 text-apple-red',
    DONE: 'bg-apple-green/10 text-apple-green',
  }
  return map[work.value?.myProgress?.status] || ''
})

async function loadWork() {
  try {
    const res = await worksApi.detail(route.params.id)
    work.value = res.data
  } catch (e) {
    console.error('加载作品详情失败', e)
  }
}

async function markAs(status) {
  try {
    await progressApi.setProgress(route.params.id, { status })
    await loadWork()
  } catch (e) {
    console.error('标记失败', e)
  }
}

async function updateProgress() {
  try {
    await progressApi.updateProgress(route.params.id, progressForm.value)
    showProgressModal.value = false
    await loadWork()
  } catch (e) {
    console.error('更新进度失败', e)
  }
}

async function removeProgress() {
  try {
    await progressApi.removeProgress(route.params.id)
    await loadWork()
  } catch (e) {
    console.error('删除进度失败', e)
  }
}

async function submitRating() {
  try {
    await progressApi.setProgress(route.params.id, {
      status: work.value?.myProgress?.status || 'WATCHING',
      rating: ratingValue.value,
    })
    showRatingModal.value = false
    await loadWork()
  } catch (e) {
    console.error('评分失败', e)
  }
}

onMounted(loadWork)
</script>
