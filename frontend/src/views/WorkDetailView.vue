<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-950 via-purple-950 to-slate-900 relative" v-if="work">
    <!-- 动态背景粒子效果 -->
    <div class="fixed inset-0 opacity-20 pointer-events-none">
      <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-anime-purple/30 rounded-full blur-[120px] animate-pulse-soft"></div>
      <div class="absolute top-3/4 right-1/4 w-80 h-80 bg-anime-pink/30 rounded-full blur-[100px] animate-pulse-soft" style="animation-delay: 1s;"></div>
    </div>

    <!-- 返回按钮 - 浮动设计 -->
    <div class="sticky top-6 z-40 px-6 py-4">
      <button
        @click="router.back()"
        class="group flex items-center gap-2 px-4 py-2 bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl text-white/70 hover:text-white hover:bg-white/10 transition-all duration-300"
        v-motion
        :initial="{ opacity: 0, x: -20 }"
        :enter="{ opacity: 1, x: 0, transition: { delay: 100 } }"
      >
        <svg class="w-4 h-4 transition-transform group-hover:-translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        返回
      </button>
    </div>

    <div class="max-w-7xl mx-auto px-6 pb-20 relative z-10">
      <!-- 英雄区域 - 非对称布局 -->
      <div class="flex flex-col lg:flex-row gap-8 mb-12">
        <!-- 左侧：封面 -->
        <div
          class="lg:w-80 flex-shrink-0"
          v-motion
          :initial="{ opacity: 0, y: 30 }"
          :enter="{ opacity: 1, y: 0, transition: { delay: 200, type: 'spring' } }"
        >
          <div class="relative group">
            <!-- 发光背景 -->
            <div class="absolute -inset-[1px] bg-gradient-to-br from-anime-purple to-anime-pink rounded-3xl opacity-0 group-hover:opacity-100 blur-xl transition-all duration-700"></div>

            <!-- 封面卡片 -->
            <div class="relative bg-slate-900/80 backdrop-blur-2xl border border-white/10 rounded-3xl overflow-hidden p-4">
              <div class="aspect-[2/3] rounded-2xl overflow-hidden bg-gradient-to-br from-slate-800 to-slate-900">
                <img v-if="work.coverUrl" :src="work.coverUrl" class="w-full h-full object-cover" />
                <div v-else class="w-full h-full flex items-center justify-center text-6xl">🎬</div>
              </div>

              <!-- 评分显示 -->
              <div v-if="work.myProgress?.rating" class="mt-4 flex items-center justify-center gap-3 py-3 bg-white/5 rounded-2xl">
                <span class="text-4xl font-black bg-gradient-to-r from-anime-purple-light to-anime-pink bg-clip-text text-transparent">
                  {{ work.myProgress.rating }}
                </span>
                <div class="text-left">
                  <p class="text-xs text-white/40">我的评分</p>
                  <div class="flex gap-0.5 mt-0.5">
                    <span v-for="i in 5" :key="i" class="text-xs"
                      :class="i * 2 <= work.myProgress.rating ? 'text-anime-pink' : 'text-white/20'">★</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：详情 -->
        <div class="flex-1 min-w-0">
          <!-- 标题和标签 -->
          <div
            v-motion
            :initial="{ opacity: 0, x: 30 }"
            :enter="{ opacity: 1, x: 0, transition: { delay: 300, type: 'spring' } }"
          >
            <h1 class="text-5xl font-black text-white mb-4 leading-tight" style="text-shadow: 0 0 30px rgba(212, 173, 252, 0.5);">
              {{ work.title }}
            </h1>

            <!-- 标签组 -->
            <div class="flex flex-wrap gap-3 mb-6">
              <span class="px-4 py-2 bg-white/10 backdrop-blur-md border border-white/20 rounded-xl text-sm font-semibold text-white">
                {{ typeLabel }}
              </span>
              <span v-if="work.totalEpisodes" class="px-4 py-2 bg-white/10 backdrop-blur-md border border-white/20 rounded-xl text-sm font-semibold text-white">
                共 {{ work.totalEpisodes }} 集
              </span>
              <span v-if="work.myProgress" class="px-4 py-2 backdrop-blur-md border rounded-xl text-sm font-semibold"
                :class="statusBadgeClass">
                {{ statusText }}
              </span>
            </div>

            <!-- 简介 -->
            <p class="text-white/70 text-base leading-relaxed mb-6">
              {{ work.description || '暂无简介' }}
            </p>

            <!-- 操作按钮组 -->
            <div class="flex flex-wrap gap-3">
              <AnimatedButton
                variant="primary"
                @click="markAs(work.myProgress?.status === 'WATCHING' ? 'WANT' : 'WATCHING')"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                {{ work.myProgress?.status === 'WATCHING' ? '正在追' : '开始追番' }}
              </AnimatedButton>

              <AnimatedButton variant="secondary" @click="markAs('WANT')">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />
                </svg>
                想看
              </AnimatedButton>

              <AnimatedButton variant="secondary" @click="showRatingModal = true">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                </svg>
                评分
              </AnimatedButton>

              <AnimatedButton variant="secondary" @click="showProgressModal = true">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
                更新进度
              </AnimatedButton>

              <AnimatedButton variant="danger" @click="confirmDelete" class="ml-auto">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                删除
              </AnimatedButton>
            </div>
          </div>
        </div>
      </div>

      <!-- 分集列表 - 始终尝试加载 -->
      <div
        v-motion
        :initial="{ opacity: 0, y: 30 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 400 } }"
      >
        <EpisodeList
          :work-id="work.id"
          @comment="scrollToComments"
          @progress-updated="onEpisodeProgressUpdated"
        />
      </div>

      <!-- 我的进度卡片 -->
      <div
        v-if="work.myProgress"
        class="bg-slate-900/60 backdrop-blur-xl border border-white/10 rounded-2xl p-6 mb-8"
        v-motion
        :initial="{ opacity: 0, y: 30 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 500 } }"
      >
        <h3 class="text-lg font-bold text-white mb-4">我的进度</h3>
        <div v-if="work.myProgress.progressDetail" class="text-white/70 text-sm mb-4">
          📍 {{ work.myProgress.progressDetail }}
        </div>
        <div class="flex gap-3">
          <button
            @click="showProgressModal = true"
            class="px-4 py-2 bg-anime-purple/20 hover:bg-anime-purple/30 border border-anime-purple-light/30 rounded-xl text-sm font-medium text-anime-purple-light transition-all"
          >
            更新进度
          </button>
          <button
            @click="removeProgress"
            class="px-4 py-2 bg-red-500/20 hover:bg-red-500/30 border border-red-400/30 rounded-xl text-sm font-medium text-red-300 transition-all"
          >
            删除进度
          </button>
        </div>
      </div>

      <!-- 朋友在看 -->
      <div
        v-if="work.friendProgresses?.length"
        class="mb-8"
        v-motion
        :initial="{ opacity: 0, y: 30 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 600 } }"
      >
        <h3 class="text-lg font-bold text-white mb-4">朋友在看</h3>
        <div class="flex gap-4 overflow-x-auto pb-2 no-scrollbar">
          <div
            v-for="(fp, index) in work.friendProgresses"
            :key="fp.userId"
            class="bg-slate-900/60 backdrop-blur-xl border border-white/10 rounded-2xl p-4 min-w-[200px] flex-shrink-0 hover:border-anime-purple-light/50 transition-all duration-300"
            v-motion
            :initial="{ opacity: 0, x: -20 }"
            :enter="{ opacity: 1, x: 0, transition: { delay: 700 + index * 50 } }"
          >
            <div class="flex items-center gap-2 mb-3">
              <div class="w-10 h-10 bg-gradient-to-br from-anime-cyan to-anime-purple rounded-xl flex items-center justify-center text-white text-sm font-bold">
                {{ fp.username.charAt(0).toUpperCase() }}
              </div>
              <span class="text-sm font-semibold text-white">{{ fp.username }}</span>
            </div>
            <p class="text-xs text-white/60 mb-1">{{ fp.progressDetail }}</p>
            <p v-if="fp.rating" class="text-xs font-semibold">
              <span class="text-anime-pink">{{ fp.rating }}</span>
              <span class="text-white/40">/10</span>
            </p>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div ref="commentsSection">
        <CommentSection
          ref="commentSectionRef"
          :work-id="work.id"
          :total-episodes="work.totalEpisodes || 0"
          :comments="work.comments"
          @refresh="loadWork"
        />
      </div>
    </div>

    <!-- 进度更新弹窗 -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showProgressModal" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-md" @click="showProgressModal = false"></div>
          <div
            class="relative bg-slate-900/90 backdrop-blur-2xl border border-white/10 rounded-3xl p-6 w-full max-w-md mx-4 shadow-anime-xl"
            v-motion
            :initial="{ opacity: 0, scale: 0.8, y: 40 }"
            :enter="{ opacity: 1, scale: 1, y: 0, transition: { type: 'spring', stiffness: 260 } }"
          >
            <div class="absolute top-0 left-0 right-0 h-[2px] bg-gradient-to-r from-anime-pink via-anime-purple to-anime-cyan"></div>

            <h3 class="text-xl font-bold text-white mb-6">更新进度</h3>

            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-white/60 mb-2">观看状态</label>
                <select
                  v-model="progressForm.status"
                  class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-anime-purple-light/50 transition-all"
                >
                  <option value="WANT">想看</option>
                  <option value="WATCHING">在看</option>
                  <option value="PAUSED">暂停</option>
                  <option value="DROPPED">弃</option>
                  <option value="DONE">看完</option>
                </select>
              </div>

              <div>
                <label class="block text-sm font-medium text-white/60 mb-2">进度描述</label>
                <input
                  v-model="progressForm.progressDetail"
                  placeholder="如：第5集 / 第3章"
                  class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-white/30 focus:outline-none focus:border-anime-purple-light/50 transition-all"
                />
              </div>
            </div>

            <div class="flex gap-3 mt-6">
              <AnimatedButton variant="ghost" @click="showProgressModal = false" class="flex-1">
                取消
              </AnimatedButton>
              <AnimatedButton variant="primary" @click="updateProgress" class="flex-1">
                保存
              </AnimatedButton>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- 评分弹窗 -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showRatingModal" class="fixed inset-0 z-[100] flex items-center justify-center">
          <div class="absolute inset-0 bg-black/60 backdrop-blur-md" @click="showRatingModal = false"></div>
          <div
            class="relative bg-slate-900/90 backdrop-blur-2xl border border-white/10 rounded-3xl p-6 w-full max-w-md mx-4 shadow-anime-xl"
            v-motion
            :initial="{ opacity: 0, scale: 0.8, y: 40 }"
            :enter="{ opacity: 1, scale: 1, y: 0, transition: { type: 'spring', stiffness: 260 } }"
          >
            <div class="absolute top-0 left-0 right-0 h-[2px] bg-gradient-to-r from-anime-pink via-anime-purple to-anime-cyan"></div>

            <h3 class="text-xl font-bold text-white mb-6">评分</h3>

            <div class="flex justify-center gap-2 mb-6">
              <button
                v-for="i in 5"
                :key="i"
                @click="ratingValue = i * 2"
                class="text-5xl transition-all duration-200 hover:scale-125"
                :class="i * 2 <= ratingValue ? 'text-anime-pink' : 'text-white/20'"
              >
                ★
              </button>
            </div>

            <p class="text-center mb-6">
              <span class="text-5xl font-black bg-gradient-to-r from-anime-purple-light to-anime-pink bg-clip-text text-transparent">
                {{ ratingValue }}
              </span>
              <span class="text-white/40 text-2xl">/10</span>
            </p>

            <div class="flex gap-3">
              <AnimatedButton variant="ghost" @click="showRatingModal = false" class="flex-1">
                取消
              </AnimatedButton>
              <AnimatedButton variant="primary" @click="submitRating" class="flex-1">
                提交
              </AnimatedButton>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import worksApi from '../api/works'
import progressApi from '../api/progress'
import CommentSection from '../components/CommentSection.vue'
import AnimatedButton from '../components/AnimatedButton.vue'
import EpisodeList from '../components/EpisodeList.vue'

const route = useRoute()
const router = useRouter()

const work = ref(null)
const showProgressModal = ref(false)
const showRatingModal = ref(false)
const ratingValue = ref(5)
const progressForm = ref({ status: 'WATCHING', progressDetail: '' })
const commentsSection = ref(null)
const commentSectionRef = ref(null)

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
    WANT: 'bg-orange-500/20 border-orange-400/30 text-orange-300',
    WATCHING: 'bg-blue-500/20 border-blue-400/30 text-blue-300',
    PAUSED: 'bg-gray-500/20 border-gray-400/30 text-gray-300',
    DROPPED: 'bg-red-500/20 border-red-400/30 text-red-300',
    DONE: 'bg-green-500/20 border-green-400/30 text-green-300',
  }
  return map[work.value?.myProgress?.status] || ''
})

async function loadWork() {
  try {
    const res = await worksApi.detail(route.params.id)
    work.value = res.data

    if (work.value.myProgress) {
      progressForm.value = {
        status: work.value.myProgress.status,
        progressDetail: work.value.myProgress.progressDetail || '',
      }
      ratingValue.value = work.value.myProgress.rating || 5
    }
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
  if (confirm('确定要删除进度记录吗？')) {
    try {
      await progressApi.removeProgress(route.params.id)
      await loadWork()
    } catch (e) {
      console.error('删除进度失败', e)
    }
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

function confirmDelete() {
  if (confirm(`确定要删除「${work.value?.title}」吗？此操作不可恢复。`)) {
    deleteWork()
  }
}

async function deleteWork() {
  try {
    await worksApi.remove(route.params.id)
    router.push('/')
  } catch (e) {
    console.error('删除作品失败', e)
  }
}

function scrollToComments(episodeNum) {
  // 切换到分集感想标签并定位到该集
  commentSectionRef.value?.focusEpisode(episodeNum)
  if (commentsSection.value) {
    commentsSection.value.scrollIntoView({ behavior: 'smooth' })
  }
}

function onEpisodeProgressUpdated(watchedCount, totalCount) {
  // 可选：根据分集进度自动更新作品进度
  console.log(`观看进度更新：${watchedCount}/${totalCount}`)
}

onMounted(loadWork)
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
