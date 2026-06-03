<template>
  <ElDialog
    :title="dialogTitle"
    :model-value="visible"
    @update:model-value="handleCancel"
    width="600px"
    align-center
    @closed="handleClosed"
  >
    <ArtForm
      ref="formRef"
      v-model="form"
      :items="formItems"
      :rules="rules"
      :span="24"
      label-width="80px"
      :show-reset="false"
      :show-submit="false"
    >
      <template #parentId>
        <ElTreeSelect
          v-model="form.parentId"
          :data="parentDeptOptions"
          node-key="id"
          :props="{ label: 'name', children: 'children' }"
          :render-after-expand="false"
          check-strictly
          clearable
          default-expand-all
          placeholder="选择上级部门（不选则为一级）"
          style="width: 100%"
        />
      </template>
    </ArtForm>

    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleCancel">取消</ElButton>
        <ElButton type="primary" :loading="submitting" @click="handleSubmit">确定</ElButton>
      </span>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormRules } from 'element-plus'
  import { ElTreeSelect } from 'element-plus'
  import type { FormItem } from '@/components/core/forms/art-form/index.vue'
  import ArtForm from '@/components/core/forms/art-form/index.vue'
  import { fetchAddDept, fetchUpdateDept, fetchGetDeptTree } from '@/api/system/department'

  interface Props {
    visible: boolean
    editData?: Api.System.DeptVO | null
    defaultParentId?: number
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'success'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    visible: false,
    editData: null,
    defaultParentId: 0
  })

  const emit = defineEmits<Emits>()

  const formRef = ref()
  const isEdit = ref(false)
  const submitting = ref(false)
  const deptTreeData = ref<Api.System.DeptVO[]>([])

  // 表单数据
  const form = reactive<Api.System.DeptDTO>({
    parentId: 0,
    name: '',
    leader: '',
    phonenumber: '',
    email: '',
    sort: 1,
    status: 1,
    remark: ''
  })

  // 校验规则
  const rules: FormRules = {
    name: [
      { required: true, message: '请输入部门名称', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    phonenumber: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    email: [
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
  }

  /**
   * 构建父级部门选项（排除当前编辑的部门及其子部门）
   */
  const buildParentOptions = (list: Api.System.DeptVO[], excludeId?: number): any[] => {
    const result: any[] = []
    
    for (const item of list) {
      if (item.id === excludeId) continue
      
      const node: any = {
        id: item.id,
        name: item.name,
        children: item.children?.length ? buildParentOptions(item.children, excludeId) : undefined
      }
      if (!node.children?.length) {
        delete node.children
      }
      result.push(node)
    }
    
    return result
  }

  // 父级部门选项
  const parentDeptOptions = computed(() => {
    const editId = isEdit.value ? props.editData?.id : undefined
    return buildParentOptions(deptTreeData.value, editId)
  })

  /**
   * 加载部门树数据
   */
  const loadDeptTree = async () => {
    try {
      const list = await fetchGetDeptTree()
      deptTreeData.value = list || []
    } catch (error) {
      console.error('获取部门树失败:', error)
    }
  }

  /**
   * 表单项配置
   */
  const formItems = computed<FormItem[]>(() => [
    { label: '上级部门', key: 'parentId' },
    { label: '部门名称', key: 'name', type: 'input', props: { placeholder: '请输入部门名称' } },
    { label: '负责人', key: 'leader', type: 'input', props: { placeholder: '请输入负责人' } },
    { label: '联系电话', key: 'phonenumber', type: 'input', props: { placeholder: '请输入联系电话' } },
    { label: '邮箱', key: 'email', type: 'input', props: { placeholder: '请输入邮箱' } },
    { label: '排序', key: 'sort', type: 'number', props: { min: 0, controlsPosition: 'right', style: { width: '100%' } } },
    { label: '状态', key: 'status', type: 'switch', props: { activeValue: 1, inactiveValue: 0 } },
    { label: '备注', key: 'remark', type: 'textarea', props: { placeholder: '请输入备注', rows: 3 } }
  ])

  const dialogTitle = computed(() => isEdit.value ? '编辑部门' : '新建部门')

  /**
   * 重置表单数据
   */
  const resetForm = (): void => {
    formRef.value?.reset()
    Object.assign(form, {
      id: undefined,
      parentId: 0,
      name: '',
      leader: '',
      phonenumber: '',
      email: '',
      sort: 1,
      status: 1,
      remark: ''
    })
  }

  /**
   * 加载表单数据（编辑模式）
   */
  const loadFormData = (): void => {
    if (!props.editData) return

    isEdit.value = true
    const row = props.editData

    Object.assign(form, {
      id: row.id,
      parentId: row.parentId ?? 0,
      name: row.name || '',
      leader: row.leader || '',
      phonenumber: row.phonenumber || '',
      email: row.email || '',
      sort: row.sort ?? 1,
      status: row.status ?? 1,
      remark: row.remark || ''
    })
  }

  /**
   * 提交表单
   */
  const handleSubmit = async (): Promise<void> => {
    if (!formRef.value || submitting.value) return

    try {
      await formRef.value.validate()
      submitting.value = true

      const submitData = { ...form }
      if (!submitData.parentId) {
        submitData.parentId = 0
      }

      if (isEdit.value) {
        await fetchUpdateDept(submitData)
      } else {
        await fetchAddDept(submitData)
      }

      emit('success')
      handleCancel()
    } catch {
      // 表单校验失败或接口报错
    } finally {
      submitting.value = false
    }
  }

  /**
   * 取消操作
   */
  const handleCancel = (): void => {
    emit('update:visible', false)
  }

  /**
   * 对话框关闭后的回调
   */
  const handleClosed = (): void => {
    resetForm()
    isEdit.value = false
  }

  /**
   * 监听对话框显示状态
   */
  watch(
    () => props.visible,
    (newVal) => {
      if (newVal) {
        loadDeptTree()
        if (props.editData) {
          nextTick(() => loadFormData())
        } else if (props.defaultParentId) {
          form.parentId = props.defaultParentId
        }
      }
    }
  )
</script>
