import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  async function login(data) {
    const res = await authApi.login(data)
    token.value = res.data?.token
    userInfo.value = res.data?.userInfo
    localStorage.setItem('token', res.data?.token)
    return res
  }

  async function getInfo() {
    const res = await authApi.getInfo()
    userInfo.value = res.data
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, login, getInfo, logout }
})
