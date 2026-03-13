<template>
  <div class="home-container">
    <el-container>
      <el-aside :width="currentFirstLevelMenu?.children && currentFirstLevelMenu.children.length > 0 ? '250px' : '100px'" style="border-right: 1px solid #eaeaea; display: flex;">
        <!-- 左侧一级菜单 -->
        <div class="menu-left" style="width: 100px; border-right: 1px solid #eaeaea;">
          <div class="logo">
            <h1>{{ configStore.systemName }}</h1>
          </div>
          <el-menu
            :default-active="activeFirstLevelMenu"
            class="el-menu-vertical-demo"
            @select="handleFirstLevelMenuSelect"
          >
            <template v-for="item in menuOptions" :key="item.key">
              <el-menu-item :index="item.key">
                <el-icon v-if="item.icon" class="menu-icon">
                  <!-- 直接使用后端返回的图标名称 -->
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.label }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </div>
        
        <!-- 右侧二级菜单 -->
        <div v-if="currentFirstLevelMenu?.children && currentFirstLevelMenu.children.length > 0" class="menu-right" style="width: 150px; overflow-y: auto;">
          <div class="menu-right-header">
            <h3>{{ currentFirstLevelMenu.label }}</h3>
          </div>
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            @select="handleMenuUpdate"
          >
            <template v-for="item in currentFirstLevelMenu?.children || []" :key="item.key">
              <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.key">
                <template #title>
                  <span>{{ item.label }}</span>
                </template>
                <el-menu-item 
                  v-for="child in item.children" 
                  :key="child.key"
                  :index="child.key"
                >
                  <span>{{ child.label }}</span>
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item v-else :index="item.key">
                <span>{{ item.label }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </div>
      </el-aside>
      <el-container>
        <el-header style="border-bottom: 1px solid #eaeaea; padding: 0; height: auto;">
          <!-- 面包屑和用户信息区域 -->
          <div class="breadcrumb-container">
            <div class="breadcrumb-left">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item v-for="(item, index) in breadcrumbPath" :key="index">
                  {{ item }}
                </el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <div class="breadcrumb-right">
              <div class="user-section">
                <span class="username">{{ userStore.userInfo.username || '管理员' }}</span>
                <el-avatar :size="32" style="margin-left: 12px;" />
                <el-dropdown trigger="click">
                  <el-button link style="margin-left: 12px;">
                    <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>
          
          <!-- 标签栏 -->
          <div class="tabs-container">
            <el-tabs v-model="activeTab" type="card" closable @tab-remove="handleTabClose" @tab-click="handleTabUpdate">
              <el-tab-pane 
                v-for="tab in tabs" 
                :key="tab.key" 
                :label="tab.label" 
                :name="tab.key"
              >
                <!-- 标签内容由路由视图渲染 -->
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-header>
        <el-main>
          <!-- 主内容区域 -->
          <div class="main-content">
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, getCurrentInstance } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useConfigStore } from '../stores/config'
import { House, ArrowDown, Setting, Document, DataAnalysis, List, Timer, User } from '@element-plus/icons-vue'
import api from '@/api'
import { addDynamicRoutes } from '../router'

// 导入所有可能的图标组件
import * as Icons from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const configStore = useConfigStore()
const activeMenu = ref('user')
const activeTab = ref('user')
const activeFirstLevelMenu = ref('1') // 默认选中第一个一级菜单
const currentFirstLevelMenu = ref(null)

// 标签页数据
const tabs = ref([])

// 菜单数据（从后端获取）
const menuOptions = ref([])

// 递归处理菜单数据
const processMenuItems = (items: any[]) => {
  return items.map((item: any) => {
    let path = item.path
    console.log('Processing menu item:', item.name, 'path:', path)
    return {
      label: item.name,
      key: item.id.toString(),
      path: path,
      icon: item.icon,
      component: item.component,
      children: item.children ? processMenuItems(item.children) : []
    }
  })
}

// 从后端获取菜单数据
const fetchMenuOptions = async () => {
  try {
    const response = await api.get('/menu/tree')
    console.log('Menu data from backend:', response)
    if (response && response.length > 0) {
      // 转换后端数据结构为前端期望的结构，递归处理所有级别菜单
      menuOptions.value = processMenuItems(response)
      
      console.log('Menu options:', menuOptions.value)
      // 设置默认选中的一级菜单
      if (menuOptions.value.length > 0) {
        activeFirstLevelMenu.value = menuOptions.value[0].key
        currentFirstLevelMenu.value = menuOptions.value[0]
      }
    }
  } catch (error) {
    console.error('获取菜单数据失败:', error)
    // 使用默认菜单数据
  }
}

// 处理一级菜单选择
const handleFirstLevelMenuSelect = (key: string) => {
  activeFirstLevelMenu.value = key
  // 查找当前一级菜单
  const menu = menuOptions.value.find(item => item.key === key)
  if (menu) {
    currentFirstLevelMenu.value = menu
    // 如果有子菜单，默认选中第一个子菜单
    if (menu.children && menu.children.length > 0) {
      activeMenu.value = menu.children[0].key
      // 跳转到第一个子菜单的路径
      if (menu.children[0].path) {
        router.push(menu.children[0].path)
        addTab(menu.children[0])
      }
    } else {
      // 如果没有子菜单，设置当前菜单为activeMenu
      activeMenu.value = menu.key
      // 跳转到当前一级菜单的路径
      if (menu.path) {
        router.push(menu.path)
        addTab(menu)
      }
    }
  }
}

// 根据路径查找菜单项
const findMenuByPath = (items: any[], path: string): any => {
  for (const item of items) {
    if (item.path === path) {
      return item
    }
    if (item.children && item.children.length > 0) {
      const childMenu = findMenuByPath(item.children, path)
      if (childMenu) {
        return childMenu
      }
    }
  }
  return null
}

// 递归构建面包屑路径
const buildBreadcrumbPath = (items: any[], path: string): string[] => {
  for (const item of items) {
    // 只匹配非空路径且完全相同的路径
    if (item.path && item.path === path) {
      return [item.label]
    }
    // 对于有子菜单的菜单项，递归查找
    if (item.children && item.children.length > 0) {
      const result = buildBreadcrumbPath(item.children, path)
      if (result.length > 0) {
        return [item.label, ...result]
      }
    }
  }
  return []
}

// 面包屑路径
const breadcrumbPath = computed(() => {
  const currentPath = route.path
  console.log('Current path:', currentPath)
  
  // 从菜单数据中构建面包屑路径
  const breadcrumb = buildBreadcrumbPath(menuOptions.value, currentPath)
  console.log('Final breadcrumb path:', breadcrumb)
  
  // 如果没有找到匹配的菜单，使用路径部分作为面包屑
  if (breadcrumb.length === 0) {
    const pathParts = currentPath.split('/').filter(part => part)
    return pathParts
  }
  
  return breadcrumb
})

// 处理菜单更新
const handleMenuUpdate = (key: string, keyPath: string[]) => {
  activeMenu.value = key
  // 递归查找当前菜单
  const findMenu = (items: any[]) => {
    for (const item of items) {
      if (item.key === key) {
        return item
      }
      if (item.children && item.children.length > 0) {
        const childMenu = findMenu(item.children)
        if (childMenu) {
          return childMenu
        }
      }
    }
    return null
  }
  const menu = findMenu(menuOptions.value)
  // 只有当菜单没有子菜单时才跳转
  if (menu && menu.path && (!menu.children || menu.children.length === 0)) {
    router.push(menu.path)
    addTab(menu)
  }
}

// 处理标签关闭
const handleTabClose = (key: string) => {
  // 不允许关闭最后一个标签
  if (tabs.value.length <= 1) return
  
  // 找到要关闭的标签索引
  const index = tabs.value.findIndex(tab => tab.key === key)
  if (index === -1) return
  
  // 移除标签
  tabs.value.splice(index, 1)
  
  // 如果关闭的是当前活动标签，切换到前一个标签
  if (key === activeTab.value) {
    const newActiveTab = tabs.value[index - 1] || tabs.value[0]
    activeTab.value = newActiveTab.key
    activeMenu.value = newActiveTab.key
    router.push(newActiveTab.path)
  }
}

// 处理标签更新
const handleTabUpdate = (tab: { props: { name: string } }) => {
  const key = tab.props.name
  activeTab.value = key
  const tabItem = tabs.value.find(t => t.key === key)
  if (tabItem) {
    activeMenu.value = tabItem.key
    router.push(tabItem.path)
  }
}

// 添加标签
const addTab = (menu: any) => {
  // 检查标签是否已存在
  const existingTab = tabs.value.find(tab => tab.key === menu.key)
  if (!existingTab) {
    tabs.value.push(menu)
  }
  // 设置当前活动标签
  activeTab.value = menu.key
}

// 监听路由变化，更新标签栏
watch(() => route.path, (newPath) => {
  console.log('Route changed to:', newPath)
  // 递归查找当前菜单
  const findMenu = (items: any[]) => {
    for (const item of items) {
      if (item.path === newPath) {
        return item
      }
      if (item.children && item.children.length > 0) {
        const childMenu = findMenu(item.children)
        if (childMenu) {
          return childMenu
        }
      }
    }
    return null
  }
  const menu = findMenu(menuOptions.value)
  if (menu) {
    console.log('Found menu for path:', menu)
    activeMenu.value = menu.key
    addTab(menu)
    
    // 查找并设置一级菜单
    const findFirstLevelMenu = (items: any[], targetMenu: any) => {
      for (let i = 0; i < items.length; i++) {
        const item = items[i]
        if (item.key === targetMenu.key) {
          return item
        }
        if (item.children && item.children.length > 0) {
          const found = findFirstLevelMenu(item.children, targetMenu)
          if (found) {
            return item
          }
        }
      }
      return null
    }
    
    const firstLevelMenu = findFirstLevelMenu(menuOptions.value, menu)
    if (firstLevelMenu) {
      activeFirstLevelMenu.value = firstLevelMenu.key
      currentFirstLevelMenu.value = firstLevelMenu
    }
  } else {
    console.log('No menu found for path:', newPath)
  }
})

// 初始化
onMounted(async () => {
  
  // 调试用户信息
  console.log('User info:', userStore.userInfo)
  console.log('Token:', userStore.token)
  console.log('Username:', userStore.userInfo.username || '管理员')
  
  // 检查是否有token
  if (userStore.token) {
    // 检查是否有token但没有用户信息
    if (!userStore.userInfo.username) {
      // 尝试重新获取用户信息
      try {
        const userData = await api.get('/auth/user')
        userStore.setUserInfo(userData.user)
        console.log('User info updated:', userStore.userInfo)
      } catch (error) {
        console.error('获取用户信息失败:', error)
        userStore.logout()
        router.push('/login')
        return
      }
    }
    
    // 从后端获取系统配置
    await configStore.loadConfig()
    
    // 从后端获取菜单数据（仅用于显示菜单，不添加路由，路由由路由守卫添加）
    await fetchMenuOptions()
    
    // 检查当前路径是否为根路径，如果是，跳转到默认首页
    if (route.path === '/') {
      const defaultHome = configStore.defaultHome
      router.push(defaultHome)
      return
    }
    
    // 根据当前路由路径初始化菜单选中状态
    setTimeout(() => {
      const currentPath = route.path
      console.log('Current path on mount:', currentPath)
      
      // 递归查找当前菜单
      const findMenu = (items: any[]) => {
        for (let i = 0; i < items.length; i++) {
          const item = items[i]
          if (item.path === currentPath) {
            return item
          }
          if (item.children && item.children.length > 0) {
            const childMenu = findMenu(item.children)
            if (childMenu) {
              return childMenu
            }
          }
        }
        return null
      }
      
      const menu = findMenu(menuOptions.value)
      if (menu) {
        console.log('Found menu for current path:', menu)
        activeMenu.value = menu.key
        addTab(menu)
        
        // 查找并设置一级菜单
        const findFirstLevelMenu = (items: any[], targetMenu: any) => {
          for (let i = 0; i < items.length; i++) {
            const item = items[i]
            if (item.key === targetMenu.key) {
              return item
            }
            if (item.children && item.children.length > 0) {
              const found = findFirstLevelMenu(item.children, targetMenu)
              if (found) {
                return item
              }
            }
          }
          return null
        }
        
        const firstLevelMenu = findFirstLevelMenu(menuOptions.value, menu)
        if (firstLevelMenu) {
          activeFirstLevelMenu.value = firstLevelMenu.key
          currentFirstLevelMenu.value = firstLevelMenu
        }
      }
    }, 100)
  } else {
    // 没有token，跳转到登录页
    router.push('/login')
  }
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  display: flex;
}

.logo {
  padding: 12px 4px;
  text-align: center;
  border-bottom: 1px solid #eaeaea;
  height: 48px;
  box-sizing: border-box;
}

.logo h1 {
  font-size: 12px;
  margin: 0;
  color: #1890ff;
}

/* 左侧一级菜单 */
.menu-left {
  height: 100%;
  overflow-y: auto;
  text-align: center;
  display: flex;
  flex-direction: column;
  user-select: none;
}

/* 右侧二级菜单 */
.menu-right {
  user-select: none;
}

/* 面包屑和用户信息区域 */
.breadcrumb-container {
  user-select: none;
}

/* 标签栏 */
.tabs-container {
  user-select: none;
}

.menu-left .el-menu {
  flex: 1;
  border-right: none;
}

.menu-left .el-menu-item {
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 0 0 0 !important;
  height: auto;
  min-height: 80px;
}

.menu-left .el-menu-item__content {
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 !important;
}

.menu-left .el-menu-item .el-icon {
  margin-right: 0;
  margin-bottom: 8px;
  font-size: 24px;
}

/* 右侧二级菜单 */
.menu-right {
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.menu-right-header {
  padding: 12px;
  border-bottom: 1px solid #eaeaea;
  background-color: #f5f5f5;
  height: 48px;
  box-sizing: border-box;
}

.menu-right-header h3 {
  font-size: 14px;
  margin: 0;
  color: #303133;
}

.menu-right .el-menu {
  flex: 1;
  border-right: none;
}

.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
  padding-right: 12px;
}

/* 面包屑和用户信息区域 */
.breadcrumb-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-bottom: 1px solid #eaeaea;
  background-color: #fff;
  height: 48px;
  box-sizing: border-box;
}

.breadcrumb-left {
  display: flex;
  align-items: center;
}

.breadcrumb-right {
  display: flex;
  align-items: center;
}

.username {
  font-size: 14px;
  margin-right: 8px;
}

.user-section {
  display: flex;
  align-items: center;
  padding: 2px 6px;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.user-section:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

/* 标签栏 */
.tabs-container {
  margin: 0 !important;
  padding: 0 !important;
  background-color: #fff;
}

/* 使用深度选择器覆盖Element Plus默认样式 */
:deep(.el-tabs__header) {
  margin: 0 !important;
  padding: 0 !important;
}

/* 确保标签页内容区域也没有边距 */
:deep(.el-tabs__content) {
  margin: 0 !important;
  padding: 0 !important;
}

/* 覆盖Element Plus默认的el-main样式 */
:deep(.el-main) {
  padding: 0 !important;
}

.main-content {
  padding: 0;
  background-color: #f5f5f5;
  height: 100%;
  overflow-y: auto;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>