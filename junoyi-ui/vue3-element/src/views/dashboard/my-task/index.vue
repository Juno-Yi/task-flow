<template>
  <div class="art-full-height">
    <div class="flex h-full flex-col">
      <div class="mb-4 flex items-center justify-between px-2">
        <div class="flex items-center gap-3">
          <h1 class="m-0 text-2xl font-bold">我的任务</h1>
          <ElTag type="info" size="large">{{ totalTaskCount }} 个任务</ElTag>
          <ElTag type="danger" size="large">{{ urgentTaskCount }} 个紧急</ElTag>
        </div>
        <div class="flex items-center gap-2">
          <ElInput v-model="searchKeyword" placeholder="搜索任务标题/描述" clearable class="w-60" :prefix-icon="Search" />
          <ElButton :icon="Refresh" :loading="loading" @click="loadTasks" v-ripple>刷新</ElButton>
        </div>
      </div>

      <ElCard class="flex-1 min-h-0 overflow-hidden" shadow="never">
        <div class="flex h-full gap-4">
          <TaskColumn title="待处理" :tasks="groupedTasks.todo" status="0" color="#909399" @task-click="handleTaskClick" />
          <TaskColumn title="进行中" :tasks="groupedTasks.doing" status="1" color="#409EFF" @task-click="handleTaskClick" />
          <TaskColumn title="待验收" :tasks="groupedTasks.review" status="2" color="#E6A23C" @task-click="handleTaskClick" />
          <TaskColumn title="已驳回" :tasks="groupedTasks.rejected" status="3" color="#F56C6C" @task-click="handleTaskClick" />
          <TaskColumn title="已完成" :tasks="groupedTasks.done" status="4" color="#67C23A" @task-click="handleTaskClick" />
        </div>
      </ElCard>
    </div>

    <ElDialog v-model="detailVisible" title="任务详情" width="960px" top="5vh" class="task-detail-dialog">
      <ElSkeleton :loading="detailLoading" animated>
        <template #default>
          <div v-if="currentTaskDetail" class="space-y-4">
            <!-- 项目信息卡片 -->
            <ElCard v-if="currentProjectInfo" shadow="never" class="project-info-card">
              <div class="flex items-center gap-3">
                <div class="flex items-center gap-2 flex-1">
                  <ElIcon :size="20" color="#409EFF">
                    <Folder />
                  </ElIcon>
                  <span class="text-gray-500">所属项目：</span>
                  <span class="font-medium text-primary">{{ currentProjectInfo.name }}</span>
                  <ElTag v-if="currentProjectInfo.typeLabel" :type="(currentProjectInfo.typeLabelType || 'info') as any" size="small">
                    {{ currentProjectInfo.typeLabel }}
                  </ElTag>
                </div>
              </div>
              <div v-if="currentProjectInfo.description" class="mt-2 text-sm text-gray-600 ml-7">
                {{ currentProjectInfo.description }}
              </div>
            </ElCard>

            <ElDescriptions :column="2" border>
              <ElDescriptionsItem label="任务标题" :span="2">{{ currentTaskDetail.title || '-' }}</ElDescriptionsItem>
              <ElDescriptionsItem label="任务描述" :span="2">{{ currentTaskDetail.description || '-' }}</ElDescriptionsItem>
              <ElDescriptionsItem label="负责人">{{ currentTaskDetail.ownerUser?.nickName || '-' }}</ElDescriptionsItem>
              <ElDescriptionsItem label="优先级">{{ getPriorityLabel(currentTaskDetail.priority) }}</ElDescriptionsItem>
              <ElDescriptionsItem label="开始时间">{{ formatTime(currentTaskDetail.planStartTime) }}</ElDescriptionsItem>
              <ElDescriptionsItem label="截止时间">{{ formatTime(currentTaskDetail.planEndTime) }}</ElDescriptionsItem>
              <ElDescriptionsItem label="执行人" :span="2">{{ getUserNames(currentTaskDetail.taskUserList) }}</ElDescriptionsItem>
              <ElDescriptionsItem label="备注" :span="2">{{ currentTaskDetail.remark || '-' }}</ElDescriptionsItem>
            </ElDescriptions>

            <ElCard v-if="currentTaskDetail.latestRejectRecord" shadow="never" class="reject-record-card">
              <template #header>
                <div class="flex items-center gap-2 font-medium text-danger">
                  <ElTag type="danger" effect="light">驳回</ElTag>
                  <span>最近一次驳回信息</span>
                </div>
              </template>
              <div class="space-y-2 text-sm">
                <div>驳回人：{{ currentTaskDetail.latestRejectRecord.operatorName || '-' }}</div>
                <div>驳回时间：{{ formatTime(currentTaskDetail.latestRejectRecord.createTime) }}</div>
                <div>驳回原因：{{ currentTaskDetail.latestRejectRecord.remark || '-' }}</div>
              </div>
            </ElCard>

            <ElCard v-if="currentTaskDetail.latestSubmitRecord" shadow="never">
              <template #header>
                <div class="font-medium">最近一次提交</div>
              </template>
              <div class="space-y-2 text-sm">
                <div>提交人：{{ currentTaskDetail.latestSubmitRecord.operatorName || '-' }}</div>
                <div>提交时间：{{ formatTime(currentTaskDetail.latestSubmitRecord.createTime) }}</div>
                <div>提交说明：{{ currentTaskDetail.latestSubmitRecord.remark || '-' }}</div>
                <div v-if="currentTaskDetail.latestSubmitRecord.attachments?.length" class="space-y-1">
                  <div>提交附件：</div>
                  <div class="flex flex-wrap gap-2">
                    <ElLink
                      v-for="item in currentTaskDetail.latestSubmitRecord.attachments"
                      :key="`${item.id}-${item.fileUrl}`"
                      :href="getFileUrl(item.fileUrl)"
                      target="_blank"
                      type="primary"
                    >
                      {{ item.fileName || '附件' }}
                    </ElLink>
                  </div>
                </div>
              </div>
            </ElCard>

            <ElCard v-if="currentTaskDetail.recordList?.length" shadow="never">
              <template #header>
                <div class="font-medium">处理记录</div>
              </template>
              <ElTimeline>
                <ElTimelineItem
                  v-for="record in currentTaskDetail.recordList"
                  :key="record.id"
                  :timestamp="formatTime(record.createTime)"
                  placement="top"
                >
                  <div class="space-y-1">
                    <div class="font-medium">{{ record.actionTypeLabel || '任务操作' }}</div>
                    <div class="text-sm text-gray-600">操作人：{{ record.operatorName || '-' }}</div>
                    <div class="text-sm text-gray-600">说明：{{ record.remark || '-' }}</div>
                    <div v-if="record.attachments?.length" class="flex flex-wrap gap-2 pt-1">
                      <ElLink
                        v-for="item in record.attachments"
                        :key="`${item.id}-${item.fileUrl}`"
                        :href="getFileUrl(item.fileUrl)"
                        target="_blank"
                        type="primary"
                      >
                        {{ item.fileName || '附件' }}
                      </ElLink>
                    </div>
                  </div>
                </ElTimelineItem>
              </ElTimeline>
            </ElCard>
          </div>
        </template>
      </ElSkeleton>
      <template #footer>
        <div v-if="currentTaskDetail?.status === 0" class="flex justify-end gap-2">
          <ElButton :disabled="startLoading" @click="detailVisible = false">取消</ElButton>
          <ElButton type="primary" :loading="startLoading" @click="handleStartTask">开始</ElButton>
        </div>
        <div v-else-if="currentTaskDetail?.status === 1 || currentTaskDetail?.status === 3" class="flex justify-end gap-2">
          <ElButton :disabled="submitLoading" @click="detailVisible = false">取消</ElButton>
          <ElButton type="warning" :loading="submitLoading" @click="openSubmitDialog">提交任务</ElButton>
        </div>
      </template>
    </ElDialog>

    <ElDialog v-model="submitVisible" title="提交任务" width="640px">
      <ElForm ref="submitFormRef" :model="submitForm" :rules="submitRules" label-width="90px">
        <ElFormItem label="提交说明" prop="remark">
          <ElInput v-model="submitForm.remark" type="textarea" :rows="5" placeholder="请填写提交说明" maxlength="1000" show-word-limit />
        </ElFormItem>
        <ElFormItem label="任务附件">
          <div class="w-full space-y-3">
            <ElUpload
              :show-file-list="false"
              :http-request="handleTaskAttachmentUpload"
              :before-upload="beforeTaskAttachmentUpload"
              multiple
            >
              <ElButton plain>上传附件</ElButton>
            </ElUpload>
            <div v-if="submitForm.attachments?.length" class="space-y-2">
              <div v-for="(item, index) in submitForm.attachments" :key="`${item.fileUrl}-${index}`" class="flex items-center gap-2 rounded border border-gray-200 px-3 py-2">
                <div class="min-w-0 flex-1">
                  <div class="truncate text-sm font-medium">{{ item.fileName || '-' }}</div>
                </div>
                <ElButton type="danger" plain @click="removeAttachment(index)">删除</ElButton>
              </div>
            </div>
          </div>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <div class="flex justify-end gap-2">
          <ElButton :disabled="submitLoading" @click="submitVisible = false">取消</ElButton>
          <ElButton type="primary" :loading="submitLoading" @click="handleSubmitTask">确认提交</ElButton>
        </div>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { Search, Refresh, Folder } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadRequestOptions } from 'element-plus'
import { fetchGetMyTaskDetail, fetchGetMyTaskList, fetchStartMyTask, fetchSubmitMyTask  } from '@/api/task/my-task'
import { fetchDeleteTaskAttachment, fetchUploadTaskAttachment } from '@/api/task/attachment'
import { fetchGetProjectInfo } from '@/api/project/detail'
import { getFileUrl } from '@/utils/file'
import TaskColumn from './modules/task-column.vue'
import type { MyTask, MyTaskDetail } from './types'

defineOptions({ name: 'MyTask' })

const searchKeyword = ref('')
const loading = ref(false)
const detailLoading = ref(false)
const startLoading = ref(false)
const submitLoading = ref(false)
const detailVisible = ref(false)
const submitVisible = ref(false)
const currentTaskDetail = ref<MyTaskDetail | null>(null)
const currentProjectInfo = ref<Api.Project.ProjectInfoVO | null>(null)
const tasks = ref<MyTask[]>([])
const submitFormRef = ref<FormInstance>()
const submitForm = reactive<Api.Task.TaskSubmitDTO>({ taskId: 0, remark: '', attachments: [] })
const attachmentRemoving = ref(false)
const submitRules: FormRules = {
  remark: [{ required: true, message: '请填写提交说明', trigger: 'blur' }]
}

const loadTasks = async () => {
  loading.value = true
  try {
    tasks.value = await fetchGetMyTaskList()
  } finally {
    loading.value = false
  }
}

const filteredTasks = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) return tasks.value
  return tasks.value.filter(task => [task.title, task.ownerUser?.nickName].filter(Boolean).some(v => String(v).toLowerCase().includes(keyword)))
})

const sortTasks = (taskList: MyTask[]) => {
  return [...taskList].sort((a, b) => {
    // 计算任务的综合紧急度分数
    const getUrgencyScore = (task: MyTask) => {
      const now = Date.now()
      let score = 0

      // 优先级基础分（紧急=40, 高=30, 中=20, 低=10）
      const priorityScore = {
        4: 40, // 紧急
        3: 30, // 高
        2: 20, // 中
        1: 10  // 低
      }[task.priority || 1] || 10

      score += priorityScore

      // TODO: 截止时间紧迫度分数（最高60分）
      const dueTime = null
      if (dueTime) {
        const dueTimeMs = new Date(dueTime).getTime()
        const timeLeft = dueTimeMs - now
        const hoursLeft = timeLeft / (1000 * 60 * 60)

        if (hoursLeft < 0) {
          // 已逾期，加60分
          score += 60
        } else if (hoursLeft <= 24) {
          // 24小时内，加50分
          score += 50
        } else if (hoursLeft <= 48) {
          // 48小时内，加40分
          score += 40
        } else if (hoursLeft <= 72) {
          // 3天内，加30分
          score += 30
        } else if (hoursLeft <= 168) {
          // 7天内，加20分
          score += 20
        } else {
          // 7天以上，加10分
          score += 10
        }
      }

      return score
    }

    const scoreA = getUrgencyScore(a)
    const scoreB = getUrgencyScore(b)

    // 分数高的排在前面
    if (scoreA !== scoreB) {
      return scoreB - scoreA
    }

    // 分数相同时，按截止时间早的在前
    // const aDueTime = a.DueTime || a.dueTime
    // const bDueTime = b.DueTime || b.dueTime

    const aDueTime = null
    const bDueTime = null

    if (aDueTime && bDueTime) {
      return new Date(aDueTime).getTime() - new Date(bDueTime).getTime()
    }

    // 有截止时间的排在前面
    if (aDueTime && !bDueTime) return -1
    if (!aDueTime && bDueTime) return 1

    return 0
  })
}

const groupedTasks = computed(() => ({
  todo: sortTasks(filteredTasks.value.filter(item => item.status === 0)),
  doing: sortTasks(filteredTasks.value.filter(item => item.status === 1)),
  review: sortTasks(filteredTasks.value.filter(item => item.status === 2)),
  rejected: sortTasks(filteredTasks.value.filter(item => item.status === 3)),
  done: sortTasks(filteredTasks.value.filter(item => item.status === 4))
}))

const totalTaskCount = computed(() => filteredTasks.value.length)
const urgentTaskCount = computed(() => filteredTasks.value.filter(item => item.priority === 4 && item.status !== 4).length)

const handleTaskClick = async (task: MyTask) => {
  detailLoading.value = true
  detailVisible.value = true
  currentProjectInfo.value = null
  try {
    currentTaskDetail.value = await fetchGetMyTaskDetail(task.id)

    // 如果任务有项目ID，获取项目信息
    if (currentTaskDetail.value?.projectId && currentTaskDetail.value.projectId !== 0) {
      try {
        const projectData = await fetchGetProjectInfo(currentTaskDetail.value.projectId)
        if (projectData) {
          currentProjectInfo.value = projectData
        }
      } catch (error) {
        console.error('获取项目信息失败:', error)
      }
    }
  } finally {
    detailLoading.value = false
  }
}

const handleStartTask = async () => {
  if (!currentTaskDetail.value?.id || startLoading.value) return

  startLoading.value = true
  try {
    await fetchStartMyTask(currentTaskDetail.value.id)
    await loadTasks()
    currentTaskDetail.value = await fetchGetMyTaskDetail(currentTaskDetail.value.id)

    ElMessage.success('任务已开始')
    detailVisible.value = false
  } finally {
    startLoading.value = false
  }
}

const openSubmitDialog = () => {
  if (!currentTaskDetail.value?.id) return
  submitForm.taskId = currentTaskDetail.value.id
  submitForm.remark = ''
  submitForm.attachments = []
  submitVisible.value = true
}

const beforeTaskAttachmentUpload = (file: File) => {
  const isLt20M = file.size / 1024 / 1024 < 20
  if (!isLt20M) {
    ElMessage.error('附件大小不能超过 20MB')
    return false
  }
  return true
}

const handleTaskAttachmentUpload = async (options: UploadRequestOptions) => {
  try {
    const fileInfo = await fetchUploadTaskAttachment(options.file as File)
    submitForm.attachments = [
      ...(submitForm.attachments || []),
      {
        fileName: fileInfo.originalName || (options.file as File).name,
        fileUrl: fileInfo.fileUrl,
        filePath: fileInfo.filePath
      }
    ]
    ElMessage.success('附件上传成功')
    options.onSuccess?.(fileInfo)
  } catch (error) {
    console.error('任务附件上传失败:', error)
    ElMessage.error('附件上传失败')
  }
}

const removeAttachment = async (index: number) => {
  const attachment = submitForm.attachments?.[index]
  if (!attachment || attachmentRemoving.value) return

  await ElMessageBox.confirm('删除后将同步移除已上传文件，是否继续？', '删除附件', {
    type: 'warning'
  }).catch(() => false)

  if (!attachment.filePath) {
    submitForm.attachments = (submitForm.attachments || []).filter((_, i) => i !== index)
    return
  }

  attachmentRemoving.value = true
  try {
    await fetchDeleteTaskAttachment(attachment.filePath)
    submitForm.attachments = (submitForm.attachments || []).filter((_, i) => i !== index)
    ElMessage.success('附件已删除')
  } finally {
    attachmentRemoving.value = false
  }
}

const handleSubmitTask = async () => {
  if (!submitFormRef.value || submitLoading.value || !currentTaskDetail.value?.id) return
  const valid = await submitFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await fetchSubmitMyTask({
      taskId: currentTaskDetail.value.id,
      remark: submitForm.remark,
      attachments: (submitForm.attachments || []).filter(item => item.fileUrl)
    })
    await loadTasks()
    currentTaskDetail.value = await fetchGetMyTaskDetail(currentTaskDetail.value.id)
    submitVisible.value = false
    detailVisible.value = false
    ElMessage.success('任务提交成功')
  } finally {
    submitLoading.value = false
  }
}

const formatTime = (value?: string) => {
  if (!value) return '-'
  const date = new Date(value)
  if (isNaN(date.getTime())) return value
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}
const getPriorityLabel = (priority?: number) => {
  switch (priority) {
    case 1:
      return '低'
    case 2:
      return '中'
    case 3:
      return '高'
    case 4:
      return '紧急'
    default:
      return '-'
  }
}
const getUserNames = (users?: Api.Task.TaskUser[]) => users?.map(item => item.nickName || `用户${item.userId}`).join('、') || '-'

onMounted(loadTasks)
</script>

<style scoped>
:deep(.el-card__body) {
  height: 100%;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

:deep(.task-detail-dialog .el-dialog) {
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

:deep(.task-detail-dialog .el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  max-height: calc(90vh - 120px);
  padding: 20px;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

:deep(.task-detail-dialog .el-dialog__body::-webkit-scrollbar) {
  display: none;
}

:deep(.task-detail-dialog .el-dialog__footer) {
  padding-top: 12px;
}

:deep(.reject-record-card) {
  border-color: var(--el-color-danger-light-7);
  background: var(--el-color-danger-light-9);
}

:deep(.reject-record-card .el-card__header) {
  border-bottom-color: var(--el-color-danger-light-7);
}

:deep(.project-info-card) {
  border-color: var(--el-color-primary-light-7);
  background: var(--el-color-primary-light-9);
}

:deep(.project-info-card .el-card__body) {
  padding: 16px;
}

.text-danger {
  color: var(--el-color-danger);
}
</style>
