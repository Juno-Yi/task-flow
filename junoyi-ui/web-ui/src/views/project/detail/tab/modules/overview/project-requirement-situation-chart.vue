<template>
  <div ref="chartRef" class="overview-chart-container"></div>
</template>

<script setup lang="ts">
import { echarts, type EChartsOption } from '@/plugins/echarts'

defineOptions({ name: 'ProjectRequirementSituationChart' })

interface Props {
  data: Api.Project.ProjectRequirementSituationVO[]
}

const props = defineProps<Props>()
const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

const renderChart = () => {
  if (!chartRef.value) return

  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }

  const chartData = (props.data || []).map(item => ({
    name: item.statusLabel,
    value: item.count,
    itemStyle: {
      color: getChartColor(item.statusType)
    }
  }))

  const option: EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>数量：{c}（{d}%）'
    },
    legend: {
      bottom: 0,
      left: 'center',
      itemWidth: 10,
      itemHeight: 10
    },
    series: [
      {
        name: '需求情况',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '44%'],
        avoidLabelOverlap: true,
        label: {
          show: true,
          formatter: '{b}\n{c}'
        },
        labelLine: {
          length: 12,
          length2: 10
        },
        data: chartData
      }
    ]
  }

  chartInstance.setOption(option)
}

const getChartColor = (type?: string) => {
  const colorMap: Record<string, string> = {
    primary: '#409EFF',
    success: '#67C23A',
    warning: '#E6A23C',
    danger: '#F56C6C',
    error: '#F56C6C',
    info: '#909399'
  }
  return colorMap[(type || 'info').toLowerCase()] || colorMap.info
}

watch(() => props.data, () => {
  renderChart()
}, { deep: true })

useResizeObserver(chartRef, () => {
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
.overview-chart-container {
  width: 100%;
  height: 320px;
}
</style>

