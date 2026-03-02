import request from '@/utils/request'

export const frontApi = {
  getArticles: params => request.get('/articles', { params }),
  getArticle: id => request.get(`/articles/${id}`),
  getArticlesByCategory: (id, params) => request.get(`/articles/category/${id}`, { params }),
  getArticlesByTag: (id, params) => request.get(`/articles/tag/${id}`, { params }),
  getArticlesByTags: (ids, params) => request.get('/articles/tags?' + ids.map(id => `tagIds=${id}`).join('&'), { params }),
  getCategories: () => request.get('/categories'),
  getTags: () => request.get('/tags'),
  getArchives: () => request.get('/articles/archives')
}
