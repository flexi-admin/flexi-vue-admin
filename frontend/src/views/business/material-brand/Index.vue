<template>
  <div class="material-brand-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物资品类管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="品类名称" />
        <el-table-column label="所属分类" width="150">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="sn" label="商品条码" />
        <el-table-column prop="specification" label="规格型号" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="packUnit" label="包装单位" />
        <el-table-column prop="packQuantity" label="包装数量" />
        <el-table-column prop="packBarcode" label="包装条码" />
        <el-table-column prop="brand" label="品牌" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteBrand(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="所属分类">
          <el-select v-model="form.categoryId" placeholder="请选择物资分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="品类名称">
          <el-input v-model="form.name" placeholder="请输入品类名称" />
        </el-form-item>
        <el-form-item label="商品条码">
          <el-input v-model="form.sn" placeholder="请输入商品条码" />
        </el-form-item>
        <el-form-item label="规格型号">
          <el-input v-model="form.specification" placeholder="请输入规格型号" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="包装单位">
          <el-input v-model="form.packUnit" placeholder="请输入包装单位" />
        </el-form-item>
        <el-form-item label="包装数量">
          <el-input v-model.number="form.packQuantity" type="number" placeholder="请输入包装数量" />
        </el-form-item>
        <el-form-item label="包装条码">
          <el-input v-model="form.packBarcode" placeholder="请输入包装条码" />
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model.number="form.price" type="number" step="0.01" placeholder="请输入价格" />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const tableData = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增物资品类')
const form = ref({
  id: '',
  categoryId: '',
  name: '',
  sn: '',
  specification: '',
  unit: '',
  packUnit: '',
  packQuantity: 0,
  packBarcode: '',
  brand: '',
  price: 0,
  remark: '',
  status: 1
})

// 加载列表数据
const loadData = async () => {
  try {
    const response = await api.get('/material-brand')
    tableData.value = response
  } catch (error) {
    ElMessage.error('加载物资品类失败')
    console.error('加载物资品类失败:', error)
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

// 根据分类ID获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未知分类'
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增物资品类'
  form.value = {
    id: '',
    categoryId: '',
    name: '',
    sn: '',
    specification: '',
    unit: '',
    packUnit: '',
    packQuantity: 0,
    packBarcode: '',
    brand: '',
    price: 0,
    remark: '',
    status: 1
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (data) => {
  dialogTitle.value = '编辑物资品类'
  form.value = { ...data }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    if (form.value.id) {
      // 编辑
      await api.put('/material-brand', form.value)
      ElMessage.success('编辑成功')
    } else {
      // 新增
      await api.post('/material-brand', form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('操作失败:', error)
  }
}

// 删除物资品类
const deleteBrand = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该品类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/material-brand/${id}`)
    ElMessage.success('删除成功')
    loadData()
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
})
</script>

<style scoped>
.material-brand-container {
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
