<template>
  <div class="overview-task-completed-chart">
    <div class="mb-3 flex justify-end">
      <ElRadioGroup v-model="activeRange" size="small">
        <ElRadioButton label="7d">近7天</ElRadioButton>
        <ElRadioButton label="30d">近30天</ElRadioButton>
        <ElRadioButton label="90d">近90天</ElRadioButton>
      </ElRadioGroup>
    </div>
    <div v-if="!hasData" class="empty-state">
      <ElEmpty description="暂无任务完成数据" :image-size="100" />
    </div>
    <div v-show="hasData" ref="chartRef" class="overview-chart-container"></div>
  </div>
</template>

<script setup lang="ts">
import { echarts, type EChartsOption } from '@/plugins/echarts'

defineOptions({ name: 'ProjectTaskCompletedChart' })

interface Props {
  data: Api.Project.ProjectTaskCompletedVO
}

const props = defineProps<Props>()
const chartRef = ref<HTMLDivElement>()
const activeRange = ref<'7d' | '30d' | '90d'>('7d')
let chartInstance: echarts.ECharts | null = null

const currentList = computed(() => {
  if (activeRange.value === '30d') return props.data?.thirtyDayList || []
  if (activeRange.value === '90d') return props.data?.ninetyDayList || []
  return props.data?.sevenDayList || []
})

const hasData = computed(() => {
  return currentList.value && currentList.value.length > 0
})

const renderChart = () => {
  if (!chartRef.value) return
  if (!hasData.value) return
  if (!chartInstance) chartInstance = echarts.init(chartRef.value)

  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: 24,
      right: 16,
      top: 16,
      bottom: 24,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: currentList.value.map(item => item.date),
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#909399' }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#ebeef5' } },
      axisLabel: { color: '#909399' }
    },
    series: [
      {
        name: '完成任务数',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        data: currentList.value.map(item => item.count),
        lineStyle: { color: '#67C23A', width: 3 },
        itemStyle: { color: '#67C23A' },
        areaStyle: { color: 'rgba(103, 194, 58, 0.12)' }
      }
    ]
  }

  chartInstance.setOption(option)
}

watch([() => props.data, activeRange], () => {
  renderChart()
}, { deep: true })

useResizeObserver(chartRef, () => {
  chartInstance?.resize()
})

useResizeObserver(chartRef.value?.parentElement || null, () => {
  chartInstance?.resize()
})

onMounted(() => {
  renderChart()
})

onBeforeUnmount(() => {
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<style scoped lang="scss">
.overview-task-completed-chart {
  min-height: 300px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 240px;
}

.overview-chart-container {
  width: 100%;
  flex: 1;
  min-height: 240px;
}
</style>

