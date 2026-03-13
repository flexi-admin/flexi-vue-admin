<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">添加角色</el-button>
        </div>
      </template>
      <el-table :data="roles" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="description" label="角色描述" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            {{ scope.row.status ? '启用' : '禁用' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="primary" @click="handleAssignPermission(scope.row)">分配权限</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.itemCount"
          @update:page-size="handlePageSizeChange"
          @update:current-page="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog v-model="permissionDialogVisible" :title="`为 ${currentRole?.name || ''} 分配权限`" width="600px">
      <div class="permission-container">
        <el-tree
          v-if="currentRole"
          ref="treeRef"
          :data="permissionTree"
          :props="permissionTreeProps"
          node-key="id"
          show-checkbox
          default-expand-all
          :checked-keys="checkedPermissionIds"
          @check-change="handlePermissionCheck"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePermission">保存权限</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const dialogVisible = ref(false)
const dialogTitle = ref('添加角色')
const formRef = ref()
const roles = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const form = reactive({
  id: '',
  name: '',
  description: '',
  status: true
})

const rules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ]
}

// 权限分配相关变量
const permissionDialogVisible = ref(false)
const currentRole = ref(null)
const permissionTree = ref([])
const permissionTreeProps = {
  label: 'name',
  children: 'children'
}
const checkedPermissionIds = ref([])
const treeRef = ref()

const columns = [
  { title: 'ID', key: 'id' },
  { title: '角色名称', key: 'name' },
  { title: '角色描述', key: 'description' },
  { title: '状态', key: 'status', render: (row: any) => row.status ? '启用' : '禁用' },
  { title: '创建时间', key: 'createTime' },
  { title: '操作', key: 'actions' }
]

const fetchRoles = async () => {
  try {
    const response = await api.get('/role/page', {
      params: {
        page: pagination.page,
        pageSize: pagination.pageSize
      }
    })
    roles.value = response.list
    pagination.itemCount = response.total
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加角色'
  Object.assign(form, {
    id: '',
    name: '',
    description: '',
    status: true
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (id: string) => {
  try {
    await api.delete(`/role/${id}`)
    fetchRoles()
  } catch (error) {
    console.error('删除角色失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.id) {
          await api.put('/role', form)
        } else {
          await api.post('/role', form)
        }
        dialogVisible.value = false
        fetchRoles()
      } catch (error) {
        console.error('保存角色失败:', error)
      }
    }
  })
}

// 处理权限分配
const handleAssignPermission = async (role: any) => {
  currentRole.value = role
  permissionDialogVisible.value = false // 先关闭对话框
  // 获取权限树
  await getPermissionTree()
  // 获取角色的权限
  await getRolePermissions(role.id)
  // 延迟打开对话框，确保权限树和权限ID都已准备就绪
  setTimeout(() => {
    permissionDialogVisible.value = true
    // 等待对话框和树渲染完成后，手动设置选中状态
    nextTick(() => {
      if (treeRef.value) {
        console.log('手动设置选中状态:', checkedPermissionIds.value)
        treeRef.value.setCheckedKeys(checkedPermissionIds.value)
      }
    })
  }, 100)
}

// 获取权限树
const getPermissionTree = async () => {
  try {
    const response = await api.get('/role-permission/tree')
    console.log('获取权限树成功:', response)
    permissionTree.value = response
    // 检查权限树节点的id类型
    if (response.length > 0) {
      console.log('第一个节点的id类型:', typeof response[0].id)
      console.log('第一个节点的id值:', response[0].id)
    }
  } catch (error) {
    console.error('获取权限树失败:', error)
  }
}

// 获取角色的权限
const getRolePermissions = async (roleId: string) => {
  try {
    const response = await api.get(`/role-permission/role/${roleId}`)
    console.log('获取角色权限成功:', response)
    // 确保权限ID是数字类型，与权限树节点的id类型保持一致
    checkedPermissionIds.value = response.map((id: any) => Number(id))
    console.log('设置checkedPermissionIds:', checkedPermissionIds.value)
  } catch (error) {
    console.error('获取角色权限失败:', error)
  }
}

// 处理权限勾选
const handlePermissionCheck = (data: any, checked: boolean, indeterminate: boolean) => {
  // 权限勾选逻辑由 el-tree 自动处理
}

// 保存权限
const handleSavePermission = async () => {
  if (!currentRole.value) return
  try {
    await api.post('/role-permission', {
      roleId: currentRole.value.id,
      permissionIds: checkedPermissionIds.value
    })
    // 提示保存成功
    ElMessage.success('权限保存成功')
    permissionDialogVisible.value = false
  } catch (error) {
    console.error('保存权限失败:', error)
    ElMessage.error('保存权限失败')
  }
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchRoles()
}

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchRoles()
}

onMounted(() => {
  fetchRoles()
})
</script>

<style scoped>
.role-container {
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.permission-container {
  max-height: 400px;
  overflow-y: auto;
}
</style>