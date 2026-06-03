<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
    width="30%"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <ElFormItem label="用户名" prop="userName">
        <ElInput v-model="formData.userName" placeholder="请输入用户名" />
      </ElFormItem>
      <ElFormItem v-if="dialogType === 'add'" label="密码" prop="password">
        <ElInput v-model="formData.password" type="password" placeholder="请输入密码" show-password />
      </ElFormItem>
      <ElFormItem label="昵称" prop="nickName">
        <ElInput v-model="formData.nickName" placeholder="请输入昵称" />
      </ElFormItem>
      <ElFormItem label="手机号" prop="phonenumber">
        <ElInput v-model="formData.phonenumber" placeholder="请输入手机号" />
      </ElFormItem>
      <ElFormItem label="邮箱" prop="email">
        <ElInput v-model="formData.email" placeholder="请输入邮箱" />
      </ElFormItem>
      <ElFormItem label="性别" prop="sex">
        <ElSelect v-model="formData.sex" placeholder="请选择性别">
          <ElOption label="未知" value="0" />
          <ElOption label="男" value="1" />
          <ElOption label="女" value="2" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem v-if="dialogType === 'edit'" label="状态" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio :value="1">启用</ElRadio>
          <ElRadio :value="0">禁用</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem label="备注" prop="remark">
        <ElInput v-model="formData.remark" type="textarea" placeholder="请输入备注" :rows="3" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">提交</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { fetchAddUser, fetchUpdateUser } from '@/api/system/user'
  import type { FormInstance, FormRules } from 'element-plus'

  interface Props {
    visible: boolean
    type: string
    userData?: Partial<Api.System.SysUserVO>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  // 对话框显示控制
  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)

  // 表单实例
  const formRef = ref<FormInstance>()

  // 表单数据
  const formData = reactive({
    userName: '',
    password: '',
    nickName: '',
    phonenumber: '',
    email: '',
    sex: '0',
    status: 1,
    remark: ''
  })

  // 表单验证规则
  const rules: FormRules = {
    userName: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    phonenumber: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
    ],
    email: [
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }

  /**
   * 初始化表单数据
   */
  const initFormData = () => {
    const isEdit = props.type === 'edit' && props.userData
    const row = props.userData

    Object.assign(formData, {
      userName: isEdit && row ? row.userName || '' : '',
      password: '',
      nickName: isEdit && row ? row.nickName || '' : '',
      phonenumber: isEdit && row ? row.phonenumber || '' : '',
      email: isEdit && row ? row.email || '' : '',
      sex: isEdit && row ? row.sex || '0' : '0',
      status: isEdit && row ? row.status ?? 1 : 1,
      remark: isEdit && row ? row.remark || '' : ''
    })
  }

  /**
   * 监听对话框状态变化
   */
  watch(
    () => [props.visible, props.type, props.userData],
    ([visible]) => {
      if (visible) {
        initFormData()
        nextTick(() => {
          formRef.value?.clearValidate()
        })
      }
    },
    { immediate: true }
  )

  /**
   * 提交表单
   */
  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          if (dialogType.value === 'add') {
            await fetchAddUser({
              userName: formData.userName,
              password: formData.password,
              nickName: formData.nickName,
              phonenumber: formData.phonenumber,
              email: formData.email,
              sex: formData.sex,
              remark: formData.remark
            })
          } else {
            await fetchUpdateUser({
              id: props.userData?.userId,
              userName: formData.userName,
              nickName: formData.nickName,
              phonenumber: formData.phonenumber,
              email: formData.email,
              sex: formData.sex,
              status: formData.status,
              remark: formData.remark
            })
          }
          dialogVisible.value = false
          emit('submit')
        } catch (error) {
          console.error('提交失败:', error)
        }
      }
    })
  }
</script>
