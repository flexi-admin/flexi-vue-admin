<template>
  <div class="asset-location-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产位置管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>
      
      <el-tree
        :data="treeData"
        :props="defaultProps"
        node-key="id"
        :default-expand-all="true"
        @node-click="handleNodeClick"
        @node-contextmenu="handleContextMenu"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span>{{ data.name }}</span>
            <span class="node-actions">
              <el-button size="small" @click.stop="openEditDialog(data)">
                编辑
              </el-button>
              <el-button size="small" type="danger" @click.stop="deleteLocation(data.id)">
                删除
              </el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入资产位置名称" />
        </el-form-item>
        <el-form-item label="父位置">
          <el-select v-model="form.parentId" placeholder="请选择父位置" filterable>
            <el-option
              v-for="option in parentOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
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
import { ref, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { getParentOptions } from '@/utils/treeUtils'

// 计算所有可选的父位置选项
const parentOptions = computed(() => {
  return getParentOptions(treeData.value, currentEditingId.value)
})

const treeData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增资产位置')
const formRef = ref(null)

const rules = {
  name: [
    { required: true, message: '请输入资产位置名称', trigger: 'blur' }
  ]
}

const form = ref({
  id: '',
  name: '',
  parentId: 0,
  remark: ''
})

const currentEditingId = ref('')

const defaultProps = {
  children: 'children',
  label: 'name'
}

// 加载树形数据
const loadTreeData = async () => {
  try {
    const response = await api.get('/asset-location/tree')
    treeData.value = response
  } catch (error) {
    ElMessage.error('加载资产位置失败')
    console.error('加载资产位置失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增资产位置'
  form.value = {
    id: '',
    name: '',
    parentId: 0,
    remark: ''
  }
  // 重置当前编辑ID
  currentEditingId.value = ''
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = async (data) => {
  dialogTitle.value = '编辑资产位置'
  // 确保treeData已经加载
  if (treeData.value.length === 0) {
    await loadTreeData()
  }
  // 保存当前编辑的ID
  currentEditingId.value = data.id
  // 确保parentId是数字类型
  form.value = {
    ...data,
    parentId: Number(data.parentId)
  }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    // 表单验证
    await formRef.value.validate()
    
    if (form.value.id) {
      // 编辑
      await api.put('/asset-location', form.value)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/asset-location', form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadTreeData()
  } catch (error) {
    if (error !== 'Error: Submit Failed') {
      ElMessage.error('操作失败')
      console.error('操作失败:', error)
    }
  }
}

// 删除资产位置
const deleteLocation = async (id) => {
  try {
    await api.delete(`/asset-location/${id}`)
    ElMessage.success('删除成功')
    loadTreeData()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error('删除失败:', error)
  }
}

// 节点点击事件
const handleNodeClick = (data) => {
  console.log('点击节点:', data)
}

// 右键菜单事件
const handleContextMenu = (event, data) => {
  event.preventDefault()
  console.log('右键菜单:', data)
}

// 组件挂载时加载数据
onMounted(() => {
  loadTreeData()
})
</script>

<style scoped>
.asset-location-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-node {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.node-actions {
  visibility: hidden;
}

.tree-node:hover .node-actions {
  visibility: visible;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>