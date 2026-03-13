<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">添加用户</el-button>
        </div>
      </template>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column label="角色" width="180">
          <template #default="scope">
            <el-tag v-for="role in getUserRoleNames(scope.row.id)" :key="role" size="small" style="margin-right: 4px;">
              {{ role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="部门" width="180">
          <template #default="scope">
            <el-tag v-for="dept in getUserDeptNames(scope.row.id)" :key="dept" size="small" style="margin-right: 4px;">
              {{ dept }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            {{ scope.row.status ? '启用' : '禁用' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
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
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色">
            <el-option
              v-for="role in roles"
              :key="role.value"
              :label="role.label"
              :value="role.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" />
        </el-form-item>
        <el-form-item label="部门" prop="deptIds">
          <el-select v-model="form.deptIds" multiple placeholder="请选择部门">
            <el-option
              v-for="dept in depts"
              :key="dept.value"
              :label="dept.label"
              :value="dept.value"
            />
          </el-select>
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
import { ref, reactive, onMounted } from 'vue'
import api from '@/api'

const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const formRef = ref()
const users = ref([])
const roles = ref([])
const depts = ref([])
const userDepts = ref({})
const userRoles = ref({})
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const form = reactive({
  id: '',
  username: '',
  password: '',
  roleIds: [],
  status: true,
  deptIds: []
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  roleIds: [
    { required: true, message: '请选择角色', trigger: 'blur' }
  ]
}

const columns = [
  { title: 'ID', key: 'id' },
  { title: '用户名', key: 'username' },
  { title: '角色', key: 'roleName' },
  { title: '状态', key: 'status', render: (row: any) => row.status ? '启用' : '禁用' },
  { title: '创建时间', key: 'createTime' },
  { title: '操作', key: 'actions' }
]

const fetchUsers = async () => {
  try {
    const response = await api.get('/user/list', {
      params: {
        page: pagination.page,
        pageSize: pagination.pageSize
      }
    })
    users.value = response.list
    pagination.itemCount = response.total
    // 从返回的用户列表中获取角色和部门信息
    for (const user of users.value) {
      if (user.roleIds) {
        userRoles.value[user.id] = user.roleIds
      }
      if (user.deptIds) {
        userDepts.value[user.id] = user.deptIds
      }
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

const fetchRoles = async () => {
  try {
    const response = await api.get('/role/list')
    roles.value = response.map((role: any) => ({
      label: role.name,
      value: role.id
    }))
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

const fetchDepts = async () => {
  try {
    const response = await api.get('/dept/list')
    depts.value = response.map((dept: any) => ({
      label: dept.name,
      value: dept.id
    }))
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

const fetchUserDepts = async (userId: string) => {
  try {
    const response = await api.get(`/user/depts/${userId}`)
    userDepts.value[userId] = response
  } catch (error) {
    console.error('获取用户部门关联失败:', error)
  }
}

const fetchUserRoles = async (userId: string) => {
  try {
    const response = await api.get(`/user/roles/${userId}`)
    userRoles.value[userId] = response
  } catch (error) {
    console.error('获取用户角色关联失败:', error)
  }
}

const getUserDeptNames = (userId: string) => {
  const deptIds = userDepts.value[userId] || []
  return deptIds.map((deptId: number) => {
    const dept = depts.value.find((d: any) => d.value === deptId)
    return dept ? dept.label : ''
  }).filter(Boolean)
}

const getUserRoleNames = (userId: string) => {
  const roleIds = userRoles.value[userId] || []
  return roleIds.map((roleId: number) => {
    const role = roles.value.find((r: any) => r.value === roleId)
    return role ? role.label : ''
  }).filter(Boolean)
}

const handleAdd = () => {
  dialogTitle.value = '添加用户'
  Object.assign(form, {
    id: '',
    username: '',
    password: '',
    roleIds: [],
    status: true,
    deptIds: []
  })
  dialogVisible.value = true
}

const handleEdit = async (row: any) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, row)
  // 获取用户的部门信息
  if (row.id) {
    await fetchUserDepts(row.id)
    form.deptIds = userDepts.value[row.id] || []
    // 获取用户的角色信息
    await fetchUserRoles(row.id)
    form.roleIds = userRoles.value[row.id] || []
  }
  dialogVisible.value = true
}

const handleDelete = async (id: string) => {
  try {
    await api.delete(`/user/${id}`)
    fetchUsers()
  } catch (error) {
    console.error('删除用户失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        let userId: string
        if (form.id) {
          await api.put('/user', form)
          userId = form.id
        } else {
          const response = await api.post('/user', form)
          userId = response.id
        }
        // 保存用户角色关联
        if (userId && form.roleIds.length > 0) {
          await api.post('/user/assignRole', {
            userId: userId,
            roleIds: form.roleIds
          })
        }
        // 保存用户部门关联
        if (userId && form.deptIds.length > 0) {
          await api.post('/user/assignDept', {
            userId: userId,
            deptIds: form.deptIds
          })
        }
        dialogVisible.value = false
        fetchUsers()
      } catch (error) {
        console.error('保存用户失败:', error)
      }
    }
  })
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchUsers()
}

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
  fetchDepts()
})
</script>

<style scoped>
.user-container {
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
</style>