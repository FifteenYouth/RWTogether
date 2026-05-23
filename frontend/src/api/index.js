import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 10000,
})

api.interceptors.request.use(config => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  console.log('[API Request]', config.method?.toUpperCase(), config.baseURL + config.url)
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    console.error('[API Error]', error.config?.method?.toUpperCase(), error.config?.baseURL + error.config?.url, error.response?.status, error.response?.data)
    if (error.response?.status === 401) {
      const auth = useAuthStore()
      auth.logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
