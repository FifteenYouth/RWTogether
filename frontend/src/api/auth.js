import api from './index'

export default {
  login(data) { return api.post('/auth/login', data) },
  getMe() { return api.get('/auth/me') },
  updateProfile(data) { return api.put('/auth/profile', data) },
}
