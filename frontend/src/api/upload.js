import api from './index'

export default {
  // 上传头像，返回 { url }
  avatar(file) {
    const form = new FormData()
    form.append('file', file)
    return api.post('/upload/avatar', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 30000,
    })
  },
}
