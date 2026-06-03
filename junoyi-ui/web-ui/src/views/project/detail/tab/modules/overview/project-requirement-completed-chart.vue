<template>
  <div class="overview-completed-chart">
    <div class="mb-3 flex justify-end">
      <ElRadioGroup v-model="activeRange" size="small">
        <ElRadioButton label="7d">近7天</ElRadioButton>
        <ElRadioButton label="30d">近30天</ElRadioButton>
        <ElRadioButton label="90d">近90天</ElRadioButton>
      </ElRadioGroup>
    </div>
    <div ref="chartRef" class="overview-chart-container"></div>
  </div>
</template>

<script setup lang="ts">
import { echarts, type EChartsOption } from '@/plugins/echarts'

defineOptions({ name: 'ProjectRequirementCompletedChart' })

interface Props {
  data: Api.Project.ProjectRequirementCompletedVO
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

const renderChart = () => {
  if (!chartRef.value) return
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
        name: '完成数量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        data: currentList.value.map(item => item.count),
        lineStyle: { color: '#409EFF', width: 3 },
        itemStyle: { color: '#409EFF' },
        areaStyle: { color: 'rgba(64, 158, 255, 0.12)' }
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
.overview-completed-chart {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.overview-chart-container {
  width: 100%;
  flex: 1;
  min-height: 0;
}
</style>

