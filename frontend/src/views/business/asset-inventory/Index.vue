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
        <el-table-column prop="inventoryCode" label="盘点单号" width="250" />
        <el-table-column prop="inventoryName" label="盘点名称" width="180" />
        <el-table-column prop="inventoryType" label="盘点类型">
          <template #default="{ row }">
            {{ getInventoryTypeText(row.inventoryType) }}
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="440">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button size="small" type="warning" @click="syncInventory(row.id, row.inventoryType, row.inventoryDepts, row.inventoryCategories)">
              同步
            </el-button>
            <el-button size="small" type="primary" @click="openDetailDialog(row.id, row.inventoryName)">
              明细
            </el-button>
            <el-button size="small" type="info" @click="issueInventory(row.id, row.inventoryName)">
              下发
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
          <el-col :span="24" v-if="form.id">
            <el-form-item label="盘点单号">
              <el-input v-model="form.inventoryCode" placeholder="请输入盘点单号" :disabled="true" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="盘点类型">
              <el-select v-model="form.inventoryType" placeholder="请选择盘点类型">
                <el-option 
                  v-for="option in inventoryTypeOptions" 
                  :key="option.value" 
                  :label="option.label" 
                  :value="option.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.inventoryType === 'dept'">
            <el-form-item label="盘点部门">
              <el-cascader
                v-model="selectedDepts"
                :options="deptOptions"
                :props="deptProps"
                placeholder="请选择盘点部门"
                collapse-tags
                :filterable="true"
                :clearable="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.inventoryType === 'category'">
            <el-form-item label="盘点分类">
              <el-cascader
                v-model="selectedCategories"
                :options="categoryOptions"
                :props="categoryProps"
                placeholder="请选择盘点分类"
                collapse-tags
                :filterable="true"
                :clearable="true"
              />
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
          <el-col :span="24" v-if="form.id">
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

    <!-- 明细对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${detailDialogTitle} - 盘点明细`"
      width="80%"
    >
      <div class="filter-container">
        <el-select v-model="detailStatusFilter" placeholder="状态筛选" clearable @change="handleDetailStatusFilter">
          <el-option label="全部" value="" />
          <el-option label="待盘点" value="pending" />
          <el-option label="正常" value="normal" />
          <el-option label="盘盈" value="surplus" />
          <el-option label="盘亏" value="shortage" />
        </el-select>
      </div>
      <el-table :data="inventoryDetails" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="assetCode" label="资产编码" />
        <el-table-column prop="assetName" label="资产名称" />
        <el-table-column prop="assetType" label="资产类型" />
        <el-table-column prop="assetLocation" label="资产位置" />
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="userName" label="使用人" />
        <el-table-column prop="adminUserName" label="管理员" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            {{ getDetailStatusText(row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="detailCurrentPage"
          v-model:page-size="detailPageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="detailTotal"
          @update:page-size="handleDetailSizeChange"
          @update:current-page="handleDetailCurrentChange"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 下发对话框 -->
    <el-dialog
      v-model="issueDialogVisible"
      :title="`${issueDialogTitle} - 下发数据`"
      width="80%"
    >
      <div class="issue-container">
        <el-divider content-position="left">Print Service 响应数据</el-divider>
        <pre style="background-color: #f5f7fa; padding: 16px; border-radius: 4px; overflow-x: auto;">
          {{ JSON.stringify(printServiceResponse, null, 2) }}
        </pre>
        
        <el-divider content-position="left">原始接口返回数据</el-divider>
        <pre style="background-color: #f5f7fa; padding: 16px; border-radius: 4px; overflow-x: auto;">
          {{ JSON.stringify(issueData, null, 2) }}
        </pre>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="issueDialogVisible = false">关闭</el-button>
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
import { useConfigStore } from '@/stores/config'

const configStore = useConfigStore()

const inventoryList = ref([])
const inventoryStatusOptions = ref([
  { label: '未开始', value: 'pending' },
  { label: '进行中', value: 'in_progress' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
])
const inventoryTypeOptions = ref([
  { label: '全部盘点', value: 'all' },
  { label: '按部门盘点', value: 'dept' },
  { label: '按分类盘点', value: 'category' }
])
const dialogVisible = ref(false)
const dialogTitle = ref('新增盘点')

// 明细对话框
const detailDialogVisible = ref(false)
const detailDialogTitle = ref('')
const currentInventoryId = ref(0)
const detailStatusFilter = ref('')
const inventoryDetails = ref([])
const detailCurrentPage = ref(1)
const detailPageSize = ref(10)
const detailTotal = ref(0)

// 下发对话框
const issueDialogVisible = ref(false)
const issueDialogTitle = ref('')
const issueData = ref({
  inventoryCode: '',
  inventoryName: '',
  inventoryType: '',
  startTime: '',
  endTime: '',
  status: '',
  details: []
})
const printServiceResponse = ref({})

// 字典数据
const inventoryDetailStatusDicts = ref([])

// 部门和分类选项
const deptOptions = ref([])
const categoryOptions = ref([])
const selectedDepts = ref([])
const selectedCategories = ref([])

// 部门和分类树形配置
const deptProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  multiple: true
}
const categoryProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  multiple: true
}

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = ref({
  id: '',
  inventoryCode: '',
  inventoryName: '',
  inventoryType: 'all',
  inventoryDepts: '',
  inventoryCategories: '',
  startTime: '',
  endTime: '',
  status: 'pending',
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
    case 'pending':
      return 'info'
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
    case 'pending':
      return '未开始'
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

// 获取盘点类型文本
const getInventoryTypeText = (type) => {
  switch (type) {
    case 'all':
      return '全部盘点'
    case 'dept':
      return '按部门盘点'
    case 'category':
      return '按分类盘点'
    default:
      return type
  }
}

// 加载部门列表
const loadDepts = async () => {
  try {
    const response = await api.get('/dept/tree')
    deptOptions.value = response
  } catch (error) {
    console.error('加载部门列表失败:', error)
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await api.get('/asset-type/tree')
    categoryOptions.value = response
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = async () => {
  dialogTitle.value = '新增盘点'
  form.value = {
    id: '',
    inventoryCode: '',
    inventoryName: '',
    inventoryType: 'all',
    inventoryDepts: '',
    inventoryCategories: '',
    startTime: '',
    endTime: '',
    status: 'pending',
    creatorId: '',
    remark: ''
  }
  selectedDepts.value = []
  selectedCategories.value = []
  
  // 加载部门和分类数据
  await loadDepts()
  await loadCategories()
  
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = async (row) => {
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
  
  // 处理部门和分类数据
  selectedDepts.value = formData.inventoryDepts ? formData.inventoryDepts.split(',').map(id => parseInt(id)) : []
  selectedCategories.value = formData.inventoryCategories ? formData.inventoryCategories.split(',').map(id => parseInt(id)) : []
  
  // 加载部门和分类数据
  await loadDepts()
  await loadCategories()
  
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
    
    // 根据盘点类型设置部门和分类
    if (formData.inventoryType === 'all') {
      formData.inventoryDepts = ''
      formData.inventoryCategories = ''
    } else if (formData.inventoryType === 'dept') {
      // 处理部门数据
      formData.inventoryDepts = selectedDepts.value.join(',')
      formData.inventoryCategories = ''
    } else if (formData.inventoryType === 'category') {
      // 处理分类数据
      formData.inventoryCategories = selectedCategories.value.join(',')
      formData.inventoryDepts = ''
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

// 同步盘点
const syncInventory = async (id, inventoryType, inventoryDepts, inventoryCategories) => {
  try {
    await api.post('/asset/inventory/sync', {
      inventoryId: id,
      inventoryType: inventoryType,
      inventoryDepts: inventoryDepts,
      inventoryCategories: inventoryCategories
    })
    ElMessage.success('同步成功')
  } catch (error) {
    ElMessage.error('同步失败')
    console.error('同步失败:', error)
  }
}

// 打开明细对话框
const openDetailDialog = async (id, name) => {
  currentInventoryId.value = id
  detailDialogTitle.value = name
  detailCurrentPage.value = 1
  detailPageSize.value = 10
  detailStatusFilter.value = ''
  await loadInventoryDetails(id, detailCurrentPage.value, detailPageSize.value, detailStatusFilter.value)
  detailDialogVisible.value = true
}

// 加载盘点明细
const loadInventoryDetails = async (inventoryId, page, size, status) => {
  try {
    const response = await api.get('/asset/inventory/detail/list', {
      params: {
        inventoryId: inventoryId,
        page: page,
        size: size,
        status: status
      }
    })
    inventoryDetails.value = response.records
    detailTotal.value = response.total
  } catch (error) {
    ElMessage.error('加载盘点明细失败')
    console.error('加载盘点明细失败:', error)
  }
}

// 明细分页大小变化处理
const handleDetailSizeChange = (size) => {
  detailPageSize.value = size
  loadInventoryDetails(currentInventoryId.value, detailCurrentPage.value, detailPageSize.value, detailStatusFilter.value)
}

// 明细当前页码变化处理
const handleDetailCurrentChange = (current) => {
  detailCurrentPage.value = current
  loadInventoryDetails(currentInventoryId.value, detailCurrentPage.value, detailPageSize.value, detailStatusFilter.value)
}

// 处理状态筛选
const handleDetailStatusFilter = () => {
  detailCurrentPage.value = 1
  loadInventoryDetails(currentInventoryId.value, detailCurrentPage.value, detailPageSize.value, detailStatusFilter.value)
}

// 下发盘点
const issueInventory = async (id, name) => {
  issueDialogTitle.value = name
  try {
    const response = await api.get(`/asset/inventory/issue/${id}`)
    issueData.value = response
    
    // 调用printServiceUrl + 'issue'接口
    if (configStore.printServiceUrl) {
      const printResponse = await fetch(configStore.printServiceUrl + '/issue', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({data: issueData.value})
      })
      printServiceResponse.value = await printResponse.json()
    }
    
    issueDialogVisible.value = true
  } catch (error) {
    ElMessage.error('下发失败')
    console.error('下发失败:', error)
  }
}

// 获取明细状态文本
const getDetailStatusText = (status) => {
  const dictItem = inventoryDetailStatusDicts.value.find(item => item.code === status)
  return dictItem ? dictItem.value : status
}

// 加载字典数据
const loadDicts = async () => {
  try {
    const response = await api.get('/dict/list-by-type', {
      params: {
        type: 'inventory_detail_status'
      }
    })
    inventoryDetailStatusDicts.value = response
  } catch (error) {
    console.error('加载字典数据失败:', error)
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
onMounted(async () => {
  await loadDicts()
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

.filter-container {
  margin-bottom: 16px;
}

</style>
