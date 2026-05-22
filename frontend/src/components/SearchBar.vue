<template>
  <div class="relative">
    <input
      v-model="query"
      @keyup.enter="$emit('search', query)"
      placeholder="搜索作品、番剧..."
      class="w-full bg-apple-bg border border-gray-200/80 rounded-xl px-4 py-2.5 text-sm text-apple-text placeholder-apple-gray focus:outline-none focus:ring-2 focus:ring-apple-blue/30 focus:border-apple-blue transition-all"
    />
    <svg class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-apple-gray" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
    </svg>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const query = ref('')
defineEmits(['search'])
</script>
