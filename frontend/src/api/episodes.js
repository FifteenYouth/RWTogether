import api from './index'

export default {
  list(workId) {
    return api.get(`/works/${workId}/episodes`)
  },
  toggleWatched(workId, episodeId) {
    return api.post(`/works/${workId}/episodes/${episodeId}/toggle`)
  },
  rate(workId, episodeId, rating) {
    return api.post(`/works/${workId}/episodes/${episodeId}/rate`, { rating })
  },
}
