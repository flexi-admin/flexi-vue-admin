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
        <el-table-column prop="statusName" label="状态" />
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
      width="70%"
    >
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="资产名称">
              <el-input v-model="form.name" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.id">
            <el-form-item label="资产编码">
              <el-input v-model="form.code" placeholder="请输入资产编码" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产类型">
              <el-cascader
                v-model="form.typeId"
                :options="assetTypeTree"
                :props="assetTypeProps"
                placeholder="请选择资产类型"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产位置">
              <el-cascader
                v-model="form.locationId"
                :options="assetLocationTree"
                :props="assetLocationProps"
                placeholder="请选择资产位置"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规格">
              <el-input v-model="form.specification" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="型号">
              <el-input v-model="form.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="制造商">
              <el-input v-model="form.manufacturer" placeholder="请输入制造商" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="供应商">
              <el-input v-model="form.supplier" placeholder="请输入供应商" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="购买日期">
              <el-date-picker
                v-model="form.purchaseDate"
                type="date"
                placeholder="请选择购买日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="价格">
              <el-input v-model="form.price" type="number" placeholder="请输入价格" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="当前价值">
              <el-input v-model="form.currentValue" type="number" placeholder="请输入当前价值" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option 
                  v-for="option in assetStatusOptions" 
                  :key="option.value" 
                  :label="option.label" 
                  :value="option.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="图片">
              <el-input v-model="form.image" placeholder="请输入图片路径" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="标签类型">
              <el-select v-model="form.labelType" placeholder="请选择标签类型">
                <el-option label="普通标签" value="普通标签" />
                <el-option label="RFID标签" value="RFID标签" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="标签编码">
              <el-input v-model="form.labelCode" placeholder="请输入标签编码" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="管理员">
              <el-input v-model="form.adminUserId" type="number" placeholder="请输入管理员用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="使用人">
              <el-input v-model="form.userId" type="number" placeholder="请输入使用人用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="部门">
              <el-input v-model="form.deptId" type="number" placeholder="请输入部门ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="SN码">
              <el-input v-model="form.sn" placeholder="请输入SN码" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产来源">
              <el-select v-model="form.source" placeholder="请选择资产来源">
                <el-option label="自产" value="自产" />
                <el-option label="采购" value="采购" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计量单位">
              <el-select v-model="form.unit" placeholder="请选择计量单位">
                <el-option label="台" value="台" />
                <el-option label="张" value="张" />
                <el-option label="个" value="个" />
                <el-option label="套" value="套" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model="form.remark"
                type="textarea"
                placeholder="请输入备注信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
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
const assetTypeTree = ref([])
const assetTypeProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  checkStrictly: true,
  emitPath: false,
  checkStrategy: 'leaf',
  filterOption: (node) => {
    return node.isLeaf
  }
}
const assetLocations = ref([])
const assetLocationTree = ref([])
const assetLocationProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  checkStrictly: true,
  emitPath: false,
  checkStrategy: 'leaf',
  filterOption: (node) => {
    return node.isLeaf
  }
}
const assetStatusOptions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增资产')
const form = ref({
  id: '',
  name: '',
  code: '',
  typeId: [],
  locationId: [],
  specification: '',
  model: '',
  manufacturer: '',
  supplier: '',
  purchaseDate: '',
  price: '',
  status: '',
  remark: '',
  image: '',
  labelType: '',
  labelCode: '',
  adminUserId: '',
  userId: '',
  deptId: '',
  sn: '',
  source: '',
  currentValue: '',
  unit: ''
})

// 加载资产列表
const loadAssetList = async () => {
  try {
    const assets = await api.get('/asset')
    
    // 加载资产类型、位置和状态信息
    await loadAssetTypes()
    await loadAssetLocations()
    await loadAssetStatus()
    
    // 为每个资产添加类型、位置和状态名称
    assetList.value = assets.map(asset => {
      const type = assetTypes.value.find(t => t.id === asset.typeId)
      const location = assetLocations.value.find(l => l.id === asset.locationId)
      const statusOption = assetStatusOptions.value.find(option => option.value === asset.status)
      return {
        ...asset,
        typeName: type ? type.name : '',
        locationName: location ? location.name : '',
        statusName: statusOption ? statusOption.label : asset.status
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
    // 获取树形结构数据
    const treeData = await api.get('/asset-type/tree')
    // 处理树形数据，添加isLeaf字段
    const processTree = (nodes) => {
      return nodes.map(node => {
        const processedNode = { ...node }
        processedNode.isLeaf = !processedNode.children || processedNode.children.length === 0
        if (processedNode.children) {
          processedNode.children = processTree(processedNode.children)
        }
        return processedNode
      })
    }
    assetTypeTree.value = processTree(treeData)
    
    // 同时获取扁平化列表用于其他操作
    assetTypes.value = await api.get('/asset-type')
  } catch (error) {
    console.error('加载资产类型失败:', error)
  }
}

// 加载资产位置列表
const loadAssetLocations = async () => {
  try {
    // 获取树形结构数据
    const treeData = await api.get('/asset-location/tree')
    // 处理树形数据，添加isLeaf字段
    const processTree = (nodes) => {
      return nodes.map(node => {
        const processedNode = { ...node }
        processedNode.isLeaf = !processedNode.children || processedNode.children.length === 0
        if (processedNode.children) {
          processedNode.children = processTree(processedNode.children)
        }
        return processedNode
      })
    }
    assetLocationTree.value = processTree(treeData)
    
    // 同时获取扁平化列表用于其他操作
    assetLocations.value = await api.get('/asset-location')
  } catch (error) {
    console.error('加载资产位置失败:', error)
  }
}

// 加载资产状态字典数据
const loadAssetStatus = async () => {
  try {
    const response = await api.get('/dict/list', {
      params: {
        page: 1,
        pageSize: 100
      }
    })
    // 过滤出资产状态类型的字典
    assetStatusOptions.value = response.list
      .filter(item => item.type === 'asset_status')
      .map(item => ({
        label: item.value,
        value: item.code
      }))
  } catch (error) {
    console.error('加载资产状态失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增资产'
  form.value = {
    id: '',
    name: '',
    typeId: [],
    locationId: [],
    specification: '',
    model: '',
    manufacturer: '',
    supplier: '',
    purchaseDate: '',
    price: '',
    status: '',
    remark: '',
    image: '',
    labelType: '',
    labelCode: '',
    adminUserId: '',
    userId: '',
    deptId: '',
    sn: '',
    source: '',
    currentValue: '',
    unit: ''
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  dialogTitle.value = '编辑资产'
  const formData = { ...row }
  // 将typeId转换为数组格式，以适应el-cascader
  formData.typeId = row.typeId ? [row.typeId] : []
  // 将locationId转换为数组格式，以适应el-cascader
  formData.locationId = row.locationId ? [row.locationId] : []
  form.value = formData
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    // 处理表单数据，将typeId和locationId从数组转换为单个值
    const formData = { ...form.value }
    // 如果typeId是数组且有值，取第一个元素作为typeId
    if (Array.isArray(formData.typeId) && formData.typeId.length > 0) {
      formData.typeId = formData.typeId[0]
    } else {
      formData.typeId = null
    }
    // 如果locationId是数组且有值，取第一个元素作为locationId
    if (Array.isArray(formData.locationId) && formData.locationId.length > 0) {
      formData.locationId = formData.locationId[0]
    } else {
      formData.locationId = null
    }
    
    if (form.value.id) {
      // 编辑
      await api.put('/asset', formData)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/asset', formData)
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