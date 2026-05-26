<template>
  <div class="activity-heatmap">
    <div class="heatmap-header">
      <div>
        <div class="heatmap-title">最近一年活跃度</div>
        <div class="heatmap-subtitle">截止到今天，按天统计项目活跃量</div>
      </div>
      <div class="heatmap-legend-wrap">
        <span class="heatmap-legend-text">低</span>
        <div class="heatmap-legend">
          <span v-for="level in 5" :key="level" class="legend-cell" :class="`level-${level - 1}`"></span>
        </div>
        <span class="heatmap-legend-text">高</span>
      </div>
    </div>

    <div class="heatmap-body">
      <div class="weeks-grid" :style="{ gridTemplateColumns: `repeat(${weeks.length}, max-content)` }">
        <div v-for="(week, weekIndex) in weeks" :key="weekIndex" class="week-column">
          <template v-for="cell in week" :key="cell.key">
            <ElTooltip v-if="!cell.placeholder" :content="getTooltipText(cell)" placement="top">
              <div class="day-cell" :class="`level-${getLevel(cell.count)}`"></div>
            </ElTooltip>
            <div v-else class="day-cell day-cell-placeholder"></div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'ProjectActivityTrendChart' })

interface Props {
  data: Api.Project.ProjectActivityTrendVO[]
}

interface HeatmapCell {
  key: string
  date: string
  count: number
  placeholder: boolean
}

const props = defineProps<Props>()

const normalizedData = computed(() => {
  return [...(props.data || [])]
    .map(item => ({
      date: item.date,
      count: Number(item.count || 0)
    }))
    .sort((a, b) => a.date.localeCompare(b.date))
})

const maxCount = computed(() => Math.max(...normalizedData.value.map(item => item.count), 0))

const cells = computed<HeatmapCell[]>(() => normalizedData.value.map(item => ({
  key: item.date,
  date: item.date,
  count: item.count,
  placeholder: false
})))

const weeks = computed(() => {
  if (!cells.value.length) return []
  const result: HeatmapCell[][] = []
  let currentWeek: HeatmapCell[] = []

  cells.value.forEach((cell, index) => {
    const day = new Date(`${cell.date}T00:00:00`).getDay()
    const weekDay = day === 0 ? 7 : day
    if (index === 0) {
      for (let i = 1; i < weekDay; i++) {
        currentWeek.push({ key: `empty-start-${i}`, date: '', count: 0, placeholder: true })
      }
    }
    currentWeek.push(cell)
    if (weekDay === 7) {
      result.push(currentWeek)
      currentWeek = []
    }
  })

  if (currentWeek.length) {
    result.push(currentWeek)
  }
  return result
})

const getLevel = (count: number) => {
  if (!count || maxCount.value === 0) return 0
  const ratio = count / maxCount.value
  if (ratio <= 0.2) return 1
  if (ratio <= 0.45) return 2
  if (ratio <= 0.7) return 3
  return 4
}

const getTooltipText = (cell: HeatmapCell) => `${cell.date}：${cell.count} 次活跃`
</script>

<style scoped lang="scss">
.activity-heatmap {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 220px;
  height: 100%;
}

.heatmap-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  flex-shrink: 0;
}

.heatmap-title {
  color: var(--el-text-color-primary);
  font-size: 15px;
  font-weight: 600;
  line-height: 1.2;
}

.heatmap-subtitle {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.4;
}

.heatmap-legend-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  white-space: nowrap;
}

.heatmap-legend {
  display: flex;
  gap: 4px;
}

.heatmap-body {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 4px 0 6px;
}

.heatmap-body::-webkit-scrollbar {
  height: 6px;
}

.heatmap-body::-webkit-scrollbar-thumb {
  background: var(--el-border-color);
  border-radius: 999px;
}

.weeks-grid {
  display: grid;
  gap: 4px;
  width: max-content;
  align-items: center;
  flex-shrink: 0;
}

.week-column {
  display: grid;
  grid-template-rows: repeat(7, 12px);
  gap: 4px;
}

.day-cell {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  background: #ebedf0;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.day-cell:not(.day-cell-placeholder):hover {
  transform: scale(1.08);
  box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.18);
}

.day-cell-placeholder {
  visibility: hidden;
  pointer-events: none;
}

.legend-cell {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.level-0 { background: #ebedf0; }
.level-1 { background: #d3f1cf; }
.level-2 { background: #95de8b; }
.level-3 { background: #4cb050; }
.level-4 { background: #1f7a31; }

@media (max-width: 768px) {
  .heatmap-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .weeks-grid {
    min-width: 720px;
  }
}
</style>
