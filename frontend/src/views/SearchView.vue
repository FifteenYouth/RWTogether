<template>
  <div class="min-h-screen bg-apple-bg">
    <!-- 导航栏 -->
    <nav class="sticky top-0 z-50 bg-white/80 backdrop-blur-xl border-b border-gray-200/50">
      <div class="max-w-6xl mx-auto px-6 h-14 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-2">
          <span class="text-lg">🎬</span>
          <span class="font-bold text-apple-text tracking-tight">RWTogether</span>
        </router-link>
      </div>
    </nav>

    <div class="max-w-2xl mx-auto px-6 py-12">
      <!-- 搜索框 -->
      <div class="mb-8">
        <input
          v-model="query"
          @keyup.enter="search"
          placeholder="搜索番剧、电视剧、电影、书籍..."
          class="w-full bg-white border border-gray-200/80 rounded-2xl px-6 py-4 text-base text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30 focus:border-apple-blue transition-all shadow-sm"
          autofocus
        />
      </div>

      <!-- 类型筛选 -->
      <div class="flex gap-2 mb-6">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          @click="activeType = tab.value; search()"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium transition-all',
            activeType === tab.value
              ? 'bg-apple-text text-white'
              : 'bg-gray-100 text-apple-gray hover:bg-gray-200'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 搜索结果 -->
      <div v-if="results.length" class="space-y-3">
        <div
          v-for="(item, i) in results"
          :key="i"
          @click="selectWork(item)"
          class="bg-white rounded-2xl p-4 shadow-sm flex gap-4 cursor-pointer hover:shadow-md transition-shadow"
          v-motion
          :initial="{ opacity: 0, y: 20 }"
          :enter="{ opacity: 1, y: 0, transition: { delay: i * 80 } }"
        >
          <!-- 封面 -->
          <div class="w-16 h-22 rounded-xl flex-shrink-0 overflow-hidden bg-gray-100">
            <img v-if="item.coverUrl" :src="item.coverUrl" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex items-center justify-center text-xl">🎬</div>
          </div>
          <!-- 信息 -->
          <div class="flex-1 min-w-0">
            <h3 class="font-semibold text-apple-text text-sm truncate">{{ item.title }}</h3>
            <div class="flex gap-2 mt-1">
              <span class="text-xs text-apple-gray">{{ typeLabel(item.type) }}</span>
              <span v-if="item.totalEpisodes" class="text-xs text-apple-gray">{{ item.totalEpisodes }} 集</span>
            </div>
            <p v-if="item.apiSource" class="text-xs text-apple-gray mt-1">来源: {{ item.apiSource }}</p>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="searched && !results.length" class="text-center py-20">
        <p class="text-4xl mb-3">🔍</p>
        <p class="text-apple-gray text-sm">没有找到相关作品</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import worksApi from '../api/works'

const router = useRouter()
const route = useRoute()

const query = ref('')
const activeType = ref('all')
const results = ref([])
const searched = ref(false)

const tabs = [
  { label: '全部', value: 'all' },
  { label: '追番', value: 'ANIME' },
  { label: '追剧', value: 'DRAMA' },
  { label: '电影', value: 'MOVIE' },
  { label: '阅读', value: 'BOOK' },
]

function typeLabel(type) {
  const map = { ANIME: '番剧', DRAMA: '电视剧', MOVIE: '电影', BOOK: '书籍' }
  return map[type] || type
}

async function search() {
  if (!query.value) return
  try {
    const res = await worksApi.search(query.value, activeType.value)
    results.value = res.data
    searched.value = true
  } catch (e) {
    console.error('搜索失败', e)
  }
}

async function selectWork(item) {
  try {
    // 通过 API 导入，自动获取详细信息
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
    router.push(`/works/${res.data.id}`)
  } catch (e) {
    console.error('添加作品失败', e)
  }
}

onMounted(() => {
  if (route.query.q) {
    query.value = route.query.q
    search()
  }
})
</script>
