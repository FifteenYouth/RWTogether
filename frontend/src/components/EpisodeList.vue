<template>
  <div class="space-y-4">
    <!-- 分集统计卡片 -->
    <div class="bg-slate-900/60 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-bold text-white">分集列表</h3>
        <div class="flex items-center gap-4 text-sm">
          <span class="text-white/60">
            已看 <span class="text-anime-purple-light font-bold">{{ watchedCount }}</span> / {{ episodes.length }} 集
          </span>
          <div class="h-2 w-32 bg-white/10 rounded-full overflow-hidden">
            <div
              class="h-full bg-gradient-to-r from-anime-purple to-anime-pink rounded-full transition-all duration-700"
              :style="{ width: progressPercent + '%' }"
            ></div>
          </div>
        </div>
      </div>

      <!-- 分集列表 -->
      <div class="space-y-2">
        <div
          v-for="(episode, index) in episodes"
          :key="episode.id"
          class="group relative bg-white/5 hover:bg-white/10 border border-white/10 hover:border-anime-purple-light/50 rounded-xl p-4 transition-all duration-300 cursor-pointer"
          @click="toggleWatched(episode)"
          v-motion
          :initial="{ opacity: 0, x: -20 }"
          :enter="{ opacity: 1, x: 0, transition: { delay: index * 30 } }"
        >
          <!-- 左侧：集数和状态 -->
          <div class="flex items-center gap-4">
            <!-- 集数标记 -->
            <div
              :class="[
                'w-12 h-12 rounded-xl flex items-center justify-center font-bold text-sm transition-all duration-300',
                episode.watched
                  ? 'bg-gradient-to-br from-anime-purple to-anime-pink text-white shadow-glow-purple'
                  : 'bg-white/5 text-white/40 group-hover:bg-white/10'
              ]"
            >
              {{ episode.episodeNum }}
            </div>

            <!-- 标题和信息 -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-3 mb-1">
                <h4 class="font-semibold text-white truncate">
                  {{ episode.title || `第 ${episode.episodeNum} 集` }}
                </h4>
                <span v-if="episode.watched" class="flex items-center gap-1 text-xs text-anime-purple-light">
                  <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                  已看
                </span>
              </div>
              <div class="flex items-center gap-3 text-xs text-white/50">
                <span v-if="episode.airDate">{{ formatDate(episode.airDate) }}</span>
                <span v-if="episode.watchedAt" class="flex items-center gap-1">
                  <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  {{ formatDate(episode.watchedAt) }}
                </span>
              </div>
            </div>

            <!-- 评分 -->
            <div class="flex items-center gap-1">
              <button
                v-for="star in 5"
                :key="star"
                @click.stop="rateEpisode(episode, star * 2)"
                class="w-6 h-6 transition-all duration-200 hover:scale-125"
                :class="episode.rating && star <= episode.rating / 2 ? 'text-anime-pink' : 'text-white/20 hover:text-anime-pink/50'"
              >
                <svg class="w-full h-full" :fill="episode.rating && star <= episode.rating / 2 ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                </svg>
              </button>
              <span v-if="episode.rating" class="ml-2 text-xs font-semibold text-white/60">{{ episode.rating }}/10</span>
            </div>

            <!-- 评论按钮 -->
            <button
              @click.stop="$emit('comment', episode.episodeNum)"
              class="px-3 py-1.5 bg-white/5 hover:bg-anime-purple/20 border border-white/10 hover:border-anime-purple-light/50 rounded-lg text-xs font-medium text-white/60 hover:text-anime-purple-light transition-all duration-200"
            >
              评论
            </button>
          </div>

          <!-- 悬停光效 -->
          <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-1000 pointer-events-none rounded-xl"></div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="episodes.length === 0" class="text-center py-12">
        <p class="text-white/40 text-sm">暂无分集信息</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import episodesApi from '../api/episodes'

const props = defineProps({
  workId: {
    type: Number,
    required: true,
  },
})

const emit = defineEmits(['comment', 'progress-updated'])

const episodes = ref([])
const loading = ref(false)

const watchedCount = computed(() => episodes.value.filter(ep => ep.watched).length)
const progressPercent = computed(() => {
  if (episodes.value.length === 0) return 0
  return Math.round((watchedCount.value / episodes.value.length) * 100)
})

async function loadEpisodes() {
  loading.value = true
  try {
    const res = await episodesApi.list(props.workId)
    episodes.value = res.data
  } catch (e) {
    console.error('加载分集失败', e)
  } finally {
    loading.value = false
  }
}

async function toggleWatched(episode) {
  try {
    const res = await episodesApi.toggleWatched(props.workId, episode.id)
    // 更新本地状态
    const index = episodes.value.findIndex(ep => ep.id === episode.id)
    if (index !== -1) {
      episodes.value[index] = res.data
    }
    // 通知父组件进度已更新
    emit('progress-updated', watchedCount.value, episodes.value.length)
  } catch (e) {
    console.error('切换观看状态失败', e)
  }
}

async function rateEpisode(episode, rating) {
  try {
    const res = await episodesApi.rate(props.workId, episode.id, rating)
    // 更新本地状态
    const index = episodes.value.findIndex(ep => ep.id === episode.id)
    if (index !== -1) {
      episodes.value[index] = res.data
    }
  } catch (e) {
    console.error('评分失败', e)
  }
}

function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 监听 workId 变化
watch(() => props.workId, () => {
  if (props.workId) {
    loadEpisodes()
  }
}, { immediate: true })
</script>
