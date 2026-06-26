<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-950 via-purple-950 to-slate-900 relative overflow-hidden">
    <!-- 动态背景粒子效果 -->
    <div class="fixed inset-0 opacity-30 pointer-events-none">
      <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-anime-purple/30 rounded-full blur-[120px] animate-pulse-soft"></div>
      <div class="absolute top-3/4 right-1/4 w-80 h-80 bg-anime-pink/30 rounded-full blur-[100px] animate-pulse-soft" style="animation-delay: 1s;"></div>
      <div class="absolute top-1/2 left-1/2 w-72 h-72 bg-anime-cyan/20 rounded-full blur-[100px] animate-pulse-soft" style="animation-delay: 2s;"></div>
    </div>

    <!-- 浮动导航栏 -->
    <nav class="fixed top-6 left-1/2 -translate-x-1/2 z-50 w-[90%] max-w-7xl">
      <div
        class="relative bg-white/5 backdrop-blur-2xl border border-white/10 rounded-3xl px-8 py-4 shadow-anime-xl"
        v-motion
        :initial="{ opacity: 0, y: -30 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 100, type: 'spring', stiffness: 200 } }"
      >
        <!-- 顶部彩虹线 -->
        <div class="absolute top-0 left-0 right-0 h-[2px] bg-gradient-to-r from-anime-pink via-anime-purple to-anime-cyan"></div>

        <div class="flex items-center justify-between">
          <!-- Logo - 非对称设计 -->
          <div class="flex items-center gap-4 group cursor-pointer">
            <div class="relative w-12 h-12">
              <div class="absolute inset-0 bg-gradient-to-br from-anime-purple to-anime-pink rounded-2xl rotate-6 group-hover:rotate-12 transition-transform duration-500"></div>
              <div class="absolute inset-0 bg-gradient-to-br from-anime-cyan to-anime-purple rounded-2xl -rotate-6 group-hover:-rotate-12 transition-transform duration-500 opacity-70"></div>
              <span class="absolute inset-0 flex items-center justify-center text-2xl font-bold text-white z-10">R</span>
            </div>
            <div class="flex flex-col">
              <span class="text-xl font-bold bg-gradient-to-r from-white via-anime-purple-light to-anime-pink-light bg-clip-text text-transparent">
                RWTogether
              </span>
              <span class="text-[10px] font-mono text-white/40 tracking-widest uppercase">追番记录</span>
            </div>
          </div>

          <!-- 搜索框 - 磁性设计 -->
          <div class="flex-1 max-w-md mx-8">
            <SearchBar @search="goSearch" />
          </div>

          <!-- 右侧按钮组 -->
          <div class="flex items-center gap-3">
            <button
              @click="showAddModal = true"
              class="group relative px-6 py-3 bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep rounded-2xl font-semibold text-white text-sm overflow-hidden transition-all duration-300 hover:shadow-glow-purple hover:scale-105"
            >
              <span class="relative z-10 flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 4v16m8-8H4" />
                </svg>
                添加作品
              </span>
              <!-- 悬停光效 -->
              <div class="absolute inset-0 bg-gradient-to-r from-white/0 via-white/20 to-white/0 translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700"></div>
            </button>

            <!-- 用户头像 - 发光效果 -->
            <button
              @click="router.push('/profile')"
              class="relative w-12 h-12 rounded-2xl bg-gradient-to-br from-anime-cyan to-anime-purple flex items-center justify-center font-bold text-white text-lg shadow-glow-cyan hover:scale-110 transition-all duration-300 group"
            >
              <span class="relative z-10">{{ auth.user?.username?.charAt(0)?.toUpperCase() }}</span>
              <!-- 旋转边框 -->
              <div class="absolute inset-0 rounded-2xl border-2 border-white/30 group-hover:rotate-180 transition-transform duration-700"></div>
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容区 - 从顶部偏移 -->
    <div class="pt-32 pb-16 px-6 max-w-7xl mx-auto relative z-10">

      <!-- 欢迎区 - 非对称布局 -->
      <div
        class="mb-12 relative"
        v-motion
        :initial="{ opacity: 0, x: -50 }"
        :enter="{ opacity: 1, x: 0, transition: { delay: 200, type: 'spring' } }"
      >
        <div class="absolute -left-4 top-0 w-1 h-full bg-gradient-to-b from-anime-pink via-anime-purple to-transparent"></div>
        <h1 class="text-5xl font-black text-white mb-2 tracking-tight">
          <span class="inline-block" style="text-shadow: 0 0 30px rgba(212, 173, 252, 0.5);">
            欢迎回来
          </span>
        </h1>
        <p class="text-xl text-white/60 font-light">{{ auth.user?.username }}，继续你的追番之旅</p>
      </div>

      <!-- 统计卡片 - 浮动网格 -->
      <div class="grid grid-cols-3 gap-6 mb-16">
        <!-- 正在追 -->
        <div
          class="group relative bg-gradient-to-br from-anime-purple-deep/20 to-anime-purple/10 backdrop-blur-xl border border-white/10 rounded-3xl p-6 hover:border-anime-purple-light/50 transition-all duration-500 hover:-translate-y-2 cursor-pointer"
          v-motion
          :initial="{ opacity: 0, y: 30, scale: 0.9 }"
          :enter="{ opacity: 1, y: 0, scale: 1, transition: { delay: 300, type: 'spring', stiffness: 200 } }"
        >
          <!-- 背景光晕 -->
          <div class="absolute inset-0 bg-gradient-to-br from-anime-purple/0 to-anime-purple/20 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>

          <div class="relative z-10">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-mono uppercase tracking-wider text-white/50">正在追</span>
              <div class="w-2 h-2 rounded-full bg-anime-purple animate-pulse-soft"></div>
            </div>
            <p class="text-5xl font-black bg-gradient-to-br from-anime-purple-light to-white bg-clip-text text-transparent mb-2">
              {{ stats.watching }}
            </p>
            <div class="h-1 bg-white/10 rounded-full overflow-hidden">
              <div class="h-full bg-gradient-to-r from-anime-purple to-anime-purple-light rounded-full w-3/4"></div>
            </div>
          </div>
        </div>

        <!-- 已看完 -->
        <div
          class="group relative bg-gradient-to-br from-green-500/20 to-emerald-500/10 backdrop-blur-xl border border-white/10 rounded-3xl p-6 hover:border-green-400/50 transition-all duration-500 hover:-translate-y-2 cursor-pointer"
          v-motion
          :initial="{ opacity: 0, y: 30, scale: 0.9 }"
          :enter="{ opacity: 1, y: 0, scale: 1, transition: { delay: 400, type: 'spring', stiffness: 200 } }"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-green-500/0 to-green-500/20 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>

          <div class="relative z-10">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-mono uppercase tracking-wider text-white/50">已看完</span>
              <div class="w-2 h-2 rounded-full bg-green-400 animate-pulse-soft"></div>
            </div>
            <p class="text-5xl font-black bg-gradient-to-br from-green-300 to-white bg-clip-text text-transparent mb-2">
              {{ stats.done }}
            </p>
            <div class="h-1 bg-white/10 rounded-full overflow-hidden">
              <div class="h-full bg-gradient-to-r from-green-500 to-green-300 rounded-full w-full"></div>
            </div>
          </div>
        </div>

        <!-- 想看 -->
        <div
          class="group relative bg-gradient-to-br from-anime-pink-deep/20 to-anime-pink/10 backdrop-blur-xl border border-white/10 rounded-3xl p-6 hover:border-anime-pink-light/50 transition-all duration-500 hover:-translate-y-2 cursor-pointer"
          v-motion
          :initial="{ opacity: 0, y: 30, scale: 0.9 }"
          :enter="{ opacity: 1, y: 0, scale: 1, transition: { delay: 500, type: 'spring', stiffness: 200 } }"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-anime-pink/0 to-anime-pink/20 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>

          <div class="relative z-10">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-mono uppercase tracking-wider text-white/50">想看</span>
              <div class="w-2 h-2 rounded-full bg-anime-pink animate-pulse-soft"></div>
            </div>
            <p class="text-5xl font-black bg-gradient-to-br from-anime-pink-light to-white bg-clip-text text-transparent mb-2">
              {{ stats.want }}
            </p>
            <div class="h-1 bg-white/10 rounded-full overflow-hidden">
              <div class="h-full bg-gradient-to-r from-anime-pink-deep to-anime-pink-light rounded-full w-1/2"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分类标签 - 流动设计 -->
      <div class="flex gap-3 mb-10 overflow-x-auto pb-2 no-scrollbar">
        <button
          v-for="(tab, index) in tabs"
          :key="tab.value"
          @click="activeTab = tab.value"
          :class="[
            'relative px-6 py-3 rounded-2xl text-sm font-semibold transition-all duration-300 whitespace-nowrap group',
            activeTab === tab.value
              ? 'bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep text-white shadow-glow-purple scale-105'
              : 'bg-white/5 text-white/60 hover:bg-white/10 hover:text-white border border-white/10'
          ]"
          v-motion
          :initial="{ opacity: 0, x: -20 }"
          :enter="{ opacity: 1, x: 0, transition: { delay: 600 + index * 50 } }"
        >
          <span class="relative z-10">{{ tab.label }}</span>
          <!-- 活跃指示器 -->
          <div
            v-if="activeTab === tab.value"
            class="absolute inset-0 bg-gradient-to-r from-white/0 via-white/20 to-white/0 animate-shimmer rounded-2xl"
            style="background-size: 200% 100%;"
          ></div>
        </button>
      </div>

      <!-- 作品列表 - 砖石布局 -->
      <section v-if="filteredWorks.length">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <WorkCard
            v-for="(work, index) in filteredWorks"
            :key="work.id"
            :work="work"
            :status="work.status"
            :index="index"
            @click="router.push(`/works/${work.id}`)"
            @delete="deleteWork"
          />
        </div>
      </section>

      <!-- 空状态 - 梦幻设计 -->
      <div
        v-if="!filteredWorks.length"
        class="text-center py-32"
        v-motion
        :initial="{ opacity: 0, scale: 0.8 }"
        :enter="{ opacity: 1, scale: 1, transition: { delay: 400, type: 'spring' } }"
      >
        <div class="relative inline-block mb-6">
          <div class="absolute inset-0 bg-anime-purple/30 blur-3xl rounded-full animate-pulse-soft"></div>
          <p class="relative text-8xl animate-float">🌸</p>
        </div>
        <h3 class="text-2xl font-bold text-white mb-3">开启你的追番旅程</h3>
        <p class="text-white/50 mb-8">还没有添加任何作品</p>
        <button
          @click="showAddModal = true"
          class="inline-flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep rounded-2xl font-semibold text-white hover:shadow-glow-purple hover:scale-105 transition-all duration-300"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
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

const filteredWorks = computed(() => {
  if (activeTab.value === 'all') return works.value
  return works.value.filter(w => w.type === activeTab.value)
})

const watchingWorks = computed(() => filteredWorks.value.filter(w => w.status === 'WATCHING'))
const wantWorks = computed(() => filteredWorks.value.filter(w => w.status === 'WANT'))

function goSearch(q) {
  router.push(`/search?q=${q}`)
}

function onWorkAdded(work) {
  loadWorks()
  showAddModal.value = false
}

async function loadWorks() {
  try {
    const res = await worksApi.list()
    works.value = res.data
  } catch (e) {
    console.error('加载作品失败', e)
  }
}

async function deleteWork(workId) {
  try {
    await worksApi.remove(workId)
    works.value = works.value.filter(w => w.id !== workId)
  } catch (e) {
    console.error('删除作品失败', e)
  }
}

onMounted(() => {
  loadWorks()
})
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
