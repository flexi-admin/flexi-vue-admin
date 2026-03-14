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
        <el-table-column prop="supplierName" label="供应商" />
        <el-table-column prop="sourceName" label="资产来源" />
        <el-table-column prop="unitName" label="计量单位" />
        <el-table-column prop="adminUserName" label="管理员" />
        <el-table-column prop="userName" label="使用人" />
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="statusName" label="状态" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button size="small" type="primary" @click="copyAsset(row)">
              复制
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
              <el-select 
                v-model="form.supplierId" 
                placeholder="请选择供应商"
                filterable
                remote
                :remote-method="searchSuppliers"
                :loading="supplierLoading"
              >
                <el-option 
                  v-for="supplier in suppliers" 
                  :key="supplier.id" 
                  :label="supplier.name" 
                  :value="supplier.id" 
                />
              </el-select>
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
              <ImageSelector v-model="form.image" />
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
            <el-form-item label="管理员">
              <el-select 
                v-model="form.adminUserId" 
                placeholder="请选择管理员" 
                filterable
                remote
                :remote-method="remoteMethod"
                :loading="userLoading"
              >
                <el-option 
                  v-for="user in users" 
                  :key="user.value" 
                  :label="user.label" 
                  :value="user.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="使用人">
              <el-select 
                v-model="form.userId" 
                placeholder="请选择使用人" 
                filterable
                remote
                :remote-method="remoteMethod"
                :loading="userLoading"
              >
                <el-option 
                  v-for="user in users" 
                  :key="user.value" 
                  :label="user.label" 
                  :value="user.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="部门">
              <el-cascader
                v-model="form.deptId"
                :options="deptTree"
                :props="deptProps"
                placeholder="请选择部门"
              />
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
                <el-option 
                  v-for="option in assetSourceOptions" 
                  :key="option.value" 
                  :label="option.label" 
                  :value="option.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计量单位">
              <el-select v-model="form.unit" placeholder="请选择计量单位">
                <el-option 
                  v-for="option in assetUnitOptions" 
                  :key="option.value" 
                  :label="option.label" 
                  :value="option.value" 
                />
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
import { ref, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { useConfigStore } from '@/stores/config'
import ImageSelector from '@/components/ImageSelector.vue'

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
const assetSourceOptions = ref([])
const assetUnitOptions = ref([])
const suppliers = ref([])
const users = ref([])
const depts = ref([])
const deptTree = ref([])
const deptProps = {
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
const supplierLoading = ref(false)
const userLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增资产')


// 配置store
const configStore = useConfigStore()
// 图片基础路径
const imageBaseUrl = computed(() => configStore.imageBaseUrl)
const form = ref({
  id: '',
  name: '',
  code: '',
  typeId: [],
  locationId: [],
  specification: '',
  model: '',
  manufacturer: '',
  supplierId: '',
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
    // 直接使用后端返回的带有关联数据的资产列表
    const assets = await api.get('/asset')
    // 处理资产图片路径，确保格式正确
    assetList.value = assets.map(asset => {
      if (asset.image) {
        asset.image = asset.image.startsWith('/') ? imageBaseUrl.value + asset.image : imageBaseUrl.value + '/' + asset.image
      }
      return asset
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
  } catch (error) {
    console.error('加载资产位置失败:', error)
  }
}

// 加载资产相关字典数据
const loadAssetDicts = async () => {
  try {
    // 一次性请求资产相关的字典类型
    const response = await api.get('/dict/list-by-type', {
      params: {
        type: 'asset_status,asset_source,asset_unit'
      }
    })
    
    // 处理资产状态类型的字典
    assetStatusOptions.value = response
      .filter(item => item.type === 'asset_status')
      .map(item => ({
        label: item.value,
        value: item.code
      }))
    // 处理资产来源类型的字典
    assetSourceOptions.value = response
      .filter(item => item.type === 'asset_source')
      .map(item => ({
        label: item.value,
        value: item.code
      }))
    // 处理计量单位类型的字典
    assetUnitOptions.value = response
      .filter(item => item.type === 'asset_unit')
      .map(item => ({
        label: item.value,
        value: item.code
      }))
  } catch (error) {
    console.error('加载资产字典数据失败:', error)
  }
}

// 加载供应商列表
const loadSuppliers = async () => {
  try {
    const response = await api.get('/asset-supplier')
    suppliers.value = response
  } catch (error) {
    console.error('加载供应商失败:', error)
  }
}



// 搜索供应商
const searchSuppliers = async (query) => {
  if (query) {
    supplierLoading.value = true
    try {
      // 这里简化处理，直接在前端过滤
      // 实际项目中应该调用后端接口进行搜索
      const response = await api.get('/asset-supplier')
      suppliers.value = response.filter(supplier => 
        supplier.name.toLowerCase().includes(query.toLowerCase()) ||
        supplier.contactPerson.toLowerCase().includes(query.toLowerCase()) ||
        supplier.contactPhone.includes(query)
      )
    } catch (error) {
      console.error('搜索供应商失败:', error)
    } finally {
      supplierLoading.value = false
    }
  } else {
    // 如果查询为空，加载所有供应商
    await loadSuppliers()
  }
}

// 加载用户列表
const loadUsers = async (keyword = '') => {
  try {
    const response = await api.get('/user/list', {
      params: {
        page: 1,
        pageSize: 100,
        keyword: keyword
      }
    })
    users.value = response.list.map(user => ({
      label: user.username,
      value: user.id
    }))
  } catch (error) {
    console.error('加载用户失败:', error)
  }
}

// 远程搜索用户
const remoteMethod = async (query) => {
  if (query !== '') {
    userLoading.value = true
    await loadUsers(query)
    userLoading.value = false
  } else {
    users.value = []
  }
}

// 加载部门列表
const loadDepts = async () => {
  try {
    // 获取树形结构数据
    const treeData = await api.get('/dept/tree')
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
    deptTree.value = processTree(treeData)
  } catch (error) {
    console.error('加载部门失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = async () => {
  dialogTitle.value = '新增资产'
  
  // 确保资产类型和位置的树形数据已经加载
  if (assetTypeTree.value.length === 0) {
    await loadAssetTypes()
  }
  if (assetLocationTree.value.length === 0) {
    await loadAssetLocations()
  }
  // 确保字典数据已经加载
  if (assetStatusOptions.value.length === 0) {
    await loadAssetDicts()
  }
  // 确保用户和部门数据已经加载
  if (users.value.length === 0) {
    await loadUsers()
  }
  if (depts.value.length === 0) {
    await loadDepts()
  }
  
  form.value = {
    id: '',
    name: '',
    typeId: [],
    locationId: [],
    specification: '',
    model: '',
    manufacturer: '',
    supplierId: '',
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
const openEditDialog = async (row) => {
  dialogTitle.value = '编辑资产'
  
  // 确保资产类型和位置的树形数据已经加载
  if (assetTypeTree.value.length === 0) {
    await loadAssetTypes()
  }
  if (assetLocationTree.value.length === 0) {
    await loadAssetLocations()
  }
  // 确保字典数据已经加载
  if (assetStatusOptions.value.length === 0) {
    await loadAssetDicts()
  }
  // 确保用户和部门数据已经加载
  if (users.value.length === 0) {
    await loadUsers()
  }
  if (depts.value.length === 0) {
    await loadDepts()
  }
  
  const formData = { ...row }
  // 将typeId转换为数组格式，以适应el-cascader
  formData.typeId = row.typeId ? [row.typeId] : []
  // 将locationId转换为数组格式，以适应el-cascader
  formData.locationId = row.locationId ? [row.locationId] : []
  form.value = formData
  dialogVisible.value = true
}

// 复制资产
const copyAsset = async (row) => {
  dialogTitle.value = '新增资产（复制）'
  
  // 确保资产类型和位置的树形数据已经加载
  if (assetTypeTree.value.length === 0) {
    await loadAssetTypes()
  }
  if (assetLocationTree.value.length === 0) {
    await loadAssetLocations()
  }
  // 确保字典数据已经加载
  if (assetStatusOptions.value.length === 0) {
    await loadAssetDicts()
  }
  // 确保用户和部门数据已经加载
  if (users.value.length === 0) {
    await loadUsers()
  }
  if (depts.value.length === 0) {
    await loadDepts()
  }
  
  // 复制资产数据，清除ID、编码和标签编码
  const formData = { ...row }
  formData.id = ''
  formData.code = ''
  formData.labelCode = ''
  // 由于el-cascader的emitPath设置为false，直接使用数字值
  formData.typeId = row.typeId || ''
  formData.locationId = row.locationId || ''
  form.value = formData
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    // 处理表单数据，将typeId和locationId从数组转换为单个值
    const formData = { ...form.value }
    // 处理typeId：如果是数组且有值，取第一个元素；如果是数字，直接使用；否则设置为null
    if (Array.isArray(formData.typeId) && formData.typeId.length > 0) {
      formData.typeId = formData.typeId[0]
    } else if (typeof formData.typeId === 'number') {
      // 已经是数字，直接使用
    } else {
      formData.typeId = null
    }
    // 处理locationId：如果是数组且有值，取第一个元素；如果是数字，直接使用；否则设置为null
    if (Array.isArray(formData.locationId) && formData.locationId.length > 0) {
      formData.locationId = formData.locationId[0]
    } else if (typeof formData.locationId === 'number') {
      // 已经是数字，直接使用
    } else {
      formData.locationId = null
    }
    // 处理deptId：如果是数组且有值，取第一个元素；如果是数字，直接使用；否则设置为null
    if (Array.isArray(formData.deptId) && formData.deptId.length > 0) {
      formData.deptId = formData.deptId[0]
    } else if (typeof formData.deptId === 'number') {
      // 已经是数字，直接使用
    } else {
      formData.deptId = null
    }
    // 如果supplierId为空，设置为null
    if (!formData.supplierId) {
      formData.supplierId = null
    }
    // 如果adminUserId为空，设置为null
    if (!formData.adminUserId) {
      formData.adminUserId = null
    }
    // 如果userId为空，设置为null
    if (!formData.userId) {
      formData.userId = null
    }
    // 如果deptId为空，设置为null
    if (!formData.deptId) {
      formData.deptId = null
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
onMounted(async () => {
  // 加载配置
  await configStore.loadConfig()
  // 加载资产列表
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