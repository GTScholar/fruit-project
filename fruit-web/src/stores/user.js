import { defineStore } from 'pinia'
import { login as loginApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('fruit_token') || '',
    userInfo: JSON.parse(localStorage.getItem('fruit_user') || 'null')
  }),

  getters: {
    isLogin: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.userType === 2
  },

  actions: {
    /** 登录：保存 token 与用户信息 */
    async login(form) {
      const res = await loginApi(form)
      this.token = res.data.token
      this.userInfo = res.data.user
      localStorage.setItem('fruit_token', this.token)
      localStorage.setItem('fruit_user', JSON.stringify(this.userInfo))
    },

    /** 更新本地用户信息 */
    setUserInfo(info) {
      this.userInfo = info
      localStorage.setItem('fruit_user', JSON.stringify(info))
    },

    /** 退出登录 */
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('fruit_token')
      localStorage.removeItem('fruit_user')
    }
  }
})
