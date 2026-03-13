<template>
  <div class="task-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>定时任务管理</span>
          <el-button type="primary" @click="handleAdd">添加任务</el-button>
        </div>
      </template>
      <el-table :data="tasks" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="任务名称" />
        <el-table-column prop="cronExpression" label="Cron表达式" />
        <el-table-column prop="className" label="任务类名" />
        <el-table-column label="状态" width="80">
          <template #default="scope">
            {{ scope.row.status ? '启用' : '禁用' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
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
    
    <!-- 添加/编辑任务对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="Cron表达式" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="请输入Cron表达式" />
        </el-form-item>
        <el-form-item label="任务类名" prop="className">
          <el-input v-model="form.className" placeholder="请输入任务类名" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import api from '@/api'

const dialogVisible = ref(false)
const dialogTitle = ref('添加任务')
const formRef = ref()
const tasks = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const form = reactive({
  id: '',
  name: '',
  cronExpression: '',
  className: '',
  description: '',
  status: false
})

const rules = {
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  cronExpression: [
    { required: true, message: '请输入Cron表达式', trigger: 'blur' }
  ],
  className: [
    { required: true, message: '请输入任务类名', trigger: 'blur' }
  ]
}

const columns = [
  { title: 'ID', key: 'id' },
  { title: '任务名称', key: 'name' },
  { title: 'Cron表达式', key: 'cronExpression' },
  { title: '任务类名', key: 'className' },
  { title: '状态', key: 'status' },
  { title: '描述', key: 'description' },
  { title: '创建时间', key: 'createTime' },
  { title: '操作', key: 'actions' }
]

const fetchTasks = async () => {
  try {
    const response = await api.get('/task/list', {
      params: {
        page: pagination.page,
        pageSize: pagination.pageSize
      }
    })
    tasks.value = response.list
    pagination.itemCount = response.total
  } catch (error) {
    console.error('获取任务列表失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加任务'
  Object.assign(form, {
    id: '',
    name: '',
    cronExpression: '',
    className: '',
    description: '',
    status: false
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑任务'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (id: string) => {
  try {
    await api.delete(`/task/${id}`)
    fetchTasks()
  } catch (error) {
    console.error('删除任务失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.id) {
          await api.put('/task', form)
        } else {
          await api.post('/task', form)
        }
        dialogVisible.value = false
        fetchTasks()
      } catch (error) {
        console.error('保存任务失败:', error)
      }
    }
  })
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchTasks()
}

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchTasks()
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.task-container {
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