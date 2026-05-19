<!-- 项目信息弹窗 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <ElFormItem label="项目名称" prop="name">
        <ElInput
          v-model="formData.name"
          placeholder="请输入项目名称"
          clearable
        />
      </ElFormItem>

      <ElFormItem label="项目描述" prop="description">
        <ElInput
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入项目描述"
          clearable
        />
      </ElFormItem>

      <ElFormItem label="项目负责人" prop="leader">
        <ElSelect
          v-model="formData.leader"
          placeholder="请输入昵称搜索项目负责人"
          filterable
          remote
          reserve-keyword
          :remote-method="handleUserRemoteSearch"
          :loading="userLoading"
          class="w-full"
          clearable
        >
          <ElOption
            v-for="user in userList"
            :key="user.userId"
            :label="user.nickName"
            :value="user.userId"
          />
        </ElSelect>
      </ElFormItem>

      <ElFormItem label="项目类型" prop="type">
        <ElSelect
          v-model="formData.type"
          placeholder="请选择项目类型"
          class="w-full"
        >
          <ElOption
            v-for="item in projectTypeOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="Number(item.dictValue)"
          />
        </ElSelect>
      </ElFormItem>

      <ElFormItem label="项目优先级" prop="priority">
        <ElSelect
          v-model="formData.priority"
          placeholder="请选择项目优先级"
          class="w-full"
        >
          <ElOption
            v-for="item in projectPriorityOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="Number(item.dictValue)"
          />
        </ElSelect>
      </ElFormItem>

      <ElFormItem label="备注" prop="remark">
        <ElInput
          v-model="formData.remark"
          type="textarea"
          :rows="2"
          placeholder="请输入备注"
          clearable
        />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElSpace>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="submitLoading" @click="handleSubmit" v-ripple>
          确定
        </ElButton>
      </ElSpace>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { type FormInstance, type FormRules } from 'element-plus'
  import { DialogType } from '@/types'
  import { fetchGetDictDataByType } from '@/api/system/dict'
  // import { fetchGetUserSelectList } from '@/api/system/user'
  import { fetchAddRepo, fetchUpdateRepo } from '@/api/project/list'
  import {fetchGetUserOptions} from "@/api/system/user";

  defineOptions({ name: 'RepoDialog' })

  interface RepoFormData {
    id?: number
    name: string
    description: string
    leader?: number
    type?: number
    status?: number
    priority?: number
    remark?: string
  }

  interface Props {
    type: DialogType
    repoData?: Partial<RepoFormData>
  }

  const props = defineProps<Props>()
  const dialogVisible = defineModel<boolean>('visible', { required: true })

  const emit = defineEmits<{
    submit: []
  }>()

  const formRef = ref<FormInstance>()
  const submitLoading = ref(false)
  const userLoading = ref(false)

  // 用户列表
  const userList = ref<Api.System.SysUserVO[]>([])
  // 项目类型字典选项
  const projectTypeOptions = ref<Api.System.DictDataVO[]>([])
  // 项目优先级字典选项
  const projectPriorityOptions = ref<Api.System.DictDataVO[]>([])

  const formData = ref<RepoFormData>({
    name: '',
    description: '',
    leader: undefined,
    type: undefined,
    status: undefined,
    priority: undefined,
    remark: ''
  })

  const rules = computed<FormRules>(() => ({
    name: [
      { required: true, message: '请输入项目名称', trigger: 'blur' },
      { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
    ],
    leader: [
      { required: true, message: '请选择项目负责人', trigger: 'change' }
    ],
    type: [
      { required: true, message: '请选择项目类型', trigger: 'change' }
    ],
    status: props.type === 'edit' ? [
      { required: true, message: '请选择状态', trigger: 'change' }
    ] : []
  }))

  const dialogTitle = computed(() => {
    return props.type === 'add' ? '新建项目' : '编辑项目'
  })

  /**
   * 加载字典数据
   */
  const loadDictData = async () => {
    try {
      // 加载项目类型字典
      projectTypeOptions.value = await fetchGetDictDataByType('project_type')
      // 加载项目优先级字典
      projectPriorityOptions.value = await fetchGetDictDataByType('project_priority')
    } catch (error) {
      console.error('加载字典数据失败:', error)
    }
  }

  /**
   * 加载用户列表
   */
  const loadUserList = async (keyword?: string) => {
    userLoading.value = true
    try {
      userList.value = await fetchGetUserOptions({ nickName: keyword || undefined })
    } catch (error) {
      console.error('加载用户列表失败:', error)
      ElMessage.error('加载用户列表失败')
    } finally {
      userLoading.value = false
    }
  }

  /**
   * 远程搜索用户
   */
  const handleUserRemoteSearch = (keyword: string) => {
    loadUserList(keyword)
  }

  watch(
    () => props.repoData,
    (val) => {
      if (val && Object.keys(val).length > 0) {
        formData.value = { ...formData.value, ...val }
      }
    },
    { immediate: true, deep: true }
  )

  watch(dialogVisible, (val) => {
    if (val) {
      // 弹窗打开时加载数据
      loadDictData()
      loadUserList()
    }
  })

  // 组件挂载时预加载数据
  onMounted(() => {
    loadDictData()
    loadUserList()
  })

  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      submitLoading.value = true

      // 调用 API 保存数据
      if (props.type === 'add') {
        await fetchAddRepo(formData.value as Api.Project.ProjectListDTO)
        ElMessage.success('创建成功')
      } else {
        await fetchUpdateRepo(formData.value as Api.Project.ProjectListDTO)
        ElMessage.success('更新成功')
      }

      dialogVisible.value = false
      emit('submit')
    } catch (error) {
      console.error('提交失败:', error)
      if (props.type === 'add')
        ElMessage.error('创建失败')
    } finally {
      submitLoading.value = false
    }
  }

  const handleClosed = () => {
    formRef.value?.resetFields()
    formData.value = {
      name: '',
      description: '',
      leader: undefined,
      type: undefined,
      status: undefined,
      priority: undefined,
      remark: ''
    }
  }
</script>
