<template>
  <div class="material-category-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物资分类管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>

      <el-tree
        :data="treeData"
        :props="defaultProps"
        node-key="id"
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
              <el-button size="small" type="danger" @click.stop="deleteCategory(data.id)">
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
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入物资分类名称" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" placeholder="请选择父分类">
            <el-option label="根节点" :value="0" />
            <el-option
              v-for="item in flatList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
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
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const treeData = ref([])
const flatList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增物资分类')
const form = ref({
  id: '',
  name: '',
  parentId: 0,
  remark: ''
})

const defaultProps = {
  children: 'children',
  label: 'name'
}

// 加载树形数据
const loadTreeData = async () => {
  try {
    const response = await api.get('/material-category/tree')
    treeData.value = response
  } catch (error) {
    ElMessage.error('加载物资分类失败')
    console.error('加载物资分类失败:', error)
  }
}

// 加载平铺列表（用于父分类选择）
const loadFlatList = async () => {
  try {
    const response = await api.get('/material-category')
    flatList.value = response
  } catch (error) {
    console.error('加载物资分类列表失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增物资分类'
  form.value = {
    id: '',
    name: '',
    parentId: 0,
    remark: ''
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (data) => {
  dialogTitle.value = '编辑物资分类'
  form.value = { ...data }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    if (form.value.id) {
      // 编辑
      await api.put('/material-category', form.value)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/material-category', form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadTreeData()
    loadFlatList()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

// 删除物资分类
const deleteCategory = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/material-category/${id}`)
    ElMessage.success('删除成功')
    loadTreeData()
    loadFlatList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
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
  loadFlatList()
})
</script>

<style scoped>
.material-category-container {
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
