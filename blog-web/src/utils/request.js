import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const payload = response.data
    if (payload && payload.code !== 200) {
      ElMessage.error(payload.message || 'Request failed')
      if (payload.code === 401) {
        localStorage.removeItem('token')
        window.location.href = '/admin/login'
      }
      return Promise.reject(new Error(payload.message || 'Request failed'))
    }
    return payload
  },
  error => {
    const msg = error.response?.data?.message || 'Request failed'
    ElMessage.error(msg)
    if (error.response?.status === 401 || error.response?.data?.code === 401) {
      localStorage.removeItem('token')
      window.location.href = '/admin/login'
    }
    return Promise.reject(error)
  }
)

export default request
