<template>
  <ElDialog v-model="visible" :title="dialogTitle" width="1100px" @close="handleClose">
    <ElForm ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <ElFormItem label="需求标题" prop="title">
        <ElInput v-model="formData.title" placeholder="请输入需求标题" maxlength="100" show-word-limit />
      </ElFormItem>

      <ElFormItem label="需求描述" prop="description">
        <div class="w-full rounded-lg border border-gray-200 bg-white p-3">
          <div class="mb-3 text-sm font-medium text-gray-700">Markdown 文档</div>
          <div ref="editorRef" class="min-h-[520px]"></div>
        </div>
      </ElFormItem>

      <div class="grid grid-cols-3 gap-4">
        <ElFormItem label="优先级" prop="priority">
          <ElSelect v-model="formData.priority" placeholder="请选择优先级" class="w-full">
            <ElOption v-for="item in priorityDictList" :key="item.dictCode" :label="item.dictLabel" :value="Number(item.dictValue)" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="需求来源" prop="source">
          <ElSelect v-model="formData.source" placeholder="请选择需求来源" class="w-full">
            <ElOption v-for="item in sourceDictList" :key="item.dictCode" :label="item.dictLabel" :value="Number(item.dictValue)" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="需求类型" prop="type">
          <ElSelect v-model="formData.type" placeholder="请选择需求类型" class="w-full">
            <ElOption v-for="item in typeDictList" :key="item.dictCode" :label="item.dictLabel" :value="Number(item.dictValue)" />
          </ElSelect>
        </ElFormItem>
      </div>
    </ElForm>

    <template #footer>
      <div class="flex justify-end gap-2">
        <ElButton @click="visible = false">取消</ElButton>
        <ElButton type="primary" :loading="submitLoading" @click="handleSubmit">确定</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { type FormInstance, type FormRules } from 'element-plus'

defineOptions({ name: 'RequirementFormDialog' })

interface Props {
  modelValue: boolean
  dialogTitle: string
  submitLoading: boolean
  formData: Api.Project.ProjectRequirementDTO
  formRules: FormRules
  priorityDictList: Api.System.DictDataVO[]
  sourceDictList: Api.System.DictDataVO[]
  typeDictList: Api.System.DictDataVO[]
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit'): void
  (e: 'close'): void
}>()

const formRef = ref<FormInstance>()
const editorRef = ref<HTMLElement>()
const vditor = ref<Vditor>()
const visible = computed({ get: () => props.modelValue, set: (value: boolean) => emit('update:modelValue', value) })

const initEditor = async () => {
  if (!editorRef.value || vditor.value) return

  vditor.value = new Vditor(editorRef.value, {
    height: 520,
    mode: 'wysiwyg',
    placeholder: '请输入 Markdown 文档内容',
    cache: { enable: false },
    toolbarConfig: { pin: true },
    after: () => {
      vditor.value?.setValue(props.formData.description || '')
    },
    input: (value: string) => {
      props.formData.description = value
    }
  })
}

watch(
  () => props.modelValue,
  async (value) => {
    if (value) {
      await nextTick()
      await initEditor()
      vditor.value?.setValue(props.formData.description || '')
    }
  }
)

watch(
  () => props.formData.description,
  (value) => {
    if (vditor.value && vditor.value.getValue() !== (value || '')) {
      vditor.value.setValue(value || '')
    }
  }
)

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  emit('submit')
}

const handleClose = () => {
  formRef.value?.resetFields()
  emit('close')
}
</script>


