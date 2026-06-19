<!-- 任务健康度仪表盘 -->
<template>
  <div class="art-card p-5 mb-5 max-sm:mb-4">
    <div class="pb-3.5">
      <span class="text-base font-medium">任务健康度</span>
    </div>
    <div
      ref="chartRef"
      class="relative w-full"
      :style="{ height: '16rem' }"
    ></div>
  </div>
</template>

<script setup lang="ts">
import type { EChartsOption } from '@/plugins/echarts'
import { useChartComponent } from '@/hooks/core/useChart'

defineOptions({ name: 'TaskHealthGauge' })

interface BaseChartProps {
  height?: string
  loading?: boolean
  isEmpty?: boolean
  colors?: string[]
}

type Dimension = 'month' | 'quarter' | 'year' | 'all'

const props = defineProps<{
  dimension: Dimension
  data?: Api.Task.TaskCoreKpiVO | null
}>()

/** 根据维度取对应完成率作为健康度 */
const healthValue = computed(() => {
  if (!props.data) return 0
  switch (props.dimension) {
    case 'month': return props.data.monthData?.completionRate ?? 0
    case 'quarter': return props.data.quarterData?.completionRate ?? 0
    case 'year': return props.data.yearData?.completionRate ?? 0
    case 'all': return props.data.allData?.completionRate ?? 0
    default: return 0
  }
})

const chartProps: BaseChartProps = {
  height: '16rem',
  loading: false,
  isEmpty: false,
}

const { chartRef, isDark } = useChartComponent({
  props: chartProps,
  watchSources: [() => healthValue.value],
  generateOptions: (): EChartsOption => {
    const val = healthValue.value

    // 根据分值确定颜色
    const getColor = (v: number): string => {
      if (v >= 80) return '#67C23A'
      if (v >= 60) return '#E6A23C'
      return '#F56C6C'
    }

    // 根据分值确定状态文字
    const getStatus = (v: number): string => {
      if (v >= 80) return '健康'
      if (v >= 60) return '一般'
      return '风险'
    }

    return {
      series: [
        {
          type: 'gauge',
          startAngle: 210,
          endAngle: -30,
          min: 0,
          max: 100,
          pointer: {
            show: true,
            length: '60%',
            width: 4,
            itemStyle: {
              color: getColor(val),
            },
          },
          progress: {
            show: true,
            roundCap: true,
            width: 12,
            itemStyle: {
              color: getColor(val),
            },
          },
          axisLine: {
            roundCap: true,
            lineStyle: {
              width: 12,
              color: [[1, isDark.value ? '#333' : '#eee']],
            },
          },
          axisTick: { show: false },
          splitLine: { show: false },
          axisLabel: { show: false },
          title: {
            show: true,
            offsetCenter: [0, '70%'],
            fontSize: 14,
            color: isDark.value ? '#aaa' : '#666',
          },
          detail: {
            valueAnimation: true,
            offsetCenter: [0, '40%'],
            fontSize: 28,
            fontWeight: 'bold',
            color: getColor(val),
            formatter: `{value}分`,
          },
          data: [
            {
              value: val,
              name: getStatus(val),
            },
          ],
        },
      ],
    }
  },
})
</script>

