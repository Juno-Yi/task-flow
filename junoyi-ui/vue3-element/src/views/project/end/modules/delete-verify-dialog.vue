<!-- 删除验证弹窗 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="删除确认"
    width="450px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="dialog-content">
      <ElAlert
        type="warning"
        :closable="false"
        show-icon
      >
        <template #title>
          <div class="text-sm leading-relaxed">
            此操作将删除 <span class="font-bold text-danger">{{ projectCount }}</span> 个项目，请输入您的账号密码进行验证
          </div>
        </template>
      </ElAlert>

      <ElForm
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="70px"
        class="password-form"
        @submit.prevent="handleSubmit"
      >
        <ElFormItem label="密码" prop="password">
          <ElInput
            v-model="formData.password"
            type="password"
            placeholder="请输入当前账号密码"
            show-password
            clearable
            autocomplete="off"
            @keyup.enter="handleSubmit"
          >
            <template #prefix>
              <ArtSvgIcon icon="ri:lock-line" />
            </template>
          </ElInput>
        </ElFormItem>
      </ElForm>
    </div>

    <template #footer>
      <div class="flex justify-end gap-3">
        <ElButton @click="handleClose">取消</ElButton>
        <ElButton
          type="danger"
          :loading="loading"
          @click="handleSubmit"
        >
          确认删除
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ref, computed, watch } from 'vue'
  import type { FormInstance, FormRules } from 'element-plus'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'

  interface Props {
    visible: boolean
    projectCount: number
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'confirm', data: { password: string }): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const formRef = ref<FormInstance>()
  const loading = ref(false)

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const formData = ref({
    password: ''
  })

  const rules: FormRules = {
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' }
    ]
  }

  // 监听弹窗打开，重置表单
  watch(() => props.visible, (newVal) => {
    if (newVal) {
      formData.value = {
        password: ''
      }
      formRef.value?.clearValidate()
    }
  })

  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      emit('confirm', { ...formData.value })
    } catch (error) {
      // 表单验证失败，不需要处理
    }
  }

  const handleClose = () => {
    dialogVisible.value = false
  }

  defineExpose({
    setLoading: (value: boolean) => {
      loading.value = value
    }
  })
</script>

<style scoped>
  .dialog-content {
    padding: 8px 0;
  }

  .password-form {
    margin-top: 32px;
  }

  :deep(.el-alert__title) {
    margin-bottom: 0;
  }
  
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
</style>
