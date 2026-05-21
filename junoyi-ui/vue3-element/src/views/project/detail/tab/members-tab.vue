<!-- 项目成员 Tab -->
<template>
  <div class="h-full flex flex-col">
    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div class="flex items-center gap-4">
        <div class="text-sm text-gray-500">
          共 {{ members.length }} 名成员
        </div>
      </div>
      <ElButton 
        v-if="projectRole.isOwner.value"
        type="primary" 
        @click="handleAdd"
      >
        <ArtSvgIcon icon="ri:user-add-line" class="mr-1" />
        添加成员
      </ElButton>
    </div>

    <!-- 成员列表 -->
    <div class="flex-1 overflow-auto">
      <ElTable
        :data="members"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <ElTableColumn type="index" label="序号" width="60" align="center" />
        
        <ElTableColumn prop="userName" label="成员" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <ElAvatar 
                :size="36" 
                :src="row.avatar"
              >
                <ArtSvgIcon icon="ri:user-line" :size="20" />
              </ElAvatar>
              <div class="flex flex-col">
                <span class="font-medium">{{ row.nickName || row.userName }}</span>
                <span class="text-xs text-gray-400">@{{ row.userName }}</span>
              </div>
            </div>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="role" label="角色" width="120">
          <template #default="{ row }">
            <ElTag :type="getProjectRoleTagType(row.role)" size="small">
              {{ getProjectRoleName(row.role) }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '在职' : '离职' }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="joinTime" label="加入时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.joinTime) }}
          </template>
        </ElTableColumn>

        <ElTableColumn label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <ElButton 
              v-if="projectRole.isOwner.value"
              text 
              type="primary" 
              size="small"
              @click="handleEditRole(row)"
            >
              编辑
            </ElButton>
            <ElButton 
              v-if="projectRole.isOwner.value"
              text 
              type="danger" 
              size="small"
              @click="handleRemove(row)"
            >
              移除
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </div>

    <!-- 添加成员对话框 -->
    <AddMemberDialog
      v-model:visible="addDialogVisible"
      :project-id="projectInfo.id"
      @success="loadMembers"
    />

    <!-- 编辑角色对话框 -->
    <EditMemberRoleDialog
      v-model:visible="editRoleDialogVisible"
      :project-id="projectInfo.id"
      :member-info="currentEditMember"
      @success="loadMembers"
    />
  </div>
</template>

<script setup lang="ts">
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import AddMemberDialog from './modules/add-member-dialog.vue'
  import EditMemberRoleDialog from './modules/edit-member-role-dialog.vue'
  import { fetchGetProjectMembers, fetchRemoveMember } from '@/api/project/member'
  import { getProjectRoleName, getProjectRoleTagType } from '@/enums/project'
  import { useProjectRole } from '@/hooks/useProjectRole'

  defineOptions({ name: 'MembersTab' })

  interface Props {
    projectInfo: Api.Project.ProjectDetailVO
  }

  const props = defineProps<Props>()

  // 使用项目角色权限
  const projectRole = useProjectRole(computed(() => props.projectInfo.currentUserRole))

  const members = ref<Api.Project.ProjectMemberVO[]>([])
  const loading = ref(false)
  const addDialogVisible = ref(false)
  const editRoleDialogVisible = ref(false)
  const currentEditMember = ref<Api.Project.ProjectMemberVO | null>(null)

  /**
   * 加载成员列表
   */
  const loadMembers = async () => {
    try {
      loading.value = true
      const data = await fetchGetProjectMembers(props.projectInfo.id)
      members.value = data
    } catch (error) {
      console.error('加载成员列表失败:', error)
      ElMessage.error('加载成员列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 格式化日期
   */
  const formatDate = (dateStr: string | undefined): string => {
    if (!dateStr) return '-'
    if (dateStr.includes('T')) {
      return dateStr.replace('T', ' ').substring(0, 19)
    }
    return dateStr
  }

  /**
   * 添加成员
   */
  const handleAdd = () => {
    addDialogVisible.value = true
  }

  /**
   * 编辑成员角色
   */
  const handleEditRole = (member: Api.Project.ProjectMemberVO) => {
    currentEditMember.value = member
    editRoleDialogVisible.value = true
  }

  /**
   * 移除成员
   */
  const handleRemove = async (member: Api.Project.ProjectMemberVO) => {
    try {
      await ElMessageBox.confirm(
        `确定要移除成员「${member.nickName || member.userName}」吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      await fetchRemoveMember(props.projectInfo.id, member.id)
      ElMessage.success('移除成功')
      await loadMembers()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('移除失败:', error)
        ElMessage.error('移除失败')
      }
    }
  }

  // 监听项目信息变化
  watch(() => props.projectInfo.id, (newId) => {
    if (newId) {
      loadMembers()
    }
  }, { immediate: true })
</script>

<style scoped lang="scss">
</style>
