<!-- 核心KPI统计 -->
<template>
  <div class="art-card h-82 p-5 mb-5 overflow-hidden max-lg:h-auto max-sm:mb-4">
    <div class="art-card-header pr-0">
      <div class="title">
        <h4>核心KPI</h4>
      </div>
    </div>

    <div class="mt-2">
      <ElRow :gutter="20">
        <ElCol :span="6" :xs="24" v-for="(item, index) in kpiItems" :key="index">
          <div
            class="flex px-5 flex-col justify-center h-55 border border-g-300/85 rounded-xl max-lg:mb-4 max-sm:flex-row max-sm:justify-between max-sm:items-center max-sm:h-40"
          >
            <div class="size-12 rounded-lg flex-cc" :class="item.iconBg">
              <ArtSvgIcon :icon="item.icon" class="text-xl" :class="item.iconColor" />
            </div>

            <div class="max-sm:ml-4 mt-3.5 max-sm:mt-0 max-sm:text-end">
              <div class="text-2xl font-medium">
                <ArtCountTo :target="item.value" :duration="1500" :decimals="item.decimals ?? 0" />
                <span v-if="item.suffix" class="text-base text-g-500 ml-0.5">{{ item.suffix }}</span>
              </div>
              <p class="mt-2 text-base text-g-600 max-sm:mt-1">{{ item.label }}</p>
            </div>
          </div>
        </ElCol>
      </ElRow>
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'TaskCoreKpi' })

type Dimension = 'month' | 'quarter' | 'year' | 'all'

const props = defineProps<{
  dimension: Dimension
  data?: Api.Task.TaskCoreKpiVO | null
}>()

interface KpiItem {
  label: string
  value: number
  suffix?: string
  decimals?: number
  icon: string
  iconBg: string
  iconColor: string
}

/** 根据维度取对应数据 */
const currentData = computed<Api.Task.TaskCoreKpiItem | null>(() => {
  if (!props.data) return null
  switch (props.dimension) {
    case 'month': return props.data.monthData
    case 'quarter': return props.data.quarterData
    case 'year': return props.data.yearData
    case 'all': return props.data.allData
    default: return null
  }
})

const kpiItems = computed<KpiItem[]>(() => {
  const d = currentData.value
  return [
    {
      label: '任务完成率',
      value: d?.completionRate ?? 0,
      suffix: '%',
      decimals: 1,
      icon: 'ri:pie-chart-line',
      iconBg: 'bg-success/10',
      iconColor: 'text-success',
    },
    {
      label: '逾期任务数',
      value: d?.overdueTaskCount ?? 0,
      icon: 'ri:alarm-warning-line',
      iconBg: 'bg-danger/10',
      iconColor: 'text-danger',
    },
    {
      label: '平均处理时长',
      value: d?.avgProcessHours ?? 0,
      suffix: 'h',
      decimals: 1,
      icon: 'ri:timer-line',
      iconBg: 'bg-warning/10',
      iconColor: 'text-warning',
    },
    {
      label: '本期新增任务',
      value: d?.newTaskCount ?? 0,
      icon: 'ri:add-circle-line',
      iconBg: 'bg-primary/10',
      iconColor: 'text-primary',
    },
  ]
})
</script>

