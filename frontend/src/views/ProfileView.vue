<template>
  <div class="min-h-screen bg-apple-bg">
    <!-- 导航栏 -->
    <nav class="sticky top-0 z-50 bg-white/80 backdrop-blur-xl border-b border-gray-200/50">
      <div class="max-w-6xl mx-auto px-6 h-14 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-2">
          <span class="text-lg">🎬</span>
          <span class="font-bold text-apple-text tracking-tight">RWTogether</span>
        </router-link>
        <button @click="auth.logout(); router.push('/login')" class="text-apple-gray text-sm hover:text-apple-red transition-colors">
          退出
        </button>
      </div>
    </nav>

    <div class="max-w-2xl mx-auto px-6 py-12">
      <!-- 用户信息 -->
      <div class="bg-white rounded-2xl p-6 shadow-sm mb-6">
        <div class="flex items-center gap-4">
          <div class="w-16 h-16 bg-apple-blue/10 rounded-full flex items-center justify-center text-apple-blue text-2xl font-bold">
            {{ auth.user?.username?.charAt(0)?.toUpperCase() }}
          </div>
          <div class="flex-1">
            <h2 class="text-lg font-bold text-apple-text">{{ auth.user?.username }}</h2>
            <p class="text-apple-gray text-sm">{{ auth.user?.bio || '这个人很懒，什么都没写' }}</p>
          </div>
          <button @click="editing = !editing" class="text-apple-blue text-sm font-medium hover:underline">
            {{ editing ? '完成' : '编辑' }}
          </button>
        </div>

        <!-- 编辑简介 -->
        <div v-if="editing" class="mt-4">
          <textarea v-model="bioForm" placeholder="写点什么..." rows="2"
            class="w-full bg-apple-bg border border-gray-200 rounded-xl px-3 py-2 text-sm text-apple-text resize-none focus:outline-none focus:ring-2 focus:ring-apple-blue/30"></textarea>
          <button @click="updateProfile" class="mt-2 px-4 py-1.5 bg-apple-blue text-white rounded-lg text-sm font-medium hover:bg-blue-600">
            保存
          </button>
        </div>
      </div>

      <!-- 统计 -->
      <div class="grid grid-cols-4 gap-3 mb-6">
        <div class="bg-white rounded-2xl p-4 shadow-sm text-center">
          <p class="text-xl font-bold text-apple-blue">{{ stats.watching }}</p>
          <p class="text-xs text-apple-gray mt-1">在看</p>
        </div>
        <div class="bg-white rounded-2xl p-4 shadow-sm text-center">
          <p class="text-xl font-bold text-apple-green">{{ stats.done }}</p>
          <p class="text-xs text-apple-gray mt-1">看完</p>
        </div>
        <div class="bg-white rounded-2xl p-4 shadow-sm text-center">
          <p class="text-xl font-bold text-apple-orange">{{ stats.want }}</p>
          <p class="text-xs text-apple-gray mt-1">想看</p>
        </div>
        <div class="bg-white rounded-2xl p-4 shadow-sm text-center">
          <p class="text-xl font-bold text-apple-red">{{ stats.dropped }}</p>
          <p class="text-xs text-apple-gray mt-1">弃</p>
        </div>
      </div>

      <!-- 作品列表 -->
      <div v-if="works.length">
        <h3 class="font-semibold text-apple-text mb-3">我的作品</h3>
        <div class="space-y-2">
          <div v-for="work in works" :key="work.id"
            @click="router.push(`/works/${work.id}`)"
            class="bg-white rounded-xl p-3 shadow-sm flex items-center gap-3 cursor-pointer hover:shadow-md transition-shadow">
            <div class="w-10 h-14 rounded-lg bg-gray-100 flex-shrink-0 overflow-hidden">
              <img v-if="work.coverUrl" :src="work.coverUrl" class="w-full h-full object-cover" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-apple-text truncate">{{ work.title }}</p>
              <p class="text-xs text-apple-gray">{{ work.type }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import authApi from '../api/auth'
import usersApi from '../api/users'

const router = useRouter()
const auth = useAuthStore()

const editing = ref(false)
const bioForm = ref(auth.user?.bio || '')
const works = ref([])
const stats = ref({ watching: 0, done: 0, want: 0, dropped: 0 })

async function updateProfile() {
  try {
    const res = await authApi.updateProfile({ bio: bioForm.value })
    auth.user = res.data
    localStorage.setItem('user', JSON.stringify(res.data))
    editing.value = false
  } catch (e) {
    console.error('更新简介失败', e)
  }
}

onMounted(async () => {
  try {
    if (auth.user?.id) {
      const res = await usersApi.getWorks(auth.user.id)
      works.value = res.data
    }
  } catch (e) {
    console.error('加载作品失败', e)
  }
})
</script>
