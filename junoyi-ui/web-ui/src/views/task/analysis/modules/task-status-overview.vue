<!-- 任务状态总览 -->
<template>
  <div class="art-card p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>任务状态总览</h4>
        <p>各状态任务数量统计</p>
      </div>
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
import { fetchGetTaskStatusOverview } from '@/api/task/analysis'

defineOptions({ name: 'TaskStatusOverview' })

type Dimension = 'month' | 'quarter' | 'year' | 'all'

const props = defineProps<{
  dimension: Dimension
}>()

interface StatsItem {
  label: string
  count: number
  icon: string
  iconStyle: string
}

const overviewData = ref<Api.Task.TaskStatusOverviewVO | null>(null)

/** 根据维度取对应数据 */
const currentData = computed<Api.Task.TaskStatusOverviewItem | null>(() => {
  if (!overviewData.value) return null
  switch (props.dimension) {
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
  return [
    { label: '待处理', count: data?.pendingTaskCount ?? 0, icon: 'ri:time-line', iconStyle: 'bg-warning' },
    { label: '进行中', count: data?.ongoingTaskCount ?? 0, icon: 'ri:play-circle-line', iconStyle: 'bg-primary' },
    { label: '待审核', count: data?.reviewTaskCount ?? 0, icon: 'ri:file-search-line', iconStyle: 'bg-info' },
    { label: '已驳回', count: data?.rejectedTaskCount ?? 0, icon: 'ri:close-circle-line', iconStyle: 'bg-danger' },
    { label: '已完成', count: data?.completedTaskCount ?? 0, icon: 'ri:checkbox-circle-line', iconStyle: 'bg-success' },
  ]
})

const loadOverviewData = async () => {
  overviewData.value = await fetchGetTaskStatusOverview()
}

onMounted(loadOverviewData)
</script>

