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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getAssetMyList } from '@/api';
import { useConfigStore } from '@/stores/config';

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