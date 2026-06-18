<!-- 任务中心 - 任务分析 -->
<template>
  <div>
    <!-- 任务总览统计 -->
    <TaskOverview />

    <ElRow :gutter="20">
      <h1>任务完成趋势折线图</h1>
    </ElRow>

    <ElRow :gutter="20">
      <h1>任务状态分析</h1>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
import TaskOverview from './modules/task-overview.vue'

defineOptions({ name: 'TaskAnalysis' })
</script>