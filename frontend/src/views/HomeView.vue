<template>
  <div class="min-h-screen bg-apple-bg">
    <!-- 顶部导航栏 -->
    <nav class="sticky top-0 z-50 bg-white/80 backdrop-blur-xl border-b border-gray-200/50">
      <div class="max-w-6xl mx-auto px-6 h-14 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="text-lg">🎬</span>
          <span class="font-bold text-apple-text tracking-tight">RWTogether</span>
        </div>
        <SearchBar class="flex-1 max-w-md mx-8" @search="goSearch" />
        <div class="flex items-center gap-3">
          <button @click="showAddModal = true" class="flex items-center gap-1.5 px-4 py-1.5 bg-apple-blue text-white rounded-xl text-sm font-medium hover:bg-blue-600 transition-colors">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            添加作品
          </button>
          <button @click="router.push('/profile')" class="w-8 h-8 bg-apple-blue/10 rounded-full flex items-center justify-center text-apple-blue text-sm font-semibold">
            {{ auth.user?.username?.charAt(0)?.toUpperCase() }}
          </button>
        </div>
      </div>
    </nav>

    <div class="max-w-6xl mx-auto px-6 py-8">
      <!-- 欢迎语 -->
      <h1 class="text-[28px] font-bold tracking-tightest text-apple-text mb-6">
        你好，{{ auth.user?.username }}
      </h1>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-3 gap-4 mb-8">
        <div class="bg-white rounded-2xl p-5 shadow-sm">
          <p class="text-apple-gray text-xs font-medium mb-1">正在追</p>
          <p class="text-3xl font-bold text-apple-blue">{{ stats.watching }}</p>
        </div>
        <div class="bg-white rounded-2xl p-5 shadow-sm">
          <p class="text-apple-gray text-xs font-medium mb-1">已看完</p>
          <p class="text-3xl font-bold text-apple-green">{{ stats.done }}</p>
        </div>
        <div class="bg-white rounded-2xl p-5 shadow-sm">
          <p class="text-apple-gray text-xs font-medium mb-1">想看</p>
          <p class="text-3xl font-bold text-apple-orange">{{ stats.want }}</p>
        </div>
      </div>

      <!-- 分类标签 -->
      <div class="flex gap-2 mb-6">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          @click="activeTab = tab.value"
          :class="[
            'px-4 py-1.5 rounded-full text-sm font-medium transition-all',
            activeTab === tab.value
              ? 'bg-apple-text text-white'
              : 'bg-gray-100 text-apple-gray hover:bg-gray-200'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 正在追 -->
      <section v-if="watchingWorks.length" class="mb-10">
        <h2 class="text-lg font-semibold text-apple-text mb-4">正在追</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <WorkCard
            v-for="work in watchingWorks"
            :key="work.id"
            :work="work"
            status="WATCHING"
            @click="router.push(`/works/${work.id}`)"
          />
        </div>
      </section>

      <!-- 想看 -->
      <section v-if="wantWorks.length" class="mb-10">
        <h2 class="text-lg font-semibold text-apple-text mb-4">想看</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <WorkCard
            v-for="work in wantWorks"
            :key="work.id"
            :work="work"
            status="WANT"
            @click="router.push(`/works/${work.id}`)"
          />
        </div>
      </section>

      <!-- 空状态 -->
      <div v-if="!watchingWorks.length && !wantWorks.length" class="text-center py-20">
        <p class="text-5xl mb-4">🔍</p>
        <p class="text-apple-gray text-sm">还没有标记任何作品</p>
        <button @click="showAddModal = true" class="mt-4 text-apple-blue text-sm font-medium hover:underline">
          添加第一部作品
        </button>
      </div>
    </div>

    <!-- 添加作品弹窗 -->
    <AddWorkModal :show="showAddModal" @close="showAddModal = false" @added="onWorkAdded" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import worksApi from '../api/works'
import WorkCard from '../components/WorkCard.vue'
import SearchBar from '../components/SearchBar.vue'
import AddWorkModal from '../components/AddWorkModal.vue'

const router = useRouter()
const auth = useAuthStore()

const activeTab = ref('all')
const works = ref([])
const stats = ref({ watching: 0, done: 0, want: 0 })
const showAddModal = ref(false)

const tabs = [
  { label: '全部', value: 'all' },
  { label: '追番', value: 'ANIME' },
  { label: '追剧', value: 'DRAMA' },
  { label: '电影', value: 'MOVIE' },
  { label: '阅读', value: 'BOOK' },
]

const watchingWorks = computed(() => works.value.filter(w => w._status === 'WATCHING'))
const wantWorks = computed(() => works.value.filter(w => w._status === 'WANT'))

function goSearch(q) {
  router.push(`/search?q=${q}`)
}

function onWorkAdded(work) {
  router.push(`/works/${work.id}`)
}

onMounted(async () => {
  try {
    const res = await worksApi.list()
    works.value = res.data
  } catch (e) {
    console.error('加载作品失败', e)
  }
})
</script>
