<template>
  <div class="location-tracking-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产定位</span>
        </div>
      </template>
      
      <div class="map-container" ref="mapContainer">
        
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, useTemplateRef } from 'vue';
// 引入 ooomap SDK
import * as om from 'ooomap' 

let mapInstance = null
const mapContainer = useTemplateRef('mapContainer')

// 页面加载时执行
onMounted(() => {
  console.log(mapContainer.value)
  // 初始化逻辑
  mapInstance = new om.Map({
      // 地图容器, domElement
      container: mapContainer.value,
      viewMode: '2d',
      zoomLevel: 20,
      viewAngle: 0,
      // 地图容器, domElement
      container: mapContainer.value,
      // 在ooomap编辑器中生成，或由官方提供
      verifyUrl: 'https://www.ooomap.com/ooomap-verify/check/e3d8d90f98664ca47b7f585fa637de4b',
      appID: 'aa0eeb7f7d6cd354f0c448767c058874'
  })

  // 注册地图元素的点击事件
  mapInstance.on('picked', result => {
      console.log(result);
  })

  mapInstance.on('sceneLoaded', scene => {
    console.log('场景加载完成', scene);
    scene.findData({id: '2K43YM'}).then(arr => console.log(arr));
  })

  mapInstance.on('buildingLoaded', building => {
    console.log('建筑加载完成', building);
    building.findData({id: 'mo8hqk8d0bu9g2kv'}).then(arr => console.log(arr));
    building.findData(obj => obj.tag == 'test').then(res => console.log(res))
    building.keywordsFind('test').then(res => console.log(res));
    //building.getNodeByID('2K43YM').then(nodeData => console.log(nodeData));
  })

  // let node = om.Node.findData({id: '7TE30U'}, {});
  // console.log(node)
});



// 组件卸载时销毁地图实例，释放资源
onBeforeUnmount(() => {
  if (mapInstance) {
    mapInstance.dispose()
    mapInstance = null
  }
})
</script>

<style scoped>
.location-tracking-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content {
  margin-top: 20px;
}

.map-container {
  width: 100%;
  height: 100vh;
}
</style>