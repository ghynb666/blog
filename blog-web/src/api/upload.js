import request from '@/utils/request'

export const upload = file => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const image = async file => {
  const res = await upload(file)
  return res
}
