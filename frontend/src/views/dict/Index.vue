<template>
  <div class="dict-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span>字典管理</span>
            <el-input
              v-model="searchType"
              placeholder="请输入字典类型"
              style="width: 200px; margin-left: 20px;"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
          <el-button type="primary" @click="handleAdd">添加字典</el-button>
        </div>
      </template>
      <el-table :data="dicts" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="字典类型" />
        <el-table-column prop="code" label="字典编码" />
        <el-table-column prop="value" label="字典值" />
        <el-table-column prop="orderNum" label="排序" width="80" />
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
    
    <!-- 添加/编辑字典对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="字典类型" prop="type">
          <el-input v-model="form.type" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="字典编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入字典编码" />
        </el-form-item>
        <el-form-item label="字典值" prop="value">
          <el-input v-model="form.value" placeholder="请输入字典值" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" placeholder="请输入排序号" />
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
import { Search } from '@element-plus/icons-vue'

const dialogVisible = ref(false)
const dialogTitle = ref('添加字典')
const formRef = ref()
const dicts = ref([])
const searchType = ref('')
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const form = reactive({
  id: '',
  type: '',
  code: '',
  value: '',
  orderNum: 0
})

const rules = {
  type: [
    { required: true, message: '请输入字典类型', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入字典编码', trigger: 'blur' }
  ],
  value: [
    { required: true, message: '请输入字典值', trigger: 'blur' }
  ]
}

const columns = [
  { title: 'ID', key: 'id' },
  { title: '字典类型', key: 'type' },
  { title: '字典编码', key: 'code' },
  { title: '字典值', key: 'value' },
  { title: '排序', key: 'orderNum' },
  { title: '创建时间', key: 'createTime' },
  { title: '操作', key: 'actions' }
]

const fetchDicts = async () => {
  try {
    const response = await api.get('/dict/list', {
      params: {
        page: pagination.page,
        pageSize: pagination.pageSize,
        type: searchType.value
      }
    })
    dicts.value = response.list
    pagination.itemCount = response.total
  } catch (error) {
    console.error('获取字典列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchDicts()
}

const handleAdd = () => {
  dialogTitle.value = '添加字典'
  Object.assign(form, {
    id: '',
    type: '',
    code: '',
    value: '',
    orderNum: 0
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑字典'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (id: string) => {
  try {
    await api.delete(`/dict/${id}`)
    fetchDicts()
  } catch (error) {
    console.error('删除字典失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.id) {
          await api.put('/dict', form)
        } else {
          await api.post('/dict', form)
        }
        dialogVisible.value = false
        fetchDicts()
      } catch (error) {
        console.error('保存字典失败:', error)
      }
    }
  })
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchDicts()
}

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchDicts()
}

onMounted(() => {
  fetchDicts()
})
</script>

<style scoped>
.dict-container {
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
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