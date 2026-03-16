<template>
  <div>
    <!-- 图片预览 -->
    <div v-if="modelValue" class="image-preview">
      <el-image
        :src="modelValue"
        fit="cover"
        style="width: 100px; height: 100px"
      />
      <el-button type="danger" size="small" @click="removeImage" style="margin-top: 10px">移除图片</el-button>
    </div>
    <!-- 选择按钮 -->
    <div v-else>
      <el-button type="primary" @click="openImageLibrary">选择图片</el-button>
    </div>
    
    <!-- 图片库弹窗 -->
    <el-dialog
      v-model="imageLibraryVisible"
      title="选择图片"
      width="800px"
    >
      <div class="image-library">
        <el-input
          v-model="imageSearch"
          placeholder="搜索图片"
          prefix-icon="Search"
          style="margin-bottom: 20px"
        />
        <div class="image-grid">
          <div
            v-for="image in images"
            :key="image.id"
            class="image-item"
            @click="selectImage(image)"
          >
            <el-image
              :src="image.url"
              fit="cover"
              style="width: 100px; height: 100px"
            />
            <div class="image-name">{{ image.name }}</div>
          </div>
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

<script setup>
import { ref, computed, onMounted } from 'vue'
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

// 加载图片库数据
const loadImages = async () => {
  try {
    // 调用后端的图片管理接口获取图片数据
    const response = await api.get('/image/list', {
      params: {
        page: 1,
        size: 100
      }
    })
    // 处理图片数据，确保格式正确
    images.value = response.records.map(image => ({
      id: image.id,
      name: image.originalFilename,
      url: image.filename
    }))
  } catch (error) {
    console.error('加载图片库失败:', error)
    ElMessage.error('加载图片库失败')
  }
}

// 选择图片
const selectImage = (image) => {
  emit('update:modelValue', image.url)
  imageLibraryVisible.value = false
}

// 移除图片
const removeImage = () => {
  emit('update:modelValue', '')
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
</style>