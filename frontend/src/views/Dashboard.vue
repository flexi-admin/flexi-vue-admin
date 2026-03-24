<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="left-info">
        <span class="company-name">东营市洁丽赛电子科技有限责任公司</span>
      </div>
      <div class="center-title">
        <h1>固定资产数据大屏</h1>
      </div>
      <div class="header-right">
        <div class="date-info">
          <span>{{ currentDate }}</span>
          <span class="day-of-week">{{ dayOfWeek }}</span>
        </div>
        <el-button type="primary" @click="toggleFullscreen" :icon="isFullscreen ? 'FullscreenExit' : 'Fullscreen'">
          {{ isFullscreen ? '退出全屏' : '全屏' }}
        </el-button>
      </div>
    </div>
    
    <div class="dashboard-grid">
      <!-- 顶部统计卡片 -->
      <div class="dashboard-row">
        <div class="dashboard-card stat-card">
          <div class="card-title">资产总数</div>
          <div class="card-value">{{ totalAssets }}</div>
          <div class="card-trend positive">年环比 +100%</div>
        </div>
        <div class="dashboard-card stat-card">
          <div class="card-title">固定资产净值</div>
          <div class="card-value">{{ fixedAssetValue }}</div>
          <div class="card-trend positive">年环比 +100%</div>
        </div>
        <div class="dashboard-card stat-card">
          <div class="card-title">本年新增</div>
          <div class="card-value">{{ newAssetsThisYear }}</div>
          <div class="card-trend positive">年环比 +100%</div>
        </div>
        <div class="dashboard-card stat-card">
          <div class="card-title">本年报废</div>
          <div class="card-value">{{ scrappedAssetsThisYear }}</div>
          <div class="card-trend negative">年环比 -0%</div>
        </div>
        <div class="dashboard-card stat-card">
          <div class="card-title">年折旧费</div>
          <div class="card-value">{{ annualDepreciation }}</div>
          <div class="card-trend positive">年环比 +0%</div>
        </div>
      </div>
      
      <!-- 主要图表区域 -->
      <div class="dashboard-row">
        <!-- 资产类型资产总数 -->
        <div class="dashboard-card chart-card">
          <div class="card-title">
            资产统计 - 按类型
            <div class="chart-controls">
              <el-select v-model="selectedAssetType" @change="updateAssetTypeChart" placeholder="选择资产类型" style="width: 12vw;">
                <el-option label="全部类型" value="all"></el-option>
                <el-option v-for="type in assetTypes" :key="type.id" :label="type.name" :value="type.name"></el-option>
              </el-select>
            </div>
          </div>
          <div class="chart-container" ref="categoryChartRef"></div>
        </div>
        
        <!-- 资产状态环形图 -->
        <div class="dashboard-card chart-card">
          <div class="card-title">
            资产状态
            <div class="chart-controls">
              <el-select v-model="selectedAssetStatusType" @change="fetchAssetStatusByType" placeholder="选择资产类型" style="width: 12vw;">
                <el-option label="全部类型" value="all"></el-option>
                <el-option v-for="type in assetTypes" :key="type.id" :label="type.name" :value="type.name"></el-option>
              </el-select>
            </div>
          </div>
          <div class="chart-container" ref="assetStatusChartRef"></div>
        </div>
        
        <!-- 右侧统计 -->
        <div class="dashboard-card">
          <div class="card-title">最新盘点统计 - {{ latestInventoryName || latestInventoryDate }}</div>
          <div class="inventory-stats">
            <div class="inventory-stats-info">
              <div class="inventory-stat-item">
                <span class="inventory-stat-label">盘点资产总数：</span>
                <span class="inventory-stat-value">{{ totalInventoryAssets }}</span>
              </div>
              <div class="inventory-stat-item">
                <span class="inventory-stat-label">已盘点资产总数：</span>
                <span class="inventory-stat-value">{{ normalInventoryAssets }}</span>
              </div>
            </div>
            <div class="inventory-chart">
              <div class="chart-container" ref="inventoryRateChartRef"></div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 底部图表区域 -->
      <div class="dashboard-row">
        <!-- 金额统计 -->
        <div class="dashboard-card chart-card">
          <div class="card-title">金额统计</div>
          <div class="chart-container" ref="amountChartRef"></div>
        </div>
        
        <!-- 历史盘点正常率列表 -->
        <div class="dashboard-card">
          <div class="card-title">历史盘点正常率</div>
          <div class="idle-rate-list">
            <div class="idle-rate-item" v-for="(item, index) in inventoryRateHistory" :key="index">
              <div class="idle-rate-info">
                <div class="idle-rate-name">{{ item.inventoryName || item.date }}</div>
                <div class="idle-rate-value">{{ item.rate }}</div>
              </div>
              <div class="idle-rate-bar">
                <div class="idle-rate-progress" :style="{ width: item.rate }"></div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 闲置率分析 -->
        <div class="dashboard-card">
          <div class="card-title">闲置率分析</div>
          <div class="idle-rate-list">
            <div class="idle-rate-item" v-for="(item, index) in idleRateData" :key="index">
              <div class="idle-rate-info">
                <div class="idle-rate-name">{{ item.category }}</div>
                <div class="idle-rate-value">{{ item.rate }}</div>
              </div>
              <div class="idle-rate-bar">
                <div class="idle-rate-progress" :style="{ width: item.rate }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, onBeforeUnmount } from 'vue'
import { useConfigStore } from '../stores/config'
import { Fullscreen, FullscreenExit, Calendar, Goods, Delete, DataAnalysis, Check } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import api from '../api'

const configStore = useConfigStore()

// 数据变量
const totalAssets = ref(0)
const fixedAssetValue = ref(0)
const newAssetsThisYear = ref(0)
const scrappedAssetsThisYear = ref(0)
const annualDepreciation = ref(0)
const currentTime = ref('')
const currentDate = ref('')
const dayOfWeek = ref('')
const isFullscreen = ref(false)

// 最新盘点数据
const latestInventoryDate = ref('')
const latestInventoryName = ref('')
const totalInventoryAssets = ref(0)
const normalInventoryAssets = ref(0)
const normalRate = ref('0%')
const normalRateChange = ref('较上次 +0%')

// 图表引用
const categoryChartRef = ref<HTMLElement | null>(null)
const assetStatusChartRef = ref<HTMLElement | null>(null)
const inventoryRateChartRef = ref<HTMLElement | null>(null)
const amountChartRef = ref<HTMLElement | null>(null)

// 图表实例
let categoryChart: echarts.ECharts | null = null
let assetStatusChart: echarts.ECharts | null = null
let inventoryRateChart: echarts.ECharts | null = null
let amountChart: echarts.ECharts | null = null

// 资产类型选择
const selectedAssetType = ref('all')
const selectedAssetStatusType = ref('all')

// 资产类型列表
const assetTypes = ref<any[]>([])

// 数据
const idleRateData = ref<any[]>([])
const inventoryRateHistory = ref<any[]>([])
const assetTypeDistribution = ref<any[]>([])
const assetStatusDistribution = ref<any[]>([])
const amountStatistics = ref<any[]>([])

// 获取资产类型列表
const fetchAssetTypes = async () => {
  try {
    const types = await api.get('/asset-type')
    assetTypes.value = types
  } catch (error) {
    console.error('获取资产类型失败:', error)
  }
}

// 从后端获取数据
const fetchData = async () => {
  try {
    // 获取资产类型列表
    await fetchAssetTypes()
    
    // 获取资产统计数据
    const stats = await api.get('/asset/statistics')
    totalAssets.value = stats.totalAssets
    fixedAssetValue.value = stats.fixedAssetValue
    newAssetsThisYear.value = stats.newAssetsThisYear
    scrappedAssetsThisYear.value = stats.scrappedAssetsThisYear
    annualDepreciation.value = stats.annualDepreciation

    // 获取资产类型分布
    assetTypeDistribution.value = await api.get('/asset/type-distribution')

    // 获取资产状态分布
    assetStatusDistribution.value = await api.get('/asset/status-distribution')

    // 获取闲置率分析
    idleRateData.value = await api.get('/asset/idle-rate-analysis')
    // 按照闲置率从大到小排序
    idleRateData.value.sort((a, b) => {
      // 提取百分比数值并转换为数字
      const rateA = parseInt(a.rate.replace('%', ''))
      const rateB = parseInt(b.rate.replace('%', ''))
      return rateB - rateA
    })

    // 获取金额统计
    amountStatistics.value = await api.get('/asset/amount-statistics')

    // 获取最新盘点统计
    const inventoryStats = await api.get('/asset/inventory/latest-statistics')
    latestInventoryDate.value = inventoryStats.date
    latestInventoryName.value = inventoryStats.inventoryName
    totalInventoryAssets.value = inventoryStats.totalAssets
    normalInventoryAssets.value = inventoryStats.normalAssets
    normalRate.value = inventoryStats.normalRate
    normalRateChange.value = inventoryStats.rateChange

    // 获取历史盘点正常率
    inventoryRateHistory.value = await api.get('/asset/inventory/rate-history')

    // 重新初始化图表
    initCharts()
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

// 根据资产类型获取资产状态分布
const fetchAssetStatusByType = async (type: string) => {
  try {
    assetStatusDistribution.value = await api.get(`/asset/status-distribution-by-type?type=${type}`)
    updateAssetStatusChart()
  } catch (error) {
    console.error('获取资产状态分布失败:', error)
  }
}



// 更新当前时间
const updateCurrentTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
  
  // 更新日期
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
  
  // 更新星期
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  dayOfWeek.value = weekdays[now.getDay()]
}

// 切换全屏
const toggleFullscreen = () => {
  if (!isFullscreen.value) {
    const element = document.documentElement
    if (element.requestFullscreen) {
      element.requestFullscreen()
    } else if (element.mozRequestFullScreen) {
      element.mozRequestFullScreen()
    } else if (element.webkitRequestFullscreen) {
      element.webkitRequestFullscreen()
    } else if (element.msRequestFullscreen) {
      element.msRequestFullscreen()
    }
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    } else if (document.mozCancelFullScreen) {
      document.mozCancelFullScreen()
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen()
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen()
    }
  }
}

// 初始化资产类型资产总数图表
const initCategoryChart = () => {
  if (categoryChartRef.value) {
    categoryChart = echarts.init(categoryChartRef.value)
    updateAssetTypeChart()
  }
}

// 更新资产类型资产总数图表
const updateAssetTypeChart = () => {
  if (!categoryChart) return
  
  let data = []
  
  // 使用从后端获取的数据
  if (selectedAssetType.value === 'all') {
    data = assetTypeDistribution.value
  } else {
    // 这里可以根据需要从后端获取特定类型的子分类数据
    // 暂时使用模拟数据，后续可以添加相应的API
    // 由于资产类型现在是从后端获取的，我们可以根据类型名称进行处理
    // 这里简单处理，使用默认数据
    data = [
      { value: 0, name: selectedAssetType.value + '子类型1' },
      { value: 0, name: selectedAssetType.value + '子类型2' },
      { value: 0, name: selectedAssetType.value + '子类型3' }
    ]
  }
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [
      {
        name: '资产数量',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#0f172a',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          color: '#e2e8f0',
          fontSize: 12,
          formatter: '{b}: {c}'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold',
            color: '#e2e8f0'
          }
        },
        labelLine: {
          show: true,
          lineStyle: {
            color: '#94a3b8'
          }
        },
        data: data
      },
      {
        name: '总数',
        type: 'pie',
        radius: ['0%', '35%'],
        avoidLabelOverlap: false,
        itemStyle: {
          color: 'transparent'
        },
        label: {
          show: true,
          position: 'center',
          color: '#10b981',
          fontSize: 24,
          fontWeight: 'bold',
          formatter: function(params) {
            const total = data.reduce((sum, item) => sum + item.value, 0);
            return total + '\n{value|总数}';
          },
          rich: {
            value: {
              fontSize: 12,
              color: '#94a3b8',
              marginTop: 5
            }
          }
        },
        data: [{
          value: 1,
          name: '总数'
        }]
      }
    ]
  }
  
  categoryChart.setOption(option)
}

// 初始化资产状态环形图
const initAssetStatusChart = () => {
  if (assetStatusChartRef.value) {
    assetStatusChart = echarts.init(assetStatusChartRef.value)
    updateAssetStatusChart()
  }
}

// 更新资产状态环形图
const updateAssetStatusChart = () => {
  if (!assetStatusChart) return
  
  // 使用从后端获取的数据
  const data = assetStatusDistribution.value
  const total = data.reduce((sum, item) => sum + item.value, 0)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [
      {
        name: '资产状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#0f172a',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          color: '#e2e8f0',
          fontSize: 12,
          formatter: '{b}: {c}'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold',
            color: '#e2e8f0'
          }
        },
        labelLine: {
          show: true,
          lineStyle: {
            color: '#94a3b8'
          }
        },
        data: data
      },
      {
        name: '总数',
        type: 'pie',
        radius: ['0%', '35%'],
        avoidLabelOverlap: false,
        itemStyle: {
          color: 'transparent'
        },
        label: {
          show: true,
          position: 'center',
          color: '#10b981',
          fontSize: 24,
          fontWeight: 'bold',
          formatter: total + '\n{value|总数}',
          rich: {
            value: {
              fontSize: 12,
              color: '#94a3b8',
              marginTop: 5
            }
          }
        },
        data: [{
          value: 1,
          name: '总数'
        }]
      }
    ]
  }
  
  assetStatusChart.setOption(option)
}

// 初始化盘点正常率图表
const initInventoryRateChart = () => {
  if (inventoryRateChartRef.value) {
    // 确保容器有明确的尺寸
    const container = inventoryRateChartRef.value
    container.style.height = '100%'
    container.style.width = '100%'
    
    inventoryRateChart = echarts.init(container)
    
    // 计算异常资产数量
    const abnormalAssets = totalInventoryAssets.value - normalInventoryAssets.value
    
    const option = {
      series: [
        {
          name: '盘点正常率',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#0f172a',
            borderWidth: 2
          },
          label: {
            show: true,
            position: 'center',
            fontSize: '30',
            fontWeight: 'bold',
            color: '#10b981',
            formatter: `${normalRate.value}\n{value|正常率}`,
            rich: {
              value: {
                fontSize: '14',
                color: '#94a3b8',
                marginTop: 5
              }
            }
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '30',
              fontWeight: 'bold',
              color: '#10b981',
              formatter: `${normalRate.value}\n{value|正常率}`,
              rich: {
                value: {
                  fontSize: '14',
                  color: '#94a3b8',
                  marginTop: 5
                }
              }
            }
          },
          labelLine: {
            show: false
          },
          data: [
            {
              value: normalInventoryAssets.value,
              name: '正常资产',
              itemStyle: {
                color: '#10b981'
              }
            },
            {
              value: abnormalAssets,
              name: '异常资产',
              itemStyle: {
                color: '#334155'
              }
            }
          ]
        }
      ]
    }
    inventoryRateChart.setOption(option)
  }
}

// 初始化金额统计图表
const initAmountChart = () => {
  if (amountChartRef.value) {
    amountChart = echarts.init(amountChartRef.value)
    
    // 从后端数据中提取月份和数据
    const months = amountStatistics.value.map(item => item.month)
    const purchaseValues = amountStatistics.value.map(item => item.purchaseValue)
    const totalValues = amountStatistics.value.map(item => item.totalValue)
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['资产采购值', '资产总值'],
        textStyle: {
          color: '#94a3b8'
        }
      },
      xAxis: {
        type: 'category',
        data: months.length > 0 ? months : ['1月', '2月', '3月', '4月', '5月', '6月'],
        axisLine: {
          lineStyle: {
            color: '#334155'
          }
        },
        axisLabel: {
          color: '#94a3b8'
        }
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#334155'
          }
        },
        axisLabel: {
          color: '#94a3b8'
        },
        splitLine: {
          lineStyle: {
            color: '#1e293b'
          }
        }
      },
      series: [
        {
          name: '资产采购值',
          type: 'bar',
          data: purchaseValues.length > 0 ? purchaseValues : [12000, 19000, 3000, 5000, 2000, 3000],
          itemStyle: {
            color: '#3b82f6'
          }
        },
        {
          name: '资产总值',
          type: 'line',
          data: totalValues.length > 0 ? totalValues : [12000, 31000, 34000, 39000, 41000, 44000],
          itemStyle: {
            color: '#10b981'
          }
        }
      ]
    }
    amountChart.setOption(option)
  }
}

// 初始化所有图表
const initCharts = () => {
  initCategoryChart()
  initAssetStatusChart()
  initInventoryRateChart()
  initAmountChart()
}

// 销毁所有图表
const destroyCharts = () => {
  categoryChart?.dispose()
  assetStatusChart?.dispose()
  inventoryRateChart?.dispose()
  amountChart?.dispose()
}

// 防抖函数
const debounce = (func, delay) => {
  let timer = null
  return function() {
    clearTimeout(timer)
    timer = setTimeout(() => {
      func.apply(this, arguments)
    }, delay)
  }
}

// 调整图表大小
const resizeCharts = debounce(() => {
  // 使用requestAnimationFrame确保DOM尺寸更新后再调整图表大小
  requestAnimationFrame(() => {
    categoryChart?.resize()
    assetStatusChart?.resize()
    inventoryRateChart?.resize()
    amountChart?.resize()
  })
}, 200)

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!(document.fullscreenElement || document.mozFullScreenElement || document.webkitFullscreenElement || document.msFullscreenElement)
}

// 调整页面高度
const adjustHeight = () => {
  const container = document.querySelector('.dashboard-container')
  if (container) {
    container.style.height = `${window.innerHeight}px`
  }
}

let timeInterval: number

onMounted(() => {
  // 初始化时间
  updateCurrentTime()
  // 每秒更新时间
  timeInterval = window.setInterval(updateCurrentTime, 1000)
  
  // 初始化页面高度
  adjustHeight()
  // 监听窗口大小变化
  window.addEventListener('resize', adjustHeight)
  window.addEventListener('resize', resizeCharts)
  // 监听全屏状态变化
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  document.addEventListener('mozfullscreenchange', handleFullscreenChange)
  document.addEventListener('webkitfullscreenchange', handleFullscreenChange)
  document.addEventListener('msfullscreenchange', handleFullscreenChange)
  
  // 获取真实数据
  fetchData()
  
  console.log('Dashboard page mounted')
})

onBeforeUnmount(() => {
  // 清除定时器
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  // 移除事件监听器
  window.removeEventListener('resize', adjustHeight)
  window.removeEventListener('resize', resizeCharts)
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
  document.removeEventListener('mozfullscreenchange', handleFullscreenChange)
  document.removeEventListener('webkitfullscreenchange', handleFullscreenChange)
  document.removeEventListener('msfullscreenchange', handleFullscreenChange)
  
  // 销毁图表
  destroyCharts()
})
</script>

<style>
/* 全局样式 */
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}
</style>

<style scoped>
.dashboard-container {
  padding: 1vw;
  background-color: #0f172a;
  height: 100%;
  color: #e2e8f0;
  overflow: hidden;
  box-sizing: border-box;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1vw;
  padding: 1vw;
  background-color: rgba(15, 23, 42, 0.8);
  border-radius: 0.6vw;
  border: 1px solid rgba(30, 41, 59, 1);
}

.left-info {
  display: flex;
  align-items: center;
}

.center-title {
  flex: 1;
  text-align: center;
  margin: 0 1vw;
}

.company-name {
  font-size: 0.9vw;
  color: #94a3b8;
}

.dashboard-header h1 {
  font-size: 1.6vw;
  margin: 0;
  color: #ffffff;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1vw;
}

.date-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.3vw;
}

.date-info span {
  font-size: 0.9vw;
  color: #94a3b8;
}

.day-of-week {
  font-size: 0.8vw;
  color: #94a3b8;
}

.dashboard-time {
  font-size: 0.9vw;
  color: #94a3b8;
  font-family: monospace;
}

.dashboard-grid {
  height: calc(100% - 8vw);
  overflow: hidden;
}

.dashboard-grid > .dashboard-row {
  margin-bottom: 1vw;
}

.dashboard-row {
  display: flex;
  gap: 1vw;
  flex-wrap: nowrap;
  overflow-x: auto;
}

/* 主要图表区域的行 */
.dashboard-row:nth-child(2) {
  min-height: 20vw;
}

/* 底部图表区域的行 */
.dashboard-row:nth-child(3) {
  min-height: 20vw;
}

/* 顶部统计卡片特殊处理 */
.dashboard-row:first-child {
  min-height: 8vw;
}

.dashboard-row:first-child .dashboard-card {
  min-width: 15vw;
  flex: 1;
  padding: 0.8vw;
}

.dashboard-row:first-child .card-title {
  font-size: 0.8vw;
  margin-bottom: 0.6vw;
}

.dashboard-row:first-child .card-value {
  font-size: 1.8vw;
  margin: 0.3vw 0;
}

.dashboard-row:first-child .card-trend {
  font-size: 0.6vw;
  margin-top: 0.3vw;
}

.dashboard-card {
  background-color: rgba(15, 23, 42, 0.8);
  border-radius: 0.6vw;
  border: 1px solid rgba(30, 41, 59, 1);
  padding: 1vw;
  flex: 1;
  min-width: 20vw;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  box-sizing: border-box;
}

.dashboard-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.card-title {
  font-size: 0.9vw;
  font-weight: 500;
  color: #94a3b8;
  margin-bottom: 0.8vw;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: nowrap;
}

.card-title > div {
  display: flex;
  align-items: center;
  gap: 0.5vw;
}

.stat-card {
  text-align: center;
}

.card-value {
  font-size: 2.2vw;
  font-weight: bold;
  color: #10b981;
  margin: 0.4vw 0;
}

.card-trend {
  font-size: 0.7vw;
  font-weight: 500;
  padding: 0.15vw 0.4vw;
  border-radius: 0.8vw;
  display: inline-block;
  margin-top: 0.4vw;
}

.card-trend.positive {
  color: #10b981;
  background-color: rgba(16, 185, 129, 0.1);
}

.card-trend.negative {
  color: #ef4444;
  background-color: rgba(239, 68, 68, 0.1);
}

.chart-card {
  min-height: 15vw;
  max-height: 25vw;
  max-width: 40vw;
  flex: 1;
  overflow: hidden;
}

.chart-container {
  height: calc(100% - 1.5vw);
  max-height: 23vw;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 图表标签 */
.chart-tabs {
  display: flex;
  gap: 1vw;
  margin-left: auto;
}

.chart-tabs .tab {
  font-size: 0.8vw;
  color: #94a3b8;
  cursor: pointer;
  padding: 0.3vw 0.6vw;
  border-radius: 0.4vw;
}

.chart-tabs .tab.active {
  color: #10b981;
  background-color: rgba(16, 185, 129, 0.1);
}

/* 资产状态图表 */
.asset-status-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1vw;
  width: 100%;
  height: 100%;
}

.chart-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.total-assets {
  font-size: 3vw;
  font-weight: bold;
  color: #10b981;
}

.total-label {
  font-size: 0.9vw;
  color: #94a3b8;
  margin-top: 0.5vw;
}

.status-legend {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.5vw;
  width: 100%;
  padding: 0 1vw;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5vw;
  font-size: 0.8vw;
  color: #e2e8f0;
}

.color-box {
  width: 0.8vw;
  height: 0.8vw;
  border-radius: 50%;
}

.color-box.blue {
  background-color: #3b82f6;
}

.color-box.green {
  background-color: #10b981;
}

.color-box.red {
  background-color: #ef4444;
}

.color-box.purple {
  background-color: #8b5cf6;
}

.color-box.light-blue {
  background-color: #06b6d4;
}

.color-box.gray {
  background-color: #6b7280;
}

.color-box.yellow {
  background-color: #f59e0b;
}

.color-box.other {
  background-color: #6366f1;
}

.legend-item .count {
  margin-left: auto;
  color: #94a3b8;
}

/* 右侧统计 */
.right-stats {
  display: flex;
  flex-direction: column;
  gap: 1vw;
  height: 100%;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.8vw;
  padding: 0.8vw;
  background-color: rgba(30, 41, 59, 0.5);
  border-radius: 0.4vw;
}

.stat-icon {
  font-size: 1.2vw;
  color: #10b981;
  width: 2.5vw;
  height: 2.5vw;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(16, 185, 129, 0.1);
  border-radius: 0.4vw;
}

.stat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.3vw;
}

.stat-label {
  font-size: 0.8vw;
  color: #94a3b8;
}

.stat-value {
  font-size: 1.2vw;
  font-weight: bold;
  color: #e2e8f0;
}

.stat-sub {
  font-size: 0.7vw;
  color: #94a3b8;
}

/* 利用率统计 */
.utilization-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.8vw;
  margin-top: auto;
}

.utilization-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0.8vw;
  background-color: rgba(30, 41, 59, 0.5);
  border-radius: 0.4vw;
}

.utilization-label {
  font-size: 0.8vw;
  color: #94a3b8;
  margin-bottom: 0.3vw;
}

.utilization-value {
  font-size: 1.2vw;
  font-weight: bold;
  color: #e2e8f0;
  margin-bottom: 0.3vw;
}

.utilization-value.normal-rate {
  font-size: 1.8vw;
  color: #10b981;
  font-weight: bold;
}

.utilization-change {
  font-size: 0.7vw;
  color: #94a3b8;
}

/* 投资金额统计 */
.investment-stats {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 1vw;
}

.investment-tabs {
  display: flex;
  gap: 0.5vw;
  flex-wrap: wrap;
}

.investment-tabs .tab {
  font-size: 0.7vw;
  color: #94a3b8;
  cursor: pointer;
  padding: 0.3vw 0.6vw;
  border-radius: 0.4vw;
  white-space: nowrap;
}

.investment-tabs .tab.active {
  color: #10b981;
  background-color: rgba(16, 185, 129, 0.1);
}

.inventory-stats {
  display: flex;
  flex-direction: column;
  gap: 0.8vw;
  margin-top: auto;
}

.inventory-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.6vw;
  background-color: rgba(30, 41, 59, 0.5);
  border-radius: 0.4vw;
}

.inventory-label {
  font-size: 0.8vw;
  color: #94a3b8;
}

.inventory-value {
  font-size: 0.8vw;
  color: #e2e8f0;
}

/* 最新盘点统计样式 */
.inventory-stats {
  display: flex;
  flex-direction: column;
  gap: 1vw;
  height: 100%;
}

.inventory-stats .stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.6vw;
  background-color: rgba(30, 41, 59, 0.5);
  border-radius: 0.4vw;
}

.inventory-stats .stat-label {
  font-size: 0.8vw;
  color: #94a3b8;
}

.inventory-stats .stat-value {
  font-size: 0.9vw;
  font-weight: bold;
  color: #e2e8f0;
}

.inventory-stats-info {
  display: flex;
  justify-content: space-between;
  padding: 1vw;
  background-color: rgba(30, 41, 59, 0.5);
  border-radius: 0.4vw;
  margin-bottom: 1vw;
}

.inventory-stat-item {
  display: flex;
  align-items: center;
  gap: 0.5vw;
}

.inventory-stat-label {
  font-size: 0.8vw;
  color: #94a3b8;
}

.inventory-stat-value {
  font-size: 0.9vw;
  font-weight: bold;
  color: #10b981;
}

.inventory-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1vw;
  background-color: rgba(30, 41, 59, 0.5);
  border-radius: 0.4vw;
  flex: 1;
  width: 100%;
  min-height: 15vw;
  max-height: 20vw;
  overflow: hidden;
}

.inventory-chart .chart-container {
  flex: 1;
  width: 100%;
  min-height: 13vw;
  max-height: 18vw;
  overflow: hidden;
}

.chart-circle {
  position: relative;
  width: 10vw;
  height: 10vw;
  border-radius: 50%;
  background: conic-gradient(#10b981 0deg, #10b981 360deg, #334155 360deg);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 图表控件 */
.chart-controls {
  display: flex;
  align-items: center;
  gap: 0.5vw;
}

/* 闲置率分析列表 */
.idle-rate-list {
  margin-top: 1vw;
  display: flex;
  flex-direction: column;
  gap: 1vw;
}

.idle-rate-item {
  display: flex;
  flex-direction: column;
  gap: 0.5vw;
}

.idle-rate-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.idle-rate-name {
  font-size: 0.9vw;
  color: #e2e8f0;
}

.idle-rate-value {
  font-size: 0.9vw;
  font-weight: bold;
  color: #10b981;
}

.idle-rate-bar {
  height: 0.5vw;
  background-color: rgba(30, 41, 59, 0.5);
  border-radius: 0.25vw;
  overflow: hidden;
}

.idle-rate-progress {
  height: 100%;
  background-color: #10b981;
  border-radius: 0.25vw;
  transition: width 0.5s ease;
}



/* 图表图例 */
.chart-legend {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.5vw;
  margin-top: 1vw;
  padding: 0 1vw;
}

.chart-legend .legend-item {
  display: flex;
  align-items: center;
  gap: 0.5vw;
  font-size: 0.8vw;
  color: #e2e8f0;
}

.chart-legend .color-box {
  width: 0.8vw;
  height: 0.8vw;
  border-radius: 50%;
}

.chart-legend .color-box.blue {
  background-color: #3b82f6;
}

.chart-legend .color-box.green {
  background-color: #10b981;
}

.chart-legend .color-box.red {
  background-color: #ef4444;
}

.chart-legend .color-box.purple {
  background-color: #8b5cf6;
}

.circle-inner {
  width: 8.5vw;
  height: 8.5vw;
  border-radius: 50%;
  background-color: rgba(15, 23, 42, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(30, 41, 59, 1);
}

.circle-percentage {
  font-size: 1.8vw;
  font-weight: bold;
  color: #10b981;
  line-height: 1;
}

.circle-label {
  font-size: 0.8vw;
  color: #94a3b8;
  margin-top: 0.3vw;
}

.chart-info {
  text-align: center;
}

.chart-change {
  font-size: 0.8vw;
  color: #94a3b8;
}

.full-width {
  flex: 1 1 100%;
  min-width: 100%;
}



/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard-row {
    flex-direction: column;
  }
  
  .dashboard-card {
    min-width: 100%;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .dashboard-card {
    padding: 15px;
  }
  
  .card-value {
    font-size: 28px;
  }
}
</style>