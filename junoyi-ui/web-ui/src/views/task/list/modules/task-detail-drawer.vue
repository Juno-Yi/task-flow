<template>
  <ElDrawer 
    v-model="drawerVisible" 
    title="任务详情" 
    size="760px" 
    destroy-on-close 
    class="task-detail-drawer"
  >
    <ElSkeleton :loading="loading" animated>
      <template #default>
        <div v-if="taskDetail" class="task-detail-content">
          <!-- 基本信息 -->
          <ElCard shadow="never" class="detail-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">基本信息</span>
              </div>
            </template>
            <ElDescriptions :column="2" border>
              <ElDescriptionsItem label="任务标题" :span="2">
                <span class="font-medium">{{ taskDetail.title || '-' }}</span>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="任务描述" :span="2">
                <div class="task-description">{{ taskDetail.description || '-' }}</div>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="任务状态">
                <ElTag :type="taskDetail.statusType as any" size="default">
                  {{ taskDetail.statusLabel || '-' }}
                </ElTag>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="优先级">
                <ElTag :type="taskDetail.priorityType as any" size="default">
                  {{ taskDetail.priorityLabel || '-' }}
                </ElTag>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="是否逾期">
                <ElTag :type="taskDetail.isOverdue ? 'danger' : 'success'" size="default">
                  {{ taskDetail.isOverdue ? '已逾期' : '未逾期' }}
                </ElTag>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="所属项目">
                {{ taskDetail.projectId ? `项目 #${taskDetail.projectId}` : '普通任务' }}
              </ElDescriptionsItem>
            </ElDescriptions>
          </ElCard>

          <!-- 人员信息 -->
          <ElCard shadow="never" class="detail-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">人员信息</span>
              </div>
            </template>
            <ElDescriptions :column="2" border>
              <ElDescriptionsItem label="负责人">
                <div v-if="taskDetail.ownerUser" class="user-info">
                  <ElAvatar :size="32" :src="taskDetail.ownerUser.avatar">
                    {{ taskDetail.ownerUser.nickName?.slice(0, 1) || 'U' }}
                  </ElAvatar>
                  <span class="user-name">{{ taskDetail.ownerUser.nickName || '-' }}</span>
                </div>
                <span v-else>-</span>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="协作人" :span="2">
                <div v-if="taskDetail.taskUserList?.length" class="user-list">
                  <div v-for="user in taskDetail.taskUserList" :key="user.userId" class="user-info">
                    <ElAvatar :size="32" :src="user.avatar">
                      {{ user.nickName?.slice(0, 1) || 'U' }}
                    </ElAvatar>
                    <span class="user-name">{{ user.nickName || '-' }}</span>
                  </div>
                </div>
                <span v-else>-</span>
              </ElDescriptionsItem>
            </ElDescriptions>
          </ElCard>

          <!-- 时间信息 -->
          <ElCard shadow="never" class="detail-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">时间信息</span>
              </div>
            </template>
            <ElDescriptions :column="2" border>
              <ElDescriptionsItem label="计划开始时间">
                <div class="time-info">
                  <ElIcon class="time-icon"><Clock /></ElIcon>
                  <span>{{ formatTime(taskDetail.planStartTime) }}</span>
                </div>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="计划结束时间">
                <div class="time-info">
                  <ElIcon class="time-icon"><Clock /></ElIcon>
                  <span>{{ formatTime(taskDetail.planEndTime) }}</span>
                </div>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="实际开始时间">
                <div class="time-info">
                  <ElIcon class="time-icon"><VideoPlay /></ElIcon>
                  <span>{{ formatTime(taskDetail.startTime) }}</span>
                </div>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="实际结束时间">
                <div class="time-info">
                  <ElIcon class="time-icon"><CircleCheck /></ElIcon>
                  <span>{{ formatTime(taskDetail.endTime) }}</span>
                </div>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="计划工时" :span="2">
                <div class="time-info">
                  <ElIcon class="time-icon"><Timer /></ElIcon>
                  <span>{{ calculateHours(taskDetail.planStartTime, taskDetail.planEndTime) }}</span>
                </div>
              </ElDescriptionsItem>
            </ElDescriptions>
          </ElCard>

          <!-- 其他信息 -->
          <ElCard shadow="never" class="detail-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">其他信息</span>
              </div>
            </template>
            <ElDescriptions :column="2" border>
              <ElDescriptionsItem label="创建人">
                {{ taskDetail.createBy || '-' }}
              </ElDescriptionsItem>
              <ElDescriptionsItem label="创建时间">
                {{ formatTime(taskDetail.createTime) }}
              </ElDescriptionsItem>
              <ElDescriptionsItem label="更新人">
                {{ taskDetail.updateBy || '-' }}
              </ElDescriptionsItem>
              <ElDescriptionsItem label="更新时间">
                {{ formatTime(taskDetail.updateTime) }}
              </ElDescriptionsItem>
              <ElDescriptionsItem label="备注" :span="2">
                <div class="task-remark">{{ taskDetail.remark || '-' }}</div>
              </ElDescriptionsItem>
            </ElDescriptions>
          </ElCard>

          <!-- 操作记录 -->
          <ElCard v-if="taskDetail.recordList?.length" shadow="never" class="detail-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">操作记录</span>
                <ElTag size="small" type="info">{{ taskDetail.recordList.length }} 条记录</ElTag>
              </div>
            </template>
            <ElTimeline>
              <ElTimelineItem
                v-for="record in taskDetail.recordList"
                :key="record.id"
                :timestamp="formatTime(record.createTime)"
                placement="top"
              >
                <ElCard shadow="hover" class="record-card">
                  <div class="record-header">
                    <ElTag :type="getActionTypeTag(record.actionType)" size="small">
                      {{ record.actionTypeLabel || '任务操作' }}
                    </ElTag>
                    <div class="record-operator">
                      <ElAvatar :size="24" :src="record.operatorAvatar">
                        {{ record.operatorName?.slice(0, 1) || 'U' }}
                      </ElAvatar>
                      <span class="operator-name">{{ record.operatorName || '-' }}</span>
                    </div>
                  </div>
                  <div v-if="record.remark" class="record-remark">
                    {{ record.remark }}
                  </div>
                  <div v-if="record.attachments?.length" class="record-attachments">
                    <div class="attachments-title">附件：</div>
                    <div class="attachments-list">
                      <ElLink
                        v-for="item in record.attachments"
                        :key="`${item.id}-${item.fileUrl}`"
                        :href="getFileUrl(item.fileUrl)"
                        target="_blank"
                        type="primary"
                        class="attachment-link"
                      >
                        <ElIcon><Document /></ElIcon>
                        <span>{{ item.fileName || '附件' }}</span>
                      </ElLink>
                    </div>
                  </div>
                </ElCard>
              </ElTimelineItem>
            </ElTimeline>
          </ElCard>

          <!-- 空状态 -->
          <ElEmpty v-else description="暂无操作记录" />
        </div>
      </template>
    </ElSkeleton>
  </ElDrawer>
</template>

<script setup lang="ts">
import { ElIcon } from 'element-plus'
import { Clock, VideoPlay, CircleCheck, Timer, Document } from '@element-plus/icons-vue'
import { getFileUrl } from '@/utils/file'

defineOptions({ name: 'TaskDetailDrawer' })

interface Props {
  modelValue: boolean
  taskDetail?: Api.Task.TaskListDetailVO
  loading?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  taskDetail: undefined,
  loading: false
})

const emit = defineEmits<Emits>()

const drawerVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

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
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

/**
 * 计算两个时间之间的小时数
 */
const calculateHours = (startTime?: string, endTime?: string) => {
  if (!startTime || !endTime) return '-'

  const start = new Date(startTime)
  const end = new Date(endTime)
  const diffMs = end.getTime() - start.getTime()
  const diffHours = diffMs / (1000 * 60 * 60)

  if (diffHours <= 0) return '-'

  // 如果小于1小时，显示分钟
  if (diffHours < 1) {
    const minutes = Math.round(diffHours * 60)
    return `${minutes} 分钟`
  }

  // 如果小于24小时，显示小时
  if (diffHours < 24) {
    return `${diffHours.toFixed(1)} 小时`
  }

  // 如果大于24小时，显示天数
  const days = Math.floor(diffHours / 24)
  const hours = Math.round(diffHours % 24)
  return hours > 0 ? `${days} 天 ${hours} 小时` : `${days} 天`
}

/**
 * 获取操作类型标签颜色
 */
const getActionTypeTag = (actionType?: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const typeMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    1: 'primary',   // 创建
    2: 'success',   // 开始
    3: 'warning',   // 提交
    4: 'success',   // 通过
    5: 'danger',    // 驳回
    6: 'info',      // 完成
    7: 'warning'    // 其他
  }
  return typeMap[actionType || 0] || 'info'
}
</script>

<style scoped lang="scss">
.task-detail-drawer {
  :deep(.el-drawer__body) {
    padding: 0;
  }
}

.task-detail-content {
  padding: 20px;
}

.detail-card {
  margin-bottom: 16px;

  &:last-child {
    margin-bottom: 0;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}

.task-description,
.task-remark {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  color: var(--el-text-color-regular);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;

  .user-name {
    font-size: 14px;
    color: var(--el-text-color-primary);
  }
}

.user-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;

  .time-icon {
    color: var(--el-color-primary);
  }
}

.record-card {
  margin-bottom: 8px;

  .record-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;

    .record-operator {
      display: flex;
      align-items: center;
      gap: 6px;

      .operator-name {
        font-size: 13px;
        color: var(--el-text-color-regular);
      }
    }
  }

  .record-remark {
    margin-bottom: 8px;
    padding: 8px 12px;
    background-color: var(--el-fill-color-light);
    border-radius: 4px;
    font-size: 14px;
    line-height: 1.6;
    color: var(--el-text-color-regular);
    white-space: pre-wrap;
    word-break: break-word;
  }

  .record-attachments {
    .attachments-title {
      font-size: 13px;
      color: var(--el-text-color-secondary);
      margin-bottom: 6px;
    }

    .attachments-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .attachment-link {
        display: flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        background-color: var(--el-fill-color-light);
        border-radius: 4px;
        font-size: 13px;

        &:hover {
          background-color: var(--el-fill-color);
        }
      }
    }
  }
}
</style>
