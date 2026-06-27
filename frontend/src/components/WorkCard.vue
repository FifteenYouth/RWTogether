<template>
  <div
    class="group relative cursor-pointer"
    @click="$emit('click')"
    v-motion
    :initial="{ opacity: 0, y: 30 }"
    :enter="{ opacity: 1, y: 0, transition: { delay: index * 80, type: 'spring', stiffness: 200 } }"
  >
    <!-- 发光背景层 -->
    <div
      class="absolute -inset-[1px] rounded-3xl opacity-0 group-hover:opacity-100 transition-all duration-700 blur-xl"
      :class="glowClass"
    ></div>

    <!-- 主卡片容器 -->
    <div class="relative bg-slate-900/80 backdrop-blur-2xl border rounded-3xl overflow-hidden transition-all duration-500 group-hover:-translate-y-3 group-hover:scale-[1.02]"
      :class="borderClass">

      <!-- 顶部彩虹条 -->
      <div class="absolute top-0 left-0 right-0 h-[3px]" :style="{ background: coverGradient }"></div>

      <!-- 封面区域 - 非对称设计 -->
      <div class="relative h-48 overflow-hidden bg-gradient-to-br from-slate-800 to-slate-900">
        <!-- 封面图片 -->
        <img
          v-if="work.coverUrl && !coverBroken"
          :src="work.coverUrl"
          :alt="work.title"
          class="absolute inset-0 w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
          @error="coverBroken = true"
        />
        <!-- 动态渐变背景（无封面时作为占位） -->
        <div class="absolute inset-0 opacity-40" :style="{ background: coverGradient }"></div>

        <!-- 图片上的暗角渐变，保证标签可读 -->
        <div v-if="work.coverUrl && !coverBroken" class="absolute inset-0 bg-gradient-to-t from-slate-900/90 via-slate-900/20 to-slate-900/40"></div>

        <!-- 网格纹理 -->
        <div v-if="!work.coverUrl || coverBroken" class="absolute inset-0 opacity-10" style="background-image: repeating-linear-gradient(0deg, transparent, transparent 2px, white 2px, white 3px), repeating-linear-gradient(90deg, transparent, transparent 2px, white 2px, white 3px);"></div>

        <!-- 删除按钮 - 磁性设计 -->
        <button
          @click.stop="confirmDelete"
          class="absolute top-3 right-3 w-9 h-9 bg-red-500/20 backdrop-blur-md rounded-xl flex items-center justify-center text-red-300 hover:bg-red-500 hover:text-white hover:scale-110 hover:rotate-90 transition-all duration-300 opacity-0 group-hover:opacity-100 border border-red-500/30"
          title="删除"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>

        <!-- 类型标签 - 霓虹设计 -->
        <div class="absolute top-3 left-3 px-3 py-1.5 rounded-xl text-xs font-bold text-white shadow-lg backdrop-blur-md border"
          :class="typeColorClass">
          <span class="relative z-10">{{ typeLabel }}</span>
        </div>

        <!-- 状态标签 - 底部浮动 -->
        <div
          v-if="status"
          class="absolute bottom-3 left-3 px-3 py-1.5 rounded-xl text-xs font-bold text-white backdrop-blur-md border shadow-lg"
          :class="statusColorClass"
        >
          {{ statusLabel }}
        </div>

        <!-- 进度环 - 右下角 -->
        <div v-if="progress > 0" class="absolute bottom-3 right-3">
          <div class="relative w-12 h-12">
            <!-- 背景圆环 -->
            <svg class="w-full h-full transform -rotate-90">
              <circle cx="24" cy="24" r="20" stroke="rgba(255,255,255,0.1)" stroke-width="3" fill="none" />
              <circle
                cx="24"
                cy="24"
                r="20"
                stroke="url(#progress-gradient)"
                stroke-width="3"
                fill="none"
                :stroke-dasharray="`${progress * 1.26} 126`"
                class="transition-all duration-700"
              />
            </svg>
            <span class="absolute inset-0 flex items-center justify-center text-xs font-bold text-white">
              {{ progress }}%
            </span>
            <svg width="0" height="0">
              <defs>
                <linearGradient id="progress-gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#D4ADFC;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#FFB3D9;stop-opacity:1" />
                </linearGradient>
              </defs>
            </svg>
          </div>
        </div>
      </div>

      <!-- 信息区域 -->
      <div class="p-5 relative">
        <!-- 标题 -->
        <h3 class="text-base font-bold text-white mb-2 line-clamp-2 leading-tight group-hover:text-transparent group-hover:bg-clip-text group-hover:bg-gradient-to-r group-hover:from-anime-purple-light group-hover:to-anime-pink-light transition-all duration-300">
          {{ work.title }}
        </h3>

        <!-- 元信息 -->
        <div class="flex items-center gap-3 text-xs text-white/50">
          <span class="flex items-center gap-1">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 4v16M17 4v16M3 8h4m10 0h4M3 12h18M3 16h4m10 0h4M4 20h16a1 1 0 001-1V5a1 1 0 00-1-1H4a1 1 0 00-1 1v14a1 1 0 001 1z" />
            </svg>
            {{ work.totalEpisodes }} 集
          </span>
          <span v-if="work._progressDetail" class="flex items-center gap-1">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
            {{ work._progressDetail }}
          </span>
        </div>

        <!-- 进度条 - 如果有进度 -->
        <div v-if="progress > 0" class="mt-4">
          <div class="h-1.5 bg-white/10 rounded-full overflow-hidden relative">
            <!-- 动态光效 -->
            <div
              class="absolute inset-0 bg-gradient-to-r from-transparent via-white/30 to-transparent"
              style="background-size: 200% 100%; animation: shimmer 2s linear infinite;"
            ></div>
            <!-- 实际进度 -->
            <div
              class="h-full rounded-full transition-all duration-700 relative overflow-hidden"
              :style="{ width: progress + '%', background: progressGradient }"
            >
              <!-- 内部闪光 -->
              <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/40 to-transparent"
                style="background-size: 200% 100%; animation: shimmer 2s linear infinite;"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 悬停时的光效扫描 -->
      <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-1000 pointer-events-none"></div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  work: Object,
  status: String,
  index: { type: Number, default: 0 },
})

const coverBroken = ref(false)

const emit = defineEmits(['click', 'delete'])

function confirmDelete() {
  if (confirm(`确定要删除「${props.work.title}」吗？此操作不可恢复。`)) {
    emit('delete', props.work.id)
  }
}

const typeLabel = computed(() => {
  const map = { ANIME: '番剧', DRAMA: '电视剧', MOVIE: '电影', BOOK: '书籍' }
  return map[props.work.type] || props.work.type
})

const statusLabel = computed(() => {
  const map = { WANT: '想看', WATCHING: '在看', PAUSED: '暂停', DROPPED: '弃', DONE: '看完' }
  return map[props.status] || ''
})

const typeColorClass = computed(() => {
  const map = {
    ANIME: 'bg-purple-500/30 border-purple-400/50 shadow-glow-purple',
    DRAMA: 'bg-pink-500/30 border-pink-400/50 shadow-glow-pink',
    MOVIE: 'bg-cyan-500/30 border-cyan-400/50 shadow-glow-cyan',
    BOOK: 'bg-green-500/30 border-green-400/50',
  }
  return map[props.work.type] || 'bg-purple-500/30 border-purple-400/50'
})

const statusColorClass = computed(() => {
  const map = {
    WANT: 'bg-orange-500/30 border-orange-400/50',
    WATCHING: 'bg-blue-500/30 border-blue-400/50',
    PAUSED: 'bg-gray-500/30 border-gray-400/50',
    DROPPED: 'bg-red-500/30 border-red-400/50',
    DONE: 'bg-green-500/30 border-green-400/50',
  }
  return map[props.status] || 'bg-gray-500/30 border-gray-400/50'
})

const borderClass = computed(() => {
  const map = {
    ANIME: 'border-purple-500/20',
    DRAMA: 'border-pink-500/20',
    MOVIE: 'border-cyan-500/20',
    BOOK: 'border-green-500/20',
  }
  return map[props.work.type] || 'border-purple-500/20'
})

const glowClass = computed(() => {
  const map = {
    ANIME: 'bg-gradient-to-br from-purple-500 to-purple-600',
    DRAMA: 'bg-gradient-to-br from-pink-500 to-pink-600',
    MOVIE: 'bg-gradient-to-br from-cyan-500 to-cyan-600',
    BOOK: 'bg-gradient-to-br from-green-500 to-green-600',
  }
  return map[props.work.type] || 'bg-gradient-to-br from-purple-500 to-purple-600'
})

const coverGradient = computed(() => {
  const colors = {
    ANIME: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    DRAMA: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    MOVIE: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    BOOK: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  }
  return colors[props.work.type] || colors.ANIME
})

const progressGradient = computed(() => {
  const colors = {
    ANIME: 'linear-gradient(90deg, #667eea 0%, #764ba2 100%)',
    DRAMA: 'linear-gradient(90deg, #f093fb 0%, #f5576c 100%)',
    MOVIE: 'linear-gradient(90deg, #4facfe 0%, #00f2fe 100%)',
    BOOK: 'linear-gradient(90deg, #43e97b 0%, #38f9d7 100%)',
  }
  return colors[props.work.type] || colors.ANIME
})

const progress = computed(() => {
  if (!props.work._progressDetail || !props.work.totalEpisodes) return 0
  const match = props.work._progressDetail.match(/(\d+)/)
  if (match) {
    return Math.round((parseInt(match[1]) / props.work.totalEpisodes) * 100)
  }
  return 0
})
</script>

<style scoped>
@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}
</style>
