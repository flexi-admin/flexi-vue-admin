import { defineStore } from 'pinia'
import api from '@/api'

export const useConfigStore = defineStore('config', {
  state: () => ({
    config: {},
    loading: false,
    loaded: false
  }),

  getters: {
    getConfig: (state) => (key: string, defaultValue: any = null) => {
      return state.config[key] || defaultValue
    },

    systemName: (state) => {
      return state.config['system.name'] || 'Flexi Admin'
    },

    defaultHome: (state) => {
      return state.config['system.default_home'] || '/system/user'
    }
  },

  actions: {
    async loadConfig() {
      if (this.loaded || this.loading) return

      this.loading = true
      try {
        const response = await api.get('/config/all')
        this.config = response
        this.loaded = true
      } catch (error) {
        console.error('获取系统配置失败:', error)
      } finally {
        this.loading = false
      }
    },

    resetConfig() {
      this.config = {}
      this.loaded = false
    }
  }
})
