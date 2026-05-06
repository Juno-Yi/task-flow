<!-- 用户重置密码弹窗 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="重置密码"
    width="450px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      label-position="right"
    >
      <ElFormItem label="新密码" prop="newPassword">
        <ElInput
          v-model="formData.newPassword"
          type="password"
          placeholder="请输入新密码（至少8位）"
          show-password
          clearable
        />
      </ElFormItem>
      <ElFormItem label="确认新密码" prop="confirmPassword">
        <ElInput
          v-model="formData.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
          clearable
        />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="submitting" @click="handleSubmit">确定</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { computed, ref, reactive, watch } from 'vue'
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import { fetchResetUserPassword } from '@/api/system/user'

  interface Props {
    visible: boolean
    userId: number | null
    userName?: string
  }

  const props = defineProps<Props>()
  const emit = defineEmits<{
    'update:visible': [value: boolean]
    'submit': []
  }>()

  const dialogVisible = computed({
    get: () => props.visible,
    set: (val) => emit('update:visible', val)
  })

  const formRef = ref<FormInstance>()
  const submitting = ref(false)

  const formData = reactive({
    newPassword: '',
    confirmPassword: ''
  })

  // 校验确认密码
  const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
    if (value !== formData.newPassword) {
      callback(new Error('两次输入的密码不一致'))
    } else {
      callback()
    }
  }

  const formRules: FormRules = {
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 8, message: '密码长度不能少于8位', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请再次输入新密码', trigger: 'blur' },
      { validator: validateConfirmPassword, trigger: 'blur' }
    ]
  }

  const handleSubmit = async () => {
    if (!formRef.value || !props.userId) return

    try {
      await formRef.value.validate()
      submitting.value = true

      await fetchResetUserPassword({
        userId: props.userId,
        newPassword: formData.newPassword
      })

      ElMessage.success('密码重置成功')
      emit('submit')
      handleClose()
    } catch (error) {
      // 校验失败或请求失败
    } finally {
      submitting.value = false
    }
  }

  const handleClose = () => {
    dialogVisible.value = false
    formRef.value?.resetFields()
    formData.newPassword = ''
    formData.confirmPassword = ''
  }

  watch(() => props.visible, (val) => {
    if (!val) {
      formRef.value?.resetFields()
    }
  })
</script>
