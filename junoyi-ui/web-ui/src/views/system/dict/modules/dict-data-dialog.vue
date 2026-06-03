<template>
  <ElDialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑字典数据' : '新增字典数据'"
      width="600px"
      @close="handleClose"
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <ElFormItem label="字典类型" prop="dictType">
        <ElInput v-model="formData.dictType" disabled />
      </ElFormItem>
      <ElFormItem label="字典标签" prop="dictLabel">
        <ElInput v-model="formData.dictLabel" placeholder="请输入字典标签" clearable />
      </ElFormItem>
      <ElFormItem label="字典键值" prop="dictValue">
        <ElInput v-model="formData.dictValue" placeholder="请输入字典键值" clearable />
      </ElFormItem>
      <ElFormItem label="排序" prop="dictSort">
        <ElInputNumber v-model="formData.dictSort" :min="0" :max="9999" controls-position="right" />
      </ElFormItem>
      <ElFormItem label="样式属性" prop="cssClass">
        <ElInput v-model="formData.cssClass" placeholder="请输入样式属性" clearable />
      </ElFormItem>
      <ElFormItem label="回显样式" prop="listClass">
        <ElSelect v-model="formData.listClass" placeholder="请选择回显样式" clearable>
          <ElOption label="默认" value="default" />
          <ElOption label="主要" value="primary" />
          <ElOption label="成功" value="success" />
          <ElOption label="信息" value="info" />
          <ElOption label="警告" value="warning" />
          <ElOption label="危险" value="danger" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="是否默认" prop="isDefault">
        <ElRadioGroup v-model="formData.isDefault">
          <ElRadio label="Y">是</ElRadio>
          <ElRadio label="N">否</ElRadio>
        </ElRadioGroup>
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
import { fetchAddDictData, fetchUpdateDictData } from '@/api/system/dict'

type DictDataVO = Api.System.DictDataVO
type DictDataDTO = Api.System.DictDataDTO

interface Props {
  visible: boolean
  editData?: DictDataVO | null
  dictType?: string
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  editData: null,
  dictType: ''
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

const formData = reactive<DictDataDTO>({
  dictCode: undefined,
  dictLabel: '',
  dictValue: '',
  dictType: '',
  dictSort: 0,
  cssClass: '',
  listClass: '',
  isDefault: 'N',
  status: '0',
  remark: ''
})

const rules: FormRules = {
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典键值', trigger: 'blur' }],
  dictSort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
}

// 初始化表单数据
const initFormData = () => {
  if (props.editData) {
    Object.assign(formData, {
      dictCode: props.editData.dictCode,
      dictLabel: props.editData.dictLabel,
      dictValue: props.editData.dictValue,
      dictType: props.editData.dictType,
      dictSort: props.editData.dictSort,
      cssClass: props.editData.cssClass,
      listClass: props.editData.listClass,
      isDefault: props.editData.isDefault,
      status: props.editData.status,
      remark: props.editData.remark
    })
  } else {
    Object.assign(formData, {
      dictCode: undefined,
      dictLabel: '',
      dictValue: '',
      dictType: props.dictType,
      dictSort: 0,
      cssClass: '',
      listClass: '',
      isDefault: 'N',
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
          await fetchUpdateDictData(formData)
        } else {
          await fetchAddDictData(formData)
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
