<template>
  <div class="asset-approval-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产领用审批</span>
        </div>
      </template>
      
      <el-form :inline="true" class="mb-4" size="small">
        <el-form-item label="资产名称">
          <el-input v-model="searchForm.assetName" placeholder="请输入资产名称" style="width: 200px;"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="approvalList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="assetName" label="资产名称"></el-table-column>
        <el-table-column prop="assetCode" label="资产编码" width="180"></el-table-column>
        <el-table-column prop="userName" label="领用用户" width="120"></el-table-column>
        <el-table-column prop="deptName" label="领用部门" width="120"></el-table-column>
        <el-table-column prop="applyTimeStr" label="申请时间" width="180"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="approveApply(row.id)">
              审批通过
            </el-button>
            <el-button size="small" type="danger" @click="rejectApply(row.id)">
              审批拒绝
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

    <!-- 拒绝原因对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="审批拒绝"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            placeholder="请输入拒绝原因"
            rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReject">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'
import { useConfigStore } from '@/stores/config'

const configStore = useConfigStore()

const approvalList = ref([])
const rejectDialogVisible = ref(false)
const currentRejectId = ref(0)

// 搜索表单
const searchForm = ref({
  assetName: ''
})

// 分页相关
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 拒绝表单
const rejectForm = ref({
  reason: ''
})

const handleSearch = () => {
  loadApprovalList()
}

const resetForm = () => {
  searchForm.value = {
    assetName: ''
  }
  loadApprovalList()
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  loadApprovalList()
}

const handleCurrentChange = (current: number) => {
  pagination.value.currentPage = current
  loadApprovalList()
}

const loadApprovalList = async () => {
  try {
    const response = await api.get('/asset/approval', {
      params: {
        page: pagination.value.currentPage,
        size: pagination.value.pageSize
      }
    })
    approvalList.value = response.records
    pagination.value.total = response.total
  } catch (error) {
    ElMessage.error('获取待审批列表失败')
    console.error('获取待审批列表失败:', error)
  }
}

const approveApply = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要审批通过该申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await api.post(`/asset/approval/process/${id}`, null, {
      params: {
        action: 'approve'
      }
    })
    ElMessage.success('审批通过成功')
    loadApprovalList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审批通过失败')
      console.error('审批通过失败:', error)
    }
  }
}

const rejectApply = (id: number) => {
  currentRejectId.value = id
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

const submitReject = async () => {
  if (!rejectForm.value.reason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    await api.post(`/asset/approval/process/${currentRejectId.value}`, null, {
      params: {
        action: 'reject',
        reason: rejectForm.value.reason
      }
    })
    ElMessage.success('审批拒绝成功')
    rejectDialogVisible.value = false
    loadApprovalList()
  } catch (error) {
    ElMessage.error('审批拒绝失败')
    console.error('审批拒绝失败:', error)
  }
}



// 组件挂载时加载数据
onMounted(async () => {
  // 加载配置
  await configStore.loadConfig()
  // 加载待审批列表
  loadApprovalList()
})
</script>

<style scoped>
.asset-approval-container {
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