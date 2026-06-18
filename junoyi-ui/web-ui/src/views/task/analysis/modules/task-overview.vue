<!-- 任务总览统计 -->
<template>
  <div class="art-card p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>任务总览</h4>
        <p>各维度任务状态统计</p>
      </div>
      <!-- 维度切换 -->
      <ElRadioGroup v-model="activeDimension" size="small">
        <ElRadioButton value="month">本月</ElRadioButton>
        <ElRadioButton value="quarter">本季度</ElRadioButton>
        <ElRadioButton value="year">本年度</ElRadioButton>
        <ElRadioButton value="all">全部</ElRadioButton>
      </ElRadioGroup>
    </div>

    <div class="mt-5">
      <div class="grid grid-cols-2 gap-5 sm:grid-cols-3 md:grid-cols-5">
        <ArtStatsCard
          v-for="(item, index) in statsItems"
          :key="index"
          :icon="item.icon"
          :icon-style="item.iconStyle"
          :count="item.count"
          :description="item.label"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { fetchGetTaskAnalysisOverview } from '@/api/task/analysis'

defineOptions({ name: 'TaskOverview' })

interface StatsItem {
  label: string
  count: number
  icon: string
  iconStyle: string
}

const activeDimension = ref<'month' | 'quarter' | 'year' | 'all'>('month')

const overviewData = ref<Api.Task.TaskAnalysisOverviewVO | null>(null)

/** 当前维度的数据 */
const currentData = computed<Api.Task.TaskAnalysisOverviewItem | null>(() => {
  if (!overviewData.value) return null
  switch (activeDimension.value) {
    case 'month': return overviewData.value.monthData
    case 'quarter': return overviewData.value.quarterData
    case 'year': return overviewData.value.yearData
    case 'all': return overviewData.value.allData
    default: return null
  }
})

/** 统计卡片数据 */
const statsItems = computed<StatsItem[]>(() => {
  const data = currentData.value
  if (!data) {
    return defaultStats()
  }
  return [
    { label: '待处理', count: data.pendingTaskCount, icon: 'ri:time-line', iconStyle: 'bg-warning' },
    { label: '进行中', count: data.ongoingTaskCount, icon: 'ri:play-circle-line', iconStyle: 'bg-primary' },
    { label: '待审核', count: data.reviewTaskCount, icon: 'ri:file-search-line', iconStyle: 'bg-info' },
    { label: '已驳回', count: data.rejectedTaskCount, icon: 'ri:close-circle-line', iconStyle: 'bg-danger' },
    { label: '已完成', count: data.completedTaskCount, icon: 'ri:checkbox-circle-line', iconStyle: 'bg-success' },
  ]
})

function defaultStats(): StatsItem[] {
  return [
    { label: '待处理', count: 0, icon: 'ri:time-line', iconStyle: 'bg-warning' },
    { label: '进行中', count: 0, icon: 'ri:play-circle-line', iconStyle: 'bg-primary' },
    { label: '待审核', count: 0, icon: 'ri:file-search-line', iconStyle: 'bg-info' },
    { label: '已驳回', count: 0, icon: 'ri:close-circle-line', iconStyle: 'bg-danger' },
    { label: '已完成', count: 0, icon: 'ri:checkbox-circle-line', iconStyle: 'bg-success' },
  ]
}

const loadOverviewData = async () => {
  overviewData.value = await fetchGetTaskAnalysisOverview()
}

onMounted(loadOverviewData)
</script>

