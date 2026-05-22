import api from './index'

export default {
  list(workId) { return api.get(`/works/${workId}/comments`) },
  create(workId, data) { return api.post(`/works/${workId}/comments`, data) },
  reply(commentId, data) { return api.post(`/comments/${commentId}/reply`, data) },
  toggleLike(commentId) { return api.post(`/comments/${commentId}/like`) },
}
