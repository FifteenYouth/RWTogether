<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-950 via-purple-950 to-slate-900 relative overflow-hidden">
    <!-- 动态背景光晕 -->
    <div class="fixed inset-0 opacity-30 pointer-events-none">
      <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-anime-purple/30 rounded-full blur-[120px] animate-pulse-soft"></div>
      <div class="absolute top-3/4 right-1/4 w-80 h-80 bg-anime-pink/30 rounded-full blur-[100px] animate-pulse-soft" style="animation-delay: 1s;"></div>
      <div class="absolute top-1/2 left-1/3 w-72 h-72 bg-anime-cyan/20 rounded-full blur-[100px] animate-pulse-soft" style="animation-delay: 2s;"></div>
    </div>

    <!-- 浮动导航栏 -->
    <nav class="fixed top-6 left-1/2 -translate-x-1/2 z-50 w-[90%] max-w-5xl">
      <div class="relative bg-white/5 backdrop-blur-2xl border border-white/10 rounded-3xl px-8 py-4 shadow-anime-xl">
        <div class="absolute top-0 left-0 right-0 h-[2px] bg-gradient-to-r from-anime-pink via-anime-purple to-anime-cyan"></div>
        <div class="flex items-center justify-between">
          <router-link to="/" class="flex items-center gap-3 group cursor-pointer">
            <div class="relative w-10 h-10">
              <div class="absolute inset-0 bg-gradient-to-br from-anime-purple to-anime-pink rounded-2xl rotate-6 group-hover:rotate-12 transition-transform duration-500"></div>
              <span class="absolute inset-0 flex items-center justify-center text-xl font-bold text-white z-10">R</span>
            </div>
            <span class="text-lg font-bold bg-gradient-to-r from-white via-anime-purple-light to-anime-pink-light bg-clip-text text-transparent">
              RWTogether
            </span>
          </router-link>
          <button
            @click="auth.logout(); router.push('/login')"
            class="px-5 py-2.5 rounded-2xl text-sm font-semibold text-white/70 bg-white/5 border border-white/10 hover:bg-red-500/20 hover:text-red-300 hover:border-red-400/40 transition-all duration-300"
          >
            退出登录
          </button>
        </div>
      </div>
    </nav>

    <div class="pt-32 pb-16 px-6 max-w-5xl mx-auto relative z-10">
      <!-- 个人资料卡片 -->
      <div
        class="relative bg-white/5 backdrop-blur-2xl border border-white/10 rounded-3xl p-8 mb-8 overflow-hidden shadow-anime-xl"
        v-motion
        :initial="{ opacity: 0, y: 30 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 100, type: 'spring', stiffness: 180 } }"
      >
        <div class="absolute -top-20 -right-20 w-60 h-60 bg-anime-purple/20 rounded-full blur-3xl pointer-events-none"></div>

        <div class="relative flex flex-col sm:flex-row items-center sm:items-start gap-6">
          <!-- 头像 -->
          <div class="relative group cursor-pointer" @click="openAvatarEditor">
            <UserAvatar :avatar="auth.user?.avatar" :username="auth.user?.username" size="xl" />
            <div class="absolute inset-0 rounded-2xl bg-black/50 opacity-0 group-hover:opacity-100 flex items-center justify-center transition-opacity duration-300">
              <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </div>
          </div>

          <!-- 用户信息 -->
          <div class="flex-1 text-center sm:text-left min-w-0">
            <div class="flex items-center justify-center sm:justify-between gap-3 flex-wrap">
              <h1 class="text-3xl font-black text-white tracking-tight">{{ auth.user?.username }}</h1>
              <button
                @click="toggleEdit"
                class="px-4 py-2 rounded-xl text-sm font-semibold bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep text-white hover:shadow-glow-purple hover:scale-105 transition-all duration-300"
              >
                {{ editing ? '取消' : '编辑资料' }}
              </button>
            </div>
            <p v-if="!editing" class="mt-3 text-white/60 leading-relaxed">
              {{ auth.user?.bio || '这个人很懒，什么都没写' }}
            </p>

            <!-- 编辑表单 -->
            <div v-else class="mt-4 space-y-3">
              <textarea
                v-model="bioForm"
                placeholder="介绍一下自己..."
                rows="2"
                maxlength="200"
                class="w-full bg-white/5 border border-white/10 rounded-2xl px-4 py-3 text-sm text-white placeholder-white/30 resize-none focus:outline-none focus:ring-2 focus:ring-anime-purple/40 focus:border-transparent transition-all"
              ></textarea>
              <div class="flex items-center gap-3">
                <button
                  @click="saveProfile"
                  :disabled="saving"
                  class="px-5 py-2 rounded-xl text-sm font-semibold bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep text-white hover:shadow-glow-purple transition-all duration-300 disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存' }}
                </button>
                <span class="text-xs text-white/30">{{ bioForm.length }}/200</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-10">
        <div
          v-for="(s, i) in statCards"
          :key="s.key"
          class="relative bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-5 text-center overflow-hidden hover:-translate-y-1 transition-all duration-500"
          v-motion
          :initial="{ opacity: 0, y: 20, scale: 0.95 }"
          :enter="{ opacity: 1, y: 0, scale: 1, transition: { delay: 200 + i * 80, type: 'spring' } }"
        >
          <div class="absolute inset-0 opacity-20" :style="{ background: s.glow }"></div>
          <p class="relative text-4xl font-black bg-clip-text text-transparent mb-1" :style="{ backgroundImage: s.text }">
            {{ stats[s.key] }}
          </p>
          <p class="relative text-xs font-mono uppercase tracking-wider text-white/50">{{ s.label }}</p>
        </div>
      </div>

      <!-- 作品列表 -->
      <section v-if="works.length">
        <div class="flex items-center gap-3 mb-6">
          <div class="w-1 h-6 bg-gradient-to-b from-anime-pink to-anime-purple rounded-full"></div>
          <h2 class="text-2xl font-bold text-white">我的作品库</h2>
          <span class="text-sm text-white/40">{{ works.length }} 部</span>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <WorkCard
            v-for="(work, index) in works"
            :key="work.id"
            :work="work"
            :status="work.status"
            :index="index"
            @click="router.push(`/works/${work.id}`)"
          />
        </div>
      </section>

      <!-- 空状态 -->
      <div v-else class="text-center py-24">
        <p class="text-7xl mb-5 animate-float">📚</p>
        <h3 class="text-xl font-bold text-white mb-2">作品库还是空的</h3>
        <router-link
          to="/"
          class="inline-flex items-center gap-2 mt-4 px-6 py-3 bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep rounded-2xl font-semibold text-white hover:shadow-glow-purple hover:scale-105 transition-all duration-300"
        >
          去添加作品
        </router-link>
      </div>
    </div>

    <!-- 头像编辑弹窗 -->
    <Transition name="modal">
      <div
        v-if="avatarModal"
        class="fixed inset-0 z-[60] flex items-center justify-center p-6 bg-black/60 backdrop-blur-sm"
        @click.self="avatarModal = false"
      >
        <div class="relative w-full max-w-md bg-slate-900/90 backdrop-blur-2xl border border-white/10 rounded-3xl p-7 shadow-anime-xl">
          <div class="absolute top-0 left-0 right-0 h-[2px] bg-gradient-to-r from-anime-pink via-anime-purple to-anime-cyan rounded-t-3xl"></div>

          <h3 class="text-xl font-bold text-white mb-1">更换头像</h3>
          <p class="text-sm text-white/40 mb-6">选择一个 emoji 或填入图片链接</p>

          <!-- 预览 -->
          <div class="flex justify-center mb-6">
            <UserAvatar :avatar="avatarForm" :username="auth.user?.username" size="xl" />
          </div>

          <!-- Emoji 预设 -->
          <div class="grid grid-cols-8 gap-2 mb-5">
            <button
              v-for="emoji in emojiPresets"
              :key="emoji"
              @click="avatarForm = emoji"
              class="aspect-square rounded-xl text-xl flex items-center justify-center transition-all duration-200 hover:scale-110"
              :class="avatarForm === emoji ? 'bg-anime-purple/40 ring-2 ring-anime-purple-light' : 'bg-white/5 hover:bg-white/10'"
            >
              {{ emoji }}
            </button>
          </div>

          <!-- 自定义 URL -->
          <input
            v-model="avatarForm"
            type="text"
            placeholder="或粘贴图片 URL (https://...)"
            class="w-full bg-white/5 border border-white/10 rounded-2xl px-4 py-3 text-sm text-white placeholder-white/30 focus:outline-none focus:ring-2 focus:ring-anime-purple/40 focus:border-transparent transition-all mb-4"
          />

          <!-- 上传本地图片 -->
          <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="onFileSelected" />
          <button
            @click="fileInput?.click()"
            :disabled="uploading"
            class="w-full mb-6 px-4 py-3 rounded-2xl text-sm font-semibold text-white/80 bg-white/5 border border-dashed border-white/20 hover:bg-white/10 hover:border-anime-purple-light/50 transition-all flex items-center justify-center gap-2 disabled:opacity-50"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
            {{ uploading ? '上传中...' : '上传本地图片' }}
          </button>

          <p v-if="uploadError" class="text-xs text-red-400 mb-4 -mt-2">{{ uploadError }}</p>

          <div class="flex gap-3">
            <button
              @click="avatarModal = false"
              class="flex-1 px-4 py-3 rounded-2xl text-sm font-semibold text-white/70 bg-white/5 border border-white/10 hover:bg-white/10 transition-all"
            >
              取消
            </button>
            <button
              @click="saveAvatar"
              :disabled="saving"
              class="flex-1 px-4 py-3 rounded-2xl text-sm font-semibold bg-gradient-to-r from-anime-purple-deep to-anime-pink-deep text-white hover:shadow-glow-purple transition-all disabled:opacity-50"
            >
              {{ saving ? '保存中...' : '保存头像' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import authApi from '../api/auth'
import usersApi from '../api/users'
import uploadApi from '../api/upload'
import WorkCard from '../components/WorkCard.vue'
import UserAvatar from '../components/UserAvatar.vue'

const router = useRouter()
const auth = useAuthStore()

const editing = ref(false)
const saving = ref(false)
const bioForm = ref(auth.user?.bio || '')
const works = ref([])

const avatarModal = ref(false)
const avatarForm = ref(auth.user?.avatar || '')
const fileInput = ref(null)
const uploading = ref(false)
const uploadError = ref('')

const emojiPresets = ['🌸', '🎬', '🐱', '🦊', '🐼', '🌙', '⭐', '🍥', '🎮', '📚', '🎧', '🍜', '🌈', '🔥', '💫', '👾']

const stats = computed(() => {
  const count = (s) => works.value.filter(w => w.status === s).length
  return {
    watching: count('WATCHING'),
    done: count('DONE'),
    want: count('WANT'),
    dropped: count('DROPPED'),
  }
})

const statCards = [
  { key: 'watching', label: '在看', glow: 'linear-gradient(135deg,#667eea,#764ba2)', text: 'linear-gradient(135deg,#a5b4fc,#fff)' },
  { key: 'done', label: '看完', glow: 'linear-gradient(135deg,#43e97b,#38f9d7)', text: 'linear-gradient(135deg,#86efac,#fff)' },
  { key: 'want', label: '想看', glow: 'linear-gradient(135deg,#fa709a,#fee140)', text: 'linear-gradient(135deg,#fcd34d,#fff)' },
  { key: 'dropped', label: '弃坑', glow: 'linear-gradient(135deg,#f5576c,#f093fb)', text: 'linear-gradient(135deg,#fda4af,#fff)' },
]

function toggleEdit() {
  editing.value = !editing.value
  if (editing.value) bioForm.value = auth.user?.bio || ''
}

function openAvatarEditor() {
  avatarForm.value = auth.user?.avatar || ''
  uploadError.value = ''
  avatarModal.value = true
}

async function onFileSelected(e) {
  const file = e.target.files?.[0]
  e.target.value = '' // 允许重复选择同一文件
  if (!file) return

  if (!file.type.startsWith('image/')) {
    uploadError.value = '请选择图片文件'
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    uploadError.value = '图片不能超过 5MB'
    return
  }

  uploading.value = true
  uploadError.value = ''
  try {
    const res = await uploadApi.avatar(file)
    avatarForm.value = res.data.url
  } catch (err) {
    uploadError.value = err.response?.data?.error || '上传失败，请重试'
    console.error('头像上传失败', err)
  } finally {
    uploading.value = false
  }
}

function applyUser(data) {
  auth.user = data
  localStorage.setItem('user', JSON.stringify(data))
}

async function saveProfile() {
  saving.value = true
  try {
    const res = await authApi.updateProfile({ bio: bioForm.value })
    applyUser(res.data)
    editing.value = false
  } catch (e) {
    console.error('更新简介失败', e)
  } finally {
    saving.value = false
  }
}

async function saveAvatar() {
  saving.value = true
  try {
    const res = await authApi.updateProfile({ avatar: avatarForm.value })
    applyUser(res.data)
    avatarModal.value = false
  } catch (e) {
    console.error('更新头像失败', e)
  } finally {
    saving.value = false
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

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-active > div,
.modal-leave-active > div {
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.modal-enter-from > div,
.modal-leave-to > div {
  transform: scale(0.9) translateY(20px);
}
</style>
