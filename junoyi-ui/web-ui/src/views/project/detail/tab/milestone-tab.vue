<!-- 项目详情 - 项目里程碑tab页 -->
<template>
  <div class="h-full flex flex-col">
    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div class="text-sm text-gray-500">
        共 {{ milestoneList.length }} 个里程碑
      </div>
      <ElButton v-if="projectRole.isOwner.value || projectRole.isAdmin.value" type="primary" @click="handleAdd">
        <ArtSvgIcon icon="ri:flag-line" class="mr-1" />
        添加里程碑
      </ElButton>
    </div>

    <!-- 里程碑列表 -->
    <div v-if="milestoneList.length > 0" class="flex-1 overflow-auto">
      <div class="space-y-3">
        <ElCard
          v-for="(milestone, index) in milestoneList"
          :key="milestone.id"
          shadow="never"
          class="milestone-card"
          :class="{ 'milestone-completed': milestone.status === 2 }"
        >
          <div class="flex items-center gap-3">
            <!-- 左侧：排序序号 -->
            <div class="flex-shrink-0 w-10">
              <div class="text-base font-bold text-gray-400">
                #{{ milestone.sort || index + 1 }}
              </div>
            </div>

            <!-- 里程碑名称和状态 -->
            <div class="flex-shrink-0" style="width: 200px;">
              <div class="flex items-center gap-2">
                <span class="text-[14px] font-semibold text-gray-900 truncate">{{ milestone.name }}</span>
                <ElTag
                  :type="milestone.statusType as any"
                  size="small"
                  effect="plain"
                >
                  {{ milestone.statusLabel }}
                </ElTag>
              </div>
            </div>

            <!-- 负责人和截止时间（合并一列） -->
            <div class="flex-shrink-0" style="width: 230px;">
              <div class="space-y-1">
                <div class="flex items-center text-[13px]">
                  <span class="text-gray-500 w-[70px] flex-shrink-0">负责人：</span>
                  <span class="text-gray-700 truncate">{{ milestone.nickName || '未指定' }}</span>
                </div>
                <div class="flex items-center text-[13px]">
                  <span class="text-gray-500 w-[70px] flex-shrink-0">截止时间：</span>
                  <span class="text-gray-700 truncate">{{ milestone.dueTime ? formatDate(milestone.dueTime) : '无' }}</span>
                </div>
              </div>
            </div>

            <!-- 描述（占据更多空间，显示2行） -->
            <div class="flex-1 min-w-0 py-1">
              <div v-if="milestone.description" class="text-[13px] text-gray-600 line-clamp-2 leading-relaxed" :title="milestone.description">
                {{ milestone.description }}
              </div>
              <div v-else class="text-[13px] text-gray-400 italic">暂无描述</div>
            </div>

            <!-- 右侧：操作按钮 -->
            <div class="flex-shrink-0 flex items-center gap-1">
              <!-- 完成里程碑按钮 -->
              <ElTooltip
                v-if="milestone.status !== 1 && (projectRole.isOwner.value || projectRole.isAdmin.value)"
                content="完成里程碑"
                placement="top"
              >
                <ElButton
                  text
                  circle
                  type="success"
                  size="small"
                  @click="handleComplete(milestone)"
                >
                  <ArtSvgIcon icon="ri:checkbox-circle-line" class="text-base" />
                </ElButton>
              </ElTooltip>

              <ElTooltip content="编辑" v-if="projectRole.isOwner.value || projectRole.isAdmin.value" placement="top">
                <ElButton
                  text
                  circle
                  size="small"
                  @click="handleEdit(milestone)"
                >
                  <ArtSvgIcon icon="ri:edit-line" class="text-base" />
                </ElButton>
              </ElTooltip>

              <ElTooltip v-if="projectRole.isOwner.value" content="删除" placement="top">
                <ElButton
                  text
                  circle
                  type="danger"
                  size="small"
                  @click="handleDelete(milestone)"
                >
                  <ArtSvgIcon icon="ri:delete-bin-line" class="text-base" />
                </ElButton>
              </ElTooltip>
            </div>
          </div>
        </ElCard>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading" class="flex-1 flex items-center justify-center">
      <ElEmpty description="暂无里程碑数据">
        <ElButton type="primary" v-if="projectRole.isOwner.value || projectRole.isAdmin.value" @click="handleAdd">
          添加第一个里程碑
        </ElButton>
      </ElEmpty>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex-1 flex items-center justify-center">
      <ElSkeleton :rows="5" animated />
    </div>

    <!-- 添加/编辑里程碑对话框 -->
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
        <ElFormItem label="里程碑名称" prop="name">
          <ElInput v-model="formData.name" placeholder="请输入里程碑名称" maxlength="50" show-word-limit />
        </ElFormItem>

        <ElFormItem label="描述" prop="description">
          <ElInput
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入里程碑描述"
            maxlength="500"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem label="负责人" prop="ownerId">
          <ElSelect v-model="formData.ownerId" placeholder="请选择负责人" style="width: 100%" filterable clearable>
            <ElOption
              v-for="member in projectMembers"
              :key="member.userId"
              :label="member.nickName || member.userName"
              :value="member.userId"
            >
              <div class="flex items-center gap-2">
                <ElAvatar :size="24" :src="member.avatar">
                  <ArtSvgIcon icon="ri:user-line" />
                </ElAvatar>
                <span>{{ member.nickName || member.userName }}</span>
              </div>
            </ElOption>
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="截止时间" prop="dueTime">
          <ElDatePicker
            v-model="formData.dueTime"
            type="datetime"
            placeholder="请选择截止时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            clearable
          />
        </ElFormItem>

        <ElFormItem label="排序" prop="sort">
          <ElInputNumber v-model="formData.sort" :min="0" :max="9999" style="width: 100%" />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <div class="flex justify-end gap-2">
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </ElButton>
        </div>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { fetchGetProjectMilestoneList, fetchAddProjectMilestone, fetchUpdateProjectMilestone, fetchDeleteProjectMilestone, fetchCompleteProjectMilestone } from '@/api/project/milestone'
import { fetchGetProjectMembers } from '@/api/project/member'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { useProjectRole } from '@/hooks/useProjectRole'

defineOptions({ name: 'MilestoneTab' })

interface Props {
  projectInfo: Api.Project.ProjectDetailVO
}

const props = defineProps<Props>()

// 使用项目角色权限
const projectRole = useProjectRole(computed(() => props.projectInfo.currentUserRole))

// 里程碑列表
const milestoneList = ref<Api.Project.ProjectMilestoneVO[]>([])
const loading = ref(false)

// 项目成员列表（用于选择负责人）
const projectMembers = ref<Api.Project.ProjectMemberVO[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑里程碑' : '添加里程碑'))
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const formData = ref<Api.Project.ProjectMilestoneDTO>({
  id: undefined,
  projectId: props.projectInfo.id,
  name: '',
  description: '',
  ownerId: undefined,
  dueTime: undefined,
  sort: 0
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入里程碑名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

// 加载里程碑列表
const loadMilestoneList = async () => {
  try {
    loading.value = true
    const data = await fetchGetProjectMilestoneList(props.projectInfo.id)
    milestoneList.value = data
  } catch (error) {
    console.error('加载里程碑列表失败:', error)
    ElMessage.error('加载里程碑失败')
  } finally {
    loading.value = false
  }
}

// 加载项目成员列表
const loadProjectMembers = async () => {
  try {
    const data = await fetchGetProjectMembers(props.projectInfo.id)
    projectMembers.value = data.filter(m => m.status === 1) // 只显示在职成员
  } catch (error) {
    console.error('加载项目成员失败:', error)
  }
}

/**
 * 格式化日期
 */
const formatDate = (dateStr: string | undefined): string => {
  if (!dateStr) return '-'
  if (dateStr.includes('T')) {
    return dateStr.replace('T', ' ').substring(0, 16)
  }
  return dateStr.substring(0, 16)
}

/**
 * 添加里程碑
 */
const handleAdd = () => {
  isEdit.value = false
  formData.value = {
    id: undefined,
    projectId: props.projectInfo.id,
    name: '',
    description: '',
    ownerId: undefined,
    dueTime: undefined,
    sort: 0
  }
  dialogVisible.value = true
  loadProjectMembers()
}

/**
 * 编辑里程碑
 */
const handleEdit = (milestone: Api.Project.ProjectMilestoneVO) => {
  isEdit.value = true
  formData.value = {
    id: milestone.id,
    projectId: props.projectInfo.id,
    name: milestone.name,
    description: milestone.description,
    ownerId: milestone.ownerId,
    dueTime: milestone.dueTime,
    sort: milestone.sort
  }
  dialogVisible.value = true
  loadProjectMembers()
}

/**
 * 删除里程碑
 */
const handleDelete = async (milestone: Api.Project.ProjectMilestoneVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除里程碑「${milestone.name}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await fetchDeleteProjectMilestone(props.projectInfo.id,milestone.id)
    ElMessage.success('删除成功')
    await loadMilestoneList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 完成里程碑
 */
const handleComplete = async (milestone: Api.Project.ProjectMilestoneVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要完成里程碑「${milestone.name}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    await fetchCompleteProjectMilestone(props.projectInfo.id,milestone.id)
    ElMessage.success('里程碑已完成')
    await loadMilestoneList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成失败:', error)
      ElMessage.error('完成失败')
    }
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await fetchUpdateProjectMilestone(formData.value)
    } else {
      await fetchAddProjectMilestone(formData.value)
    }

    ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
    dialogVisible.value = false
    await loadMilestoneList()
  } catch (error) {
    console.error('提交失败:', error)
    if (error !== false) {
      ElMessage.error('提交失败')
    }
  } finally {
    submitLoading.value = false
  }
}

/**
 * 关闭对话框
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 监听项目信息变化
watch(() => props.projectInfo.id, (newId) => {
  if (newId) {
    loadMilestoneList()
  }
}, { immediate: true })
</script>

<style scoped lang="scss">
.milestone-card {
  border: 1px solid #e5e7eb;
  background-color: #ffffff;

  &:hover {
    border-color: #cbd5e1;
    background-color: #fafafa;
  }

  :deep(.el-card__body) {
    padding: 16px 20px;
  }
}

.milestone-completed {
  background-color: #f8fafc;
  border-color: #e2e8f0;

  &:hover {
    background-color: #f1f5f9;
    border-color: #cbd5e1;
  }

  :deep(.el-card__body) {
    position: relative;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 4px;
      background: linear-gradient(to bottom, #10b981, #059669);
      border-radius: 4px 0 0 4px;
    }
  }
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}
</style>
