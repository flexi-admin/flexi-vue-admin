<template>
  <div class="asset-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产管理</span>
          <div>
            <el-button type="primary" @click="openAddDialog">
              <el-icon><Plus /></el-icon> 新增
            </el-button>
            <el-button type="info" @click="syncRfidTags">
              <el-icon><Refresh /></el-icon> 同步RFID标签
            </el-button>
            <el-button type="warning" @click="batchPrint">
              <el-icon><Printer /></el-icon> 批量打印
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="assetList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.showImage"
              :src="row.showImage"
              fit="cover"
              style="width: 60px; height: 60px"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="资产名称" />
        <el-table-column label="资产编码" width="230">
          <template #default="{ row }">
            <div>
              <div>{{ row.code }}</div>
              <div v-if="row.labelCode" style="font-size: 12px; color: #666;">标签{{ row.labelCode }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="资产类型" />
        <el-table-column prop="locationName" label="资产位置" />
        <el-table-column prop="specification" label="规格" />
        <el-table-column prop="model" label="型号" />
        <el-table-column prop="adminUserName" label="管理员" />
        <el-table-column prop="userName" label="使用人" />
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="statusName" label="状态" />
        <el-table-column label="操作" width="400">
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
            <el-button size="small" type="success" @click="printAsset(row)">
              打印
            </el-button>
            <el-button size="small" type="info" @click="syncSingleRfidTag(row)">
              同步RFID标签
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="supplierName" label="供应商" />
        <el-table-column prop="sourceName" label="资产来源" />
        <el-table-column prop="unitName" label="计量单位" />
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @update:page-size="handleSizeChange"
          @update:current-page="handleCurrentChange"
        />
      </div>
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
                v-model="form.typeCode"
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
                clearable
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
                clearable
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
                clearable
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
import { Plus, Refresh, Printer } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { useConfigStore } from '@/stores/config'
import ImageSelector from '@/components/ImageSelector.vue'

const assetList = ref([])
const assetTypes = ref([])
const assetTypeTree = ref([])
const assetTypeProps = {
  value: 'code',
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

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)


// 配置store
const configStore = useConfigStore()
// 图片基础路径
const imageBaseUrl = computed(() => configStore.imageBaseUrl)
const form = ref({
  id: '',
  name: '',
  code: '',
  typeCode: null,
  locationId: null,
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
    // 调用后端接口获取分页数据
    const response = await api.get('/asset', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    console.log('响应数据:', response)
    console.log('响应数据类型:', typeof response)
    console.log('响应数据是否有records属性:', 'records' in response)
    
    // 检查响应数据结构
    if (response && response.records) {
      // 处理资产图片路径，确保格式正确
      assetList.value = response.records.map(asset => {
        if (asset.image) {
          asset.showImage = imageBaseUrl.value + asset.image
        }
        return asset
      })
      // 更新总记录数
      total.value = response.total
    } else {
      // 如果响应数据没有records属性，可能是后端返回了旧格式的数据
      console.log('响应数据没有records属性，尝试作为旧格式处理')
      // 处理资产图片路径，确保格式正确
      assetList.value = response.map(asset => {
        if (asset.image) {
          asset.showImage = asset.image.startsWith('/') ? imageBaseUrl.value + asset.image : imageBaseUrl.value + '/' + asset.image
        }
        return asset
      })
      // 更新总记录数
      total.value = response.length
    }
  } catch (error) {
    ElMessage.error('加载资产列表失败')
    console.error('加载资产列表失败:', error)
  }
}

// 加载资产类型列表
const loadAssetTypes = async () => {
  try {
    // 获取树形结构数据
    const response = await api.get('/asset-type/tree')
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
    assetTypeTree.value = processTree(response)
  } catch (error) {
    console.error('加载资产类型失败:', error)
  }
}

// 加载资产位置列表
const loadAssetLocations = async () => {
  try {
    // 获取树形结构数据
    const response = await api.get('/asset-location/tree')
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
    assetLocationTree.value = processTree(response)
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
      label: user.nickname || user.username,
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
    typeCode: null,
    locationId: null,
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
  // 确保formData中包含typeCode而不是typeId
  if (formData.typeId) {
    formData.typeCode = formData.typeId
    delete formData.typeId
  }
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
  // 由于el-cascader的emitPath设置为false，直接使用值
  formData.typeCode = row.typeCode || row.typeId || ''
  formData.locationId = row.locationId || ''
  // 删除可能存在的typeId字段
  delete formData.typeId
  form.value = formData
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    // 处理表单数据，将typeCode和locationId从数组转换为单个值
    const formData = { ...form.value }
    // 处理typeCode：如果是数组且有值，取第一个元素；否则设置为null
    if (Array.isArray(formData.typeCode) && formData.typeCode.length > 0) {
      formData.typeCode = formData.typeCode[0]
    } else if (!formData.typeCode) {
      formData.typeCode = null
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

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadAssetList()
}

// 当前页码变化处理
const handleCurrentChange = (current) => {
  currentPage.value = current
  loadAssetList()
}

// 打印资产
const printAsset = async (row) => {
  try {
    // 准备请求数据
    const data = [{
      CardName: row.name,
      CardSerial: row.code
    }]
    
    // 发送POST请求
    const response = await fetch(configStore.printServiceUrl + '/print', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
    
    // 获取响应数据
    const result = await response.json()
    
    // 在控制台打印接口输出
    console.log('打印接口输出:', result)
    
    // 显示成功消息
    ElMessage.success('打印请求已发送')
  } catch (error) {
    // 显示错误消息
    ElMessage.error('打印请求失败')
    console.error('打印请求失败:', error)
  }
}

// 同步RFID标签
const syncRfidTags = async () => {
  try {
    // 显示加载中消息
    ElMessage({ 
      message: '正在同步RFID标签...', 
      type: 'info',
      duration: 3000,
      showClose: true
    })
    
    // 从服务器获取所有code = label_code的资产记录的code
    const codes = await api.get('/asset/without-label-code')
    
    if (codes.length === 0) {
      ElMessage.success('所有资产都已有RFID标签')
      return
    }
    
    // 发送POST请求到query接口
    const queryResponse = await fetch(configStore.printServiceUrl + '/query', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(codes)
    })
    
    // 获取响应数据
    const result = await queryResponse.json()
    
    // 在控制台打印接口输出
    console.log('同步RFID标签接口输出:', result)
    
    // 提交数据到服务器更新label_code
    if (Object.keys(result).length > 0) {
      try {
        await api.post('/asset/update-label-code', result)
        ElMessage.success(`成功同步并更新 ${Object.keys(result).length} 个RFID标签`)
      } catch (error) {
        ElMessage.error('更新RFID标签失败')
        console.error('更新RFID标签失败:', error)
      }
    } else {
      ElMessage.success('同步RFID标签成功，但没有需要更新的数据')
    }
  } catch (error) {
    // 显示错误消息
    ElMessage.error('同步RFID标签失败')
    console.error('同步RFID标签失败:', error)
  }
}

// 同步单个资产的RFID标签
const syncSingleRfidTag = async (row) => {
  try {
    // 显示加载中消息
    ElMessage({ 
      message: '正在同步RFID标签...', 
      type: 'info',
      duration: 3000,
      showClose: true
    })
    
    // 准备请求数据，以数组形式
    const codes = [row.code]
    
    // 发送POST请求到query接口
    const queryResponse = await fetch(configStore.printServiceUrl + '/query', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(codes)
    })
    
    // 获取响应数据
    const result = await queryResponse.json()
    
    // 在控制台打印接口输出
    console.log('同步单个RFID标签接口输出:', result)
    
    // 提交数据到服务器更新label_code
    if (Object.keys(result).length > 0) {
      try {
        await api.post('/asset/update-label-code', result)
        ElMessage.success(`成功同步并更新资产 ${row.name} 的RFID标签`)
      } catch (error) {
        ElMessage.error('更新RFID标签失败')
        console.error('更新RFID标签失败:', error)
      }
    } else {
      ElMessage.success(`同步资产 ${row.name} 的RFID标签成功，但没有需要更新的数据`)
    }
  } catch (error) {
    // 显示错误消息
    ElMessage.error('同步单个RFID标签失败')
    console.error('同步单个RFID标签失败:', error)
  }
}

// 批量打印
const batchPrint = async () => {
  try {
    // 先执行syncRfidTags的逻辑
    await syncRfidTags()
    
    // 使用新的接口获取批量打印数据
    const response = await api.get('/asset/batch-print-data')
    const data = response
    
    // 打印数据到控制台
    console.log('批量打印数据:', data)
    
    // 发送POST请求到打印服务
    const printResponse = await fetch(configStore.printServiceUrl + '/print', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
    
    // 获取响应数据
    const result = await printResponse.json()
    
    // 在控制台打印接口输出
    console.log('打印接口输出:', result)
    
    // 显示成功消息
    ElMessage.success(`批量打印请求已发送，共 ${data.length} 个资产标签`)
  } catch (error) {
    ElMessage.error('批量打印失败')
    console.error('批量打印失败:', error)
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

.card-header > div {
  display: flex;
  gap: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

</style>