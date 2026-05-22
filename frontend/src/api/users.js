import api from './index'

export default {
  list() { return api.get('/users') },
  get(id) { return api.get(`/users/${id}`) },
  getWorks(id) { return api.get(`/users/${id}/works`) },
}
