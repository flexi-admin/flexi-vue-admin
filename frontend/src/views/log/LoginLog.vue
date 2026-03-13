<template>
  <div class="log-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>登录日志</span>
        </div>
      </template>
      <el-table :data="loginLogs" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="ip" label="IP地址" />
        <el-table-column label="状态" width="80">
          <template #default="scope">
            {{ scope.row.status ? '成功' : '失败' }}
          </template>
        </el-table-column>
        <el-table-column prop="message" label="消息" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="danger" @click="handleDeleteLoginLog(scope.row.id)">删除</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import api from '@/api'

const loginLogs = ref([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const fetchLoginLogs = async () => {
  try {
    const response = await api.get('/log/login/list', {
      params: {
        page: pagination.page,
        pageSize: pagination.pageSize
      }
    })
    loginLogs.value = response.list
    pagination.itemCount = response.total
  } catch (error) {
    console.error('获取登录日志失败:', error)
  }
}

const handleDeleteLoginLog = async (id: string) => {
  try {
    await api.delete(`/log/login/${id}`)
    fetchLoginLogs()
  } catch (error) {
    console.error('删除登录日志失败:', error)
  }
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchLoginLogs()
}

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchLoginLogs()
}

onMounted(() => {
  fetchLoginLogs()
})
</script>

<style scoped>
.log-container {
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>