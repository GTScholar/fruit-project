import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

// axios 实例：统一请求地址与拦截器
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截：自动携带 token
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('fruit_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：统一处理业务状态码
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    if (res.code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
      return Promise.reject(new Error(res.message))
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message))
  },
  (error) => {
    const message = error.response?.data?.message || '网络异常，请稍后重试'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
