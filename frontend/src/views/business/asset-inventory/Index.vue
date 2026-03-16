<template>
  <div class="asset-inventory-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产盘点</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>
      
      <el-table :data="inventoryList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="inventoryCode" label="盘点单号" />
        <el-table-column prop="inventoryName" label="盘点名称" />
        <el-table-column label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180">
          <template #default="{ row }">
            {{ row.endTime ? formatDate(row.endTime) : '未结束' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creatorId" label="创建人" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteInventory(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @update:page-size="handleSizeChange"
          @update:current-page="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="50%"
    >
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="盘点名称">
              <el-input v-model="form.inventoryName" placeholder="请输入盘点名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="盘点单号">
              <el-input v-model="form.inventoryCode" placeholder="请输入盘点单号" :disabled="form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="开始时间">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="请选择开始时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="请选择结束时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option 
                  v-for="option in inventoryStatusOptions" 
                  :key="option.value" 
                  :label="option.label" 
                  :value="option.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model="form.remark"
                type="textarea"
                placeholder="请输入备注信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>


  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const inventoryList = ref([])
const inventoryStatusOptions = ref([
  { label: '进行中', value: 'in_progress' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
])
const dialogVisible = ref(false)
const dialogTitle = ref('新增盘点')

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = ref({
  id: '',
  inventoryCode: '',
  inventoryName: '',
  startTime: '',
  endTime: '',
  status: 'in_progress',
  creatorId: '',
  remark: ''
})

// 加载盘点列表
const loadInventoryList = async () => {
  try {
    // 调用后端接口获取分页数据
    const response = await api.get('/asset/inventory/list', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    inventoryList.value = response.records
    // 更新总记录数
    total.value = response.total
  } catch (error) {
    ElMessage.error('加载盘点列表失败')
    console.error('加载盘点列表失败:', error)
  }
}

// 格式化日期
const formatDate = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString()
}

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 'in_progress':
      return 'warning'
    case 'completed':
      return 'success'
    case 'cancelled':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'in_progress':
      return '进行中'
    case 'completed':
      return '已完成'
    case 'cancelled':
      return '已取消'
    default:
      return status
  }
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增盘点'
  form.value = {
    id: '',
    inventoryCode: '',
    inventoryName: '',
    startTime: '',
    endTime: '',
    status: 'in_progress',
    creatorId: '',
    remark: ''
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  dialogTitle.value = '编辑盘点'
  const formData = { ...row }
  // 转换时间格式
  if (formData.startTime) {
    formData.startTime = new Date(formData.startTime).toISOString().slice(0, 19).replace('T', ' ')
  }
  if (formData.endTime) {
    formData.endTime = new Date(formData.endTime).toISOString().slice(0, 19).replace('T', ' ')
  }
  form.value = formData
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    const formData = { ...form.value }
    // 转换时间为时间戳
    if (formData.startTime) {
      formData.startTime = new Date(formData.startTime).getTime()
    }
    if (formData.endTime) {
      formData.endTime = new Date(formData.endTime).getTime()
    }
    
    if (form.value.id) {
      // 编辑
      await api.put('/asset/inventory', formData)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/asset/inventory', formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadInventoryList()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

// 删除盘点
const deleteInventory = async (id) => {
  try {
    await api.delete(`/asset/inventory/${id}`)
    ElMessage.success('删除成功')
    loadInventoryList()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error('删除失败:', error)
  }
}

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadInventoryList()
}

// 当前页码变化处理
const handleCurrentChange = (current) => {
  currentPage.value = current
  loadInventoryList()
}

// 组件挂载时加载数据
onMounted(() => {
  loadInventoryList()
})
</script>

<style scoped>
.asset-inventory-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

</style>
