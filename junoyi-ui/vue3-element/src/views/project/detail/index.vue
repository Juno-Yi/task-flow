<!-- 项目协作 - 项目详情 -->
<template>
  <div class="art-full-height">
    <div class="flex flex-col h-full">
      <!-- 页面头部 -->
      <ElCard shadow="never" class="mb-1.5" body-class="!p-3">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2.5">
            <ArtSvgIcon icon="ri:git-repository-line" class="text-2xl text-primary" />
            <div class="flex items-center gap-3">
              <h1 class="text-base font-bold leading-none mb-0">{{ projectInfo.name }}</h1>
              <span class="text-sm text-gray-500">项目编号：{{ projectInfo.no }}</span>
            </div>
          </div>

          <ElButton text @click="goBack" class="!px-2 !py-1">
            <ArtSvgIcon icon="ri:arrow-left-line" class="mr-1" />
            返回项目列表
          </ElButton>
        </div>
      </ElCard>

      <!-- Tab 内容 -->
      <ElCard class="flex-1 art-table-card" shadow="never">
        <ElTabs v-model="activeTab" type="border-card" class="h-full flex flex-col" @tab-change="handleTabChange">
          <!-- 概览 -->
          <ElTabPane label="概览" name="overview" class="h-full">
            <template #label>
              <span class="flex items-center">
                <ArtSvgIcon icon="ri:dashboard-line" class="mr-2" />
                概览
              </span>
            </template>
            <OverviewTab
                :project-info="projectInfo"
                @add-member="handleAddMember"
                @add-document="handleAddDocument"
                @add-milestone="handleAddMilestone"
                @switch-tab="handleSwitchTab"
            />
          </ElTabPane>


        </ElTabs>
      </ElCard>
    </div>

    <!-- 编辑项目弹窗 -->

  </div>
</template>

<script setup lang="ts">
  import OverviewTab from "@views/project/detail/tab/overview-tab.vue";
  import { useRoute, useRouter } from 'vue-router'


  defineOptions({ name: 'ProjectDetail' })

  const route = useRoute()
  const router = useRouter()

  // 激活的Tab页
  const activeTab = ref('overview')
  // 加载
  const loading = ref(false)
  // 编辑弹窗可见
  const editDialogVisible = ref(false)

  // 项目信息
  const projectInfo = ref<Api.Project.ProjectDetailVO>({
    id: 0,
    no: '',
    name: '',
    description: '',
    leader: 0,
    leaderName: '',
    type: 0,
    typeLabel: '',
    typeLabelType: '',
    status: 0,
    statusLabel: '',
    statusType: '',
    priority: 0,
    priorityLabel: '',
    priorityType: '',
    memberCount: 0,
    progress: 0,
    totalTasks: 0,
    completedTasks: 0,
    pendingTasks: 0,
    repositoryCount: 0,
    documentCount: 0,
    milestoneCount: 0,
    createTime: '',
    updateTime: '',
    remark: '',
    currentUserRole: undefined
  })

  /**
   * 从 URL 初始化 Tab
   */
  const initTabFromUrl = () => {
    const tabFromUrl = route.query.tab as string
    if (tabFromUrl) {
      activeTab.value = tabFromUrl
    }
  }

  /**
   * 加载项目详情信息数据
   */
  const loadProjectDetailData = () => {
    const projectNo = route.query.no as string
    console.log("调试:",projectNo)
  }

  // 初始化时从 URL 读取 Tab
  onMounted(() => {
    initTabFromUrl()
  })

  // 监听路由参数变化，重新加载数据
  watch(() => route.query.no, (newNo) => {
    if (newNo) {
      loadProjectDetailData()
    }
  }, { immediate: true })


  /**
   * 返回上一页
   */
  const goBack = () => {
    router.back()
  }

  /**
   * 切换 Tab 页
   */
  const handleTabChange = () => {

  }

  /**
   * 切换 Tab 标签
   */
  const handleSwitchTab = () => {

  }

  /**
   * 添加项目成员
   */
  const handleAddMember = () => {

  }

  /**
   * 添加项目文档
   */
  const handleAddDocument = () => {

  }

  /**
   * 添加项目里程碑
   */
  const handleAddMilestone = () => {

  }



</script>
