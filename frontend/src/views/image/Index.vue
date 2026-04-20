<template>
  <div class="image-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图片管理</span>
        </div>
      </template>
      
      <!-- 上传组件 -->
      <div class="upload-buttons">
        <el-upload
          ref="uploadRef"
          action="/api/image/upload"
          :headers="{ Authorization: 'Bearer ' + token }"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :show-file-list="false"
          accept="image/*"
        >
          <el-button type="primary">上传到本地</el-button>
        </el-upload>
        <el-upload
          ref="uploadOssRef"
          action="/api/image/upload-oss"
          :headers="{ Authorization: 'Bearer ' + token }"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :show-file-list="false"
          accept="image/*"
        >
          <el-button type="success">上传到阿里云</el-button>
        </el-upload>
      </div>
      
      <!-- 图片列表 -->
      <el-table :data="imageList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片预览" width="120">
          <template #default="{ row }">
            <el-image
              :src="row.filePath?.startsWith('http') ? row.filePath : imageBaseUrl + row.filename"
              fit="cover"
              style="width: 60px; height: 60px; cursor: pointer"
              @click="handleImagePreview(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="originalFilename" label="原始文件名" />
        <el-table-column prop="fileSize" label="文件大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileType" label="文件类型" width="120" />
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @update:page-size="handlePageSizeChange"
          @update:current-page="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 图片预览弹窗 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="图片预览"
      width="80%"
      center
    >
      <div class="preview-container">
        <el-image
          :src="previewImageUrl"
          fit="contain"
          style="width: 100%; height: 100%"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useUserStore } from '@/stores/user';
import { useConfigStore } from '@/stores/config';
import { ElMessage } from 'element-plus';
import api from '@/api';

const userStore = useUserStore();
const configStore = useConfigStore();
const token = computed(() => userStore.token);
const imageBaseUrl = computed(() => configStore.imageBaseUrl);

const uploadRef = ref(null);
const uploadOssRef = ref(null);
const imageList = ref([]);
const total = ref(0);
const page = ref(1);
const size = ref(10);
const previewDialogVisible = ref(false);
const previewImageUrl = ref('');

// 处理图片预览
const handleImagePreview = (row) => {
  previewImageUrl.value = row.filePath?.startsWith('http') ? row.filePath : imageBaseUrl.value + row.filename;
  previewDialogVisible.value = true;
};

// 上传成功处理
const handleUploadSuccess = (response) => {
  ElMessage.success('图片上传成功');
  getImageList();
};

// 上传失败处理
const handleUploadError = () => {
  ElMessage.error('图片上传失败');
};

// 获取图片列表
const getImageList = async () => {
  try {
    const response = await api.get('/image/list', {
      params: { page: page.value, size: size.value }
    });
    imageList.value = response.records;
    total.value = response.total;
  } catch (error) {
    ElMessage.error('获取图片列表失败');
  }
};

// 删除图片
const handleDelete = async (id) => {
  try {
    await api.delete(`/image/${id}`);
    ElMessage.success('图片删除成功');
    getImageList();
  } catch (error) {
    ElMessage.error('图片删除失败');
  }
};

// 分页处理
const handlePageSizeChange = (newSize) => {
  size.value = newSize;
  getImageList();
};

const handlePageChange = (newPage) => {
  page.value = newPage;
  getImageList();
};

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB';
  } else {
    return (size / (1024 * 1024)).toFixed(2) + ' MB';
  }
};

// 页面加载时获取图片列表
onMounted(async () => {
  // 加载配置
  await configStore.loadConfig();
  // 获取图片列表
  getImageList();
});
</script>

<style scoped>
.image-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.preview-container {
  width: 100%;
  height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.preview-container img {
  max-width: 100%;
  max-height: 100%;
}
</style>
