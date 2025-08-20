import axios from 'axios'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    console.log('[DEBUG] request interceptor: Processing request', {
      url: config.url,
      method: config.method,
      baseURL: config.baseURL,
      fullURL: config.baseURL + config.url
    })
    
    const token = Cookies.get('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('[DEBUG] request interceptor: Added Authorization header')
    } else {
      console.log('[DEBUG] request interceptor: No token found')
    }
    
    return config
  },
  error => {
    console.error('[DEBUG] request interceptor: Error in request', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    console.log('[DEBUG] response interceptor: Response received', {
      status: response.status,
      statusText: response.statusText,
      url: response.config.url,
      data: response.data
    })
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    console.error('[DEBUG] response interceptor: Error response received', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      url: error.config?.url,
      data: error.response?.data
    })
    if (error.response) {
      if (error.response.status === 401) {
        console.log('[DEBUG] response interceptor: 401 error, redirecting to login')
        Cookies.remove('token')
        window.location.href = '/login'
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default request