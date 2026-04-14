<template>
  <div>
    <!-- 图片预览 -->
    <div v-if="modelValue" class="image-preview">
      <el-image
        :src="modelValue.startsWith('http') ? modelValue : imageBaseUrl + modelValue"
        fit="cover"
        style="width: 100px; height: 100px"
      />
      <el-button type="danger" size="small" @click="removeImage" style="margin-top: 10px">移除图片</el-button>
    </div>
    <!-- 选择按钮 -->
    <div v-else>
      <el-button type="primary" @click="openImageLibrary">选择图片</el-button>
      <el-button type="success" @click="openUploadDialog" style="margin-left: 10px">上传图片</el-button>
    </div>
    
    <!-- 图片上传弹窗 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传图片"
      width="600px"
    >
      <div style="margin-bottom: 20px">
        <el-radio-group v-model="uploadTarget">
          <el-radio :value="'local'">上传到本地</el-radio>
          <el-radio :value="'oss'">上传到阿里云OSS</el-radio>
        </el-radio-group>
      </div>
      <el-upload
        class="upload-demo"
        :action="currentUploadUrl"
        :headers="uploadHeaders"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :file-list="fileList"
        :auto-upload="false"
        ref="uploadRef"
      >
        <template #trigger>
          <el-button type="default">选择文件</el-button>
        </template>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 JPG/PNG 文件，且不超过 2MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="$refs.uploadRef?.submit()">上传</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 图片库弹窗 -->
    <el-dialog
      v-model="imageLibraryVisible"
      title="选择图片"
      width="800px"
      :height="'80vh'"
      custom-class="image-library-dialog"
    >
      <div class="image-library">
        <el-input
          v-model="imageSearch"
          placeholder="搜索图片"
          prefix-icon="Search"
          style="margin-bottom: 20px"
          @keyup.enter="handleSearch"
          @input="debounceSearch"
        />
        <div class="image-grid">
          <div
            v-for="image in images"
            :key="image.id"
            class="image-item"
            @click="selectImage(image)"
          >
            <el-image
              :src="image.filePath?.startsWith('http') ? image.filePath : imageBaseUrl + image.url"
              fit="cover"
              style="width: 100px; height: 100px"
            />
            <div class="image-name">{{ image.name }}</div>
          </div>
          <div v-if="images.length === 0" class="no-images">
            暂无图片
          </div>
        </div>
        <!-- 分页控件 -->
        <div class="pagination-container" v-if="pageParams.total > 0">
          <el-pagination
            v-model:current-page="pageParams.current"
            v-model:page-size="pageParams.size"
            :total="pageParams.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="imageLibraryVisible = false">取消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive, ref as refReactive } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { useConfigStore } from '@/stores/config'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

// 配置store
const configStore = useConfigStore()
// 图片基础路径
const imageBaseUrl = computed(() => configStore.imageBaseUrl)

// 图片库相关
const imageLibraryVisible = ref(false)
const imageSearch = ref('')
const images = ref([])
const pageParams = reactive({
  current: 1,
  size: 10,
  total: 0
})
const sortOrder = ref('desc') // 默认倒序
const sortField = ref('id') // 默认按id排序

// 上传相关
const uploadDialogVisible = ref(false)
const fileList = ref([])
const uploadRef = ref()
const uploadTarget = ref('local') // local 或 oss
const uploadHeaders = ref({
  'Authorization': `Bearer ${localStorage.getItem('token')}`
})

// 计算当前上传URL
const currentUploadUrl = computed(() => {
  return uploadTarget.value === 'local' ? '/api/image/upload' : '/api/image/upload-oss'
})

// 打开图片库
const openImageLibrary = async () => {
  // 确保配置已加载
  if (!configStore.loaded) {
    await configStore.loadConfig()
  }
  // 加载图片库数据
  await loadImages()
  imageLibraryVisible.value = true
}

// 打开上传弹窗
const openUploadDialog = () => {
  uploadDialogVisible.value = true
}

// 处理上传成功
const handleUploadSuccess = async (response, uploadFile, uploadFiles) => {
  ElMessage.success('上传成功')
  // 关闭上传弹窗
  uploadDialogVisible.value = false
  // 清空文件列表
  fileList.value = []
  // 重新加载图片库数据
  await loadImages()
  // 打开图片库，让用户选择刚刚上传的图片
  imageLibraryVisible.value = true
}

// 处理上传失败
const handleUploadError = (error, uploadFile, uploadFiles) => {
  ElMessage.error('上传失败，请重试')
  console.error('上传失败:', error)
}

// 加载图片库数据
const loadImages = async () => {
  try {
    // 调用后端的图片管理接口获取图片数据
    const response = await api.get('/image/list', {
      params: {
        page: pageParams.current,
        size: pageParams.size,
        keyword: imageSearch.value,
        sortField: sortField.value,
        sortOrder: sortOrder.value
      }
    })
    // 处理图片数据，确保格式正确
    images.value = response.records.map(image => ({
      id: image.id,
      name: image.originalFilename,
      url: image.filename,
      filePath: image.filePath
    }))
    // 更新总条数
    pageParams.total = response.total
  } catch (error) {
    console.error('加载图片库失败:', error)
    ElMessage.error('加载图片库失败')
  }
}

// 选择图片
const selectImage = (image) => {
  // 检查URL是否以http开头，如果是则直接使用，否则使用filePath
  const selectedUrl = image.filePath?.startsWith('http') ? image.filePath : image.url
  emit('update:modelValue', selectedUrl)
  imageLibraryVisible.value = false
}

// 移除图片
const removeImage = () => {
  emit('update:modelValue', '')
}

// 分页事件处理
const handleSizeChange = (val: number) => {
  pageParams.size = val
  pageParams.current = 1 // 重置页码
  loadImages()
}

const handleCurrentChange = (val: number) => {
  pageParams.current = val
  loadImages()
}

// 防抖搜索
let searchTimer: ReturnType<typeof setTimeout> | null = null
const debounceSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = window.setTimeout(() => {
    handleSearch()
  }, 300)
}

// 搜索事件处理
const handleSearch = () => {
  pageParams.current = 1 // 重置页码
  loadImages()
}

// 组件挂载时加载配置
onMounted(async () => {
  if (!configStore.loaded) {
    await configStore.loadConfig()
  }
})
</script>

<style scoped>
.image-preview {
  margin-bottom: 10px;
}

.image-library {
  max-height: 400px;
  overflow-y: auto;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 15px;
}

.image-item {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.image-item:hover {
  border-color: #409eff;
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.2);
}

.image-name {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

.no-images {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  color: #909399;
  font-size: 14px;
}

.image-library-dialog .el-dialog__body {
  padding: 20px;
  max-height: calc(80vh - 100px);
  overflow-y: auto;
}

.image-library {
  max-height: calc(80vh - 120px);
  overflow-y: auto;
}
</style>