<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="show" class="fixed inset-0 z-[100] flex items-center justify-center">
        <!-- 遮罩 - 添加渐变模糊 -->
        <div
          class="absolute inset-0 bg-gradient-to-br from-black/50 via-purple-900/20 to-black/50 backdrop-blur-md"
          @click="$emit('close')"
          v-motion
          :initial="{ opacity: 0 }"
          :enter="{ opacity: 1, transition: { duration: 400 } }"
        ></div>

        <!-- 弹窗 - 增强弹簧效果 -->
        <div
          class="relative bg-white rounded-3xl shadow-anime-xl w-full max-w-lg mx-4 overflow-hidden border border-anime-purple-light/30"
          v-motion
          :initial="{ opacity: 0, scale: 0.8, y: 40 }"
          :enter="{
            opacity: 1,
            scale: 1,
            y: 0,
            transition: {
              type: 'spring',
              stiffness: 280,
              damping: 20,
              mass: 0.8
            }
          }"
        >
          <!-- 顶部装饰渐变 -->
          <div class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-anime-pink via-anime-purple to-anime-cyan"></div>
          <!-- 头部 -->
          <div class="px-6 pt-6 pb-4 border-b border-gray-100">
            <div class="flex items-center justify-between">
              <h2 class="text-lg font-bold text-apple-text">添加作品</h2>
              <button @click="$emit('close')" class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-apple-gray hover:bg-gray-200 transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- 搜索框 -->
            <div class="mt-4 relative">
              <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-apple-gray" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input
                v-model="query"
                @keyup.enter="search"
                placeholder="搜索番剧、电视剧、电影、书籍..."
                class="w-full bg-apple-bg border border-gray-200/80 rounded-xl pl-10 pr-4 py-2.5 text-sm text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30 focus:border-apple-blue transition-all"
                autofocus
              />
            </div>

            <!-- 类型筛选 -->
            <div class="flex gap-2 mt-3">
              <button
                v-for="tab in tabs"
                :key="tab.value"
                @click="activeType = tab.value; search()"
                :class="[
                  'px-3 py-1 rounded-full text-xs font-medium transition-all',
                  activeType === tab.value
                    ? 'bg-apple-text text-white'
                    : 'bg-gray-100 text-apple-gray hover:bg-gray-200'
                ]"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <!-- 搜索结果 -->
          <div class="max-h-[400px] overflow-y-auto px-6 py-4">
            <!-- 加载中 -->
            <div v-if="loading" class="flex items-center justify-center py-12">
              <div class="w-6 h-6 border-2 border-apple-blue/30 border-t-apple-blue rounded-full animate-spin"></div>
            </div>

            <!-- 结果列表 -->
            <div v-else-if="results.length" class="space-y-2">
              <div
                v-for="(item, i) in results"
                :key="i"
                @click="importWork(item)"
                class="flex gap-3 p-3 rounded-xl hover:bg-gradient-to-r hover:from-anime-purple-light/30 hover:to-anime-pink-light/30 cursor-pointer transition-all duration-300 group border border-transparent hover:border-anime-purple-light/50 hover:shadow-anime-sm"
                v-motion
                :initial="{ opacity: 0, x: -20, scale: 0.95 }"
                :enter="{
                  opacity: 1,
                  x: 0,
                  scale: 1,
                  transition: {
                    delay: i * 60,
                    type: 'spring',
                    stiffness: 260,
                    damping: 20
                  }
                }"
              >
                <!-- 封面 - 添加悬停放大 -->
                <div class="w-12 h-16 rounded-lg flex-shrink-0 overflow-hidden bg-gradient-to-br from-anime-purple-light to-anime-cyan-light shadow-sm group-hover:shadow-anime transition-all duration-300 group-hover:scale-105">
                  <img v-if="item.coverUrl" :src="item.coverUrl" class="w-full h-full object-cover" />
                  <div v-else class="w-full h-full flex items-center justify-center text-lg">
                    {{ typeIcon(item.type) }}
                  </div>
                </div>
                <!-- 信息 -->
                <div class="flex-1 min-w-0">
                  <h3 class="font-semibold text-apple-text text-sm truncate group-hover:text-transparent group-hover:bg-clip-text group-hover:bg-gradient-to-r group-hover:from-anime-purple-deep group-hover:to-anime-pink-deep transition-all duration-300">{{ item.title }}</h3>
                  <div class="flex items-center gap-2 mt-0.5">
                    <span class="text-xs text-apple-gray">{{ typeLabel(item.type) }}</span>
                    <span v-if="item.totalEpisodes" class="text-xs text-apple-gray">{{ item.totalEpisodes }} 集</span>
                  </div>
                  <p v-if="item.description" class="text-xs text-apple-gray mt-1 line-clamp-1">{{ item.description }}</p>
                </div>
                <!-- 添加按钮 - 增强动画 -->
                <div class="flex-shrink-0 self-center">
                  <div class="w-9 h-9 rounded-full bg-gradient-to-br from-apple-blue to-anime-purple-deep flex items-center justify-center text-white group-hover:shadow-glow-blue transition-all duration-300 group-hover:scale-110 group-hover:rotate-90">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 4v16m8-8H4" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else-if="searched" class="text-center py-12">
              <p class="text-3xl mb-2">🔍</p>
              <p class="text-apple-gray text-sm">没有找到相关作品</p>
            </div>

            <!-- 初始状态 -->
            <div v-else class="text-center py-12">
              <p class="text-3xl mb-2">✨</p>
              <p class="text-apple-gray text-sm">搜索你想要添加的作品</p>
              <p class="text-apple-gray/60 text-xs mt-1">支持番剧、电视剧、电影、书籍</p>
            </div>
          </div>

          <!-- 手动添加入口 -->
          <div class="px-6 py-4 border-t border-gray-100 bg-gray-50/50">
            <button @click="showManualForm = true" class="text-apple-blue text-sm font-medium hover:underline flex items-center gap-1">
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
              </svg>
              手动添加
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 手动添加弹窗 -->
    <Transition name="modal">
      <div v-if="showManualForm" class="fixed inset-0 z-[110] flex items-center justify-center">
        <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showManualForm = false"></div>
        <div
          class="relative bg-white rounded-3xl shadow-anime-xl w-full max-w-md mx-4 overflow-hidden border border-anime-purple-light/30"
          v-motion
          :initial="{ opacity: 0, scale: 0.8, y: 40 }"
          :enter="{
            opacity: 1,
            scale: 1,
            y: 0,
            transition: {
              type: 'spring',
              stiffness: 280,
              damping: 20,
              mass: 0.8
            }
          }"
        >
          <!-- 顶部装饰渐变 -->
          <div class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-anime-pink via-anime-purple to-anime-cyan"></div>
          <div class="px-6 pt-6 pb-4 border-b border-gray-100">
            <div class="flex items-center justify-between">
              <h2 class="text-lg font-bold text-apple-text">手动添加</h2>
              <button @click="showManualForm = false" class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-apple-gray hover:bg-gray-200 transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>

          <div class="px-6 py-4 space-y-4">
            <div>
              <label class="text-xs font-medium text-apple-gray mb-1 block">作品名称 *</label>
              <input v-model="manualForm.title" placeholder="输入作品名称"
                class="w-full bg-apple-bg border border-gray-200/80 rounded-xl px-4 py-2.5 text-sm text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30" />
            </div>
            <div>
              <label class="text-xs font-medium text-apple-gray mb-1 block">类型 *</label>
              <div class="flex gap-2">
                <button
                  v-for="t in manualTypes"
                  :key="t.value"
                  @click="manualForm.type = t.value"
                  :class="[
                    'flex-1 py-2 rounded-xl text-xs font-medium transition-all',
                    manualForm.type === t.value
                      ? 'bg-apple-text text-white'
                      : 'bg-gray-100 text-apple-gray hover:bg-gray-200'
                  ]"
                >
                  {{ t.icon }} {{ t.label }}
                </button>
              </div>
            </div>
            <div>
              <label class="text-xs font-medium text-apple-gray mb-1 block">封面 URL</label>
              <input v-model="manualForm.coverUrl" placeholder="https://..."
                class="w-full bg-apple-bg border border-gray-200/80 rounded-xl px-4 py-2.5 text-sm text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30" />
            </div>
            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="text-xs font-medium text-apple-gray mb-1 block">总集数</label>
                <input v-model.number="manualForm.totalEpisodes" type="number" min="0" placeholder="0"
                  class="w-full bg-apple-bg border border-gray-200/80 rounded-xl px-4 py-2.5 text-sm text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30" />
              </div>
              <div>
                <label class="text-xs font-medium text-apple-gray mb-1 block">总季数</label>
                <input v-model.number="manualForm.totalSeasons" type="number" min="0" placeholder="0"
                  class="w-full bg-apple-bg border border-gray-200/80 rounded-xl px-4 py-2.5 text-sm text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30" />
              </div>
            </div>
            <div>
              <label class="text-xs font-medium text-apple-gray mb-1 block">简介</label>
              <textarea v-model="manualForm.description" placeholder="输入作品简介..." rows="3"
                class="w-full bg-apple-bg border border-gray-200/80 rounded-xl px-4 py-2.5 text-sm text-apple-text placeholder-apple-gray resize-none focus:outline-none focus:ring-2 focus:ring-apple-blue/30"></textarea>
            </div>
          </div>

          <div class="px-6 py-4 border-t border-gray-100">
            <button @click="submitManual" :disabled="!manualForm.title || !manualForm.type || submitting"
              class="w-full py-2.5 bg-apple-blue text-white rounded-xl text-sm font-semibold hover:bg-blue-600 disabled:opacity-40 transition-colors">
              {{ submitting ? '添加中...' : '添加作品' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'
import worksApi from '../api/works'

const props = defineProps({
  show: { type: Boolean, default: false }
})

const emit = defineEmits(['close', 'added'])

const query = ref('')
const activeType = ref('all')
const results = ref([])
const searched = ref(false)
const loading = ref(false)
const submitting = ref(false)
const showManualForm = ref(false)

const manualForm = ref({
  title: '',
  type: 'ANIME',
  coverUrl: '',
  totalEpisodes: 0,
  totalSeasons: 0,
  description: ''
})

const tabs = [
  { label: '全部', value: 'all' },
  { label: '追番', value: 'ANIME' },
  { label: '追剧', value: 'DRAMA' },
  { label: '电影', value: 'MOVIE' },
  { label: '阅读', value: 'BOOK' },
]

const manualTypes = [
  { label: '番剧', value: 'ANIME', icon: '🎌' },
  { label: '电视剧', value: 'DRAMA', icon: '📺' },
  { label: '电影', value: 'MOVIE', icon: '🎬' },
  { label: '书籍', value: 'BOOK', icon: '📖' },
]

function typeLabel(type) {
  const map = { ANIME: '番剧', DRAMA: '电视剧', MOVIE: '电影', BOOK: '书籍' }
  return map[type] || type
}

function typeIcon(type) {
  const map = { ANIME: '🎌', DRAMA: '📺', MOVIE: '🎬', BOOK: '📖' }
  return map[type] || '🎬'
}

async function search() {
  if (!query.value.trim()) return
  loading.value = true
  try {
    const res = await worksApi.search(query.value, activeType.value)
    results.value = res.data
    searched.value = true
  } catch (e) {
    console.error('搜索失败', e)
  } finally {
    loading.value = false
  }
}

async function importWork(item) {
  submitting.value = true
  console.log('[importWork] item:', JSON.stringify(item))
  try {
    const res = await worksApi.importFromApi({
      apiSource: item.apiSource,
      apiId: item.apiId,
      title: item.title,
      coverUrl: item.coverUrl,
      description: item.description,
      type: item.type,
      totalEpisodes: item.totalEpisodes,
      totalSeasons: item.totalSeasons,
    })
    emit('added', res.data)
    emit('close')
  } catch (e) {
    console.error('[importWork] 导入失败', e)
    console.error('[importWork] 请求配置:', e.config)
    console.error('[importWork] 响应状态:', e.response?.status)
    console.error('[importWork] 响应数据:', e.response?.data)
  } finally {
    submitting.value = false
  }
}

async function submitManual() {
  if (!manualForm.value.title || !manualForm.value.type) return
  submitting.value = true
  try {
    const res = await worksApi.create(manualForm.value)
    emit('added', res.data)
    showManualForm.value = false
    emit('close')
  } catch (e) {
    console.error('手动添加失败', e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.25s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
