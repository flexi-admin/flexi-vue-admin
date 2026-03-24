<template>
  <div class="welcome-container">
    <div class="welcome-header">
      <h1>欢迎使用 {{ configStore.systemName || 'Flexi 管理系统' }}</h1>
      <p>高效、智能的企业管理解决方案</p>
    </div>
    
    <div class="welcome-content">
      <div class="welcome-features">
        <h2>系统功能</h2>
        <div class="feature-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon class="icon-large"><Goods /></el-icon>
            </div>
            <h3>资产管理</h3>
            <p>全面的资产管理，从采购到报废</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon class="icon-large"><User /></el-icon>
            </div>
            <h3>用户管理</h3>
            <p>灵活的用户权限控制，确保系统安全</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon class="icon-large"><Document /></el-icon>
            </div>
            <h3>审批流程</h3>
            <p>高效的工作流程管理，提升审批效率</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon class="icon-large"><Setting /></el-icon>
            </div>
            <h3>系统配置</h3>
            <p>丰富的系统配置选项，满足企业需求</p>
          </div>
        </div>
      </div>
      
      <div class="welcome-info">
        <h2>系统信息</h2>
        <div class="info-content">
          <p>系统版本: 1.0.0</p>
          <p>最后更新: 2026-03-21</p>
          <p>版权所有 © 2026 Flexi Admin</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useConfigStore } from '../stores/config'
import { Goods, User, Document, Setting } from '@element-plus/icons-vue'

const configStore = useConfigStore()

onMounted(() => {
  console.log('Welcome page mounted')
})
</script>

<style scoped>
.welcome-container {
  padding: 24px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.welcome-header {
  text-align: center;
  margin-bottom: 32px;
  padding: 40px 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.welcome-header h1 {
  font-size: 28px;
  margin: 0 0 12px 0;
  color: #1890ff;
  font-weight: 600;
}

.welcome-header p {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.welcome-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-features h2,
.welcome-info h2 {
  font-size: 18px;
  margin: 0 0 16px 0;
  color: #303133;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.feature-card {
  background-color: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.feature-icon {
  margin-bottom: 16px;
}

.icon-large {
  font-size: 48px;
  color: #1890ff;
}

.feature-card h3 {
  font-size: 16px;
  margin: 0 0 8px 0;
  color: #303133;
  font-weight: 500;
}

.feature-card p {
  font-size: 14px;
  color: #606266;
  margin: 0;
  line-height: 1.5;
}

.welcome-info {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-content p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-container {
    padding: 12px;
  }
  
  .welcome-header {
    padding: 24px 0;
  }
  
  .welcome-header h1 {
    font-size: 24px;
  }
  
  .feature-grid {
    grid-template-columns: 1fr;
  }
  
  .feature-card {
    padding: 20px;
  }
}
</style>