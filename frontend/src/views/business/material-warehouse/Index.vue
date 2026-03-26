<template>
  <div class="material-warehouse-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物资仓库管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="仓库名称" />
        <el-table-column prop="code" label="仓库编码" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" @click="viewLocations(row.id)">查看库位</el-button>
            <el-button size="small" type="danger" @click="deleteWarehouse(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="仓库名称">
          <el-input v-model="form.name" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="仓库编码">
          <el-input v-model="form.code" placeholder="请输入仓库编码" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入仓库地址" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 库位列表对话框 -->
    <el-dialog
      v-model="locationsDialogVisible"
      title="库位列表"
      width="800px"
    >
      <el-card>
        <template #header>
          <div class="card-header">
            <span>库位列表</span>
            <el-button type="primary" @click="openAddLocationDialog">
              <el-icon><Plus /></el-icon> 新增库位
            </el-button>
          </div>
        </template>
        <el-table :data="locationsData" stripe border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="库位名称" />
          <el-table-column prop="code" label="库位编码" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" @click="openEditLocationDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteLocation(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-dialog>

    <!-- 新增/编辑库位对话框 -->
    <el-dialog
      v-model="locationDialogVisible"
      :title="locationDialogTitle"
      width="500px"
    >
      <el-form :model="locationForm" label-width="80px">
        <el-form-item label="库位名称">
          <el-input v-model="locationForm.name" placeholder="请输入库位名称" />
        </el-form-item>
        <el-form-item label="库位编码">
          <el-input v-model="locationForm.code" placeholder="请输入库位编码" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="locationForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="locationForm.remark"
            type="textarea"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="locationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitLocationForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const tableData = ref([])
const locationsData = ref([])
const dialogVisible = ref(false)
const locationsDialogVisible = ref(false)
const locationDialogVisible = ref(false)
const dialogTitle = ref('新增仓库')
const locationDialogTitle = ref('新增库位')
const currentWarehouseId = ref(null)

const form = ref({
  id: '',
  name: '',
  code: '',
  address: '',
  contactPerson: '',
  contactPhone: '',
  remark: '',
  status: 1
})

const locationForm = ref({
  id: '',
  warehouseId: '',
  name: '',
  code: '',
  remark: '',
  status: 1
})

// 加载仓库数据
const loadData = async () => {
  try {
    const response = await api.get('/material-warehouse')
    tableData.value = response
  } catch (error) {
    ElMessage.error('加载仓库失败')
    console.error('加载仓库失败:', error)
  }
}

// 加载库位数据
const loadLocations = async (warehouseId) => {
  try {
    const response = await api.get(`/material-location/warehouse/${warehouseId}`)
    locationsData.value = response
  } catch (error) {
    ElMessage.error('加载库位失败')
    console.error('加载库位失败:', error)
  }
}

// 打开新增仓库对话框
const openAddDialog = () => {
  dialogTitle.value = '新增仓库'
  form.value = {
    id: '',
    name: '',
    code: '',
    address: '',
    contactPerson: '',
    contactPhone: '',
    remark: '',
    status: 1
  }
  dialogVisible.value = true
}

// 打开编辑仓库对话框
const openEditDialog = (data) => {
  dialogTitle.value = '编辑仓库'
  form.value = { ...data }
  dialogVisible.value = true
}

// 提交仓库表单
const submitForm = async () => {
  try {
    if (form.value.id) {
      // 编辑
      await api.put('/material-warehouse', form.value)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/material-warehouse', form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

// 删除仓库
const deleteWarehouse = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该仓库吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/material-warehouse/${id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

// 查看库位
const viewLocations = (warehouseId) => {
  currentWarehouseId.value = warehouseId
  loadLocations(warehouseId)
  locationsDialogVisible.value = true
}

// 打开新增库位对话框
const openAddLocationDialog = () => {
  locationDialogTitle.value = '新增库位'
  locationForm.value = {
    id: '',
    warehouseId: currentWarehouseId.value,
    name: '',
    code: '',
    remark: '',
    status: 1
  }
  locationDialogVisible.value = true
}

// 打开编辑库位对话框
const openEditLocationDialog = (data) => {
  locationDialogTitle.value = '编辑库位'
  locationForm.value = { ...data }
  locationDialogVisible.value = true
}

// 提交库位表单
const submitLocationForm = async () => {
  try {
    if (locationForm.value.id) {
      // 编辑
      await api.put('/material-location', locationForm.value)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/material-location', locationForm.value)
      ElMessage.success('新增成功')
    }
    locationDialogVisible.value = false
    loadLocations(currentWarehouseId.value)
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

// 删除库位
const deleteLocation = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该库位吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/material-location/${id}`)
    ElMessage.success('删除成功')
    loadLocations(currentWarehouseId.value)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.material-warehouse-container {
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
</style>
