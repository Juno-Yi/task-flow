<!-- 任务中心 - 任务分析 -->
<template>
  <div>
    <!-- 页面顶部：维度切换 -->
    <div class="mb-5 flex items-center justify-start max-sm:mb-4">
      <ElRadioGroup v-model="activeDimension" size="small">
        <ElRadioButton value="month">本月</ElRadioButton>
        <ElRadioButton value="quarter">本季度</ElRadioButton>
        <ElRadioButton value="year">本年度</ElRadioButton>
        <ElRadioButton value="all">全部</ElRadioButton>
      </ElRadioGroup>
    </div>

    <!-- 任务状态总览 -->
    <TaskStatusOverview :dimension="activeDimension" />

    <ElRow :gutter="20">
      <h1>任务完成趋势折线图</h1>
    </ElRow>

    <ElRow :gutter="20">
      <h1>任务状态分析</h1>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
import TaskStatusOverview from './modules/task-status-overview.vue'

defineOptions({ name: 'TaskAnalysis' })

/** 页面级维度筛选 */
const activeDimension = ref<'month' | 'quarter' | 'year' | 'all'>('month')
</script>