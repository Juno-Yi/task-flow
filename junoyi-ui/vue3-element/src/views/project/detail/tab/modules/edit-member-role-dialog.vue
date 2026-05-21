<!-- 编辑成员角色对话框 -->
<template>
  <ElDialog
    v-model="visible"
    :title="`编辑成员「${memberInfo?.nickName || memberInfo?.userName}」的角色`"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
    >
      <ElFormItem label="当前角色">
        <ElTag :type="getProjectRoleTagType(memberInfo?.role || '')" size="default">
          {{ getProjectRoleName(memberInfo?.role || '') }}
        </ElTag>
      </ElFormItem>

      <ElFormItem label="新角色" prop="role">
        <ElSelect
          v-model="formData.role"
          placeholder="请选择角色"
          style="width: 100%"
        >
          <ElOption
            v-for="(label, value) in ProjectRoleNameMap"
            :key="value"
            :label="label"
            :value="value"
          >
            <div class="flex items-center justify-between">
              <span>{{ label }}</span>
              <ElTag :type="getProjectRoleTagType(value)" size="small">
                {{ value }}
              </ElTag>
            </div>
          </ElOption>
        </ElSelect>
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
  import { fetchUpdateMemberRole } from '@/api/project/member'
  import { ProjectRoleNameMap, getProjectRoleName, getProjectRoleTagType } from '@/enums/project'
  import type { FormInstance, FormRules } from 'element-plus'

  defineOptions({ name: 'EditMemberRoleDialog' })

  interface Props {
    memberInfo: Api.Project.ProjectMemberVO | null
  }

  const props = defineProps<Props>()

  const emit = defineEmits<{
    success: []
  }>()

  const visible = defineModel<boolean>('visible', { required: true })

  const formRef = ref<FormInstance>()
  const formData = ref({
    role: ''
  })

  const rules: FormRules = {
    role: [
      { required: true, message: '请选择角色', trigger: 'change' }
    ]
  }

  const submitting = ref(false)

  /**
   * 提交表单
   */
  const handleSubmit = async () => {
    if (!formRef.value || !props.memberInfo) return

    try {
      await formRef.value.validate()

      // 检查角色是否有变化
      if (formData.value.role === props.memberInfo.role) {
        ElMessage.info('角色未发生变化')
        handleClose()
        return
      }
      
      submitting.value = true
      await fetchUpdateMemberRole({memberId: props.memberInfo.id, role: formData.value.role})
      
      ElMessage.success('角色更新成功')
      emit('success')
      handleClose()
    } catch (error) {
      if (error !== false) { // 表单验证失败会返回 false
        console.error('更新角色失败:', error)
        ElMessage.error('更新角色失败')
      }
    } finally {
      submitting.value = false
    }
  }

  /**
   * 关闭对话框
   */
  const handleClose = () => {
    visible.value = false
    formRef.value?.resetFields()
  }

  // 监听对话框打开，初始化表单数据
  watch(visible, (newVal) => {
    if (newVal && props.memberInfo) {
      formData.value.role = props.memberInfo.role
    }
  })
</script>

<style scoped lang="scss">
</style>
