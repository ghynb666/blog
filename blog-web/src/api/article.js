import request from '@/utils/request'

export const list = params => request.get('/admin/articles', { params })
export const detail = id => request.get(`/admin/articles/${id}`)
export const create = data => request.post('/admin/articles', data)
export const update = (id, data) => request.put(`/admin/articles/${id}`, data)
export const remove = id => request.delete(`/admin/articles/${id}`)
