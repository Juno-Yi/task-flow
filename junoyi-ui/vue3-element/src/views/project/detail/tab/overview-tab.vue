<!-- 项目概览 Tab -->
<template>
  <div class="h-full overflow-auto">
    <ElRow :gutter="20">
      <ElCol :span="16">
        <!-- 基本信息 -->
        <ElCard shadow="never" class="mb-4">
          <template #header>
            <div class="flex items-center">
              <ArtSvgIcon icon="ri:information-line" class="mr-2" />
              <span class="font-semibold">基本信息</span>
            </div>
          </template>
          <ElDescriptions :column="2" border>
            <ElDescriptionsItem label="项目名称" :span="2">
              {{ projectInfo.name }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="项目编号">
              <ElTag type="primary">{{ projectInfo.no }}</ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="项目状态">
              <ElTag
                  v-if="projectInfo.statusLabel"
                  :type="getValidTagType(projectInfo.statusType)"
              >
                {{ projectInfo.statusLabel }}
              </ElTag>
              <span v-else>-</span>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="项目类型">
              <ElTag
                  v-if="projectInfo.typeLabel"
                  :type="getValidTagType(projectInfo.typeLabelType)"
              >
                {{ projectInfo.typeLabel }}
              </ElTag>
              <span v-else>-</span>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="优先级">
              <ElTag
                  v-if="projectInfo.priorityLabel"
                  :type="getValidTagType(projectInfo.priorityType)"
              >
                {{ projectInfo.priorityLabel }}
              </ElTag>
              <span v-else>-</span>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="项目负责人">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:user-line" class="mr-1 text-primary" />
                {{ projectInfo.leaderName || '未设置' }}
              </div>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="成员数量">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:team-line" class="mr-1 text-primary" />
                {{ projectInfo.memberCount }} 人
              </div>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="创建时间">
              {{ formatDate(projectInfo.createTime) }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="更新时间">
              {{ formatDate(projectInfo.updateTime) }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="项目描述" :span="2">
              {{ projectInfo.description || '暂无描述' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="备注" :span="2">
              {{ projectInfo.remark || '暂无描述' }}
            </ElDescriptionsItem>
          </ElDescriptions>
        </ElCard>

        <!-- 项目活跃度 -->
        <ElCard shadow="never" class="mb-4">
          <template #header>
            <div class="flex items-center">
              <ArtSvgIcon icon="ri:bar-chart-line" class="mr-2" />
              <span class="font-semibold">项目活跃度</span>
            </div>
          </template>

        </ElCard>

      </ElCol>

      <ElCol :span="8">
        <!-- 项目成员 -->
        <ElCard shadow="never" class="mb-4">
          <template #header>
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:team-line" class="mr-2" />
                <span class="font-semibold">项目成员</span>
              </div>
              <ElButton text type="primary" size="small" @click="handleViewAllMembers">
                查看全部
              </ElButton>
            </div>
          </template>

          <div v-if="!projectInfo.recentMembers || projectInfo.recentMembers.length === 0">
            <ElEmpty description="暂无成员数据" :image-size="100" />
          </div>

          <div v-else class="space-y-3">
            <div
                v-for="member in projectInfo.recentMembers"
                :key="member.id"
                class="flex items-center gap-3 p-2 rounded hover:bg-gray-50 transition-colors"
            >
              <ElAvatar :size="40" :src="member.avatar">
                <ArtSvgIcon icon="ri:user-line" :size="20" />
              </ElAvatar>
              <div class="flex-1 min-w-0">
                <div class="font-medium truncate">{{ member.nickName || member.userName }}</div>
                <div class="text-xs text-gray-400 truncate">@{{ member.userName }}</div>
              </div>
              <ElTag :type="getProjectRoleTagType(member.role)" size="small">
                {{ getProjectRoleName(member.role) }}
              </ElTag>
            </div>
          </div>
        </ElCard>

        <!-- 快速操作 -->
        <ElCard shadow="never">
          <template #header>
            <div class="flex items-center">
              <ArtSvgIcon icon="ri:flashlight-line" class="mr-2" />
              <span class="font-semibold">快速操作</span>
            </div>
          </template>
          <ElSpace direction="vertical" :fill="true" style="width: 100%">
            <ElButton style="width: 100%" v-if="projectRole.isOwner.value || projectRole.isAdmin.value" @click="emit('add-member')">
              <ArtSvgIcon icon="ri:user-add-line" class="mr-1" />
              添加成员
            </ElButton>
            <ElButton style="width: 100%" v-if="projectRole.isOwner.value || projectRole.isAdmin.value || projectRole.isMember.value" @click="emit('add-document')">
              <ArtSvgIcon icon="ri:file-add-line" class="mr-1" />
              添加文档
            </ElButton>
            <ElButton style="width: 100%" v-if="projectRole.isOwner.value || projectRole.isAdmin.value" @click="emit('add-milestone')">
              <ArtSvgIcon icon="ri:flag-line" class="mr-1" />
              添加里程碑
            </ElButton>
            <ElEmpty v-else description="暂无快捷操作" :image-size="100" />
          </ElSpace>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { getProjectRoleName, getProjectRoleTagType } from '@/enums/project'
import { useProjectRole } from '@/hooks/useProjectRole'

defineOptions({ name: 'OverviewTab' })

interface Props {
  projectInfo: Api.Project.ProjectDetailVO
}

const props = defineProps<Props>()

// 使用项目角色权限
const projectRole = useProjectRole(computed(() => props.projectInfo.currentUserRole))

const emit = defineEmits<{
  'add-member': []
  'add-document': []
  'add-milestone': []
  'switch-tab': [tabName: string]
}>()

/**
 * 查看全部成员 - 切换到成员tab
 */
const handleViewAllMembers = () => {
  emit('switch-tab', 'members')
}

/**
 * 验证并返回有效的 Tag type
 */
const getValidTagType = (type: string | undefined): 'success' | 'info' | 'warning' | 'danger' => {
  const validTypes = ['success', 'info', 'warning', 'danger']
  if (type && validTypes.includes(type)) {
    return type as 'success' | 'info' | 'warning' | 'danger'
  }
  return 'info'
}

/**
 * 格式化日期
 */
const formatDate = (dateStr: string | undefined): string => {
  if (!dateStr) return '-'
  if (dateStr.includes('T')) {
    return dateStr.split('T')[0]
  }
  return dateStr.split(' ')[0]
}
</script>

<style scoped lang="scss">
</style>
