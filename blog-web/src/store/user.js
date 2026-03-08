import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const saveSession = res => {
    token.value = res.data?.token
    userInfo.value = res.data?.userInfo
    localStorage.setItem('token', res.data?.token || '')
    localStorage.setItem('userInfo', JSON.stringify(res.data?.userInfo))
  }

  async function login(data) {
    const res = await authApi.loginAdmin(data)
    saveSession(res)
    return res
  }

  async function loginFront(data) {
    const res = await authApi.loginUser(data)
    saveSession(res)
    return res
  }

  async function register(data, isAdmin = false) {
    const res = isAdmin
      ? await authApi.registerAdmin(data)
      : await authApi.registerUser(data)
    saveSession(res)
    return res
  }

  async function registerFront(data) {
    const res = await authApi.registerUser(data)
    saveSession(res)
    return res
  }

  async function getInfo() {
    const res = await authApi.getInfo()
    userInfo.value = res.data
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res
  }

  async function getProfile() {
    const res = await authApi.getProfile()
    userInfo.value = res.data
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res
  }

  async function logout(remote = false, isAdmin = false) {
    if (remote) {
      try {
        await (isAdmin ? authApi.logout() : authApi.logoutUser())
      } catch (e) {
        console.error('logout failed', e)
      }
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, login, loginFront, register, registerFront, getInfo, getProfile, logout }
})
