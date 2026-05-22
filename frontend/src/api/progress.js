import api from './index'

export default {
  setProgress(workId, data) { return api.post(`/works/${workId}/progress`, data) },
  updateProgress(workId, data) { return api.put(`/works/${workId}/progress`, data) },
  removeProgress(workId) { return api.delete(`/works/${workId}/progress`) },
}
