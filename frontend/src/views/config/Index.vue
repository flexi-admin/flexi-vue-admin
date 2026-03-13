<template>
  <div class="config-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统配置</span>
          <el-button type="primary" @click="handleAdd">添加配置</el-button>
        </div>
      </template>
      <el-table :data="configs" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="configKey" label="配置键" />
        <el-table-column prop="value" label="配置值" />
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
    
    <!-- 添加/编辑配置对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入配置键" />
        </el-form-item>
        <el-form-item label="配置值" prop="value">
          <el-input v-model="form.value" type="textarea" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
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
const dialogTitle = ref('添加配置')
const formRef = ref()
const configs = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const form = reactive({
  id: '',
  configKey: '',
  value: '',
  description: ''
})

const rules = {
  configKey: [
    { required: true, message: '请输入配置键', trigger: 'blur' }
  ],
  value: [
    { required: true, message: '请输入配置值', trigger: 'blur' }
  ]
}

const columns = [
  { title: 'ID', key: 'id' },
  { title: '配置键', key: 'configKey' },
  { title: '配置值', key: 'value' },
  { title: '描述', key: 'description' },
  { title: '创建时间', key: 'createTime' },
  { title: '操作', key: 'actions' }
]

const fetchConfigs = async () => {
  try {
    const response = await api.get('/config/list', {
      params: {
        page: pagination.page,
        pageSize: pagination.pageSize
      }
    })
    configs.value = response.list
    pagination.itemCount = response.total
  } catch (error) {
    console.error('获取配置列表失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加配置'
  Object.assign(form, {
    id: '',
    configKey: '',
    value: '',
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑配置'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (id: string) => {
  try {
    await api.delete(`/config/${id}`)
    fetchConfigs()
  } catch (error) {
    console.error('删除配置失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.id) {
          await api.put('/config', form)
        } else {
          await api.post('/config', form)
        }
        dialogVisible.value = false
        fetchConfigs()
      } catch (error) {
        console.error('保存配置失败:', error)
      }
    }
  })
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchConfigs()
}

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchConfigs()
}

onMounted(() => {
  fetchConfigs()
})
</script>

<style scoped>
.config-container {
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