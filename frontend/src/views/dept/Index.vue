<template>
  <div class="dept-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增部门
          </el-button>
        </div>
      </template>
      <div class="tree-container">
        <el-tree
          :data="deptTree"
          :props="deptTreeProps"
          node-key="id"
          default-expand-all
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <span>{{ data.name }}</span>
              <span class="node-info">{{ data.leader || '' }}</span>
              <span class="node-actions">
                <el-button size="small" @click="handleEdit(data)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(data.id)">删除</el-button>
              </span>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="父部门" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父部门">
            <el-option
              v-for="option in parentDeptOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input v-model.number="form.orderNum" placeholder="请输入排序" type="number" />
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
import { ref, onMounted, computed } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import api from '@/api'
import type { FormInstance } from 'element-plus'

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const formRef = ref()

// 部门数据
const deptTree = ref<any[]>([])
const allDepts = ref<any[]>([])

// 树形配置
const deptTreeProps = {
  children: 'children',
  label: 'name'
}

// 表单数据
const form = ref({
  id: undefined,
  name: '',
  parentId: 0,
  leader: '',
  phone: '',
  email: '',
  status: true,
  orderNum: 0
})

// 父部门选项
const parentDeptOptions = computed(() => {
  return [
    { label: '顶级部门', value: 0 },
    ...allDepts.value.map((dept: any) => ({
      label: dept.name,
      value: dept.id
    }))
  ]
})

// 构建部门树
const buildDeptTree = (depts: any[]) => {
  const deptMap = new Map()
  const tree: any[] = []

  // 先将所有部门放入Map中
  depts.forEach(dept => {
    deptMap.set(dept.id, { ...dept, children: [] })
  })

  // 构建树结构
  depts.forEach(dept => {
    const deptNode = deptMap.get(dept.id)
    if (dept.parentId === 0) {
      // 顶级部门
      tree.push(deptNode)
    } else {
      // 子部门
      const parentNode = deptMap.get(dept.parentId)
      if (parentNode) {
        parentNode.children.push(deptNode)
      }
    }
  })

  return tree
}

// 加载部门列表
const loadDeptList = async () => {
  try {
    const response = await api.get('/dept/list')
    allDepts.value = response
    deptTree.value = buildDeptTree(response)
  } catch (error) {
    console.error('加载部门列表失败:', error)
  }
}

// 新增部门
const handleAdd = () => {
  dialogTitle.value = '新增部门'
  form.value = {
    id: undefined,
    name: '',
    parentId: 0,
    leader: '',
    phone: '',
    email: '',
    status: true,
    orderNum: 0
  }
  dialogVisible.value = true
}

// 编辑部门
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑部门'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除部门
const handleDelete = async (id: number) => {
  try {
    await api.delete(`/dept/${id}`)
    await loadDeptList()
  } catch (error) {
    console.error('删除部门失败:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    if (form.value.id) {
      // 编辑
      await api.put('/dept', form.value)
    } else {
      // 新增
      await api.post('/dept', form.value)
    }
    dialogVisible.value = false
    await loadDeptList()
  } catch (error) {
    console.error('保存部门失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadDeptList()
})
</script>

<style scoped>
.dept-container {
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
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
