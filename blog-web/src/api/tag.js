import request from '@/utils/request'

export const list = () => request.get('/admin/tags')
export const create = data => request.post('/admin/tags', data)
export const update = (id, data) => request.put(`/admin/tags/${id}`, data)
export const remove = id => request.delete(`/admin/tags/${id}`)
