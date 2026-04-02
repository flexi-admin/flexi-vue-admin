<template>
  <div class="login-container">
    <el-card class="login-card" :body-style="{ padding: '20px' }">
      <template #header>
        <div class="card-header">
          <span>登录</span>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item v-if="multiTenantEnabled" label="租户" prop="tenantId">
          <el-select v-model="form.tenantId" placeholder="请选择租户" style="width: 100%">
            <el-option
              v-for="tenant in tenants"
              :key="tenant.id"
              :label="tenant.name"
              :value="tenant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useConfigStore } from '../stores/config'
import api from '@/api'

const formRef = ref()
const loading = ref(false)
const router = useRouter()
const userStore = useUserStore()
const configStore = useConfigStore()
const tenants = ref<any[]>([])
const multiTenantEnabled = ref(false)

const form = reactive({
  username: 'admin',
  password: '123456',
  tenantId: localStorage.getItem('tenantId') ? parseInt(localStorage.getItem('tenantId') || '') : null
})

const rules = reactive({
  tenantId: [
    { required: true, message: '请选择租户', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

// 获取系统配置
const getConfig = async () => {
  try {
    const response = await api.get('/auth/config')
    multiTenantEnabled.value = response.multiTenantEnabled === 'true'
    // 根据是否启用多租户调整验证规则
    if (!multiTenantEnabled.value) {
      delete rules.tenantId
    }
  } catch (error) {
    console.error('获取系统配置失败:', error)
  }
}

// 获取租户列表
const getTenants = async () => {
  try {
    const response = await api.get('/auth/tenants')
    tenants.value = response
  } catch (error) {
    console.error('获取租户列表失败:', error)
  }
}

const handleLogin = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        // 1. 调用登录接口获取 token
        const loginData = await api.post('/auth/login', form)
        userStore.setToken(loginData.token)
        // 存储租户ID到本地存储
        if (multiTenantEnabled.value && form.tenantId) {
          localStorage.setItem('tenantId', form.tenantId.toString())
        }
        
        // 2. 使用 token 获取用户信息
        const userData = await api.get('/auth/user')
        userStore.setUserInfo(userData.user)
        
        // 3. 重置菜单数据状态和动态路由状态，确保切换用户时重新获取菜单和路由
        configStore.resetMenuData()
        
        // 5. 获取系统配置，跳转到默认首页
        try {
          await configStore.loadConfig()
          const defaultHome = configStore.defaultHome
          router.push(defaultHome)
        } catch (configError) {
          console.error('获取系统配置失败，使用默认路径:', configError)
          router.push('/')
        }
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 页面加载时获取系统配置和租户列表
onMounted(async () => {
  await getConfig()
  if (multiTenantEnabled.value) {
    await getTenants()
  }
})
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
}
</style>