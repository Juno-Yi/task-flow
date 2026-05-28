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
        <div v-if="task.dueTime || task.DueTime" class="flex items-center gap-1" :class="dueDateClass">
          <ArtSvgIcon icon="ri:calendar-line" class="text-sm" />
          <span>{{ formatDateTime(task.dueTime || task.DueTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import type { MyTask } from '../types'

interface Props {
  task: MyTask
}

const props = defineProps<Props>()
const emit = defineEmits<{
  click: []
}>()

// 优先级配置
const priorityConfig: Record<number, { type: 'info' | 'primary' | 'warning' | 'danger'; text: string }> = {
  1: { type: 'info', text: '低' },
  2: { type: 'primary', text: '中' },
  3: { type: 'warning', text: '高' },
  4: { type: 'danger', text: '紧急' }
}

const getPriorityConfig = (priority?: number) => priorityConfig[priority ?? -1] || { type: 'info', text: '-' }

const formatDateTime = (value?: string) => {
  if (!value) return '-'
  const date = new Date(value)
  if (isNaN(date.getTime())) return value
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 截止日期样式
const dueDateClass = computed(() => {
  // 已完成状态：只有逾期才显示红色，否则正常颜色
  if (props.task.status === 4) {
    return props.task.isOverdue ? 'text-red-500' : ''
  }

  // 其他状态：逾期显示红色
  if (props.task.isOverdue) return 'text-red-500'

  // 其他状态：3天内到期显示橙色
  const dueTime = props.task.dueTime || props.task.DueTime
  if (!dueTime) return ''
  const today = new Date()
  const dueDate = new Date(dueTime)
  if (isNaN(dueDate.getTime())) return ''
  const diffDays = Math.ceil((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))

  if (diffDays <= 3) return 'text-orange-500'
  return ''
})

const handleClick = () => {
  emit('click')
}
</script>

<style scoped>
.task-card {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  padding: 12px;
}

.task-card:hover {
  border-color: var(--el-color-primary);
}

.task-title {
  word-break: break-word;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
