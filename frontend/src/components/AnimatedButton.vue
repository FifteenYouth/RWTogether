<template>
  <button
    :class="[
      'relative overflow-hidden transition-all duration-300',
      variantClass,
      sizeClass,
      $attrs.class
    ]"
    @click="handleClick"
    :disabled="disabled"
  >
    <!-- 涟漪效果容器 -->
    <span
      v-for="ripple in ripples"
      :key="ripple.id"
      class="absolute rounded-full bg-white/40 pointer-events-none animate-ripple"
      :style="{
        left: ripple.x + 'px',
        top: ripple.y + 'px',
        width: ripple.size + 'px',
        height: ripple.size + 'px',
      }"
    ></span>

    <!-- 按钮内容 -->
    <span class="relative z-10 flex items-center justify-center gap-2">
      <slot></slot>
    </span>
  </button>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  variant: {
    type: String,
    default: 'primary', // primary, secondary, ghost, danger
  },
  size: {
    type: String,
    default: 'md', // sm, md, lg
  },
  disabled: Boolean,
})

const emit = defineEmits(['click'])

const ripples = ref([])

const variantClass = computed(() => {
  const variants = {
    primary: 'bg-gradient-to-r from-apple-blue to-anime-purple-deep text-white hover:shadow-glow-blue disabled:opacity-40 disabled:cursor-not-allowed',
    secondary: 'bg-white/90 backdrop-blur-md text-apple-text border border-gray-200 hover:border-anime-purple-light hover:shadow-anime-sm',
    ghost: 'bg-transparent text-apple-blue hover:bg-anime-purple-light/20',
    danger: 'bg-gradient-to-r from-apple-red to-red-600 text-white hover:shadow-glow-pink',
  }
  return variants[props.variant]
})

const sizeClass = computed(() => {
  const sizes = {
    sm: 'px-3 py-1.5 text-xs rounded-lg font-medium',
    md: 'px-4 py-2.5 text-sm rounded-xl font-medium',
    lg: 'px-6 py-3 text-base rounded-xl font-semibold',
  }
  return sizes[props.size]
})

function handleClick(e) {
  if (props.disabled) return

  // 创建涟漪效果
  const button = e.currentTarget
  const rect = button.getBoundingClientRect()
  const size = Math.max(rect.width, rect.height) * 2
  const x = e.clientX - rect.left - size / 2
  const y = e.clientY - rect.top - size / 2

  const ripple = {
    id: Date.now(),
    x,
    y,
    size,
  }

  ripples.value.push(ripple)

  // 600ms 后移除涟漪
  setTimeout(() => {
    ripples.value = ripples.value.filter(r => r.id !== ripple.id)
  }, 600)

  emit('click', e)
}
</script>

<style scoped>
button {
  will-change: transform, box-shadow;
}

button:active:not(:disabled) {
  transform: scale(0.98);
}
</style>
