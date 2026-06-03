<!-- 添加项目成员对话框 -->
<template>
  <ElDialog
    v-model="visible"
    title="添加项目成员"
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
      <ElFormItem label="选择成员" prop="userId">
        <ElSelect
          v-model="formData.userId"
          placeholder="请输入用户名搜索"
          filterable
          remote
          :remote-method="handleSearch"
          :loading="searchLoading"
          style="width: 100%"
          clearable
        >
          <ElOption
            v-for="user in userOptions"
            :key="user.userId"
            :label="`${user.nickName || user.userName} (@${user.userName})`"
            :value="user.userId"
          >
            <div class="flex items-center gap-2">
              <ElAvatar :size="24" :src="user.avatar">
                <ArtSvgIcon icon="ri:user-line" :size="14" />
              </ElAvatar>
              <span>{{ user.nickName || user.userName }}</span>
              <span class="text-xs text-gray-400">@{{ user.userName }}</span>
            </div>
          </ElOption>
        </ElSelect>
      </ElFormItem>

      <ElFormItem label="项目角色" prop="role">
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
          />
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
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { fetchGetUserOptions } from '@/api/system/user'
  import { fetchAddProjectMember } from '@/api/project/member'
  import { ProjectRole, ProjectRoleNameMap } from '@/enums/project'
  import type { FormInstance, FormRules } from 'element-plus'

  defineOptions({ name: 'AddMemberDialog' })

  interface Props {
    projectId: number
  }

  const props = defineProps<Props>()

  const emit = defineEmits<{
    success: []
  }>()

  const visible = defineModel<boolean>('visible', { required: true })

  const formRef = ref<FormInstance>()
  const formData = ref({
    userId: undefined as number | undefined,
    role: ProjectRole.MEMBER as string
  })

  const rules: FormRules = {
    userId: [
      { required: true, message: '请选择成员', trigger: 'change' }
    ],
    role: [
      { required: true, message: '请选择角色', trigger: 'change' }
    ]
  }

  const userOptions = ref<Api.System.SysUserVO[]>([])
  const searchLoading = ref(false)
  const submitting = ref(false)

  /**
   * 搜索用户
   */
  const handleSearch = async (keyword: string) => {
    try {
      searchLoading.value = true
      const data = await fetchGetUserOptions({nickName: keyword})
      userOptions.value = data
      console.log('加载用户列表成功，数量:', data.length, '关键词:', keyword)
    } catch (error) {
      console.error('加载用户列表失败:', error)
      ElMessage.error('加载用户列表失败')
    } finally {
      searchLoading.value = false
    }
  }

  /**
   * 提交表单
   */
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      
      submitting.value = true
      await fetchAddProjectMember({
        projectId: props.projectId,
        userId: formData.value.userId!,
        role: formData.value.role
      })
      
      ElMessage.success('添加成员成功')
      emit('success')
      handleClose()
    } catch (error) {
      if (error !== false) { // 表单验证失败会返回 false
        console.error('添加成员失败:', error)
        ElMessage.error('添加成员失败')
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
    formData.value.userId = undefined
    formData.value.role = ProjectRole.MEMBER
    userOptions.value = []
  }

  // 对话框打开时加载初始用户列表
  watch(visible, (newVal) => {
    if (newVal) {
      handleSearch('')
    }
  })
</script>

<style scoped lang="scss">
</style>
