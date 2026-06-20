<template>
  <ElDialog v-model="visible" title="发布通知" width="860px" @close="handleClose">
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
      <ElFormItem label="通知标题" prop="title">
        <ElInput v-model="form.title" placeholder="请输入通知标题" maxlength="100" show-word-limit />
      </ElFormItem>

      <ElFormItem label="通知内容" prop="content">
        <div class="w-full rounded-lg border border-gray-200 bg-white p-3">
          <div class="mb-3 text-sm font-medium text-gray-700">Markdown 文档</div>
          <div ref="editorRef" class="min-h-[420px]"></div>
        </div>
      </ElFormItem>

      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem label="通知类型" prop="type">
            <ElSelect v-model="form.type" placeholder="请选择通知类型" style="width: 100%">
              <ElOption
                v-for="item in typeOptions"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="Number(item.dictValue)"
              />
            </ElSelect>
          </ElFormItem>
        </ElCol>
        <ElCol :span="12">
          <ElFormItem label="目标范围" prop="targetType">
            <ElSelect v-model="form.targetType" placeholder="请选择目标范围" style="width: 100%" @change="handleTargetTypeChange">
              <ElOption :value="0" label="全部用户" />
              <ElOption :value="1" label="按部门" />
              <ElOption :value="2" label="按角色" />
              <ElOption :value="3" label="指定用户" />
            </ElSelect>
          </ElFormItem>
        </ElCol>
      </ElRow>

      <!-- 部门选择 -->
      <ElFormItem v-if="form.targetType === 1" label="选择部门" prop="targetIds">
        <ElTreeSelect
          v-model="form.targetIds"
          :data="deptTree"
          multiple
          :render-after-expand="false"
          placeholder="请选择部门"
          node-key="id"
          :props="{ label: 'name', children: 'children' }"
          check-strictly
          show-checkbox
          style="width: 100%"
        />
      </ElFormItem>

      <!-- 角色选择 -->
      <ElFormItem v-if="form.targetType === 2" label="选择角色" prop="targetIds">
        <ElSelect
          v-model="form.targetIds"
          multiple
          collapse-tags
          collapse-tags-tooltip
          placeholder="请选择角色"
          style="width: 100%"
        >
          <ElOption
            v-for="item in roleOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </ElSelect>
      </ElFormItem>

      <!-- 指定用户 -->
      <ElFormItem v-if="form.targetType === 3" label="选择用户" prop="targetIds">
        <ElSelect
          v-model="form.targetIds"
          multiple
          collapse-tags
          collapse-tags-tooltip
          filterable
          remote
          reserve-keyword
          :remote-method="handleUserSearch"
          :loading="userLoading"
          placeholder="请输入昵称搜索用户"
          style="width: 100%"
        >
          <ElOption v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
        </ElSelect>
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton :loading="submitting" @click="handleSaveDraft">存为草稿</ElButton>
      <ElButton type="primary" :loading="submitting" @click="handlePublish">立即发布</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { fetchAddNotification } from '@/api/notification/manage'
import { fetchGetDictDataByType } from '@/api/system/dict'
import { fetchGetDeptTree } from '@/api/system/department'
import { fetchGetRoleOptions } from '@/api/system/role'
import { fetchGetUserOptions } from '@/api/system/user'

const emit = defineEmits<{
  (e: 'success'): void
}>()

const visible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const editorRef = ref<HTMLElement>()
const vditor = ref<Vditor>()

interface FormData {
  title: string
  content: string
  type: number | undefined
  targetType: number | undefined
  targetIds: number[]
}

const defaultForm = (): FormData => ({
  title: '',
  content: '',
  type: undefined,
  targetType: 0,
  targetIds: []
})

const form = reactive<FormData>(defaultForm())

const rules: FormRules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
  targetType: [{ required: true, message: '请选择目标范围', trigger: 'change' }],
  targetIds: [{ required: true, message: '请选择通知目标', trigger: 'change' }]
}

// 通知类型字典
const typeOptions = ref<Api.System.DictDataVO[]>([])

// 部门树
const deptTree = ref<any[]>([])

// 角色列表
const roleOptions = ref<{ label: string; value: number }[]>([])

// 用户列表
const userOptions = ref<{ label: string; value: number }[]>([])
const userLoading = ref(false)

/**
 * 打开弹窗
 */
const open = async () => {
  visible.value = true
  Object.assign(form, defaultForm())
  await loadOptions()
  await nextTick()
  initEditor()
}

/**
 * 初始化 Vditor 编辑器
 */
const initEditor = () => {
  if (!editorRef.value) return
  vditor.value = new Vditor(editorRef.value, {
    height: 420,
    mode: 'ir',
    placeholder: '请输入通知内容...',
    cache: { enable: false },
    after: () => {
      vditor.value?.setValue(form.content || '')
    }
  })
}

/**
 * 加载选项数据
 */
const loadOptions = async () => {
  try {
    // 加载通知类型字典
    typeOptions.value = await fetchGetDictDataByType('notification_type')
  } catch (error) {
    console.error('加载字典数据失败:', error)
  }
}

/**
 * 目标范围切换时清空已选目标
 */
const handleTargetTypeChange = async () => {
  form.targetIds = []

  if (form.targetType === 1 && deptTree.value.length === 0) {
    const data = await fetchGetDeptTree()
    deptTree.value = data || []
  }

  if (form.targetType === 2 && roleOptions.value.length === 0) {
    const data = await fetchGetRoleOptions()
    roleOptions.value = (data || []).map((item: any) => ({
      label: item.roleName,
      value: item.id
    }))
  }
}

/**
 * 远程搜索用户
 */
const handleUserSearch = async (query: string) => {
  if (!query) {
    userOptions.value = []
    return
  }
  userLoading.value = true
  try {
    const data = await fetchGetUserOptions({ nickName: query })
    userOptions.value = (data || []).map((item: any) => ({
      label: item.nickName,
      value: item.userId
    }))
  } finally {
    userLoading.value = false
  }
}

/**
 * 提交（草稿或发布）
 */
const handleSubmit = async (status: number) => {
  await formRef.value?.validate()

  // 全部时不校验 targetIds
  if (form.targetType !== 0 && form.targetIds.length === 0) {
    ElMessage.warning('请选择通知目标')
    return
  }

  // 从编辑器获取内容
  const content = vditor.value?.getValue() || ''

  submitting.value = true
  try {
    await fetchAddNotification({
      title: form.title,
      content,
      type: form.type!,
      status,
      targetType: form.targetType!,
      targetIds: form.targetType === 0 ? undefined : form.targetIds
    })
    ElMessage.success(status === 1 ? '发布成功' : '已保存为草稿')
    emit('success')
    handleClose()
  } finally {
    submitting.value = false
  }
}

const handlePublish = () => handleSubmit(1)
const handleSaveDraft = () => handleSubmit(0)

const handleClose = () => {
  visible.value = false
  formRef.value?.resetFields()
  if (vditor.value) {
    vditor.value.destroy()
    vditor.value = undefined
  }
}

defineExpose({ open })
</script>

