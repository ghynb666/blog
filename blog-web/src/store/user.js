import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  async function login(data) {
    const res = await authApi.login(data)
    token.value = res.data?.token
    userInfo.value = res.data?.userInfo
    localStorage.setItem('token', res.data?.token || '')
    localStorage.setItem('userInfo', JSON.stringify(res.data?.userInfo))
    return res
  }

  async function getInfo() {
    const res = await authApi.getInfo()
    userInfo.value = res.data
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, login, getInfo, logout }
})
