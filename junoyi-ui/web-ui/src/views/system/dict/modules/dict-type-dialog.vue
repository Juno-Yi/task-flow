<template>
  <ElDialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑字典类型' : '新增字典类型'"
      width="600px"
      @close="handleClose"
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <ElFormItem label="字典名称" prop="dictName">
        <ElInput v-model="formData.dictName" placeholder="请输入字典名称" clearable />
      </ElFormItem>
      <ElFormItem label="字典类型" prop="dictType">
        <ElInput
            v-model="formData.dictType"
            placeholder="请输入字典类型"
            :disabled="isEdit"
            clearable
        />
      </ElFormItem>
      <ElFormItem label="状态" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio label="0">正常</ElRadio>
          <ElRadio label="1">停用</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem label="备注" prop="remark">
        <ElInput
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
            clearable
        />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="submitLoading" @click="handleSubmit">确定</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { fetchAddDictType, fetchUpdateDictType } from '@/api/system/dict'

type DictTypeVO = Api.System.DictTypeVO
type DictTypeDTO = Api.System.DictTypeDTO

interface Props {
  visible: boolean
  editData?: DictTypeVO | null
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  editData: null
})

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const isEdit = computed(() => !!props.editData)

const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const formData = reactive<DictTypeDTO>({
  dictId: undefined,
  dictName: '',
  dictType: '',
  status: '0',
  remark: ''
})

const rules: FormRules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 初始化表单数据
const initFormData = () => {
  if (props.editData) {
    Object.assign(formData, {
      dictId: props.editData.dictId,
      dictName: props.editData.dictName,
      dictType: props.editData.dictType,
      status: props.editData.status,
      remark: props.editData.remark
    })
  } else {
    Object.assign(formData, {
      dictId: undefined,
      dictName: '',
      dictType: '',
      status: '0',
      remark: ''
    })
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await fetchUpdateDictType(formData)
        } else {
          await fetchAddDictType(formData)
        }
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        emit('success')
        handleClose()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 关闭弹窗
const handleClose = () => {
  formRef.value?.resetFields()
  emit('update:visible', false)
}

watch(
    () => props.visible,
    (val) => {
      if (val) {
        nextTick(() => {
          initFormData()
        })
      }
    }
)
</script>
