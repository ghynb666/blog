import request from '@/utils/request'

export const analyticsApi = {
  overview: params => request.get('/admin/analytics/overview', { params })
}
