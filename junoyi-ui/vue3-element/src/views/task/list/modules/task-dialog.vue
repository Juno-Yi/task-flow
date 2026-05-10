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
      <ElFormItem label="执行人" prop="userIds">
        <ElSelect
          v-model="form.userIds"
          multiple
          collapse-tags
          collapse-tags-tooltip
          placeholder="请输入昵称搜索执行人"
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
      <ElFormItem label="截止时间" prop="dueTime">
        <ElDatePicker
          v-model="form.dueTime"
          type="datetime"
          placeholder="请选择截止时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
      </ElFormItem>
      <ElFormItem label="同步日程" prop="syncSchedule">
        <ElCheckbox v-model="form.syncSchedule">同步创建企业微信日程</ElCheckbox>
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
  dueTime?: string
  remark?: string
  syncSchedule: boolean
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
  dueTime: undefined,
  remark: '',
  syncSchedule: false
})

const rules = reactive<FormRules>({
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  ownerUserId: [{ required: true, message: '请选择负责人', trigger: 'change' }],
  dueTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
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
      dueTime: props.taskData.dueTime,
      remark: props.taskData.remark || '',
      syncSchedule: !!props.taskData.dueTime
    })
    return
  }
  Object.assign(form, { id: undefined, title: '', description: '', priority: 1, ownerUserId: undefined, userIds: [], dueTime: undefined, remark: '', syncSchedule: false })
}

const handleClose = () => {
  visible.value = false
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value || submitting.value || props.loading) return
  await formRef.value.validate()
  submitting.value = true
  const userIds = Array.from(new Set([...(form.userIds || []), form.ownerUserId].filter(Boolean) as number[]))
  emit('success', { ...form, userIds })
  submitting.value = false
  handleClose()
}
</script>

