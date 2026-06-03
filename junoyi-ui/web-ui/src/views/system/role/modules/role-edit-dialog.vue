<template>
  <ElDialog
    v-model="visible"
    :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
    width="500px"
    align-center
    @close="handleClose"
  >
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
      <ElFormItem label="角色名称" prop="roleName">
        <ElInput v-model="form.roleName" placeholder="请输入角色名称" />
      </ElFormItem>
      <ElFormItem label="角色标识" prop="roleKey">
        <ElInput v-model="form.roleKey" placeholder="请输入角色标识" />
      </ElFormItem>
      <ElFormItem label="排序" prop="sort">
        <ElInputNumber
          v-model="form.sort"
          :min="0"
          controls-position="right"
          style="width: 100%"
        />
      </ElFormItem>
      <ElFormItem label="数据范围" prop="dataScope">
        <ElSelect v-model="form.dataScope" placeholder="请选择数据范围" style="width: 100%">
          <ElOption label="全部数据" value="1" />
          <ElOption label="本部门数据" value="2" />
          <ElOption label="本部门及以下" value="3" />
          <ElOption label="仅本人数据" value="4" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem v-if="dialogType === 'edit'" label="状态" prop="status">
        <ElRadioGroup v-model="form.status">
          <ElRadio :value="1">启用</ElRadio>
          <ElRadio :value="0">禁用</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem label="备注" prop="remark">
        <ElInput
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="submitting" @click="handleSubmit">确定</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import { fetchAddRole, fetchUpdateRole } from '@/api/system/role'

  type RoleVO = Api.System.RoleVO

  interface Props {
    modelValue: boolean
    dialogType: 'add' | 'edit'
    roleData?: RoleVO
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'success'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: false,
    dialogType: 'add',
    roleData: undefined
  })

  const emit = defineEmits<Emits>()

  const formRef = ref<FormInstance>()
  const submitting = ref(false)

  /**
   * 弹窗显示状态双向绑定
   */
  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
  })

  /**
   * 表单验证规则
   */
  const rules = reactive<FormRules>({
    roleName: [
      { required: true, message: '请输入角色名称', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    roleKey: [
      { required: true, message: '请输入角色标识', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
    dataScope: [{ required: true, message: '请选择数据范围', trigger: 'change' }]
  })

  /**
   * 表单数据
   */
  const form = reactive<Api.System.RoleDTO>({
    id: undefined,
    roleName: '',
    roleKey: '',
    sort: 1,
    dataScope: '1',
    status: 1,
    remark: ''
  })

  /**
   * 监听弹窗打开，初始化表单数据
   */
  watch(
    () => props.modelValue,
    (newVal) => {
      if (newVal) initForm()
    }
  )

  /**
   * 初始化表单数据
   */
  const initForm = () => {
    if (props.dialogType === 'edit' && props.roleData) {
      Object.assign(form, {
        id: props.roleData.id,
        roleName: props.roleData.roleName,
        roleKey: props.roleData.roleKey,
        sort: props.roleData.sort,
        dataScope: props.roleData.dataScope,
        status: props.roleData.status,
        remark: props.roleData.remark || ''
      })
    } else {
      Object.assign(form, {
        id: undefined,
        roleName: '',
        roleKey: '',
        sort: 1,
        dataScope: '1',
        status: 1,
        remark: ''
      })
    }
  }

  /**
   * 关闭弹窗并重置表单
   */
  const handleClose = () => {
    visible.value = false
    formRef.value?.resetFields()
  }

  /**
   * 提交表单
   */
  const handleSubmit = async () => {
    if (!formRef.value || submitting.value) return

    try {
      await formRef.value.validate()
      submitting.value = true

      if (props.dialogType === 'add') {
        await fetchAddRole(form)
      } else {
        await fetchUpdateRole(form)
      }

      emit('success')
      handleClose()
    } catch (error) {
      console.log('表单验证失败:', error)
    } finally {
      submitting.value = false
    }
  }
</script>
