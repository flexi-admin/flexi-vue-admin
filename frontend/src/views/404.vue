<template>
  <div class="error-container">
    <el-card class="error-card">
      <template #header>
        <div class="card-header">
          <span>404 - 页面不存在</span>
        </div>
      </template>
      <div class="error-content">
        <el-result
          icon="error"
          title="404"
          sub-title="抱歉，您访问的页面不存在"
        >
          <template #extra>
            <el-button type="primary" @click="goHome">返回首页</el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.error-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
}

.error-card {
  width: 500px;
}

.card-header {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
}

.error-content {
  text-align: center;
}
</style>