import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  async function login(data) {
    const res = await authApi.login(data)
    token.value = res.token
    localStorage.setItem('token', res.token)
    return res
  }

  async function getInfo() {
    const res = await authApi.getInfo()
    userInfo.value = res
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, login, getInfo, logout }
})
