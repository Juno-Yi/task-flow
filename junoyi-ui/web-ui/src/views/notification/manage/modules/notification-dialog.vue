<template>
  <ElDialog v-model="visible" title="发布通知" width="680px" @close="handleClose">
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
      <ElFormItem label="通知标题" prop="title">
        <ElInput v-model="form.title" placeholder="请输入通知标题" maxlength="100" show-word-limit />
      </ElFormItem>

      <ElFormItem label="通知内容" prop="content">
        <ElInput
          v-model="form.content"
          type="textarea"
          :rows="6"
          placeholder="请输入通知内容（支持 Markdown 格式）"
        />
      </ElFormItem>

      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem label="通知类型" prop="type">
            <ElSelect v-model="form.type" placeholder="请选择通知类型" style="width: 100%">
              <ElOption
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
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
          :props="{ label: 'deptName', children: 'children' }"
          check-strictly
          style="width: 100%"
        />
      </ElFormItem>

      <!-- 角色选择 -->
      <ElFormItem v-if="form.targetType === 2" label="选择角色" prop="targetIds">
        <ElSelect v-model="form.targetIds" multiple collapse-tags collapse-tags-tooltip placeholder="请选择角色" style="width: 100%">
          <ElOption v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
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
import { ref, reactive } from 'vue'
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
  targetType: undefined,
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
const typeOptions = ref<{ label: string; value: number }[]>([])

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
}

/**
 * 加载选项数据
 */
const loadOptions = async () => {
  // 加载通知类型字典
  const { data: typeData } = await fetchGetDictDataByType('notification_type')
  typeOptions.value = (typeData || []).map((item: any) => ({
    label: item.dictLabel,
    value: Number(item.dictValue)
  }))
}

/**
 * 目标范围切换时清空已选目标
 */
const handleTargetTypeChange = async () => {
  form.targetIds = []

  if (form.targetType === 1 && deptTree.value.length === 0) {
    const { data } = await fetchGetDeptTree()
    deptTree.value = data || []
  }

  if (form.targetType === 2 && roleOptions.value.length === 0) {
    const { data } = await fetchGetRoleOptions()
    roleOptions.value = (data || []).map((item: any) => ({
      label: item.roleName,
      value: item.roleId
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
    const { data } = await fetchGetUserOptions({ nickName: query })
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

  submitting.value = true
  try {
    await fetchAddNotification({
      title: form.title,
      content: form.content,
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
}

defineExpose({ open })
</script>

