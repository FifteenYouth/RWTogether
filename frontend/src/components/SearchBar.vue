<template>
  <div class="relative group">
    <!-- 输入框发光背景 -->
    <div class="absolute -inset-[1px] bg-gradient-to-r from-anime-purple/50 via-anime-pink/50 to-anime-cyan/50 rounded-2xl opacity-0 group-focus-within:opacity-100 blur-sm transition-opacity duration-500"></div>

    <!-- 主输入框 -->
    <div class="relative">
      <input
        v-model="query"
        @keyup.enter="$emit('search', query)"
        @focus="focused = true"
        @blur="focused = false"
        placeholder="搜索番剧、电视剧、电影..."
        class="w-full bg-white/5 border border-white/10 rounded-2xl pl-12 pr-4 py-3 text-sm text-white placeholder-white/40 focus:outline-none focus:bg-white/10 focus:border-anime-purple-light/50 transition-all duration-300 backdrop-blur-xl"
      />

      <!-- 搜索图标 - 动态效果 -->
      <div class="absolute left-4 top-1/2 -translate-y-1/2">
        <svg
          :class="['w-4 h-4 transition-all duration-300', focused ? 'text-anime-purple-light scale-110 rotate-12' : 'text-white/40']"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
      </div>

      <!-- 清空按钮 -->
      <button
        v-if="query"
        @click="query = ''"
        class="absolute right-3 top-1/2 -translate-y-1/2 w-6 h-6 rounded-lg bg-white/10 hover:bg-white/20 flex items-center justify-center text-white/60 hover:text-white transition-all duration-200 hover:scale-110"
      >
        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const query = ref('')
const focused = ref(false)

defineEmits(['search'])
</script>
