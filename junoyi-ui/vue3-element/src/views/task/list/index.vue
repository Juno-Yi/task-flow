<template>
  <div class="art-full-height">
    <TaskSearch
      v-show="showSearchBar"
      v-model="searchForm"
      @search="handleSearch"
      @reset="handleReset"
    />

    <ElCard
      class="art-table-card"
      shadow="never"
      :style="{ 'margin-top': showSearchBar ? '12px' : '0' }"
    >
      <ArtTableHeader
        v-model:columns="columnChecks"
        v-model:showSearchBar="showSearchBar"
        :loading="loading"
        @refresh="refreshData"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton  @click="showDialog('add')"  v-permission="'task.ui.list.add.button'" v-ripple>新增任务</ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>
    <TaskDialog
      v-model="dialogVisible"
      :dialog-type="dialogType"
      :task-data="currentTaskData"
      :loading="dialogLoading"
      @success="handleDialogSuccess"
    />

    <ElDrawer v-model="detailVisible" title="任务详情" size="760px" destroy-on-close class="task-detail-drawer">
      <ElSkeleton :loading="detailLoading" animated>
        <template #default>
          <div v-if="currentTaskDetail" class="space-y-4">
            <ElDescriptions :column="2" border>
              <ElDescriptionsItem label="任务标题" :span="2">{{ currentTaskDetail.title || '-' }}</ElDescriptionsItem>
              <ElDescriptionsItem label="任务描述" :span="2">{{ currentTaskDetail.description || '-' }}</ElDescriptionsItem>
              <ElDescriptionsItem label="负责人">{{ currentTaskDetail.ownerUser?.nickName || '-' }}</ElDescriptionsItem>
              <ElDescriptionsItem label="截止时间">{{ formatTime(currentTaskDetail.dueTime) }}</ElDescriptionsItem>
              <ElDescriptionsItem label="执行人" :span="2">{{ getUserNames(currentTaskDetail.taskUserList) }}</ElDescriptionsItem>
              <ElDescriptionsItem label="备注" :span="2">{{ currentTaskDetail.remark || '-' }}</ElDescriptionsItem>
            </ElDescriptions>

            <ElCard v-if="currentTaskDetail.recordList?.length" shadow="never">
              <template #header><div class="font-medium">操作记录</div></template>
              <ElTimeline>
                <ElTimelineItem v-for="record in currentTaskDetail.recordList" :key="record.id" :timestamp="formatTime(record.createTime)" placement="top">
                  <div class="space-y-1">
                    <div class="font-medium">{{ record.actionTypeLabel || '任务操作' }}</div>
                    <div class="text-sm text-gray-600">操作人：{{ record.operatorName || '-' }}</div>
                    <div class="text-sm text-gray-600">说明：{{ record.remark || '-' }}</div>
                    <div v-if="record.attachments?.length" class="flex flex-wrap gap-2 pt-1">
                      <ElLink v-for="item in record.attachments" :key="`${item.id}-${item.fileUrl}`" :href="getFileUrl(item.fileUrl)" target="_blank" type="primary">{{ item.fileName || '附件' }}</ElLink>
                    </div>
                  </div>
                </ElTimelineItem>
              </ElTimeline>
            </ElCard>
          </div>
        </template>
      </ElSkeleton>
    </ElDrawer>
  </div>
</template>

<script setup lang="ts">
import { ElAvatar, ElMessage, ElMessageBox, ElTag, ElTooltip } from 'element-plus'
import ArtButtonMore, { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
import { usePermission } from '@/hooks/core/usePermission'
import { useTable } from '@/hooks/core/useTable'
import { fetchAddTask, fetchGetTaskDetail, fetchGetTaskList, fetchRemindTask, fetchUpdateTask } from '@/api/task/list'
import { fetchGetDictDataByType } from '@/api/system/dict'
import { getFileUrl } from '@/utils/file'
import TaskSearch from './modules/task-search.vue'
import TaskDialog from './modules/task-dialog.vue'

defineOptions({ name: 'TaskList' })

type TaskListVO = Api.Task.TaskListVO
type TaskFormData = Api.Task.TaskFormData
type TaskActionKey = 'detail' | 'edit' | 'delete' | 'remind'

const { hasPermission } = usePermission()
const statusDict = ref<Api.System.DictDataVO[]>([])
const priorityDict = ref<Api.System.DictDataVO[]>([])

const searchForm = ref<Api.Task.TaskListQueryDTO & { timeRange?: string[] }>({
  title: undefined,
  status: undefined,
  priority: undefined,
  userId: undefined,
  startTime: undefined,
  endTime: undefined,
  timeRange: undefined
})

const showSearchBar = ref(true)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const dialogLoading = ref(false)
const detailLoading = ref(false)
const currentTaskData = ref<Api.Task.TaskListDetailVO | undefined>(undefined)
const currentTaskDetail = ref<Api.Task.TaskListDetailVO | undefined>(undefined)


const showDialog = async (type: 'add' | 'edit', row?: TaskListVO) => {
  dialogType.value = type

  if (type === 'add') {
    currentTaskData.value = undefined
    dialogVisible.value = true
    return
  }

  if (!row?.id) {
    ElMessage.warning('任务ID不能为空')
    return
  }

  dialogLoading.value = true
  try {
    currentTaskData.value = await fetchGetTaskDetail(row.id)
    dialogVisible.value = true
  } finally {
    dialogLoading.value = false
  }
}

const handleDialogSuccess = async (formData: TaskFormData) => {
  const payload: Api.Task.TaskListDTO = {
    id: formData.id,
    title: formData.title,
    description: formData.description,
    priority: formData.priority,
    ownerUserId: formData.ownerUserId,
    userIds: formData.userIds,
    dueTime: formData.dueTime,
    remark: formData.remark,
    syncSchedule: formData.syncSchedule
  }

  if (dialogType.value === 'edit') {
    await fetchUpdateTask(payload)
    ElMessage.success('修改任务成功')
    refreshData()
    return
  }

  await fetchAddTask(payload)
  ElMessage.success('新增任务成功')
  refreshData()
}

const loadTaskDict = async () => {
  const [statusRes, priorityRes] = await Promise.all([
    fetchGetDictDataByType('task_status'),
    fetchGetDictDataByType('task_priority')
  ])
  statusDict.value = statusRes || []
  priorityDict.value = priorityRes || []
}

const openDetailDrawer = async (row: TaskListVO) => {
  if (!row?.id) {
    ElMessage.warning('任务ID不能为空')
    return
  }
  detailVisible.value = true
  detailLoading.value = true
  try {
    currentTaskDetail.value = await fetchGetTaskDetail(row.id)
  } finally {
    detailLoading.value = false
  }
}

const handleRemindTask = async (row: TaskListVO) => {
  if (!row?.id) {
    ElMessage.warning('任务ID不能为空')
    return
  }
  await ElMessageBox.confirm(`确认向任务“${row.title || row.id}”的关联人员发送催办通知吗？`, '催办确认', {
    type: 'warning',
    confirmButtonText: '确认催办',
    cancelButtonText: '取消'
  })
  await fetchRemindTask(row.id)
  ElMessage.success('催办通知已发送')
}

const handleButtonMoreClick = (item: ButtonMoreItem, row: TaskListVO) => {
  switch (String(item.key) as TaskActionKey) {
    case 'detail':
      openDetailDrawer(row)
      break
    case 'edit':
      showDialog('edit', row)
      break
    case 'remind':
      handleRemindTask(row)
      break
    case 'delete':
      ElMessage.warning(`请接入删除接口：${row.title}`)
      break
  }
}

const getUserNames = (users?: Api.Task.TaskUser[]) => users?.map(item => item.nickName || `用户${item.userId}`).join('、') || '-'

const renderAssignees = (users?: Api.Task.TaskUser[]) => {
  if (!users?.length) return '-'

  const visibleUsers = users.slice(0, 3)
  const hiddenUsers = users.slice(3)

  return h('div', { class: 'flex items-center' }, [
    ...visibleUsers.map((user, index) =>
      h(
        ElTooltip,
        { content: user.nickName || `用户${user.userId}`, placement: 'top' },
        {
          default: () =>
            h(
              ElAvatar,
              {
                size: 28,
                src: user.avatar,
                class: index > 0 ? '-ml-2 border-2 border-white dark:border-[var(--el-bg-color)]' : ''
              },
              () => (user.nickName?.slice(0, 1) || 'U').toUpperCase()
            )
        }
      )
    ),
    hiddenUsers.length > 0
      ? h(
          ElTooltip,
          {
            content: hiddenUsers.map(user => user.nickName || `用户${user.userId}`).join('、'),
            placement: 'top'
          },
          {
            default: () =>
              h(
                'div',
                {
                  class:
                    'ml-2 flex h-7 min-w-7 items-center justify-center rounded-full bg-[var(--el-fill-color-light)] px-2 text-xs text-[var(--el-text-color-regular)]'
                },
                `+${hiddenUsers.length}`
              )
          }
        )
      : null
  ])
}

const {
  columns,
  columnChecks,
  data,
  loading,
  pagination,
  getData,
  searchParams,
  resetSearchParams,
  handleSizeChange,
  handleCurrentChange,
  refreshData
} = useTable({
  core: {
    apiFn: fetchGetTaskList,
    apiParams: {
      current: 1,
      size: 20
    },
    columnsFactory: () => [
      {
        prop: 'title',
        label: '任务',
        align: 'center',
        headerAlign: 'center',
        minWidth: 220,
        showOverflowTooltip: true
      },
      {
        prop: 'statusLabel',
        label: '状态',
        align: 'center',
        headerAlign: 'center',
        width: 100,
        formatter: (row: TaskListVO) => {
          const text = row.statusLabel || getTaskStatusLabel(row.status)
          const type = (row.statusType || getTaskStatusType(row.status)) as
            | 'success'
            | 'info'
            | 'warning'
            | 'danger'
          return h(ElTag, { type, size: 'small' }, () => text)
        }
      },
      {
        prop: 'priorityLabel',
        label: '优先级',
        align: 'center',
        headerAlign: 'center',
        width: 100,
        formatter: (row: TaskListVO) => {
          const text = row.priorityLabel || getTaskPriorityLabel(row.priority)
          const type = (row.priorityType || getTaskPriorityType(row.priority)) as
            | 'success'
            | 'info'
            | 'warning'
            | 'danger'
          return h(ElTag, { type, size: 'small' }, () => text)
        }
      },
      {
        prop: 'ownerUser.nickName',
        label: '负责人',
        align: 'center',
        headerAlign: 'center',
        minWidth: 120,
        formatter: (row: TaskListVO) => row.ownerUser?.nickName || '-'
      },
      {
        prop: 'taskUserList',
        label: '协助人',
        headerAlign: 'center',
        minWidth: 180,
        formatter: (row: TaskListVO) => renderAssignees(row.taskUserList)
      },
      {
        prop: 'planPeriod',
        label: '计划时间',
        width: 250,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: TaskListVO) => {
          if (row.planStartTime && row.planEndTime) {
            const startDate = formatTime(row.planStartTime as any)
            const endDate = formatTime(row.planEndTime as any)
            return `${startDate} ~ ${endDate}`
          } else if (row.planStartTime) {
            return `${formatTime(row.planStartTime as any)} ~ 未设置`
          } else if (row.planEndTime) {
            return `未设置 ~ ${formatTime(row.planEndTime as any)}`
          }
          return '未设置'
        }
      },
      {
        prop: 'realPeriod',
        label: '实际时间',
        width: 250,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: TaskListVO) => {
          if (row.startTime && row.endTime) {
            const startDate = formatTime(row.startTime as any)
            const endDate = formatTime(row.endTime as any)
            return `${startDate} ~ ${endDate}`
          } else if (row.startTime) {
            return `${formatTime(row.startTime as any)} ~ 未结束`
          } else if (row.endTime) {
            return `无 ~ ${formatTime(row.planEndTime as any)}`
          }
          return '无'
        }
      },
      {
        prop: 'isOverdue',
        label: '是否逾期',
        align: 'center',
        headerAlign: 'center',
        width: 100,
        formatter: (row: TaskListVO) => {
          return h(ElTag, { type: row.isOverdue ? 'danger' : 'success', size: 'small' }, () =>
            row.isOverdue ? '已逾期' : '正常'
          )
        }
      },
      {
        prop: 'operation',
        label: '操作',
        width: 80,
        align: 'center',
        headerAlign: 'center',
        fixed: 'right',
        formatter: (row: TaskListVO) => {
          const list: ButtonMoreItem[] = [
            { key: 'detail', label: '查看详情', icon: 'ri:eye-line' },
            {
              key: 'edit',
              label: '编辑任务',
              icon: 'ri:edit-2-line',
              auth: 'task.ui.list.edit.button'
            },
            ...(row.status !== 4
              ? [{ key: 'remind', label: '催办任务', icon: 'ri:alarm-warning-line', auth: 'task.ui.list.edit.button' } as ButtonMoreItem]
              : [])
            // { key: 'delete', label: '删除任务', icon: 'ri:delete-bin-4-line', color: '#f56c6c' }
          ]
          const visibleList = list.filter(item => !item.auth || hasPermission(item.auth))
          return visibleList.length
            ? h(ArtButtonMore, {
                list: visibleList,
                onClick: (item) => handleButtonMoreClick(item, row)
              })
            : '-'
        }
      }
    ]
  }
})


onMounted(() => {
  loadTaskDict()
})

const handleSearch = (params: Record<string, any>) => {
  const { timeRange, ...rest } = params
  Object.assign(searchParams, {
    ...rest,
    startTime: Array.isArray(timeRange) ? timeRange[0] : undefined,
    endTime: Array.isArray(timeRange) ? timeRange[1] : undefined
  })
  getData()
}

const handleReset = () => {
  Object.assign(searchForm.value, {
    title: undefined,
    status: undefined,
    priority: undefined,
    userId: undefined,
    startTime: undefined,
    endTime: undefined,
    timeRange: undefined
  })
  resetSearchParams()
  getData()
}

const formatTime = (time?: string): string => {
  if (!time) return '-'
  const date = new Date(time)
  if (isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const getTaskStatusLabel = (status?: number) => {
  const match = statusDict.value.find(item => Number(item.dictValue) === status)
  if (match?.dictLabel) return match.dictLabel
  switch (status) {
    case 0:
      return '待处理'
    case 1:
      return '进行中'
    case 2:
      return '已完成'
    case 3:
      return '已关闭'
    default:
      return '-'
  }
}

const getTaskStatusType = (status?: number) => {
  const match = statusDict.value.find(item => Number(item.dictValue) === status)
  if (match?.listClass) return match.listClass as 'success' | 'info' | 'warning' | 'danger'
  switch (status) {
    case 0:
      return 'info'
    case 1:
      return 'warning'
    case 2:
      return 'success'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
}

const getTaskPriorityLabel = (priority?: number) => {
  const match = priorityDict.value.find(item => Number(item.dictValue) === priority)
  if (match?.dictLabel) return match.dictLabel
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

const getTaskPriorityType = (priority?: number) => {
  const match = priorityDict.value.find(item => Number(item.dictValue) === priority)
  if (match?.listClass) return match.listClass as 'success' | 'info' | 'warning' | 'danger'
  switch (priority) {
    case 1:
      return 'info'
    case 2:
      return 'success'
    case 3:
      return 'warning'
    case 4:
      return 'danger'
    default:
      return 'info'
  }
}
</script>

<style scoped>
:deep(.task-detail-drawer .el-drawer__body) {
  overflow-y: auto;
}
</style>