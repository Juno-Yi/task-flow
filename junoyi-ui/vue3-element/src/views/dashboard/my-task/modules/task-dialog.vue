<template>
  <ElDialog :model-value="visible" title="任务处理" width="680px" @close="handleClose">
    <div class="space-y-4">
      <div class="bg-gray-50 p-4 rounded-lg">
        <h3 class="text-lg font-semibold mb-3 flex items-center gap-2">
          <ArtSvgIcon icon="ri:file-list-3-line" class="text-xl" />
          {{ formData.title || '-' }}
        </h3>

        <div class="grid grid-cols-2 gap-3 text-sm">
          <div><span class="text-gray-500">负责人：</span><span>{{ formData.ownerUser?.nickName || '-' }}</span></div>
          <div><span class="text-gray-500">优先级：</span><ElTag :type="priorityInfo.type" size="small">{{ priorityInfo.text }}</ElTag></div>
          <div><span class="text-gray-500">开始时间：</span><span>{{ formData.startTime || '-' }}</span></div>
          <div><span class="text-gray-500">截止时间：</span><span>{{ formData.DueTime || formData.dueTime || '-' }}</span></div>
          <div class="col-span-2"><span class="text-gray-500">任务描述：</span><span>{{ formData.description || '-' }}</span></div>
          <div class="col-span-2"><span class="text-gray-500">备注：</span><span>{{ formData.remark || '-' }}</span></div>
        </div>
      </div>

      <ElForm ref="formRef" :model="formModel" :rules="rules" label-width="90px">
        <ElFormItem label="任务状态" prop="status">
          <ElSelect v-model="formModel.status" class="w-full" placeholder="请选择状态">
            <ElOption label="进行中" :value="1" />
            <ElOption label="待验收" :value="2" />
            <ElOption label="已完成" :value="3" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="工作日志" prop="workLog">
          <ElInput
            v-model="formModel.workLog"
            type="textarea"
            :rows="6"
            placeholder="请记录本次工作内容..."
            maxlength="2000"
            show-word-limit
          />
        </ElFormItem>
      </ElForm>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2">
        <ElButton @click="handleClose">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">保存更新</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import type { MyTask, TaskStatus } from '../types'

interface Props {
  visible: boolean
  taskData: Partial<MyTask>
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [task: MyTask, workLog: string, attachments: string[]]
}>()

const formRef = ref<FormInstance>()
const formData = ref<Partial<MyTask>>({})
const formModel = ref<{ status: TaskStatus; workLog: string }>({ status: 1, workLog: '' })

const rules: FormRules = {
  status: [{ required: true, message: '请选择任务状态', trigger: 'change' }],
  workLog: [{ required: true, message: '请填写工作日志', trigger: 'blur' }]
}

const priorityInfo = computed(() => {
  const map = {
    0: { type: 'info' as const, text: '低' },
    1: { type: 'primary' as const, text: '中' },
    2: { type: 'warning' as const, text: '高' },
    3: { type: 'danger' as const, text: '紧急' }
  }
  return map[formData.value.priority as 0 | 1 | 2 | 3] || map[1]
})

watch(
  () => props.taskData,
  (newData) => {
    formData.value = newData ? { ...newData } : {}
    formModel.value = { status: (newData?.status as TaskStatus) ?? 1, workLog: '' }
  },
  { immediate: true, deep: true }
)

const handleClose = () => {
  emit('update:visible', false)
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  emit('submit', { ...(formData.value as MyTask), status: formModel.value.status }, formModel.value.workLog, [])
  handleClose()
}
</script>

<style scoped>
:deep(.el-dialog__body) {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}
</style>
