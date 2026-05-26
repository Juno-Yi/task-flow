<!-- 项目概览 Tab -->
<template>
  <div class="overview-page h-full overflow-auto">

    <!--  左边区域  -->
    <ElRow :gutter="20" class="overview-content-row">
      <ElCol :xs="24" :xl="17" :lg="16">
        <ElCard shadow="never" class="mb-4">
          <template #header>
            <div class="flex items-center justify-between gap-4">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:information-line" class="mr-2" />
                <span class="font-semibold">基本信息</span>
              </div>
              <div class="flex flex-wrap items-center gap-2">
                <ElTag v-if="projectInfo.statusLabel" :type="getValidTagType(projectInfo.statusType)">{{ projectInfo.statusLabel }}</ElTag>
                <ElTag v-if="projectInfo.typeLabel" :type="getValidTagType(projectInfo.typeLabelType)" effect="plain">{{ projectInfo.typeLabel }}</ElTag>
                <ElTag v-if="projectInfo.priorityLabel" :type="getValidTagType(projectInfo.priorityType)" effect="plain">{{ projectInfo.priorityLabel }}</ElTag>
              </div>
            </div>
          </template>
          <ElDescriptions :column="2" border class="overview-descriptions">
            <ElDescriptionsItem label="项目名称" :span="2">{{ projectInfo.name }}</ElDescriptionsItem>
            <ElDescriptionsItem label="项目编号"><ElTag type="primary">{{ projectInfo.no }}</ElTag></ElDescriptionsItem>
            <ElDescriptionsItem label="项目负责人">{{ projectInfo.leaderName || '未设置' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="成员数量">{{ projectInfo.memberCount }} 人</ElDescriptionsItem>
            <ElDescriptionsItem label="创建时间">{{ formatDate(projectInfo.createTime) }}</ElDescriptionsItem>
            <ElDescriptionsItem label="更新时间">{{ formatDate(projectInfo.updateTime) }}</ElDescriptionsItem>
            <ElDescriptionsItem label="项目描述" :span="2">{{ projectInfo.description || '暂无项目描述' }}</ElDescriptionsItem>
          </ElDescriptions>
        </ElCard>

        <ElCard shadow="never" class="chart-card mb-4">
          <template #header>
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <ArtSvgIcon icon="ri:line-chart-line" class="mr-2" />
                <span class="font-semibold">活跃趋势</span>
              </div>
            </div>
          </template>
          <ProjectActivityTrendChart :data="overviewData.projectActivityTrend" />
        </ElCard>

        <ElRow :gutter="20">
          <ElCol :xs="24" :md="10">
            <ElCard shadow="never" class="chart-card mb-4 h-full">
              <template #header>
                <div class="flex items-center">
                  <ArtSvgIcon icon="ri:pie-chart-line" class="mr-2" />
                  <span class="font-semibold">需求情况</span>
                </div>
              </template>
              <ProjectRequirementSituationChart :data="overviewData.projectRequirementSituation" />
            </ElCard>
          </ElCol>

          <ElCol :xs="24" :md="14">
            <ElCard shadow="never" class="chart-card mb-4 h-full">
              <template #header>
                <div class="flex items-center">
                  <ArtSvgIcon icon="ri:bar-chart-box-line" class="mr-2" />
                  <span class="font-semibold">需求近期完成情况</span>
                </div>
              </template>
              <ProjectRequirementCompletedChart :data="overviewData.projectRequirementCompletedVO" />
            </ElCard>
          </ElCol>
        </ElRow>

        <ElCard shadow="never" class="chart-card mt-4 mb-4">
          <template #header>
            <div class="flex items-center">
              <ArtSvgIcon icon="ri:line-chart-line" class="mr-2" />
              <span class="font-semibold">近期任务完成趋势</span>
            </div>
          </template>
          <div class="chart-placeholder chart-placeholder-task-trend">预留图表区域：近期任务完成趋势折线图</div>
        </ElCard>
      </ElCol>

      <!--   右边区域   -->
      <ElCol :xs="24" :xl="7" :lg="8">
        <ElCard shadow="never" class="mb-4">
          <template #header>
            <div class="flex items-center justify-between">
              <div class="flex items-center"><ArtSvgIcon icon="ri:team-line" class="mr-2" /><span class="font-semibold">项目成员</span></div>
              <ElButton text type="primary" size="small" @click="handleViewAllMembers">查看全部</ElButton>
            </div>
          </template>
          <div v-if="recentMembers.length === 0"><ElEmpty description="暂无成员数据" :image-size="90" /></div>
          <div v-else class="space-y-3">
            <div v-for="member in recentMembers" :key="member.id" class="member-card">
              <ElAvatar :size="40" :src="member.avatar"><ArtSvgIcon icon="ri:user-line" :size="20" /></ElAvatar>
              <div class="flex-1 min-w-0">
                <div class="font-medium truncate">{{ member.nickName || member.userName }}</div>
                <div class="text-xs text-gray-400 truncate">@{{ member.userName }}</div>
              </div>
              <ElTag :type="getProjectRoleTagType(member.role)" size="small">{{ getProjectRoleName(member.role) }}</ElTag>
            </div>
          </div>
        </ElCard>

        <ElCard shadow="never" class="mb-4">
          <template #header>
            <div class="flex items-center"><ArtSvgIcon icon="ri:flashlight-line" class="mr-2" /><span class="font-semibold">快速操作</span></div>
          </template>
          <ElSpace direction="vertical" :fill="true" style="width: 100%">
            <ElButton v-if="canManageProject" style="width: 100%" @click="emit('add-member')"><ArtSvgIcon icon="ri:user-add-line" class="mr-1" />添加成员</ElButton>
            <ElButton v-if="canContribute" style="width: 100%" @click="emit('add-document')"><ArtSvgIcon icon="ri:file-add-line" class="mr-1" />添加文档</ElButton>
            <ElButton v-if="canManageProject" style="width: 100%" @click="emit('add-milestone')"><ArtSvgIcon icon="ri:flag-line" class="mr-1" />添加里程碑</ElButton>
            <ElEmpty v-if="!canManageProject && !canContribute" description="暂无快捷操作" :image-size="90" />
          </ElSpace>
        </ElCard>

        <ElCard shadow="never" class="mb-4">
          <template #header>
            <div class="flex items-center"><ArtSvgIcon icon="ri:dashboard-3-line" class="mr-2" /><span class="font-semibold">关键指标</span></div>
          </template>
          <div class="side-stat-grid">
            <div class="side-stat-item">
              <span>项目完成度</span>
              <strong>--</strong>
            </div>
            <div class="side-stat-item">
              <span>进行中任务</span>
              <strong>--</strong>
            </div>
            <div class="side-stat-item">
              <span>待开始需求</span>
              <strong>--</strong>
            </div>
            <div class="side-stat-item">
              <span>逾期任务量</span>
              <strong>--</strong>
            </div>
          </div>
        </ElCard>

        <ElCard shadow="never">
          <template #header>
            <div class="flex items-center"><ArtSvgIcon icon="ri:alarm-warning-line" class="mr-2" /><span class="font-semibold">待办 / 风险预留区</span></div>
          </template>
          <div class="placeholder-list">
            <div class="placeholder-list-item">可放：即将到期事项</div>
            <div class="placeholder-list-item">可放：待处理需求 / 任务</div>
            <div class="placeholder-list-item">可放：风险提醒 / 阻塞项</div>
            <div class="placeholder-list-item">可放：最近动态</div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { fetchGetProjectOverview } from '@/api/project/detail'
import { getProjectRoleName, getProjectRoleTagType } from '@/enums/project'
import { useProjectRole } from '@/hooks/useProjectRole'
import ProjectActivityTrendChart from './modules/overview/project-activity-trend-chart.vue'
import ProjectRequirementCompletedChart from './modules/overview/project-requirement-completed-chart.vue'
import ProjectRequirementSituationChart from './modules/overview/project-requirement-situation-chart.vue'

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

const recentMembers = computed(() => props.projectInfo.recentMembers || [])
const canManageProject = computed(() => projectRole.isOwner.value || projectRole.isAdmin.value)
const canContribute = computed(() => canManageProject.value || projectRole.isMember.value)
const overviewData = ref<Api.Project.ProjectOverviewVO>({
  projectActivityTrend: [],
  projectRequirementSituation: [],
  projectRequirementCompletedVO: {
    sevenDayList: [],
    thirtyDayList: [],
    ninetyDayList: []
  }
})

/**
 * 查看全部成员 - 切换到成员tab
 */
const handleViewAllMembers = () => {
  emit('switch-tab', 'members')
}

/**
 * 验证并返回有效的 Tag type
 */
const getValidTagType = (type: string | undefined): 'success' | 'info' | 'warning' | 'danger' | 'primary' => {
  const validTypes = ['success', 'info', 'warning', 'danger', 'primary']
  if (type && validTypes.includes(type)) {
    return type as 'success' | 'info' | 'warning' | 'danger' | 'primary'
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

const loadOverviewData = async () => {
  if (!props.projectInfo.no) return
  try {
    const data = await fetchGetProjectOverview(props.projectInfo.no)
    overviewData.value = data || {
      projectActivityTrend: [],
      projectRequirementSituation: [],
      projectRequirementCompletedVO: {
        sevenDayList: [],
        thirtyDayList: [],
        ninetyDayList: []
      }
    }
  } catch (error) {
    console.error('加载项目概览数据失败：', error)
  }
}

watch(() => props.projectInfo.no, () => {
  loadOverviewData()
}, { immediate: true })
</script>

<style scoped lang="scss">
.overview-page {
  padding: 4px 2px 12px;
}


.overview-descriptions :deep(.el-descriptions__label) {
  width: 110px;
}

.chart-card {
  border-radius: 12px;
}

.chart-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.chart-placeholder {
  min-height: 240px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  border: 1px dashed var(--el-border-color);
  border-radius: 10px;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  text-align: center;
  line-height: 1.8;
}

.chart-placeholder-lg {
  min-height: 180px;
}

.chart-placeholder-md {
  min-height: 180px;
}

.chart-placeholder-task-trend {
  min-height: 240px;
}

.member-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  transition: background-color 0.2s ease;

  &:hover {
    background: var(--el-fill-color-light);
  }
}

.side-stat-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.side-stat-item {
  padding: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 10px;
  background: var(--el-fill-color-light);

  span {
    display: block;
    margin-bottom: 6px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  strong {
    font-size: 18px;
    color: var(--el-text-color-primary);
  }
}

.side-stat-item-full {
  grid-column: 1 / -1;
}

.placeholder-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.placeholder-list-item {
  padding: 12px 14px;
  border: 1px dashed var(--el-border-color);
  border-radius: 10px;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
}

@media (max-width: 768px) {
  .chart-placeholder-lg {
    min-height: 160px;
  }

  .chart-placeholder-md {
    min-height: 160px;
  }

  .chart-placeholder-task-trend {
    min-height: 200px;
  }

  .side-stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
