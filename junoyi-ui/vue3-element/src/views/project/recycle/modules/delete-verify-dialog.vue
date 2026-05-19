<!-- 删除验证对话框 -->
<template>
  <ElDialog
      v-model="dialogVisible"
      title="批量彻底删除项目"
      width="500px"
      :close-on-click-modal="false"
      @close="handleClose"
  >
    <div class="space-y-4">
      <ElAlert
          type="warning"
          :closable="false"
          show-icon
      >
        <template #title>
          <div class="text-sm">
            <p class="font-semibold mb-2">警告：此操作不可恢复！</p>
            <p>您即将彻底删除 <span class="text-red-500 font-bold">{{ projectCount }}</span> 个项目。</p>
            <p class="mt-1">删除后数据将无法恢复，请谨慎操作。</p>
          </div>
        </template>
      </ElAlert>

      <ElForm
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="80px"
      >
        <ElFormItem label="登录密码" prop="password">
          <ElInput
              v-model="formData.password"
              type="password"
              placeholder="请输入您的登录密码以确认删除"
              show-password
              clearable
          />
        </ElFormItem>
      </ElForm>
    </div>

    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="danger" :loading="loading" @click="handleConfirm">
        确认删除
      </ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

interface Props {
  visible: boolean
  projectCount: number
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'confirm', password: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref<FormInstance>()

const formData = ref({
  password: ''
})

const rules: FormRules = {
  password: [
    { required: true, message: '请输入登录密码', trigger: 'blur' },
    { min: 1, message: '密码不能为空', trigger: 'blur' }
  ]
}

// 监听 visible 变化
watch(() => props.visible, (val) => {
  dialogVisible.value = val
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
  if (!val) {
    // 关闭时重置表单
    formData.value.password = ''
    formRef.value?.resetFields()
  }
})

/**
 * 关闭对话框
 */
const handleClose = () => {
  dialogVisible.value = false
}

/**
 * 确认删除
 */
const handleConfirm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    emit('confirm', formData.value.password)
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

/**
 * 设置加载状态
 */
const setLoading = (value: boolean) => {
  loading.value = value
}

// 暴露方法给父组件
defineExpose({
  setLoading
})
</script>

<style scoped lang="scss">
:deep(.el-alert__title) {
  margin-bottom: 0;
}
</style>

