import request from '@/utils/request'

export const loginAdmin = data => request.post('/admin/login', data)
export const loginUser = data => request.post('/login', data)
export const registerAdmin = data => request.post('/admin/register', data)
export const registerUser = data => request.post('/register', data)
export const logoutUser = () => request.post('/logout')
export const logout = () => request.post('/admin/logout')
export const getInfo = () => request.get('/admin/info')
export const getProfile = () => request.get('/me')

// backward compatible alias
export const login = loginAdmin
