<template>
  <div class="bg-white rounded-2xl overflow-hidden shadow-sm cursor-pointer work-card" @click="$emit('click')">
    <!-- 封面区域 -->
    <div class="relative h-40" :style="{ background: coverGradient }">
      <!-- 类型标签 -->
      <span class="absolute top-3 right-3 px-2 py-0.5 rounded-md text-xs font-medium text-white bg-black/30 backdrop-blur-sm">
        {{ typeLabel }}
      </span>
      <!-- 状态标签 -->
      <span v-if="status" class="absolute bottom-3 left-3 px-2 py-0.5 rounded-md text-xs font-medium text-white"
        :class="statusColor">
        {{ statusLabel }}
      </span>
    </div>

    <!-- 信息区域 -->
    <div class="p-4">
      <h3 class="font-semibold text-apple-text text-sm tracking-tight truncate">{{ work.title }}</h3>
      <p class="text-apple-gray text-xs mt-1">{{ work.totalEpisodes }} 集</p>

      <!-- 进度条 -->
      <div v-if="progress > 0" class="mt-3">
        <div class="h-1 bg-gray-100 rounded-full overflow-hidden">
          <div class="h-full bg-apple-blue rounded-full transition-all duration-500" :style="{ width: progress + '%' }"></div>
        </div>
        <div class="flex justify-between mt-1.5">
          <span class="text-xs text-apple-gray">{{ work._progressDetail || '' }}</span>
          <span class="text-xs font-medium text-apple-blue">{{ progress }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  work: Object,
  status: String,
})

defineEmits(['click'])

const typeLabel = computed(() => {
  const map = { ANIME: '番剧', DRAMA: '电视剧', MOVIE: '电影', BOOK: '书籍' }
  return map[props.work.type] || props.work.type
})

const statusLabel = computed(() => {
  const map = { WANT: '想看', WATCHING: '在看', PAUSED: '暂停', DROPPED: '弃', DONE: '看完' }
  return map[props.status] || ''
})

const statusColor = computed(() => {
  const map = {
    WANT: 'bg-apple-orange/80',
    WATCHING: 'bg-apple-blue/80',
    PAUSED: 'bg-gray-500/80',
    DROPPED: 'bg-apple-red/80',
    DONE: 'bg-apple-green/80',
  }
  return map[props.status] || 'bg-gray-500/80'
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
.work-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}
</style>
