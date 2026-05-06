<!-- 系统参数编辑/添加弹窗 -->
<template>
  <ElDialog
    v-model="visible"
    :title="title"
    width="600px"
    align-center
    @close="handleClose"
  >
    <ElForm
      ref="formRef"
      :model="form"
      :rules="formRules"
      label-width="100px"
    >
      <ElFormItem label="参数名称" prop="configName">
        <ElInput
          v-model="form.configName"
          placeholder="请输入参数名称"
          clearable
        />
      </ElFormItem>

      <ElFormItem label="参数键名" prop="configKey">
        <ElInput
          v-model="form.configKey"
          placeholder="请输入参数键名"
          :disabled="isSystemBuiltIn"
          clearable
        />
      </ElFormItem>

      <ElFormItem label="参数键值" prop="configValue">
        <ElInput
          v-model="form.configValue"
          type="textarea"
          :rows="3"
          placeholder="请输入参数键值"
        />
      </ElFormItem>

      <ElFormItem label="参数类型" prop="configType">
        <ElSelect
          v-model="form.configType"
          placeholder="请选择参数类型"
          style="width: 100%"
        >
          <ElOption label="文本" value="text" />
          <ElOption label="数字" value="number" />
          <ElOption label="布尔" value="boolean" />
          <ElOption label="JSON" value="json" />
        </ElSelect>
      </ElFormItem>

      <ElFormItem label="排序" prop="sort">
        <ElInputNumber
          v-model="form.sort"
          :min="0"
          controls-position="right"
          style="width: 100%"
        />
      </ElFormItem>

      <ElFormItem label="状态" prop="status">
        <ElRadioGroup v-model="form.status">
          <ElRadio :value="0">正常</ElRadio>
          <ElRadio :value="1">停用</ElRadio>
        </ElRadioGroup>
      </ElFormItem>

      <!-- 系统内置选项仅在编辑模式下显示 -->
      <ElFormItem v-if="dialogType === 'edit'" label="系统内置" prop="isSystem">
        <ElRadioGroup v-model="form.isSystem" disabled>
          <ElRadio label="Y">是</ElRadio>
          <ElRadio label="N">否</ElRadio>
        </ElRadioGroup>
      </ElFormItem>

      <ElFormItem label="备注" prop="remark">
        <ElInput
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注"
        />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="submitting" @click="handleSubmit">
        确定
      </ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import { fetchAddConfig, fetchUpdateConfig } from '@/api/system/config'

  defineOptions({ name: 'ConfigDialog' })

  type ConfigVO = Api.System.ConfigVO
  type ConfigDTO = Api.System.ConfigDTO

  interface Props {
    modelValue: boolean
    dialogType: 'add' | 'edit'
    configData?: ConfigVO
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'success'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: false,
    dialogType: 'add',
    configData: undefined
  })

  const emit = defineEmits<Emits>()

  const visible = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  // 弹窗标题
  const title = computed(() => {
    return props.dialogType === 'add' ? '新增参数' : '编辑参数'
  })

  // 是否为系统内置参数
  const isSystemBuiltIn = computed(() => {
    if (props.dialogType !== 'edit') return false
    return form.value.isSystem === 'Y'
  })

  const formRef = ref<FormInstance>()
  const submitting = ref(false)

  // 表单数据
  const form = ref<ConfigDTO>({
    id: undefined,
    configName: '',
    configKey: '',
    configValue: '',
    configType: 'text',
    sort: 0,
    isSystem: 'N',
    status: 0,
    remark: ''
  })

  // 表单验证规则
  const formRules: FormRules = {
    configName: [
      { required: true, message: '请输入参数名称', trigger: 'blur' }
    ],
    configKey: [
      { required: true, message: '请输入参数键名', trigger: 'blur' }
    ],
    configValue: [
      { required: true, message: '请输入参数键值', trigger: 'blur' }
    ],
    configType: [
      { required: true, message: '请选择参数类型', trigger: 'change' }
    ]
  }

  // 监听弹窗打开，初始化表单数据
  watch(
    () => props.modelValue,
    (val) => {
      if (val) {
        nextTick(() => {
          if (props.dialogType === 'edit' && props.configData) {
            // 编辑模式：填充数据
            form.value = { ...props.configData } as ConfigDTO
          } else {
            // 添加模式：重置表单
            resetForm()
          }
        })
      }
    }
  )

  /**
   * 重置表单
   */
  const resetForm = () => {
    form.value = {
      id: undefined,
      configName: '',
      configKey: '',
      configValue: '',
      configType: 'text',
      sort: 0,
      isSystem: 'N', // 默认为非系统内置
      status: 0, // 默认为正常状态
      remark: ''
    }
    formRef.value?.clearValidate()
  }

  /**
   * 关闭弹窗
   */
  const handleClose = () => {
    visible.value = false
    resetForm()
  }

  /**
   * 提交表单
   */
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      submitting.value = true

      // 根据类型调用不同的 API
      if (props.dialogType === 'add') {
        await fetchAddConfig(form.value)
        ElMessage.success('新增参数成功')
      } else {
        await fetchUpdateConfig(form.value)
        ElMessage.success('更新参数成功')
        
        // 更新成功后刷新页面，以应用新的系统配置
        setTimeout(() => {
          window.location.reload()
        }, 500)
      }

      emit('success')
      handleClose()
    } catch (error) {
      console.error('表单验证失败:', error)
    } finally {
      submitting.value = false
    }
  }
</script>
