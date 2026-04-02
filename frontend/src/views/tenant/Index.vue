<template>
  <div class="tenant-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>租户管理</span>
          <el-button type="primary" @click="handleAdd" :disabled="!hasPermission('tenant:add')">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
      </template>
      
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="租户名称">
              <el-input v-model="searchForm.name" placeholder="请输入租户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="租户编码">
              <el-input v-model="searchForm.code" placeholder="请输入租户编码" />
            </el-form-item>
          </el-col>
          <el-col :span="8" class="search-buttons">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
      
      <el-table :data="tenantList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="租户名称" />
        <el-table-column prop="code" label="租户编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
              :disabled="!hasPermission('tenant:update')"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
              :disabled="!hasPermission('tenant:delete')"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="租户名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入租户名称" />
        </el-form-item>
        <el-form-item label="租户编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入租户编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" active-color="#13ce66" inactive-color="#ff4949" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '../../stores/user'
import api from '@/api'
import { Plus } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const userStore = useUserStore()

// 搜索表单
const searchForm = reactive({
  name: '',
  code: ''
})

// 表格数据
const tenantList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增租户')
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  status: true
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入租户名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入租户编码', trigger: 'blur' }
  ]
}

// 权限检查
const hasPermission = (permission: string) => {
  const permissions = userStore.userInfo.permissions || []
  return permissions.includes(permission)
}

// 获取租户列表
const getTenantList = async () => {
  try {
    const response = await api.get('/api/tenant/list')
    tenantList.value = response
    total.value = response.length
  } catch (error) {
    console.error('获取租户列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  getTenantList()
}

// 重置
const resetForm = () => {
  searchForm.name = ''
  searchForm.code = ''
  getTenantList()
}

// 分页
const handleSizeChange = (size: number) => {
  pageSize.value = size
  getTenantList()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  getTenantList()
}

// 新增
const handleAdd = () => {
  form.id = null
  form.name = ''
  form.code = ''
  form.description = ''
  form.status = true
  dialogTitle.value = '新增租户'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  form.id = row.id
  form.name = row.name
  form.code = row.code
  form.description = row.description
  form.status = row.status
  dialogTitle.value = '编辑租户'
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该租户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await api.delete(`/api/tenant/delete/${row.id}`)
      ElMessage.success('删除成功')
      getTenantList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 状态变更
const handleStatusChange = async (row: any) => {
  try {
    await api.put('/api/tenant/update', row)
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
    // 恢复原来的状态
    getTenantList()
  }
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.id) {
          // 编辑
          await api.put('/api/tenant/update', form)
          ElMessage.success('编辑成功')
        } else {
          // 新增
          await api.post('/api/tenant/add', form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getTenantList()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 页面加载时获取租户列表
onMounted(() => {
  getTenantList()
})
</script>

<style scoped>
.tenant-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.search-buttons {
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
