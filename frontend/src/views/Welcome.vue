<template>
  <div class="welcome-container">
    <div class="welcome-header">
      <h1>欢迎使用 {{ configStore.systemName || 'Flexi 管理系统' }}</h1>
      <p>高效、智能的企业管理解决方案</p>
    </div>
    
    <div class="welcome-content">
      <div class="welcome-stats">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>系统状态</span>
            </div>
          </template>
          <div class="stat-content">
            <div class="stat-item">
              <el-icon class="stat-icon"><DataAnalysis /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ userCount }}</div>
                <div class="stat-label">用户数量</div>
              </div>
            </div>
            <div class="stat-item">
              <el-icon class="stat-icon"><List /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ assetCount }}</div>
                <div class="stat-label">资产数量</div>
              </div>
            </div>
            <div class="stat-item">
              <el-icon class="stat-icon"><Timer /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ taskCount }}</div>
                <div class="stat-label">待处理任务</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      
      <div class="welcome-features">
        <h2>核心功能</h2>
        <div class="feature-grid">
          <el-card shadow="hover" class="feature-card">
            <div class="feature-icon">
              <el-icon class="icon-large"><Goods /></el-icon>
            </div>
            <h3>资产管理</h3>
            <p>全面的资产 lifecycle 管理，从采购到报废</p>
          </el-card>
          <el-card shadow="hover" class="feature-card">
            <div class="feature-icon">
              <el-icon class="icon-large"><User /></el-icon>
            </div>
            <h3>用户管理</h3>
            <p>灵活的用户权限控制，确保系统安全</p>
          </el-card>
          <el-card shadow="hover" class="feature-card">
            <div class="feature-icon">
              <el-icon class="icon-large"><Document /></el-icon>
            </div>
            <h3>审批流程</h3>
            <p>高效的工作流程管理，提升审批效率</p>
          </el-card>
          <el-card shadow="hover" class="feature-card">
            <div class="feature-icon">
              <el-icon class="icon-large"><Setting /></el-icon>
            </div>
            <h3>系统配置</h3>
            <p>丰富的系统配置选项，满足企业需求</p>
          </el-card>
        </div>
      </div>
      
      <div class="welcome-activity">
        <h2>最近活动</h2>
        <el-table :data="recentActivities" style="width: 100%">
          <el-table-column prop="time" label="时间" width="180" />
          <el-table-column prop="user" label="用户" width="120" />
          <el-table-column prop="action" label="操作" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useConfigStore } from '../stores/config'
import { DataAnalysis, List, Timer, Goods, User, Document, Setting } from '@element-plus/icons-vue'

const configStore = useConfigStore()

// 模拟数据
const userCount = ref(128)
const assetCount = ref(3560)
const taskCount = ref(12)

const recentActivities = ref([
  { time: '2026-03-19 14:30', user: '管理员', action: '系统配置更新' },
  { time: '2026-03-19 13:15', user: '张三', action: '资产添加' },
  { time: '2026-03-19 11:45', user: '李四', action: '审批通过' },
  { time: '2026-03-19 10:20', user: '王五', action: '用户权限修改' },
  { time: '2026-03-19 09:05', user: '赵六', action: '资产盘点' }
])

onMounted(() => {
  // 可以在这里获取真实数据
  console.log('Welcome page mounted')
})
</script>

<style scoped>
.welcome-container {
  padding: 24px;
  background-color: #f5f5f5;
  min-height: 100%;
}

.welcome-header {
  text-align: center;
  margin-bottom: 32px;
  padding: 24px 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.welcome-header h1 {
  font-size: 28px;
  margin: 0 0 12px 0;
  color: #1890ff;
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

.welcome-stats {
  margin-bottom: 16px;
}

.stat-content {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 150px;
}

.stat-icon {
  font-size: 32px;
  color: #1890ff;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.welcome-features h2,
.welcome-activity h2 {
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
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
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
}

.feature-card p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.welcome-activity {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stat-content {
    flex-direction: column;
    align-items: center;
  }
  
  .feature-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-container {
    padding: 12px;
  }
}
</style>