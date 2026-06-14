<!-- tab页 - 我的项目 -->
<template>
  <div class="my-project-page">
    <van-tabs v-model:active="activeTab" @change="onTabChange" animated>
      <van-tab
        v-for="tab in tabs"
        :key="tab.status"
        :title="tab.title"
      />
    </van-tabs>

    <div class="project-list-container">
      <van-pull-refresh
        v-model="refreshing"
        @refresh="onRefresh"
      >
        <van-list
          v-model:loading="loading"
          :finished="finished"
          :immediate-check="false"
          finished-text="没有更多了"
          @load="onLoad"
        >

        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup lang="ts">

defineOptions({ name: 'MyProject'})

// Tab配置
const tabs = [
  { title: '立项中', status: 0},
  { title: '执行中', status: 1},
  { title: '结项中', status: 2},
  { title: '结后', status: 3},
  { title: '已归档', status: 4}
]

// 当前激活的Tab
const activeTab = ref(0);

// 加载状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 分页参数
const pageParams = ref({
  current: 1,
  size: 10
})

/**
 * 当Tab切换时候
 */
const onTabChange = () => {

}

/**
 * 刷新时候
 */
const onRefresh = () => {

}

/**
 * 加载时候
 */
const onLoad = () => {

}

onMounted(() => {

})

</script>

<style scoped>
@use './style.scss' as *;
</style>