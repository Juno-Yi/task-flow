<template>
  <div 
    class="task-card cursor-pointer transition-all duration-200 hover:shadow-md"
    @click="handleClick"
  >
    <!-- 任务标题 -->
    <div class="flex items-start justify-between mb-2 gap-2">
      <h4 class="task-title flex-1 text-sm font-medium m-0 leading-5">
        {{ task.title }}
      </h4>
      <div class="flex items-center gap-2 flex-shrink-0">
        <ElTag v-if="task.isOverdue" type="danger" size="small">逾期</ElTag>
        <ElTag
          :type="getPriorityConfig(task.priority).type"
          size="small"
        >
          {{ getPriorityConfig(task.priority).text }}
        </ElTag>
      </div>
    </div>

    <!-- 任务描述 -->
    <p 
      v-if="task.description" 
      class="text-xs text-gray-500 mb-3 line-clamp-2"
    >
      {{ task.description }}
    </p>

    <!-- 底部信息 -->
    <div class="flex items-center justify-between text-xs text-gray-400">
      <div class="flex items-center gap-2 flex-wrap">
        <div v-if="task.ownerUser?.nickName" class="flex items-center gap-1">
          <ArtSvgIcon icon="ri:user-star-line" class="text-sm" />
          <span>{{ task.ownerUser?.nickName }}</span>
        </div>
        <div v-if="task.planStartTime || task.planEndTime" class="flex items-center gap-1" :class="dueDateClass">
          <ArtSvgIcon icon="ri:calendar-line" class="text-sm" />
          <span>{{ formatPlanPeriod(task.planStartTime, task.planEndTime) }}</span>
        </div>
        <div v-if="task.planStartTime && task.planEndTime" class="flex items-center gap-1">
          <ArtSvgIcon icon="ri:time-line" class="text-sm" />
          <span>{{ calculateHours(task.planStartTime, task.planEndTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'

interface Props {
  task: Api.Project.ProjectTaskItemVO
}

const props = defineProps<Props>()
const emit = defineEmits<{
  click: []
  edit: []
}>()

// 优先级配置
const priorityConfig: Record<number, { type: 'info' | 'primary' | 'warning' | 'danger'; text: string }> = {
  1: { type: 'info', text: '低' },
  2: { type: 'primary', text: '中' },
  3: { type: 'warning', text: '高' },
  4: { type: 'danger', text: '紧急' }
}

const getPriorityConfig = (priority?: number) => priorityConfig[priority ?? -1] || { type: 'info', text: '-' }

/**
 * 紧凑格式化时间（只显示月-日）
 */
const formatCompactTime = (value?: string) => {
  if (!value) return '-'
  const date = new Date(value)
  if (isNaN(date.getTime())) return value
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}-${day}`
}

/**
 * 格式化计划时间范围
 */
const formatPlanPeriod = (startTime?: string, endTime?: string) => {
  if (startTime && endTime) {
    return `${formatCompactTime(startTime)} ~ ${formatCompactTime(endTime)}`
  } else if (startTime) {
    return `${formatCompactTime(startTime)} ~`
  } else if (endTime) {
    return `~ ${formatCompactTime(endTime)}`
  }
  return '-'
}

/**
 * 计算预计工时
 */
const calculateHours = (startTime?: string, endTime?: string) => {
  if (!startTime || !endTime) return '-'

  const start = new Date(startTime)
  const end = new Date(endTime)
  const diffMs = end.getTime() - start.getTime()
  const diffHours = diffMs / (1000 * 60 * 60)

  if (diffHours <= 0) return '-'

  if (diffHours < 1) {
    const minutes = Math.round(diffHours * 60)
    return `${minutes}分钟`
  }

  if (diffHours < 24) {
    return `${diffHours.toFixed(1)}小时`
  }

  const days = Math.floor(diffHours / 24)
  const hours = Math.round(diffHours % 24)
  return hours > 0 ? `${days}天${hours}小时` : `${days}天`
}

/**
 * 截止日期样式
 */
const dueDateClass = computed(() => {
  if (props.task.isOverdue) {
    return 'text-red-500'
  }
  return ''
})

const handleClick = () => {
  emit('click')
}

</script>

<style scoped lang="scss">
.task-card {
  padding: 12px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;

  &:hover {
    border-color: var(--el-color-primary-light-5);
  }
}

.task-title {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-word;
}

.line-clamp-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>

