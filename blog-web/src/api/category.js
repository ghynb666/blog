import request from '@/utils/request'

export const list = () => request.get('/admin/categories')
export const create = data => request.post('/admin/categories', data)
export const update = (id, data) => request.put(`/admin/categories/${id}`, data)
export const remove = id => request.delete(`/admin/categories/${id}`)
