<template>
  <div class="min-h-screen bg-apple-bg flex items-center justify-center p-4">
    <div class="w-full max-w-sm">
      <!-- 毛玻璃卡片 -->
      <div class="bg-white/70 backdrop-blur-[40px] saturate-[180%] rounded-3xl p-8 shadow-sm">
        <!-- Logo -->
        <div class="flex flex-col items-center mb-8">
          <div class="w-14 h-14 bg-apple-blue rounded-2xl flex items-center justify-center text-2xl mb-4 shadow-sm">
            🎬
          </div>
          <h1 class="text-2xl font-bold tracking-tight text-apple-text">RWTogether</h1>
          <p class="text-apple-gray text-sm mt-1">记录每一次心动</p>
        </div>

        <!-- 表单 -->
        <form @submit.prevent="handleLogin" class="space-y-4">
          <div>
            <input
              v-model="username"
              type="text"
              placeholder="用户名"
              class="w-full bg-apple-bg border border-gray-200/80 rounded-xl px-4 py-3 text-sm text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30 focus:border-apple-blue transition-all"
            />
          </div>
          <div>
            <input
              v-model="password"
              type="password"
              placeholder="密码"
              class="w-full bg-apple-bg border border-gray-200/80 rounded-xl px-4 py-3 text-sm text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30 focus:border-apple-blue transition-all"
            />
          </div>

          <!-- 错误提示 -->
          <p v-if="error" class="text-apple-red text-xs text-center">{{ error }}</p>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-apple-blue text-white rounded-xl py-3 text-sm font-semibold hover:bg-blue-600 active:bg-blue-700 transition-colors disabled:opacity-50"
          >
            {{ loading ? '登录中...' : '开始追番' }}
          </button>
        </form>

        <p class="text-apple-gray text-xs text-center mt-6">
          新用户将自动创建账号
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  error.value = ''
  try {
    await auth.login(username.value, password.value)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.error || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>
