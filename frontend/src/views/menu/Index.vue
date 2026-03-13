<template>
  <div class="menu-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" @click="handleAdd">添加菜单</el-button>
        </div>
      </template>
      <div class="tree-container">
        <el-tree
          :data="menuTree"
          :props="menuTreeProps"
          node-key="id"
          default-expand-all
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <span>{{ data.name }}</span>
              <span class="node-info">{{ data.type === 'menu' ? data.path || '' : data.code || '' }}</span>
              <span class="node-actions">
                <el-button size="small" @click="handleEdit(data)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(data.id)">删除</el-button>
              </span>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>
    
    <!-- 添加/编辑菜单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="父菜单" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父菜单">
            <el-option
              v-for="option in parentMenuOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="菜单" value="menu" />
            <el-option label="操作" value="operation" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 'menu'" label="路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路径" />
        </el-form-item>
        <el-form-item v-if="form.type === 'menu'" label="组件" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item v-if="form.type === 'operation'" label="权限码" prop="code">
          <el-input v-model="form.code" placeholder="请输入权限码" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" placeholder="请输入排序号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import api from '@/api'

const dialogVisible = ref(false)
const dialogTitle = ref('添加菜单')
const formRef = ref()
const menuTree = ref([])
const allMenus = ref([])

const menuTreeProps = {
  children: 'children',
  label: 'name'
}

const form = reactive({
  id: '',
  name: '',
  path: '',
  component: '',
  parentId: 0,
  icon: '',
  code: '',
  type: 'menu',
  orderNum: 0,
  status: true
})

const rules = {
  name: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ]
}

const parentMenuOptions = computed(() => {
  return [
    { label: '顶级菜单', value: 0 },
    ...allMenus.value.map((menu: any) => ({
      label: menu.name,
      value: menu.id
    }))
  ]
})

const getParentName = (parentId: number) => {
  if (parentId === 0) return '顶级菜单'
  const parent = allMenus.value.find((menu: any) => menu.id === parentId)
  return parent ? parent.name : '未知'
}

// 构建菜单树
const buildMenuTree = (menus: any[]) => {
  const menuMap = new Map()
  const tree: any[] = []

  // 先将所有菜单放入Map中
  menus.forEach(menu => {
    menuMap.set(menu.id, { ...menu, children: [] })
  })

  // 构建树结构
  menus.forEach(menu => {
    const menuNode = menuMap.get(menu.id)
    if (menu.parentId === 0) {
      // 顶级菜单
      tree.push(menuNode)
    } else {
      // 子菜单
      const parentNode = menuMap.get(menu.parentId)
      if (parentNode) {
        parentNode.children.push(menuNode)
      }
    }
  })

  return tree
}

const fetchMenus = async () => {
  try {
    const response = await api.get('/menu/list')
    allMenus.value = response
    menuTree.value = buildMenuTree(response)
  } catch (error) {
    console.error('获取菜单列表失败:', error)
  }
}

const fetchAllMenus = async () => {
  try {
    const response = await api.get('/menu/list')
    allMenus.value = response
  } catch (error) {
    console.error('获取所有菜单失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加菜单'
  Object.assign(form, {
    id: '',
    name: '',
    path: '',
    component: '',
    parentId: 0,
    icon: '',
    code: '',
    type: 'menu',
    orderNum: 0,
    status: true
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (id: string) => {
  try {
    await api.delete(`/menu/${id}`)
    fetchMenus()
    fetchAllMenus()
  } catch (error) {
    console.error('删除菜单失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.id) {
          await api.put('/menu', form)
        } else {
          await api.post('/menu', form)
        }
        dialogVisible.value = false
        fetchMenus()
        fetchAllMenus()
      } catch (error) {
        console.error('保存菜单失败:', error)
      }
    }
  })
}



onMounted(() => {
  fetchMenus()
  fetchAllMenus()
})
</script>

<style scoped>
.menu-container {
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
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

.tree-container {
  max-height: 600px;
  overflow-y: auto;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 4px 0;
}

.node-info {
  flex: 1;
  margin: 0 16px;
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.node-actions {
  display: flex;
  gap: 4px;
}
</style>