import { defineStore } from 'pinia'
import Cookies from 'js-cookie'
import { authApi } from '../api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: Cookies.get('userInfo') ? JSON.parse(Cookies.get('userInfo')) : null,
    token: Cookies.get('token') || null
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    userInfo: (state) => state.user,
    isAdmin: (state) => state.user && (state.user.role === 'ADMIN' || state.user.role === 'admin')
  },
  
  actions: {
    async login(username, password) {
      try {
        const res = await authApi.login({ username, password })
        this.token = res.data.token
        this.user = res.data.user
        Cookies.set('token', this.token, { expires: 7 })
        Cookies.set('userInfo', JSON.stringify(res.data.user), { expires: 7 })
        return res
      } catch (error) {
        throw error
      }
    },
    
    async register(username, password, email) {
      try {
        const res = await authApi.register({ username, password, email })
        this.token = res.data.token
        this.user = res.data.user
        Cookies.set('token', this.token, { expires: 7 })
        Cookies.set('userInfo', JSON.stringify(res.data.user), { expires: 7 })
        return res
      } catch (error) {
        throw error
      }
    },
    
    async getUserInfo() {
      try {
        const res = await authApi.getUserInfo()
        this.user = res.data
        Cookies.set('userInfo', JSON.stringify(res.data), { expires: 7 })
        return res
      } catch (error) {
        this.logout()
        throw error
      }
    },
    
    logout() {
      this.user = null
      this.token = null
      Cookies.remove('token')
      Cookies.remove('userInfo')
    },
    
    async initializeUser() {
      if (this.token && !this.user) {
        try {
          await this.getUserInfo()
        } catch (error) {
          console.error('初始化用户信息失败:', error)
          this.logout()
        }
      }
    }
  }
})