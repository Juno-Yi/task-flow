<!-- 项目详情 - 项目任务tab页 -->
<template>
  <div class="h-full flex flex-col">
    <!-- 顶部操作栏 -->
    <div class="mb-4 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <ElTag type="info" size="large">{{ totalTaskCount }} 个任务</ElTag>
        <ElTag type="danger" size="large">{{ urgentTaskCount }} 个紧急</ElTag>
        <ElTag type="danger" size="large">{{ overdueTaskCount }} 个逾期</ElTag>
      </div>
      <div class="flex items-center gap-2">
        <ElInput
          v-model="searchKeyword"
          placeholder="搜索任务标题/描述"
          clearable
          class="w-60"
          :prefix-icon="Search"
        />
        <ElButton type="primary" :icon="Plus" @click="handleAddTask" v-ripple>
          新建任务
        </ElButton>
        <ElButton :icon="Refresh" :loading="loading" @click="loadTasks" v-ripple>
          刷新
        </ElButton>
      </div>
    </div>

    <!-- 任务看板 -->
    <ElCard class="flex-1 min-h-0 overflow-hidden" shadow="never">
      <div v-loading="loading" class="flex h-full gap-4">
        <TaskColumn
          title="待处理"
          :tasks="groupedTasks.todo"
          status="0"
          color="#909399"
          @task-click="handleTaskClick"
          @task-edit="handleEditTask"
        />
        <TaskColumn
          title="进行中"
          :tasks="groupedTasks.doing"
          status="1"
          color="#409EFF"
          @task-click="handleTaskClick"
          @task-edit="handleEditTask"
        />
        <TaskColumn
          title="待验收"
          :tasks="groupedTasks.review"
          status="2"
          color="#E6A23C"
          @task-click="handleTaskClick"
          @task-edit="handleEditTask"
        />
        <TaskColumn
          title="已驳回"
          :tasks="groupedTasks.rejected"
          status="3"
          color="#F56C6C"
          @task-click="handleTaskClick"
          @task-edit="handleEditTask"
        />
        <TaskColumn
          title="已完成"
          :tasks="groupedTasks.done"
          status="4"
          color="#67C23A"
          @task-click="handleTaskClick"
          @task-edit="handleEditTask"
        />
      </div>
    </ElCard>

    <!-- 任务详情对话框 -->
    <ElDialog
      v-model="detailVisible"
      title="任务详情"
      width="800px"
      top="5vh"
      destroy-on-close
    >
      <div v-if="currentTask" class="space-y-4">
        <ElDescriptions :column="2" border>
          <ElDescriptionsItem label="任务标题" :span="2">
            <span class="font-medium">{{ currentTask.title || '-' }}</span>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="任务描述" :span="2">
            <div class="whitespace-pre-wrap">{{ currentTask.description || '-' }}</div>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="负责人">
            <div v-if="currentTask.ownerUser" class="flex items-center gap-2">
              <ElAvatar :size="24" :src="currentTask.ownerUser.avatar">
                {{ currentTask.ownerUser.nickName?.slice(0, 1) || 'U' }}
              </ElAvatar>
              <span>{{ currentTask.ownerUser.nickName || '-' }}</span>
            </div>
            <span v-else>-</span>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="优先级">
            <ElTag :type="getPriorityType(currentTask.priority)" size="default">
              {{ getPriorityLabel(currentTask.priority) }}
            </ElTag>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="计划开始时间">
            {{ formatTime(currentTask.planStartTime) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="计划结束时间">
            {{ formatTime(currentTask.planEndTime) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="预计工时">
            {{ calculateHours(currentTask.planStartTime, currentTask.planEndTime) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="是否逾期">
            <ElTag :type="currentTask.isOverdue ? 'danger' : 'success'" size="default">
              {{ currentTask.isOverdue ? '已逾期' : '未逾期' }}
            </ElTag>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="协作人" :span="2">
            <div v-if="currentTask.taskUserList?.length" class="flex flex-wrap gap-2">
              <div
                v-for="user in currentTask.taskUserList"
                :key="user.userId"
                class="flex items-center gap-1"
              >
                <ElAvatar :size="24" :src="user.avatar">
                  {{ user.nickName?.slice(0, 1) || 'U' }}
                </ElAvatar>
                <span class="text-sm">{{ user.nickName || '-' }}</span>
              </div>
            </div>
            <span v-else>-</span>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="备注" :span="2">
            <div class="whitespace-pre-wrap">{{ currentTask.remark || '无' }}</div>
          </ElDescriptionsItem>
        </ElDescriptions>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <ElButton @click="detailVisible = false">关闭</ElButton>
          <ElButton type="primary" @click="handleEditFromDetail">编辑</ElButton>
        </div>
      </template>
    </ElDialog>

    <!-- 新建/编辑任务对话框 -->
    <ElDialog
      v-model="formVisible"
      :title="formType === 'add' ? '新建任务' : '编辑任务'"
      width="720px"
      destroy-on-close
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <ElFormItem label="任务标题" prop="title">
          <ElInput
            v-model="formData.title"
            placeholder="请输入任务标题"
            maxlength="100"
            show-word-limit
          />
        </ElFormItem>
        <ElFormItem label="任务描述" prop="description">
          <ElInput
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入任务描述"
            maxlength="500"
            show-word-limit
          />
        </ElFormItem>
        <ElFormItem label="优先级" prop="priority">
          <ElSelect v-model="formData.priority" placeholder="请选择优先级" class="w-full">
            <ElOption label="低" :value="1" />
            <ElOption label="中" :value="2" />
            <ElOption label="高" :value="3" />
            <ElOption label="紧急" :value="4" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="负责人" prop="ownerUserId">
          <ElSelect
            v-model="formData.ownerUserId"
            placeholder="请选择负责人"
            filterable
            remote
            :remote-method="handleSearchMembers"
            :loading="memberLoading"
            class="w-full"
          >
            <ElOption
              v-for="user in projectMembers"
              :key="user.userId"
              :label="user.nickName"
              :value="user.userId"
            >
              <div class="flex items-center gap-2">
                <ElAvatar :size="24" :src="user.avatar">
                  {{ user.nickName?.slice(0, 1) || 'U' }}
                </ElAvatar>
                <span>{{ user.nickName }}</span>
              </div>
            </ElOption>
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="协作人" prop="userIds">
          <ElSelect
            v-model="formData.userIds"
            placeholder="请选择协作人"
            multiple
            filterable
            remote
            :remote-method="handleSearchMembers"
            :loading="memberLoading"
            class="w-full"
          >
            <ElOption
              v-for="user in projectMembers"
              :key="user.userId"
              :label="user.nickName"
              :value="user.userId"
            >
              <div class="flex items-center gap-2">
                <ElAvatar :size="24" :src="user.avatar">
                  {{ user.nickName?.slice(0, 1) || 'U' }}
                </ElAvatar>
                <span>{{ user.nickName }}</span>
              </div>
            </ElOption>
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="计划时间">
          <div class="plan-time-wrapper">
            <!-- 开始时间 -->
            <ElDatePicker
              v-model="formData.planStartTime"
              type="datetime"
              placeholder="开始时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              class="plan-time-picker"
              @change="handleStartTimeChange"
            />

            <span class="time-separator">~</span>

            <!-- 结束时间 -->
            <ElDatePicker
              v-model="formData.planEndTime"
              type="datetime"
              placeholder="结束时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              class="plan-time-picker"
              :disabled-date="disabledEndDate"
              :disabled-hours="disabledEndHours"
              @change="handleEndTimeChange"
            />

            <!-- 小时数输入 -->
            <div class="hours-input-wrapper">
              <span class="hours-label">或</span>
              <ElInputNumber
                v-model="planHours"
                :min="0.5"
                :max="8760"
                :step="0.5"
                :precision="1"
                placeholder="小时数"
                class="hours-input"
                controls-position="right"
                @change="handleHoursChange"
              />
              <span class="hours-unit">小时</span>
            </div>
          </div>
          <div class="plan-time-tip">
            可直接选择开始和结束时间，或选择开始时间后输入小时数自动计算结束时间
          </div>
        </ElFormItem>
        <ElFormItem label="备注" prop="remark">
          <ElInput
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
            maxlength="200"
            show-word-limit
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <div class="flex justify-end gap-2">
          <ElButton @click="formVisible = false">取消</ElButton>
          <ElButton type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </ElButton>
        </div>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { fetchGetProjectTaskList, fetchAddProjectTask, fetchUpdateProjectTask } from '@/api/project/task'
import { fetchGetProjectMemberOptions } from '@/api/project/member'
import TaskColumn from './modules/task/task-column.vue'

defineOptions({ name: 'ProjectTaskTab' })

interface Props {
  projectId: number
}

const props = defineProps<Props>()

// 状态
const loading = ref(false)
const detailVisible = ref(false)
const formVisible = ref(false)
const submitLoading = ref(false)
const searchKeyword = ref('')
const formType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()

// 任务列表
const taskList = ref<Api.Project.ProjectTaskItemVO[]>([])
const currentTask = ref<Api.Project.ProjectTaskItemVO>()

// 项目成员列表
const projectMembers = ref<Api.System.SysUserVO[]>([])
const memberLoading = ref(false)

// 计划工时（小时数）
const planHours = ref<number>()

// 表单数据
const formData = ref<{
  id?: number
  title: string
  description: string
  priority: number
  ownerUserId?: number
  userIds: number[]
  planStartTime: string | null
  planEndTime: string | null
  remark: string
}>({
  title: '',
  description: '',
  priority: 2,
  ownerUserId: undefined,
  userIds: [],
  planStartTime: null,
  planEndTime: null,
  remark: ''
})

// 表单验证规则
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  ownerUserId: [
    { required: true, message: '请选择负责人', trigger: 'change' }
  ]
}

// 过滤后的任务列表
const filteredTasks = computed(() => {
  if (!searchKeyword.value) return taskList.value

  const keyword = searchKeyword.value.toLowerCase()
  return taskList.value.filter(task =>
    task.title?.toLowerCase().includes(keyword) ||
    task.description?.toLowerCase().includes(keyword)
  )
})

// 分组任务
const groupedTasks = computed(() => {
  const groups = {
    todo: [] as Api.Project.ProjectTaskItemVO[],
    doing: [] as Api.Project.ProjectTaskItemVO[],
    review: [] as Api.Project.ProjectTaskItemVO[],
    rejected: [] as Api.Project.ProjectTaskItemVO[],
    done: [] as Api.Project.ProjectTaskItemVO[]
  }

  filteredTasks.value.forEach(task => {
    switch (task.status) {
      case 0:
        groups.todo.push(task)
        break
      case 1:
        groups.doing.push(task)
        break
      case 2:
        groups.review.push(task)
        break
      case 3:
        groups.rejected.push(task)
        break
      case 4:
        groups.done.push(task)
        break
    }
  })

  return groups
})

// 统计数据
const totalTaskCount = computed(() => filteredTasks.value.length)

// 紧急任务数量（排除已完成的任务）
const urgentTaskCount = computed(() =>
  filteredTasks.value.filter(t => t.priority === 4 && t.status !== 4).length
)

// 逾期任务数量（排除已完成的任务）
const overdueTaskCount = computed(() =>
  filteredTasks.value.filter(t => t.isOverdue && t.status !== 4).length
)

/**
 * 加载任务列表
 */
const loadTasks = async () => {
  if (!props.projectId) return

  loading.value = true
  try {
    const data = await fetchGetProjectTaskList(props.projectId)
    taskList.value = data || []
  } catch (error) {
    console.error('加载任务列表失败:', error)
    ElMessage.error('加载任务列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载项目成员列表
 */
const loadProjectMembers = async (nickName?: string) => {
  if (!props.projectId) return

  memberLoading.value = true
  try {
    const data = await fetchGetProjectMemberOptions(props.projectId, nickName)
    projectMembers.value = data || []
  } catch (error) {
    console.error('加载项目成员失败:', error)
    ElMessage.error('加载项目成员失败')
  } finally {
    memberLoading.value = false
  }
}

/**
 * 搜索项目成员（远程搜索）
 */
const handleSearchMembers = (query: string) => {
  loadProjectMembers(query)
}

/**
 * 禁用结束日期（不能早于开始日期）
 */
const disabledEndDate = (time: Date) => {
  if (!formData.value.planStartTime) return false
  const startDate = new Date(formData.value.planStartTime)
  return time.getTime() < startDate.getTime() - 24 * 60 * 60 * 1000
}

/**
 * 禁用结束时间的小时（如果是同一天，不能早于开始时间）
 */
const disabledEndHours = () => {
  if (!formData.value.planStartTime || !formData.value.planEndTime) return []

  const startTime = new Date(formData.value.planStartTime)
  const endTime = new Date(formData.value.planEndTime)

  // 如果不是同一天，不禁用任何小时
  if (startTime.toDateString() !== endTime.toDateString()) {
    return []
  }

  // 如果是同一天，禁用早于开始时间的小时
  const startHour = startTime.getHours()
  const disabledHours: number[] = []
  for (let i = 0; i < startHour; i++) {
    disabledHours.push(i)
  }
  return disabledHours
}

/**
 * 开始时间变化时，清空小时数输入
 */
const handleStartTimeChange = () => {
  planHours.value = undefined
  // 如果有结束时间，计算小时数
  if (formData.value.planStartTime && formData.value.planEndTime) {
    const start = new Date(formData.value.planStartTime)
    const end = new Date(formData.value.planEndTime)
    const diffMs = end.getTime() - start.getTime()
    const diffHours = diffMs / (1000 * 60 * 60)
    if (diffHours > 0) {
      planHours.value = Math.round(diffHours * 10) / 10 // 保留1位小数
    } else {
      // 如果结束时间早于开始时间，清空结束时间
      formData.value.planEndTime = null
      ElMessage.warning('结束时间不能早于开始时间')
    }
  }
}

/**
 * 结束时间变化时，计算小时数
 */
const handleEndTimeChange = () => {
  planHours.value = undefined
  // 如果有开始时间和结束时间，计算小时数
  if (formData.value.planStartTime && formData.value.planEndTime) {
    const start = new Date(formData.value.planStartTime)
    const end = new Date(formData.value.planEndTime)
    const diffMs = end.getTime() - start.getTime()
    const diffHours = diffMs / (1000 * 60 * 60)
    if (diffHours > 0) {
      planHours.value = Math.round(diffHours * 10) / 10 // 保留1位小数
    } else {
      // 如果结束时间早于开始时间，清空结束时间并提示
      formData.value.planEndTime = null
      ElMessage.warning('结束时间不能早于开始时间')
    }
  }
}

/**
 * 小时数变化时，自动计算结束时间
 */
const handleHoursChange = (value: number | undefined) => {
  if (!value || !formData.value.planStartTime) {
    return
  }

  const startDate = new Date(formData.value.planStartTime)
  const endDate = new Date(startDate.getTime() + value * 60 * 60 * 1000)

  // 格式化为 YYYY-MM-DD HH:mm:ss
  const year = endDate.getFullYear()
  const month = String(endDate.getMonth() + 1).padStart(2, '0')
  const day = String(endDate.getDate()).padStart(2, '0')
  const hours = String(endDate.getHours()).padStart(2, '0')
  const minutes = String(endDate.getMinutes()).padStart(2, '0')
  const seconds = String(endDate.getSeconds()).padStart(2, '0')

  formData.value.planEndTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

/**
 * 处理任务点击
 */
const handleTaskClick = (task: Api.Project.ProjectTaskItemVO) => {
  currentTask.value = task
  detailVisible.value = true
}

/**
 * 处理新建任务
 */
const handleAddTask = () => {
  formType.value = 'add'
  formData.value = {
    title: '',
    description: '',
    priority: 2,
    ownerUserId: undefined,
    userIds: [],
    planStartTime: null,
    planEndTime: null,
    remark: ''
  }
  planHours.value = undefined  // 重置小时数
  formVisible.value = true

  // 加载项目成员
  loadProjectMembers()
}

/**
 * 处理编辑任务
 */
const handleEditTask = (task: Api.Project.ProjectTaskItemVO) => {
  formType.value = 'edit'
  formData.value = {
    id: task.id,
    title: task.title,
    description: task.description || '',
    priority: task.priority || 2,
    ownerUserId: task.ownerUser?.userId,
    userIds: task.taskUserList?.map(u => u.userId) || [],
    planStartTime: task.planStartTime || null,
    planEndTime: task.planEndTime || null,
    remark: task.remark || ''
  }

  // 计算小时数
  if (task.planStartTime && task.planEndTime) {
    const start = new Date(task.planStartTime)
    const end = new Date(task.planEndTime)
    const diffMs = end.getTime() - start.getTime()
    const diffHours = diffMs / (1000 * 60 * 60)
    planHours.value = diffHours > 0 ? Math.round(diffHours * 10) / 10 : undefined
  } else {
    planHours.value = undefined
  }

  formVisible.value = true

  // 加载项目成员
  loadProjectMembers()
}

/**
 * 从详情页编辑
 */
const handleEditFromDetail = () => {
  if (currentTask.value) {
    detailVisible.value = false
    handleEditTask(currentTask.value)
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (formType.value === 'add') {
        await fetchAddProjectTask({
          projectId: props.projectId,
          title: formData.value.title,
          description: formData.value.description,
          priority: formData.value.priority,
          ownerUserId: formData.value.ownerUserId,
          userIds: formData.value.userIds,
          planStartTime: formData.value.planStartTime || undefined,
          planEndTime: formData.value.planEndTime || undefined,
          remark: formData.value.remark
        })
        ElMessage.success('创建任务成功')
      } else {
        await fetchUpdateProjectTask(props.projectId, {
          id: formData.value.id!,
          title: formData.value.title,
          description: formData.value.description,
          priority: formData.value.priority,
          ownerUserId: formData.value.ownerUserId,
          userIds: formData.value.userIds,
          planStartTime: formData.value.planStartTime || undefined,
          planEndTime: formData.value.planEndTime || undefined,
          remark: formData.value.remark
        })
        ElMessage.success('更新任务成功')
      }

      formVisible.value = false
      await loadTasks()
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error(formType.value === 'add' ? '创建任务失败' : '更新任务失败')
    } finally {
      submitLoading.value = false
    }
  })
}

/**
 * 获取优先级标签
 */
const getPriorityLabel = (priority?: number) => {
  const map: Record<number, string> = {
    1: '低',
    2: '中',
    3: '高',
    4: '紧急'
  }
  return map[priority ?? 0] || '-'
}

/**
 * 获取优先级类型
 */
const getPriorityType = (priority?: number): 'info' | 'primary' | 'warning' | 'danger' => {
  const map: Record<number, 'info' | 'primary' | 'warning' | 'danger'> = {
    1: 'info',
    2: 'primary',
    3: 'warning',
    4: 'danger'
  }
  return map[priority ?? 0] || 'info'
}

/**
 * 格式化时间
 */
const formatTime = (time?: string): string => {
  if (!time) return '-'
  const date = new Date(time)
  if (isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

/**
 * 计算工时
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
    return `${minutes} 分钟`
  }

  if (diffHours < 24) {
    return `${diffHours.toFixed(1)} 小时`
  }

  const days = Math.floor(diffHours / 24)
  const hours = Math.round(diffHours % 24)
  return hours > 0 ? `${days} 天 ${hours} 小时` : `${days} 天`
}

// 生命周期
onMounted(() => {
  loadTasks()
})
</script>

<style scoped lang="scss">
// 计划时间选择器样式
.plan-time-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.plan-time-picker {
  flex: 1;
  min-width: 200px;
}

.time-separator {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  flex-shrink: 0;
}

.hours-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.hours-label {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.hours-input {
  width: 120px;
}

.hours-unit {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.plan-time-tip {
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}
</style>