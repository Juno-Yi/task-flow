<template>
  <ElDialog
    v-model="visible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <ElFormItem label="平台" prop="platform">
        <ElSelect
          v-model="formData.platform"
          placeholder="请选择平台"
          :disabled="type === 'edit'"
          style="width: 100%"
        >
          <ElOption
            v-for="item in platformOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </ElSelect>
      </ElFormItem>

      <ElFormItem label="状态" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.value"
          >
            {{ item.label }}
          </ElRadio>
        </ElRadioGroup>
      </ElFormItem>

      <ElFormItem label="配置Key" prop="configKey">
        <ElInput
          v-model="formData.configKey"
          placeholder="请输入配置Key（如：client_id）"
        />
      </ElFormItem>

      <ElFormItem label="配置Value" prop="configValue">
        <ElInput
          v-model="formData.configValue"
          placeholder="请输入配置Value"
          type="textarea"
          :rows="2"
        />
      </ElFormItem>

      <ElFormItem label="回调地址" prop="redirectUrl">
        <ElInput
          v-model="formData.redirectUrl"
          placeholder="请输入回调地址"
          type="textarea"
          :rows="3"
        />
      </ElFormItem>

      <ElFormItem label="备注" prop="remark">
        <ElInput
          v-model="formData.remark"
          placeholder="请输入备注"
          type="textarea"
          :rows="3"
          maxlength="500"
          show-word-limit
        />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="submitLoading" @click="handleSubmit">
        确定
      </ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ref, computed, watch } from 'vue'
  import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
  import { fetchGetDictDataByType } from '@/api/system/dict'
  import { fetchAddOauthConfig, fetchUpdateOauthConfig } from '@/api/system/oauth'

  interface Props {
    modelValue: boolean
    type: 'add' | 'edit'
    data?: Api.Oauth.OauthConfigVO
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'success'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const formRef = ref<FormInstance>()
  const submitLoading = ref(false)

  // 对话框标题
  const dialogTitle = computed(() => {
    return props.type === 'add' ? '新增OAuth配置' : '编辑OAuth配置'
  })

  // 对话框显示状态
  const visible = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  // 表单数据
  const formData = ref({
    id: undefined as number | undefined,
    platform: '',
    status: 1,
    configKey: '',
    configValue: '',
    redirectUrl: '',
    remark: ''
  })

  // 字典数据
  const platformOptions = ref<Array<{ label: string; value: string }>>([])
  const statusOptions = ref<Array<{ label: string; value: number }>>([])

  // 加载字典数据
  const loadDictData = async () => {
    try {
      // 加载平台字典
      const platformDict = await fetchGetDictDataByType('oauth_platform')
      platformOptions.value = platformDict.map((item) => ({
        label: item.dictLabel,
        value: item.dictValue
      }))

      // 加载状态字典
      const statusDict = await fetchGetDictDataByType('oauth_status')
      statusOptions.value = statusDict.map((item) => ({
        label: item.dictLabel,
        value: parseInt(item.dictValue)
      }))
    } catch (error) {
      console.error('加载字典数据失败:', error)
    }
  }

  // 组件挂载时加载字典
  loadDictData()

  // 表单校验规则
  const rules: FormRules = {
    platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }],
    redirectUrl: [
      { required: true, message: '请输入回调地址', trigger: 'blur' },
      { type: 'url', message: '请输入正确的URL地址', trigger: 'blur' }
    ]
  }

  // 监听数据变化，初始化表单
  watch(
    () => props.data,
    (newData) => {
      if (newData && props.type === 'edit') {
        formData.value = {
          id: newData.id,
          platform: newData.platform,
          status: newData.status,
          configKey: '',
          configValue: '',
          redirectUrl: newData.redirectUrl,
          remark: newData.remark || ''
        }
      } else {
        // 新增时重置表单
        formData.value = {
          id: undefined,
          platform: '',
          status: 1,
          configKey: '',
          configValue: '',
          redirectUrl: '',
          remark: ''
        }
      }
    },
    { immediate: true }
  )

  // 关闭对话框
  const handleClose = () => {
    formRef.value?.resetFields()
    visible.value = false
  }

  // 提交表单
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      submitLoading.value = true

      if (props.type === 'add') {
        await fetchAddOauthConfig(formData.value)
        ElMessage.success('新增成功')
      } else {
        await fetchUpdateOauthConfig(formData.value)
        ElMessage.success('更新成功')
      }

      emit('success')
      handleClose()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  }
</script>


