import request from '@/utils/request'

export const frontApi = {
  getArticles: params => request.get('/articles', { params: normalizePagination(params) }),
  getArticle: id => request.get(`/articles/${id}`),
  getArticleInteraction: id => request.get(`/articles/${id}/interaction`),
  getComments: id => request.get(`/articles/${id}/comments`),
  createComment: (id, data) => request.post(`/articles/${id}/comments`, data),
  toggleLike: id => request.post(`/articles/${id}/like`),
  subscribe: data => request.post('/subscriptions', data),
  getArticlesByCategory: (id, params, tagIds) => {
    const p = normalizePagination(params)
    if (tagIds) p.tagIds = tagIds
    return request.get(`/articles/category/${id}`, { params: p })
  },
  getArticlesByTag: (id, params, categoryId) => {
    const p = normalizePagination(params)
    if (categoryId) p.categoryId = categoryId
    return request.get(`/articles/tag/${id}`, { params: p })
  },
  getArticlesByTags: (ids, params, categoryId) => {
    let url = '/articles/tags?' + ids.map(id => `tagIds=${id}`).join('&')
    if (categoryId) url += `&categoryId=${categoryId}`
    return request.get(url, { params: normalizePagination(params) })
  },
  getCategories: () => request.get('/categories'),
  getTags: () => request.get('/tags'),
  getArchives: () => request.get('/archives')
}

function normalizePagination(params = {}) {
  const next = { ...params }
  if (next.pageSize && !next.size) {
    next.size = next.pageSize
    delete next.pageSize
  }
  return next
}
