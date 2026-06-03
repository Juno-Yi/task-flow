<template>
  <div class="task-column flex-shrink-0 flex-1">
    <!-- 列头 -->
    <div class="flex items-center justify-between mb-3 px-2">
      <div class="flex items-center gap-2">
        <div 
          class="w-3 h-3 rounded-full" 
          :style="{ backgroundColor: color }"
        ></div>
        <span class="font-semibold text-base">{{ title }}</span>
        <ElTag size="small" type="info" round>{{ tasks.length }}</ElTag>
      </div>
    </div>

    <!-- 任务卡片列表 - 独立滚动 -->
    <ElScrollbar class="task-list">
      <div class="space-y-2 px-2 pb-2">
        <TransitionGroup name="task-list">
          <TaskCard
            v-for="task in tasks"
            :key="task.id"
            :task="task"
            @click="handleTaskClick(task)"
          />
        </TransitionGroup>
        
        <!-- 空状态 -->
        <div
          v-if="tasks.length === 0"
          class="flex flex-col items-center justify-center py-12 text-gray-400"
        >
          <ArtSvgIcon icon="ri:inbox-line" class="text-5xl mb-2" />
          <span class="text-sm">暂无任务</span>
        </div>
      </div>
    </ElScrollbar>
  </div>
</template>

<script setup lang="ts">
import TaskCard from './task-card.vue'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'

interface Props {
  title: string
  tasks: Api.Project.ProjectTaskItemVO[]
  status: string
  color: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  taskClick: [task: Api.Project.ProjectTaskItemVO]
  taskEdit: [task: Api.Project.ProjectTaskItemVO]
}>()

const handleTaskClick = (task: Api.Project.ProjectTaskItemVO) => {
  emit('taskClick', task)
}

</script>

<style scoped lang="scss">
.task-column {
  background: var(--el-bg-color-overlay);
  border-radius: 8px;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.task-list {
  flex: 1;
  border-radius: 6px;
  overflow: hidden;
}

.task-list :deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

/* 任务列表过渡动画 */
.task-list-move,
.task-list-enter-active,
.task-list-leave-active {
  transition: all 0.3s ease;
}

.task-list-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.task-list-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.task-list-leave-active {
  position: absolute;
}
</style>

