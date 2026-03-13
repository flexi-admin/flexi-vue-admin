<template>
  <div class="asset-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>
      
      <el-table :data="assetList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="资产名称" />
        <el-table-column prop="code" label="资产编码" />
        <el-table-column prop="typeName" label="资产类型" />
        <el-table-column prop="locationName" label="资产位置" />
        <el-table-column prop="specification" label="规格" />
        <el-table-column prop="model" label="型号" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteAsset(row.id)">
              删除
            </el-button>
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
        <el-form-item label="资产名称">
          <el-input v-model="form.name" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="资产编码">
          <el-input v-model="form.code" placeholder="请输入资产编码" />
        </el-form-item>
        <el-form-item label="资产类型">
          <el-select v-model="form.typeId" placeholder="请选择资产类型">
            <el-option
              v-for="type in assetTypes"
              :key="type.id"
              :label="type.name"
              :value="type.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资产位置">
          <el-select v-model="form.locationId" placeholder="请选择资产位置">
            <el-option
              v-for="location in assetLocations"
              :key="location.id"
              :label="location.name"
              :value="location.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.specification" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="form.model" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="制造商">
          <el-input v-model="form.manufacturer" placeholder="请输入制造商" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="form.supplier" placeholder="请输入供应商" />
        </el-form-item>
        <el-form-item label="购买日期">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="请选择购买日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="form.price" type="number" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="维修中" value="维修中" />
            <el-option label="已报废" value="已报废" />
          </el-select>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const assetList = ref([])
const assetTypes = ref([])
const assetLocations = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增资产')
const form = ref({
  id: '',
  name: '',
  code: '',
  typeId: '',
  locationId: '',
  specification: '',
  model: '',
  manufacturer: '',
  supplier: '',
  purchaseDate: '',
  price: '',
  status: '',
  remark: ''
})

// 加载资产列表
const loadAssetList = async () => {
  try {
    const assets = await api.get('/asset')
    
    // 加载资产类型和位置信息，用于显示名称
    await loadAssetTypes()
    await loadAssetLocations()
    
    // 为每个资产添加类型和位置名称
    assetList.value = assets.map(asset => {
      const type = assetTypes.value.find(t => t.id === asset.typeId)
      const location = assetLocations.value.find(l => l.id === asset.locationId)
      return {
        ...asset,
        typeName: type ? type.name : '',
        locationName: location ? location.name : ''
      }
    })
  } catch (error) {
    ElMessage.error('加载资产列表失败')
    console.error('加载资产列表失败:', error)
  }
}

// 加载资产类型列表
const loadAssetTypes = async () => {
  try {
    assetTypes.value = await api.get('/asset-type')
  } catch (error) {
    console.error('加载资产类型失败:', error)
  }
}

// 加载资产位置列表
const loadAssetLocations = async () => {
  try {
    assetLocations.value = await api.get('/asset-location')
  } catch (error) {
    console.error('加载资产位置失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增资产'
  form.value = {
    id: '',
    name: '',
    code: '',
    typeId: '',
    locationId: '',
    specification: '',
    model: '',
    manufacturer: '',
    supplier: '',
    purchaseDate: '',
    price: '',
    status: '',
    remark: ''
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  dialogTitle.value = '编辑资产'
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    if (form.value.id) {
      // 编辑
      await api.put('/asset', form.value)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/asset', form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadAssetList()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

// 删除资产
const deleteAsset = async (id) => {
  try {
    await api.delete(`/asset/${id}`)
    ElMessage.success('删除成功')
    loadAssetList()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error('删除失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadAssetList()
})
</script>

<style scoped>
.asset-container {
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