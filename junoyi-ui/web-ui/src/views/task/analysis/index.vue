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
    <TaskStatusOverview :dimension="activeDimension" :data="analysisData?.statusOverview" />

    <!-- 核心KPI + 任务健康度 -->
    <ElRow :gutter="20">
      <ElCol :xl="14" :lg="15" :xs="24">
        <TaskCoreKpi :dimension="activeDimension" :data="analysisData?.coreKpi" />
      </ElCol>
      <ElCol :xl="10" :lg="9" :xs="24">
        <TaskHealthGauge :dimension="activeDimension" :data="analysisData?.healthScore" />
      </ElCol>
    </ElRow>

    <ElRow :gutter="20">
    </ElRow>
  </div>
</template>

<script setup lang="ts">
import { fetchGetTaskAnalysis } from '@/api/task/analysis'
import TaskStatusOverview from './modules/task-status-overview.vue'
import TaskCoreKpi from './modules/task-core-kpi.vue'
import TaskHealthGauge from './modules/task-health-gauge.vue'

defineOptions({ name: 'TaskAnalysis' })

/** 页面级维度筛选 */
const activeDimension = ref<'month' | 'quarter' | 'year' | 'all'>('month')

/** 页面综合数据 */
const analysisData = ref<Api.Task.TaskAnalysisVO | null>(null)

const loadData = async () => {
  analysisData.value = await fetchGetTaskAnalysis()
}

onMounted(loadData)
</script>