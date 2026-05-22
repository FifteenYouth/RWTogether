import { defineStore } from 'pinia'
import { ref } from 'vue'
import worksApi from '../api/works'

export const useWorksStore = defineStore('works', () => {
  const currentWork = ref(null)
  const searchResults = ref([])

  async function searchWorks(q, type) {
    const res = await worksApi.search(q, type)
    searchResults.value = res.data
  }

  async function getWorkDetail(id) {
    const res = await worksApi.detail(id)
    currentWork.value = res.data
  }

  return { currentWork, searchResults, searchWorks, getWorkDetail }
})
