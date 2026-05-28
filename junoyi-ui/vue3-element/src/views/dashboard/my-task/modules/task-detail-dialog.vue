<template>
  <ElDialog :model-value="visible" title="任务详情" width="720px" @close="handleClose">
    <div class="space-y-4">
      <div class="bg-gray-50 p-4 rounded-lg border border-gray-200">
        <h3 class="text-xl font-bold mb-4 flex items-center gap-2">
          <ArtSvgIcon icon="ri:file-list-3-line" class="text-2xl text-primary" />
          {{ taskData.title || '-' }}
        </h3>

        <div class="grid grid-cols-2 gap-4 text-sm">
          <!-- 项目信息 -->
          <div v-if="projectInfo" class="col-span-2 bg-blue-50 p-3 rounded border border-blue-200">
            <div class="flex items-center gap-2">
              <ArtSvgIcon icon="ri:folder-line" class="text-lg text-blue-600" />
              <span class="text-gray-500">所属项目：</span>
              <span class="font-medium text-blue-600">{{ projectInfo.name }}</span>
              <ElTag v-if="projectInfo.typeLabel" :type="(projectInfo.typeLabelType || 'info') as any" size="small">
                {{ projectInfo.typeLabel }}
              </ElTag>
            </div>
            <div v-if="projectInfo.description" class="mt-2 text-xs text-gray-600 ml-6">
              {{ projectInfo.description }}
            </div>
          </div>

          <div><span class="text-gray-500">负责人：</span><span>{{ taskData.ownerUser?.nickName || '-' }}</span></div>
          <div><span class="text-gray-500">优先级：</span><ElTag :type="priorityInfo.type" size="small">{{ priorityInfo.text }}</ElTag></div>
          <div><span class="text-gray-500">开始时间：</span><span>{{ taskData.startTime || '-' }}</span></div>
          <div><span class="text-gray-500">截止时间：</span><span>{{ taskData.dueTime || '-' }}</span></div>
          <div class="col-span-2"><span class="text-gray-500">执行人：</span><span>{{ executorNames }}</span></div>
          <div class="col-span-2"><span class="text-gray-500">描述：</span><span>{{ taskData.description || '-' }}</span></div>
          <div class="col-span-2"><span class="text-gray-500">备注：</span><span>{{ taskData.remark || '-' }}</span></div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end">
        <ElButton type="primary" @click="handleClose">关闭</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { fetchGetProjectInfo } from '@/api/project/detail'
import type { MyTaskDetail } from '../types'

interface Props {
  visible: boolean
  taskData: Partial<MyTaskDetail>
}

const props = defineProps<Props>()
const emit = defineEmits<{ 'update:visible': [value: boolean] }>()

// 项目信息
const projectInfo = ref<Api.Project.ProjectInfoVO | null>(null)

const priorityInfo = computed(() => {
  const map = {
    0: { type: 'info' as const, text: '低' },
    1: { type: 'primary' as const, text: '中' },
    2: { type: 'warning' as const, text: '高' },
    3: { type: 'danger' as const, text: '紧急' }
  }
  return map[props.taskData.priority as 0 | 1 | 2 | 3] || map[1]
})

const executorNames = computed(() => {
  return props.taskData.taskUserList?.map(item => item.nickName || `用户${item.userId}`).join('、') || '-'
})

// 获取项目信息
const fetchProjectInfo = async () => {
  const projectId = props.taskData.projectId
  if (!projectId || projectId === 0) {
    projectInfo.value = null
    return
  }

  try {
    const projectData = await fetchGetProjectInfo(projectId)
    if (projectData) {
      projectInfo.value = projectData
    }
  } catch (error) {
    console.error('获取项目信息失败:', error)
    projectInfo.value = null
  }
}

// 监听任务数据变化，重新获取项目信息
watch(() => props.taskData.projectId, () => {
  fetchProjectInfo()
}, { immediate: true })

const handleClose = () => emit('update:visible', false)
</script>

<style scoped>
:deep(.el-dialog__body) {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}
</style>
