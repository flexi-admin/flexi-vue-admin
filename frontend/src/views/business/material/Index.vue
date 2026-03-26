<template>
  <div class="material-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物资管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="物资名称" />
        <el-table-column prop="code" label="物资编码" />
        <el-table-column label="物资分类" width="150">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column label="物资品类" width="150">
          <template #default="{ row }">
            {{ getBrandName(row.brandId) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalQuantity" label="总数量" />
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
            <el-button size="small" @click="viewDetails(row.id)">查看明细</el-button>
            <el-button size="small" type="danger" @click="deleteMaterial(row.id)">删除</el-button>
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
        <el-form-item label="物资名称">
          <el-input v-model="form.name" placeholder="请输入物资名称" />
        </el-form-item>
        <el-form-item label="物资编码">
          <el-input v-model="form.code" placeholder="请输入物资编码" />
        </el-form-item>
        <el-form-item label="物资分类">
          <el-select v-model="form.categoryId" placeholder="请选择物资分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="物资品类">
          <el-select v-model="form.brandId" placeholder="请选择物资品类" style="width: 100%">
            <el-option
              v-for="brand in brands"
              :key="brand.id"
              :label="brand.name"
              :value="brand.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="总数量">
          <el-input v-model.number="form.totalQuantity" type="number" placeholder="请输入总数量" />
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

    <!-- 明细列表对话框 -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="物资明细"
      width="800px"
    >
      <el-card>
        <template #header>
          <div class="card-header">
            <span>明细列表</span>
            <el-button type="primary" @click="openAddDetailDialog">
              <el-icon><Plus /></el-icon> 新增明细
            </el-button>
          </div>
        </template>
        <el-table :data="detailsData" stripe border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="物资品类" width="150">
            <template #default="{ row }">
              {{ getBrandName(row.brandId) }}
            </template>
          </el-table-column>
          <el-table-column label="仓库" width="120">
            <template #default="{ row }">
              {{ getWarehouseName(row.warehouseId) }}
            </template>
          </el-table-column>
          <el-table-column label="库位" width="120">
            <template #default="{ row }">
              {{ getLocationName(row.locationId) }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" />
          <el-table-column prop="unit" label="单位" />
          <el-table-column prop="batchNo" label="批次号" />
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
              <el-button size="small" @click="openEditDetailDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteDetail(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-dialog>

    <!-- 新增/编辑明细对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="detailDialogTitle"
      width="600px"
    >
      <el-form :model="detailForm" label-width="100px">
        <el-form-item label="物资品类">
          <el-select v-model="detailForm.brandId" placeholder="请选择物资品类" style="width: 100%">
            <el-option
              v-for="brand in brands"
              :key="brand.id"
              :label="brand.name"
              :value="brand.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库">
          <el-select v-model="detailForm.warehouseId" placeholder="请选择仓库" style="width: 100%" @change="loadLocations">
            <el-option
              v-for="warehouse in warehouses"
              :key="warehouse.id"
              :label="warehouse.name"
              :value="warehouse.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库位">
          <el-select v-model="detailForm.locationId" placeholder="请选择库位" style="width: 100%">
            <el-option
              v-for="location in locations"
              :key="location.id"
              :label="location.name"
              :value="location.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model.number="detailForm.quantity" type="number" placeholder="请输入数量" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="detailForm.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="detailForm.batchNo" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="detailForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="detailForm.remark"
            type="textarea"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDetailForm">确定</el-button>
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
const detailsData = ref([])
const categories = ref([])
const brands = ref([])
const warehouses = ref([])
const locations = ref([])
const dialogVisible = ref(false)
const detailsDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('新增物资')
const detailDialogTitle = ref('新增明细')
const currentMaterialId = ref(null)

const form = ref({
  id: '',
  name: '',
  code: '',
  categoryId: '',
  brandId: '',
  totalQuantity: 0,
  status: 1,
  remark: ''
})

const detailForm = ref({
  id: '',
  materialId: '',
  brandId: '',
  warehouseId: '',
  locationId: '',
  quantity: 0,
  unit: '',
  batchNo: '',
  status: 1,
  remark: ''
})

// 加载物资数据
const loadData = async () => {
  try {
    const response = await api.get('/material')
    tableData.value = response
  } catch (error) {
    ElMessage.error('加载物资失败')
    console.error('加载物资失败:', error)
  }
}

// 加载分类数据
const loadCategories = async () => {
  try {
    const response = await api.get('/material-category')
    categories.value = response
  } catch (error) {
    console.error('加载物资分类失败:', error)
  }
}

// 加载品类数据
const loadBrands = async () => {
  try {
    const response = await api.get('/material-brand')
    brands.value = response
  } catch (error) {
    console.error('加载物资品类失败:', error)
  }
}

// 加载仓库数据
const loadWarehouses = async () => {
  try {
    const response = await api.get('/material-warehouse')
    warehouses.value = response
  } catch (error) {
    console.error('加载仓库失败:', error)
  }
}

// 加载库位数据
const loadLocations = async (warehouseId) => {
  if (warehouseId) {
    try {
      const response = await api.get(`/material-location/warehouse/${warehouseId}`)
      locations.value = response
    } catch (error) {
      console.error('加载库位失败:', error)
    }
  } else {
    locations.value = []
  }
}

// 加载明细数据
const loadDetails = async (materialId) => {
  try {
    const response = await api.get(`/material-detail/material/${materialId}`)
    detailsData.value = response
  } catch (error) {
    ElMessage.error('加载明细失败')
    console.error('加载明细失败:', error)
  }
}

// 根据分类ID获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未知分类'
}

// 根据品类ID获取品类名称
const getBrandName = (brandId) => {
  const brand = brands.value.find(b => b.id === brandId)
  return brand ? brand.name : '未知品类'
}

// 根据仓库ID获取仓库名称
const getWarehouseName = (warehouseId) => {
  const warehouse = warehouses.value.find(w => w.id === warehouseId)
  return warehouse ? warehouse.name : '未知仓库'
}

// 根据库位ID获取库位名称
const getLocationName = (locationId) => {
  const location = locations.value.find(l => l.id === locationId)
  return location ? location.name : '未知库位'
}

// 打开新增物资对话框
const openAddDialog = () => {
  dialogTitle.value = '新增物资'
  form.value = {
    id: '',
    name: '',
    code: '',
    categoryId: '',
    brandId: '',
    totalQuantity: 0,
    status: 1,
    remark: ''
  }
  dialogVisible.value = true
}

// 打开编辑物资对话框
const openEditDialog = (data) => {
  dialogTitle.value = '编辑物资'
  form.value = { ...data }
  dialogVisible.value = true
}

// 提交物资表单
const submitForm = async () => {
  try {
    if (form.value.id) {
      // 编辑
      await api.put('/material', form.value)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/material', form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

// 删除物资
const deleteMaterial = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该物资吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/material/${id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

// 查看明细
const viewDetails = (materialId) => {
  currentMaterialId.value = materialId
  loadDetails(materialId)
  detailsDialogVisible.value = true
}

// 打开新增明细对话框
const openAddDetailDialog = () => {
  detailDialogTitle.value = '新增明细'
  detailForm.value = {
    id: '',
    materialId: currentMaterialId.value,
    brandId: '',
    warehouseId: '',
    locationId: '',
    quantity: 0,
    unit: '',
    batchNo: '',
    status: 1,
    remark: ''
  }
  locations.value = []
  detailDialogVisible.value = true
}

// 打开编辑明细对话框
const openEditDetailDialog = (data) => {
  detailDialogTitle.value = '编辑明细'
  detailForm.value = { ...data }
  loadLocations(data.warehouseId)
  detailDialogVisible.value = true
}

// 提交明细表单
const submitDetailForm = async () => {
  try {
    if (detailForm.value.id) {
      // 编辑
      await api.put('/material-detail', detailForm.value)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/material-detail', detailForm.value)
      ElMessage.success('新增成功')
    }
    detailDialogVisible.value = false
    loadDetails(currentMaterialId.value)
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

// 删除明细
const deleteDetail = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该明细吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/material-detail/${id}`)
    ElMessage.success('删除成功')
    loadDetails(currentMaterialId.value)
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
  loadCategories()
  loadBrands()
  loadWarehouses()
})
</script>

<style scoped>
.material-container {
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
