<template>
  <div
    class="relative rounded-2xl overflow-hidden flex items-center justify-center font-bold text-white shadow-glow-cyan select-none"
    :class="sizeClass"
    :style="!isImage ? { background: gradient } : {}"
  >
    <img
      v-if="isImage"
      :src="avatar"
      :alt="username"
      class="w-full h-full object-cover"
      @error="onError"
    />
    <span v-else class="relative z-10" :class="textClass">
      {{ initial }}
    </span>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  avatar: { type: String, default: '' },
  username: { type: String, default: '' },
  size: { type: String, default: 'md' }, // sm | md | lg | xl
})

const broken = ref(false)
watch(() => props.avatar, () => { broken.value = false })

function onError() {
  broken.value = true
}

const isImage = computed(() => {
  if (broken.value || !props.avatar) return false
  return /^(https?:\/\/|data:image\/|\/)/.test(props.avatar)
})

const initial = computed(() => {
  // 支持 emoji 头像（非图片链接的短字符串）
  if (props.avatar && !isImage.value && props.avatar.length <= 4) return props.avatar
  return props.username?.charAt(0)?.toUpperCase() || '?'
})

const sizeClass = computed(() => ({
  sm: 'w-10 h-10',
  md: 'w-12 h-12',
  lg: 'w-20 h-20',
  xl: 'w-28 h-28',
}[props.size] || 'w-12 h-12'))

const textClass = computed(() => ({
  sm: 'text-base',
  md: 'text-lg',
  lg: 'text-3xl',
  xl: 'text-5xl',
}[props.size] || 'text-lg'))

// 根据用户名生成稳定的渐变色
const gradient = computed(() => {
  const palettes = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
  ]
  const name = props.username || ''
  let hash = 0
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash)
  return palettes[Math.abs(hash) % palettes.length]
})
</script>
