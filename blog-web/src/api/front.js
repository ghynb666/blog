import request from '@/utils/request'

export const frontApi = {
  getArticles: params => request.get('/articles', { params }),
  getArticle: id => request.get(`/articles/${id}`),
  getArticlesByCategory: (id, params, tagIds) => {
    const p = { ...params }
    if (tagIds) p.tagIds = tagIds
    return request.get(`/articles/category/${id}`, { params: p })
  },
  getArticlesByTag: (id, params, categoryId) => {
    const p = { ...params }
    if (categoryId) p.categoryId = categoryId
    return request.get(`/articles/tag/${id}`, { params: p })
  },
  getArticlesByTags: (ids, params, categoryId) => {
    let url = '/articles/tags?' + ids.map(id => `tagIds=${id}`).join('&')
    if (categoryId) url += `&categoryId=${categoryId}`
    return request.get(url, { params })
  },
  getCategories: () => request.get('/categories'),
  getTags: () => request.get('/tags'),
  getArchives: () => request.get('/archives')
}
