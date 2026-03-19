import { defineStore } from 'pinia'
import api from '@/api'

export const useConfigStore = defineStore('config', {
  state: () => ({
    config: {} as Record<string, any>,
    menuData: [] as any[],
    loading: false,
    menuLoading: false,
    loaded: false,
    menuLoaded: false,
    dynamicRoutesAdded: false
  }),

  getters: {
    getConfig: (state) => (key: string, defaultValue: any = null) => {
      return state.config[key] || defaultValue
    },

    systemName: (state) => {
      return state.config['system.name'] || 'Flexi Admin'
    },

    defaultHome: (state) => {
      return state.config['system.default_home'] || '/'
    },

    imageBaseUrl: (state) => {
      return state.config['system.image_base_url'] || '/api/images/'
    },

    printServiceUrl: (state) => {
      return state.config['system.print_service_url'] || 'http://127.0.0.1:8000'
    },

    menus: (state) => {
      return state.menuData
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

    async loadMenuData() {
      if (this.menuLoaded || this.menuLoading) return

      this.menuLoading = true
      try {
        const response = await api.get('/menu/tree')
        this.menuData = response
        this.menuLoaded = true
      } catch (error) {
        console.error('获取菜单数据失败:', error)
      } finally {
        this.menuLoading = false
      }
    },

    setMenuData(menuData: any[]) {
      this.menuData = menuData
      this.menuLoaded = true
    },

    setDynamicRoutesAdded(value: boolean) {
      this.dynamicRoutesAdded = value
    },

    resetConfig() {
      this.config = {}
      this.loaded = false
    },

    resetMenuData() {
      this.menuData = []
      this.menuLoaded = false
      this.menuLoading = false
      this.dynamicRoutesAdded = false
    }
  }
})
