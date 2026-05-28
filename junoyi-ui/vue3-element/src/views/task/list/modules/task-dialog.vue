<template>
  <ElDialog v-model="visible" :title="dialogType === 'add' ? '新增任务' : '编辑任务'" width="680px" @close="handleClose">
    <ElSkeleton :loading="loading" animated>
      <template #default>
        <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
          <ElFormItem label="任务标题" prop="title">
            <ElInput v-model="form.title" placeholder="请输入任务标题" maxlength="100" show-word-limit />
          </ElFormItem>

          <ElFormItem label="任务描述" prop="description">
            <ElInput v-model="form.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
          </ElFormItem>

          <ElRow :gutter="16">
            <ElCol :span="12">
              <ElFormItem label="优先级" prop="priority">
                <ElSelect v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                  <ElOption
                    v-for="item in priorityOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="负责人" prop="ownerUserId">
                <ElSelect
                  v-model="form.ownerUserId"
                  placeholder="请输入昵称搜索负责人"
                  filterable
                  remote
                  reserve-keyword
                  :remote-method="handleUserSearch"
                  :loading="userLoading"
                  style="width: 100%"
                >
                  <ElOption v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
                </ElSelect>
              </ElFormItem>
            </ElCol>
          </ElRow>

          <ElFormItem label="协作人" prop="userIds">
            <ElSelect
              v-model="form.userIds"
              multiple
              collapse-tags
              collapse-tags-tooltip
              placeholder="请输入昵称搜索协作人"
              filterable
              remote
              reserve-keyword
              :remote-method="handleUserSearch"
              :loading="userLoading"
              style="width: 100%"
            >
              <ElOption v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
            </ElSelect>
          </ElFormItem>

          <ElFormItem label="计划时间">
            <div class="plan-time-wrapper">
              <!-- 开始时间 -->
              <ElDatePicker
                v-model="form.planStartTime"
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
                v-model="form.planEndTime"
                type="datetime"
                placeholder="结束时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                class="plan-time-picker"
                :disabled-date="disabledEndDate"
                :disabled-hours="disabledEndHours"
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
            <ElInput v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </ElFormItem>
        </ElForm>
      </template>
    </ElSkeleton>
    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="submitting || loading" @click="handleSubmit">确定</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { fetchGetDictDataByType } from '@/api/system/dict'
import { fetchGetUserOptions } from '@/api/system/user'

interface TaskDialogData {
  id?: number
  title: string
  description?: string
  priority: number
  ownerUserId?: number
  userIds: number[]
  planStartTime?: string
  planEndTime?: string
  remark?: string
}

interface Props {
  modelValue: boolean
  dialogType: 'add' | 'edit'
  loading?: boolean
  taskData?: Api.Task.TaskListDetailVO
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  dialogType: 'add',
  loading: false,
  taskData: undefined
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success', value: TaskDialogData): void
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const userLoading = ref(false)
const planHours = ref<number>()
const userOptions = ref<{ label: string; value: number; avatar?: string }[]>([])
const priorityOptions = ref<{ label: string; value: number }[]>([])

const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const form = reactive<TaskDialogData>({
  id: undefined,
  title: '',
  description: '',
  priority: 1,
  ownerUserId: undefined,
  userIds: [],
  planStartTime: undefined,
  planEndTime: undefined,
  remark: ''
})

const rules = reactive<FormRules>({
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  ownerUserId: [{ required: true, message: '请选择负责人', trigger: 'change' }]
})

const loadUserOptions = async (nickName?: string) => {
  userLoading.value = true
  try {
    const res = await fetchGetUserOptions({ nickName: nickName || undefined })
    userOptions.value = (res || []).map(item => ({
      label: item.nickName || item.userName,
      value: item.userId,
      avatar: item.avatar
    }))
  } finally {
    userLoading.value = false
  }
}

const loadPriorityOptions = async () => {
  const res = await fetchGetDictDataByType('task_priority')
  priorityOptions.value = (res || []).map(item => ({
    label: item.dictLabel,
    value: Number(item.dictValue)
  }))
}

const handleUserSearch = (keyword: string) => {
  loadUserOptions(keyword)
}

/**
 * 禁用结束日期（不能早于开始日期）
 */
const disabledEndDate = (time: Date) => {
  if (!form.planStartTime) return false
  const startTime = new Date(form.planStartTime)
  // 只比较日期部分
  const startDate = new Date(startTime.getFullYear(), startTime.getMonth(), startTime.getDate())
  const endDate = new Date(time.getFullYear(), time.getMonth(), time.getDate())
  return endDate.getTime() < startDate.getTime()
}

/**
 * 禁用结束时间的小时（如果是同一天，不能早于开始时间）
 */
const disabledEndHours = () => {
  if (!form.planStartTime || !form.planEndTime) return []

  const startTime = new Date(form.planStartTime)
  const endTime = new Date(form.planEndTime)

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
  if (form.planStartTime && form.planEndTime) {
    const start = new Date(form.planStartTime)
    const end = new Date(form.planEndTime)
    const diffMs = end.getTime() - start.getTime()
    const diffHours = diffMs / (1000 * 60 * 60)
    if (diffHours > 0) {
      planHours.value = Math.round(diffHours * 10) / 10 // 保留1位小数
    }
  }
}

/**
 * 小时数变化时，自动计算结束时间
 */
const handleHoursChange = (value: number | undefined) => {
  if (!value || !form.planStartTime) {
    return
  }

  const startDate = new Date(form.planStartTime)
  const endDate = new Date(startDate.getTime() + value * 60 * 60 * 1000)

  // 格式化为 YYYY-MM-DD HH:mm:ss
  const year = endDate.getFullYear()
  const month = String(endDate.getMonth() + 1).padStart(2, '0')
  const day = String(endDate.getDate()).padStart(2, '0')
  const hours = String(endDate.getHours()).padStart(2, '0')
  const minutes = String(endDate.getMinutes()).padStart(2, '0')
  const seconds = String(endDate.getSeconds()).padStart(2, '0')

  form.planEndTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

watch(
  () => props.modelValue,
  async value => {
    if (value) {
      await Promise.all([loadUserOptions(), loadPriorityOptions()])
      initForm()
    }
  }
)

const initForm = () => {
  if (props.dialogType === 'edit' && props.taskData) {
    Object.assign(form, {
      id: props.taskData.id,
      title: props.taskData.title || '',
      description: props.taskData.description || '',
      priority: props.taskData.priority ?? 1,
      ownerUserId: props.taskData.ownerUser?.userId,
      userIds: props.taskData.taskUserList?.map(item => item.userId) || [],
      planStartTime: props.taskData.planStartTime,
      planEndTime: props.taskData.planEndTime,
      remark: props.taskData.remark || ''
    })

    // 计算小时数
    if (props.taskData.planStartTime && props.taskData.planEndTime) {
      const start = new Date(props.taskData.planStartTime)
      const end = new Date(props.taskData.planEndTime)
      const diffMs = end.getTime() - start.getTime()
      const diffHours = diffMs / (1000 * 60 * 60)
      if (diffHours > 0) {
        planHours.value = Math.round(diffHours * 10) / 10
      }
    }
    return
  }

  // 新增时重置表单
  Object.assign(form, {
    id: undefined,
    title: '',
    description: '',
    priority: 1,
    ownerUserId: undefined,
    userIds: [],
    planStartTime: undefined,
    planEndTime: undefined,
    remark: ''
  })
  planHours.value = undefined
}

const handleClose = () => {
  visible.value = false
  formRef.value?.resetFields()
  planHours.value = undefined
}

const handleSubmit = async () => {
  if (!formRef.value || submitting.value || props.loading) return
  await formRef.value.validate()
  submitting.value = true
  emit('success', { ...form })
  submitting.value = false
  handleClose()
}
</script>

<style scoped lang="scss">
.plan-time-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.plan-time-picker {
  flex: 1;
  min-width: 180px;
}

.time-separator {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.hours-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.hours-label {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.hours-input {
  width: 140px;
}

.hours-unit {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.plan-time-tip {
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}
</style>

