<!-- 项目仓库 Tab -->
<template>
  <div class="h-full flex flex-col">
    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div class="text-sm text-gray-500">
        共 {{ repositories.length }} 个仓库
      </div>
      <ElButton v-if="projectRole.isOwner.value" type="primary" @click="handleAdd">
        <ArtSvgIcon icon="ri:add-line" class="mr-1" />
        添加仓库
      </ElButton>
    </div>

    <!-- 仓库列表 -->
    <div v-if="repositories.length > 0" class="flex-1 overflow-auto">
      <div class="space-y-3">
        <ElCard 
          v-for="repo in repositories" 
          :key="repo.id"
          shadow="hover"
          class="repo-card"
        >
          <div class="flex items-start gap-4">
            <!-- 左侧：平台图标 -->
            <div class="flex-shrink-0">
              <div class="w-12 h-12 rounded-lg bg-gray-50 flex items-center justify-center">
                <ArtSvgIcon 
                  :icon="getPlatformIcon(repo.type)" 
                  class="text-3xl"
                  :class="getPlatformColor(repo.type)"
                />
              </div>
            </div>

            <!-- 中间：仓库信息 -->
            <div class="flex-1 min-w-0">
              <!-- 标题行 -->
              <div class="flex items-center gap-2 mb-2">
                <span class="text-base font-semibold text-gray-900">{{ repo.name }}</span>
                <ElTag v-if="repo.isMain" type="success" size="small" effect="dark">主仓库</ElTag>
                <ElTag 
                  :type="repo.status === 1 ? 'success' : 'info'" 
                  size="small"
                  effect="plain"
                >
                  {{ repo.statusLabel }}
                </ElTag>
              </div>

              <!-- 平台和分支信息 -->
              <div class="flex items-center gap-3 mb-2 text-sm text-gray-500">
                <span class="flex items-center gap-1">
                  <ArtSvgIcon icon="ri:git-branch-line" class="text-base" />
                  {{ repo.branch || 'main' }}
                </span>
                <span>·</span>
                <span>{{ repo.typeLabel }}</span>
              </div>

              <!-- 仓库地址 -->
              <div class="mb-2">
                <ElLink 
                  :href="repo.url" 
                  target="_blank" 
                  type="primary" 
                  class="text-sm"
                  :underline="false"
                >
                  <div class="flex items-center gap-1">
                    <ArtSvgIcon icon="ri:link-line" class="text-base" />
                    <span class="truncate">{{ repo.url }}</span>
                    <ArtSvgIcon icon="ri:external-link-line" class="text-xs" />
                  </div>
                </ElLink>
              </div>

              <!-- 描述 -->
              <div v-if="repo.description" class="text-sm text-gray-600 line-clamp-2">
                {{ repo.description }}
              </div>
            </div>

            <!-- 右侧：操作按钮 -->
            <div class="flex-shrink-0 flex items-center gap-1">
              <ElTooltip content="编辑" v-if="projectRole.isOwner.value || projectRole.isAdmin.value" placement="top">
                <ElButton 
                  text 
                  circle
                  @click="handleEdit(repo)"
                >
                  <ArtSvgIcon icon="ri:edit-line" class="text-lg" />
                </ElButton>
              </ElTooltip>
              <ElTooltip v-if="projectRole.isOwner.value" content="删除" placement="top">
                <ElButton 
                  text 
                  circle
                  type="danger"
                  @click="handleDelete(repo)"
                >
                  <ArtSvgIcon icon="ri:delete-bin-line" class="text-lg" />
                </ElButton>
              </ElTooltip>
            </div>
          </div>
        </ElCard>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="flex-1 flex items-center justify-center">
      <ElEmpty description="暂无仓库数据">
        <ElButton type="primary" v-if="projectRole.isOwner.value" @click="handleAdd">添加第一个仓库</ElButton>
      </ElEmpty>
    </div>

    <!-- 添加/编辑仓库对话框 -->
    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <ElFormItem label="仓库名称" prop="name">
          <ElInput v-model="formData.name" placeholder="请输入仓库名称" />
        </ElFormItem>

        <ElFormItem label="仓库平台" prop="type">
          <ElSelect v-model="formData.type" placeholder="请选择仓库平台" style="width: 100%">
            <ElOption label="Gitee" value="gitee">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:git-repository-line" class="mr-2 text-red-500" />
                Gitee
              </div>
            </ElOption>
            <ElOption label="GitHub" value="github">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:github-fill" class="mr-2" />
                GitHub
              </div>
            </ElOption>
            <ElOption label="GitLab" value="gitlab">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:gitlab-fill" class="mr-2 text-orange-500" />
                GitLab
              </div>
            </ElOption>
            <ElOption label="自定义" value="custom">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:git-repository-line" class="mr-2 text-gray-500" />
                自定义
              </div>
            </ElOption>
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="仓库地址" prop="url">
          <ElInput v-model="formData.url" placeholder="请输入仓库地址，如：https://gitee.com/xxx/xxx.git" />
        </ElFormItem>

        <ElFormItem label="默认分支" prop="branch">
          <ElInput v-model="formData.branch" placeholder="请输入默认分支，如：main" />
        </ElFormItem>

        <ElFormItem label="仓库描述" prop="description">
          <ElInput
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入仓库描述"
          />
        </ElFormItem>

        <ElFormItem label="是否主仓库" prop="isMain">
          <ElSwitch v-model="formData.isMain" />
        </ElFormItem>

        <ElFormItem label="状态" prop="status">
          <ElRadioGroup v-model="formData.status">
            <ElRadio :value="1">正常</ElRadio>
            <ElRadio :value="0">禁用</ElRadio>
          </ElRadioGroup>
        </ElFormItem>

        <ElFormItem label="备注" prop="remark">
          <ElInput
            v-model="formData.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { 
    fetchGetRepoRepositoryList, 
    fetchAddRepoRepository, 
    fetchUpdateRepoRepository, 
    fetchDeleteRepoRepository 
  } from '@/api/project/repository'
  import { useProjectRole } from '@/hooks/useProjectRole'

  defineOptions({ name: 'RepositoryTab' })

  interface Props {
    projectInfo: Api.Project.ProjectDetailVO
  }

  const props = defineProps<Props>()

  // 使用项目角色权限
  const projectRole = useProjectRole(computed(() => props.projectInfo.currentUserRole))


  // 仓库列表
  const repositories = ref<Api.Project.ProjectRepositoryVO[]>([])
  const loading = ref(false)

  // 对话框
  const dialogVisible = ref(false)
  const dialogTitle = computed(() => formData.value.id ? '编辑仓库' : '添加仓库')
  const formRef = ref()

  // 表单数据
  const formData = ref<Api.Project.ProjectRepositoryDTO>({
    id: undefined,
    projectId: props.projectInfo.id,
    name: '',
    type: 'gitee',
    url: '',
    branch: 'main',
    description: '',
    isMain: false,
    status: 1,
    remark: ''
  })

  // 表单验证规则
  const formRules = {
    name: [
      { required: true, message: '请输入仓库名称', trigger: 'blur' }
    ],
    type: [
      { required: true, message: '请选择仓库平台', trigger: 'change' }
    ],
    url: [
      { required: true, message: '请输入仓库地址', trigger: 'blur' }
    ],
    branch: [
      { required: true, message: '请输入默认分支', trigger: 'blur' }
    ]
  }

  /**
   * 加载仓库列表
   */
  const loadRepositoryList = async () => {
    try {
      loading.value = true
      const data = await fetchGetRepoRepositoryList(props.projectInfo.id)
      repositories.value = data
    } catch (error) {
      console.error('加载仓库列表失败:', error)
      ElMessage.error('加载仓库列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取平台图标
   */
  const getPlatformIcon = (type: string): string => {
    const iconMap: Record<string, string> = {
      gitee: 'ri:git-repository-line',
      github: 'ri:github-fill',
      gitlab: 'ri:gitlab-fill',
      custom: 'ri:git-repository-line'
    }
    return iconMap[type] || 'ri:git-repository-line'
  }

  /**
   * 获取平台颜色
   */
  const getPlatformColor = (type: string): string => {
    const colorMap: Record<string, string> = {
      gitee: 'text-red-500',
      github: 'text-gray-800',
      gitlab: 'text-orange-500',
      custom: 'text-gray-500'
    }
    return colorMap[type] || 'text-gray-500'
  }

  /**
   * 添加仓库
   */
  const handleAdd = () => {
    formData.value = {
      id: undefined,
      projectId: props.projectInfo.id,
      name: '',
      type: 'gitee',
      url: '',
      branch: 'main',
      description: '',
      isMain: false,
      status: 1,
      remark: ''
    }
    dialogVisible.value = true
  }

  /**
   * 编辑仓库
   */
  const handleEdit = (repo: Api.Project.ProjectRepositoryVO) => {
    formData.value = { ...repo }
    dialogVisible.value = true
  }

  /**
   * 删除仓库
   */
  const handleDelete = (repo: Api.Project.ProjectRepositoryVO) => {
    ElMessageBox.confirm(
      `确定要删除仓库「${repo.name}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        await fetchDeleteRepoRepository(repo.id)
        ElMessage.success('删除成功')
        await loadRepositoryList()
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    }).catch(() => {
      // 取消删除
    })
  }

  /**
   * 提交表单
   */
  const handleSubmit = async () => {
    if (!formRef.value) return
    
    await formRef.value.validate(async (valid: boolean) => {
      if (valid) {
        try {
          if (formData.value.id) {
            await fetchUpdateRepoRepository(formData.value)
            ElMessage.success('编辑成功')
          } else {
            await fetchAddRepoRepository(formData.value)
            ElMessage.success('添加成功')
          }
          dialogVisible.value = false
          await loadRepositoryList()
        } catch (error) {
          console.error('提交失败:', error)
          ElMessage.error('操作失败')
        }
      }
    })
  }

  /**
   * 对话框关闭
   */
  const handleDialogClose = () => {
    formRef.value?.resetFields()
  }

  // 监听项目信息变化，重新加载数据
  watch(() => props.projectInfo.id, (newId) => {
    if (newId) {
      loadRepositoryList()
    }
  }, { immediate: true })
</script>

<style scoped lang="scss">
  .repo-card {
    transition: all 0.3s ease;
    border: 1px solid #e5e7eb;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      border-color: #d1d5db;
    }
  }

  .space-y-3 > * + * {
    margin-top: 12px;
  }

  .line-clamp-2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
</style>
