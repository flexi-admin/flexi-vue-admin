import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useConfigStore } from '../stores/config'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true },
    children: []
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 动态路由状态管理由configStore处理

// 使用Vite的glob导入功能，预加载所有视图组件
const modules = import.meta.glob('../views/**/*.vue')

// 动态添加路由
const addDynamicRoutes = (menus: any[]) => {
  // 递归添加路由
  const addRoutes = (menuItems: any[]) => {
    // 确保menuItems是一个数组
    const items = Array.isArray(menuItems) ? menuItems : Object.values(menuItems)
    
    items.forEach(item => {
      console.log('Processing menu item:', item)
      if (item && item.path && item.component) {
        // 使用数据库中配置的component字段构建组件路径
        const componentPath = `../views/${item.component}.vue`
        
        // 添加路由，作为Home组件的子路由
        if (item.label) {
          // 检查路由是否已存在
          const existingRoute = router.getRoutes().find(route => route.path === item.path)
          if (!existingRoute) {
            // 将路由添加为Home组件的子路由
            router.addRoute('Home', {
              path: item.path.replace(/^\//, ''), // 移除路径开头的斜杠
              name: item.label.replace(/\s/g, ''),
              component: modules[componentPath] || (() => import('../views/404.vue')),
              meta: { title: item.label, requiresAuth: true }
            })
            console.log('Added route:', item.path, '->', componentPath, 'as child of Home')
          } else {
            console.log('Route already exists:', item.path)
          }
        }
      } else {
        console.log('Skipping menu item:', item)
      }
      
      // 递归处理子菜单
      if (item && item.children && item.children.length > 0) {
        console.log('Processing children:', item.children)
        addRoutes(item.children)
      }
    })
  }
  
  console.log('Adding dynamic routes...')
  console.log('Menus:', menus)
  addRoutes(menus)
  console.log('Dynamic routes added:', router.getRoutes().map(r => r.path))
  
  // 标记动态路由已添加
  const configStore = useConfigStore()
  configStore.setDynamicRoutesAdded(true)
}

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()
  
  // 登录页面不需要认证
  if (to.path === '/login') {
    next()
    return
  }
  
  // 所有其他页面都需要认证
  if (!userStore.isLoggedIn) {
    // 需要登录但未登录，重定向到登录页
    next('/login')
    return
  }
  
  // 已登录但用户信息为空，获取用户信息
  if (!userStore.userInfo.id) {
    try {
      const api = (await import('../api')).default
      const userData = await api.get('/auth/user')
      userStore.setUserInfo(userData.user)
    } catch (error) {
      console.error('获取用户信息失败:', error)
      userStore.logout()
      next('/login')
      return
    }
  }
  
  // 加载系统配置
  const configStore = useConfigStore()
  await configStore.loadConfig()
  
  // 页面刷新时，重置菜单数据状态，确保重新加载菜单
  if (!configStore.dynamicRoutesAdded) {
    configStore.resetMenuData()
    // 还没有添加动态路由，获取菜单数据并添加动态路由
    try {
      // 从configStore获取菜单数据
      await configStore.loadMenuData()
      const menuData = configStore.menuData
      
      // 转换菜单数据格式
      const menuOptions = menuData.map((item: any) => {
        return {
          label: item.name,
          key: item.id.toString(),
          path: item.path,
          icon: item.icon,
          component: item.component,
          children: item.children ? item.children.map((child: any) => {
            return {
              label: child.name,
              key: child.id.toString(),
              path: child.path,
              component: child.component
            }
          }) : []
        }
      })
      
      // 添加动态路由
      addDynamicRoutes(menuOptions)
      
      // 重新触发路由导航，确保新添加的路由能够被正确匹配
      next({ ...to, replace: true })
      return
    } catch (error) {
      console.error('获取菜单数据失败:', error)
    }
  }
  
  // 已登录且用户信息完整，正常跳转
  next()
})

export default router
export { addDynamicRoutes }