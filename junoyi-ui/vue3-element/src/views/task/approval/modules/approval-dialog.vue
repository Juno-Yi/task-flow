<template>
  <ElDialog 
    v-model="dialogVisible" 
    :title="dialogType === 'pass' ? '审核通过' : '驳回任务'" 
    width="520px"
    destroy-on-close
    @close="handleClose"
  >
    <ElForm 
      ref="formRef" 
      :model="formData" 
      :rules="formRules" 
      label-width="90px"
    >
      <ElFormItem 
        :label="dialogType === 'pass' ? '通过说明' : '驳回原因'" 
        prop="remark"
      >
        <ElInput 
          v-model="formData.remark" 
          type="textarea" 
          :rows="5" 
          :placeholder="dialogType === 'pass' ? '请输入通过说明（选填）' : '请输入驳回原因'" 
          maxlength="500" 
          show-word-limit 
        />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <div class="flex justify-end gap-2">
        <ElButton :disabled="loading" @click="handleCancel">取消</ElButton>
        <ElButton 
          :type="dialogType === 'pass' ? 'success' : 'danger'" 
          :loading="loading" 
          @click="handleSubmit"
        >
          {{ dialogType === 'pass' ? '确认通过' : '确认驳回' }}
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import { type FormInstance, type FormRules } from 'element-plus'

defineOptions({ name: 'ApprovalDialog' })

interface Props {
  modelValue: boolean
  dialogType: 'pass' | 'reject'
  taskId?: number
  loading?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', data: { taskId: number; remark: string }): void
  (e: 'cancel'): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  dialogType: 'pass',
  taskId: 0,
  loading: false
})

const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const formData = reactive({
  remark: ''
})

// 驳回时备注必填，通过时选填
const formRules = computed<FormRules>(() => ({
  remark: props.dialogType === 'reject' 
    ? [
        { required: true, message: '请填写驳回原因', trigger: 'blur' },
        { min: 2, max: 500, message: '长度在 2 到 500 个字符', trigger: 'blur' }
      ]
    : [
        { max: 500, message: '长度不能超过 500 个字符', trigger: 'blur' }
      ]
}))

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  emit('submit', {
    taskId: props.taskId,
    remark: formData.remark
  })
}

const handleCancel = () => {
  emit('cancel')
  dialogVisible.value = false
}

const handleClose = () => {
  formData.remark = ''
  formRef.value?.clearValidate()
}

// 监听对话框打开，重置表单
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    formData.remark = ''
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }
})
</script>

<style scoped lang="scss">
// 可以添加自定义样式
</style>

