import api from './index'

export default {
  list(params) { return api.get('/works', { params }) },
  detail(id) { return api.get(`/works/${id}`) },
  search(q, type) { return api.get('/works/search', { params: { q, type } }) },
  create(data) { return api.post('/works', data) },
  importFromApi(data) { return api.post('/works/import', data) },
  remove(id) { return api.delete(`/works/${id}`) },
}
