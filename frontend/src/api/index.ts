import axios from 'axios'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: false
})

// 为api实例添加类型定义
declare module 'axios' {
  interface AxiosInstance {
    get<T = any>(url: string, config?: any): Promise<T>
    post<T = any>(url: string, data?: any, config?: any): Promise<T>
    put<T = any>(url: string, data?: any, config?: any): Promise<T>
    delete<T = any>(url: string, config?: any): Promise<T>
  }
}

api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => {
    const data = response.data
    // 处理R<T>格式的响应
    if (data && typeof data === 'object') {
      if (data.code === 0) {
        // 成功响应，返回data字段
        return data.data
      } else {
        // 错误响应，显示错误消息并拒绝Promise
        ElMessage.error(data.message || '操作失败')
        return Promise.reject(new Error(data.message || '操作失败'))
      }
    }
    // 兼容旧格式响应
    return data
  },
  (error) => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      ElMessage.error('无权限')
    } else if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    }
    return Promise.reject(error)
  }
)

// 资产相关API
export const getAssetList = (params: { page: number; size: number; name?: string }) => {
  return api.get('/asset', { params })
}

export const getAssetMyList = (params: { page: number; size: number; name?: string }) => {
  return api.get('/asset/my', { params })
}

export const addAsset = (data: any) => {
  return api.post('/asset', data)
}

export const updateAsset = (data: any) => {
  return api.put('/asset', data)
}

export const deleteAsset = (id: number) => {
  return api.delete(`/asset/${id}`)
}

// 登录相关API
export const login = (data: { username: string; password: string }) => {
  return api.post('/auth/login', data)
}

export const getUserInfo = () => {
  return api.get('/auth/user')
}

export default api