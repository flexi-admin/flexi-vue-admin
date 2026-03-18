<template>
  <div class="asset-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的资产</span>
        </div>
      </template>
      
      <el-form :inline="true" class="mb-4" size="small">
        <el-form-item label="资产名称">
          <el-input v-model="searchForm.name" placeholder="请输入资产名称" style="width: 200px;"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="assetList" style="width: 100%">
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
        <el-table-column prop="name" label="资产名称" width="180"></el-table-column>
        <el-table-column prop="code" label="资产编码" width="180"></el-table-column>
        <el-table-column prop="typeName" label="资产类型" width="120"></el-table-column>
        <el-table-column prop="locationName" label="存放位置" width="120"></el-table-column>
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购买日期" width="120"></el-table-column>
        <el-table-column prop="price" label="价格" width="100" :formatter="formatPrice"></el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openDetailDialog(row.id)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 资产详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="资产详情"
      width="60%"
    >
      <el-form :model="assetDetail" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产名称">
              <el-input v-model="assetDetail.name" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产编码">
              <el-input v-model="assetDetail.code" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产类型">
              <el-input v-model="assetDetail.typeName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存放位置">
              <el-input v-model="assetDetail.locationName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-input v-model="assetDetail.statusName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购买日期">
              <el-input v-model="assetDetail.purchaseDate" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格">
              <el-input :value="`¥${assetDetail.price?.toFixed(2) || '0.00'}`" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用人">
              <el-input v-model="assetDetail.userName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门">
              <el-input v-model="assetDetail.deptName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="管理员">
              <el-input v-model="assetDetail.adminUserName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="assetDetail.remark" type="textarea" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getAssetMyList } from '@/api';
import { useConfigStore } from '@/stores/config';
import api from '@/api';

const configStore = useConfigStore();
const imageBaseUrl = computed(() => configStore.imageBaseUrl);

interface Asset {
  id: number;
  name: string;
  code: string;
  typeName: string;
  locationName: string;
  status: string;
  statusName: string;
  purchaseDate: string;
  price: number;
  remark: string;
  image?: string;
  showImage?: string;
}

const assetList = ref<Asset[]>([]);
const searchForm = ref({
  name: ''
});
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 详情对话框
const detailDialogVisible = ref(false);
const assetDetail = ref({
  id: 0,
  name: '',
  code: '',
  typeName: '',
  locationName: '',
  status: '',
  statusName: '',
  purchaseDate: '',
  price: 0,
  remark: '',
  userName: '',
  deptName: '',
  adminUserName: ''
});

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    'in_use': 'success',
    'idle': 'info',
    'repairing': 'warning',
    'scrapped': 'danger'
  };
  return statusMap[status] || 'info';
};

const formatPrice = (row: any, column: any, cellValue: number) => {
  return `¥${cellValue.toFixed(2)}`;
};

const handleSearch = () => {
  loadAssetList();
};

const resetForm = () => {
  searchForm.value = {
    name: ''
  };
  loadAssetList();
};

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size;
  loadAssetList();
};

const handleCurrentChange = (current: number) => {
  pagination.value.currentPage = current;
  loadAssetList();
};

const loadAssetList = async () => {
  try {
    const response = await getAssetMyList({
      page: pagination.value.currentPage,
      size: pagination.value.pageSize
    });
    // 处理资产图片路径，确保格式正确
    assetList.value = response.records.map(asset => {
      if (asset.image) {
        asset.showImage = imageBaseUrl.value + asset.image
      }
      return asset
    });
    pagination.value.total = response.total;
  } catch (error) {
    ElMessage.error('获取资产列表失败');
    console.error('Error loading asset list:', error);
  }
};

// 打开详情对话框
const openDetailDialog = async (id: number) => {
  try {
    // 获取资产详情
    const response = await api.get(`/asset/${id}`);
    assetDetail.value = response;
    detailDialogVisible.value = true;
  } catch (error) {
    ElMessage.error('获取资产详情失败');
    console.error('Error loading asset detail:', error);
  }
};

onMounted(() => {
  loadAssetList();
});
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>