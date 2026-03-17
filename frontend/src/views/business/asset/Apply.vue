<template>
  <div class="asset-apply-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产领用</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 新建领用
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" class="mb-4" size="small">
        <el-form-item label="资产名称">
          <el-input v-model="searchForm.assetName" placeholder="请输入资产名称" style="width: 200px;"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 150px;">
            <el-option label="待审批" value="pending"></el-option>
            <el-option label="已审批" value="approved"></el-option>
            <el-option label="已拒绝" value="rejected"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="applyList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="assetName" label="资产名称"></el-table-column>
        <el-table-column prop="assetCode" label="资产编码" width="180"></el-table-column>
        <el-table-column prop="userName" label="领用用户" width="120"></el-table-column>
        <el-table-column prop="deptName" label="领用部门" width="120"></el-table-column>
        <el-table-column prop="applyTimeStr" label="申请时间" width="180"></el-table-column>
        <el-table-column prop="approvalTimeStr" label="审批时间" width="180"></el-table-column>
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteApply(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="70%"
    >
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="领用类型">
              <el-select v-model="form.applyType" placeholder="请选择领用类型" @change="handleApplyTypeChange">
                <el-option label="按闲置领用" value="idle"></el-option>
                <el-option label="按类型领用" value="type"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.applyType === 'idle'">
            <el-form-item label="闲置资产">
              <el-select 
                v-model="form.assetId" 
                placeholder="请选择闲置资产"
                filterable
                remote
                :remote-method="searchIdleAssets"
                :loading="assetLoading"
              >
                <el-option 
                  v-for="asset in assets" 
                  :key="asset.id" 
                  :label="asset.name + '(' + asset.code + ')'" 
                  :value="asset.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.applyType === 'type'">
            <el-form-item label="资产类型">
              <el-cascader
                v-model="form.typeId"
                :options="assetTypeTree"
                :props="assetTypeProps"
                placeholder="请选择资产类型"
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="状态" v-if="form.id">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option label="待审批" value="pending"></el-option>
                <el-option label="已审批" value="approved"></el-option>
                <el-option label="已拒绝" value="rejected"></el-option>
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

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { useConfigStore } from '@/stores/config'
import { useUserStore } from '@/stores/user'

const configStore = useConfigStore()
const userStore = useUserStore()

const applyList = ref([])
const assets = ref([])
const assetTypeTree = ref([])
const assetTypeProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  checkStrictly: true,
  emitPath: false,
  checkStrategy: 'leaf',
  filterOption: (node) => {
    return node.isLeaf
  }
}
const assetLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新建资产领用')

// 搜索表单
const searchForm = ref({
  assetName: '',
  status: ''
})

// 分页相关
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表单数据
const form = ref({
  id: '',
  applyType: '',
  assetId: '',
  typeId: null,
  status: '',
  remark: ''
})

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return statusMap[status] || 'info'
}

const handleSearch = () => {
  loadApplyList()
}

const resetForm = () => {
  searchForm.value = {
    assetName: '',
    status: ''
  }
  loadApplyList()
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  loadApplyList()
}

const handleCurrentChange = (current: number) => {
  pagination.value.currentPage = current
  loadApplyList()
}

const loadApplyList = async () => {
  try {
    const response = await api.get('/asset/apply/my', {
      params: {
        page: pagination.value.currentPage,
        size: pagination.value.pageSize
      }
    })
    applyList.value = response.records
    pagination.value.total = response.total
  } catch (error) {
    ElMessage.error('获取资产领用列表失败')
    console.error('获取资产领用列表失败:', error)
  }
}

const searchAssets = async (query) => {
  if (query) {
    assetLoading.value = true
    try {
      const response = await api.get('/asset', {
        params: {
          page: 1,
          size: 100,
          name: query
        }
      })
      assets.value = response.records
    } catch (error) {
      console.error('搜索资产失败:', error)
    } finally {
      assetLoading.value = false
    }
  } else {
    assets.value = []
  }
}



const loadAssetTypes = async () => {
  try {
    const treeData = await api.get('/asset-type/tree')
    const processTree = (nodes) => {
      return nodes.map(node => {
        const processedNode = { ...node }
        processedNode.isLeaf = !processedNode.children || processedNode.children.length === 0
        if (processedNode.children) {
          processedNode.children = processTree(processedNode.children)
        }
        return processedNode
      })
    }
    assetTypeTree.value = processTree(treeData)
  } catch (error) {
    console.error('加载资产类型失败:', error)
  }
}

const handleApplyTypeChange = () => {
  // 重置相关字段
  form.value.assetId = ''
  form.value.typeId = null
  assets.value = []
}



const searchIdleAssets = async (query) => {
  if (query) {
    assetLoading.value = true
    try {
      const response = await api.get('/asset', {
        params: {
          page: 1,
          size: 100,
          name: query,
          status: 'idle'
        }
      })
      assets.value = response.records
    } catch (error) {
      console.error('搜索闲置资产失败:', error)
    } finally {
      assetLoading.value = false
    }
  } else {
    // 无查询条件时，直接获取所有闲置资产
    assetLoading.value = true
    try {
      const response = await api.get('/asset', {
        params: {
          page: 1,
          size: 100,
          status: 'idle'
        }
      })
      assets.value = response.records
    } catch (error) {
      console.error('获取闲置资产失败:', error)
    } finally {
      assetLoading.value = false
    }
  }
}



const openAddDialog = async () => {
  dialogTitle.value = '新建资产领用'
  
  // 确保资产类型数据已经加载
  if (assetTypeTree.value.length === 0) {
    await loadAssetTypes()
  }
  
  form.value = {
    id: '',
    applyType: '',
    assetId: '',
    typeId: null,
    status: 'pending',
    remark: ''
  }
  
  dialogVisible.value = true
}

const openEditDialog = async (row) => {
  dialogTitle.value = '编辑资产领用'
  
  // 确保资产类型数据已经加载
  if (assetTypeTree.value.length === 0) {
    await loadAssetTypes()
  }
  
  const formData = { ...row }
  form.value = formData
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    const formData = { ...form.value }
    
    // 处理typeId：如果是数组且有值，取最后一个元素；如果是有效值，直接使用；否则设置为null
    if (Array.isArray(formData.typeId) && formData.typeId.length > 0) {
      formData.typeId = formData.typeId[formData.typeId.length - 1]
    } else if (formData.typeId) {
      // 已经是单个值，直接使用
    } else {
      formData.typeId = null
    }
    
    // 按类型领用时，不需要assetId，设置为0
    if (formData.applyType === 'type') {
      formData.assetId = 0
    }else if(formData.applyType === 'idle'){
      formData.typeId = 0
    }
    
    if (form.value.id) {
      // 编辑
      await api.put('/asset/apply', formData)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      formData.userId = userStore.userInfo.id
      await api.post('/asset/apply', formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadApplyList()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

const deleteApply = async (id) => {
  try {
    await api.delete(`/asset/apply/${id}`)
    ElMessage.success('删除成功')
    loadApplyList()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error('删除失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(async () => {
  // 加载配置
  await configStore.loadConfig()
  // 加载资产类型数据
  await loadAssetTypes()
  // 加载资产领用列表
  loadApplyList()
})
</script>

<style scoped>
.asset-apply-container {
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