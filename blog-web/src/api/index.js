import request from '@/utils/request'

export const articleApi = {
  list: params => request.get('/articles', { params }),
  detail: id => request.get(`/articles/${id}`),
  create: data => request.post('/articles', data),
  update: (id, data) => request.put(`/articles/${id}`, data),
  delete: id => request.delete(`/articles/${id}`)
}

export const categoryApi = {
  list: () => request.get('/categories'),
  create: data => request.post('/categories', data),
  update: (id, data) => request.put(`/categories/${id}`, data),
  delete: id => request.delete(`/categories/${id}`)
}

export const tagApi = {
  list: () => request.get('/tags'),
  create: data => request.post('/tags', data),
  update: (id, data) => request.put(`/tags/${id}`, data),
  delete: id => request.delete(`/tags/${id}`)
}

export const userApi = {
  login: data => request.post('/users/login', data),
  register: data => request.post('/users/register', data),
  info: () => request.get('/users/info'),
  update: data => request.put('/users/info', data)
}
