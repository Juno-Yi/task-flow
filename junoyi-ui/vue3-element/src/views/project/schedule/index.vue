<!-- 项目协作 - 项目排期 -->
<template>
  <div class="art-full-height">
    <div class="flex flex-col h-full">
      <ElCard shadow="never" class="mb-4 flex-shrink-0 filter-card">
        <div class="filter-content">
          <div class="filter-item">
            <ElInput v-model="queryParams.projectTitle" placeholder="搜索项目名称" clearable class="filter-input" @clear="handleSearch" @keyup.enter="handleSearch">
              <template #prefix><ElIcon><Search /></ElIcon></template>
            </ElInput>
          </div>
          <div class="filter-item">
            <ElSelect v-model="queryParams.leader" placeholder="选择负责人" clearable filterable class="filter-select" @change="handleSearch">
              <ElOption v-for="user in leaderOptions" :key="user.userId" :label="user.nickName" :value="user.userId" />
            </ElSelect>
          </div>
          <div class="filter-item">
            <ElCheckbox v-model="showCompletedProjects" @change="handleSearch">显示已完成项目</ElCheckbox>
          </div>
          <div class="filter-item">
            <ElButton type="primary" :icon="Search" @click="handleSearch">查询</ElButton>
            <ElButton :icon="Refresh" @click="handleReset">重置</ElButton>
          </div>
        </div>
      </ElCard>
      <ElCard shadow="never" class="mb-4 flex-shrink-0">
        <div class="view-mode-selector">
          <ElRadioGroup v-model="viewMode" @change="handleViewModeChange">
            <ElRadioButton value="Day">日视图</ElRadioButton>
            <ElRadioButton value="Week">周视图</ElRadioButton>
            <ElRadioButton value="Month">月视图</ElRadioButton>
            <ElRadioButton value="Quarter">季度视图</ElRadioButton>
          </ElRadioGroup>
        </div>
      </ElCard>
      <ElCard shadow="never" class="flex-1 min-h-0 art-table-card">
        <div v-loading="loading" class="gantt-container">
          <div v-if="projectList.length === 0 && !loading" class="empty-state">
            <ElEmpty description="暂无项目数据" />
          </div>
          <div v-else ref="ganttChartRef" class="gantt-chart"></div>
        </div>
      </ElCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { fetchGetProjectScheduleGantList } from '@/api/project/schedule'
import Gantt from 'frappe-gantt'
// 注释掉CSS导入，改为在style中直接引入或使用CDN
// import 'frappe-gantt/dist/frappe-gantt.css'

const queryParams = ref({ projectTitle: '', leader: undefined })
const showCompletedProjects = ref(false)
const viewMode = ref('Month')
const loading = ref(false)
const projectList = ref([])
const leaderOptions = ref([])
const ganttChartRef = ref(null)
let ganttInstance = null

const fetchProjectList = async () => {
  loading.value = true
  try {
    const data = await fetchGetProjectScheduleGantList(queryParams.value)
    const allProjects = data || []
    const leaderMap = new Map()
    allProjects.forEach(project => {
      if (project.leader && project.leaderName) leaderMap.set(project.leader, project.leaderName)
    })
    leaderOptions.value = Array.from(leaderMap.entries()).map(([userId, nickName]) => ({ userId, nickName }))
    projectList.value = showCompletedProjects.value ? allProjects : allProjects.filter(item => item.status !== 6)
    await nextTick()
    renderGanttChart()
  } catch (error) {
    ElMessage.error('获取项目列表失败')
  } finally {
    loading.value = false
  }
}

const renderGanttChart = () => {
  if (!ganttChartRef.value || projectList.value.length === 0) return
  if (ganttInstance) {
    ganttChartRef.value.innerHTML = ''
    ganttInstance = null
  }
  const tasks = projectList.value.map(project => ({
    id: `project-${project.projectId}`,
    name: project.projectTitle,
    start: (project.planStartTime ? new Date(project.planStartTime) : new Date()).toISOString().split('T')[0],
    end: (project.planEndTime ? new Date(project.planEndTime) : new Date()).toISOString().split('T')[0],
    progress: project.completionRate || 0,
    custom_class: project.isOverdue ? 'gantt-task-overdue' : ''
  }))
  try {
    ganttInstance = new Gantt(ganttChartRef.value, tasks, {
      view_mode: viewMode.value,
      language: 'zh',
      bar_height: 30,
      bar_corner_radius: 3,
      arrow_curve: 5,
      padding: 18,
      date_format: 'YYYY-MM-DD',
      custom_popup_html: task => {
        const project = projectList.value.find(p => `project-${p.projectId}` === task.id)
        if (!project) return ''
        return `<div class="gantt-popup"><div class="popup-title">${project.projectTitle}</div><div class="popup-content"><div>编号: ${project.projectNo}</div><div>负责人: ${project.leaderName}</div><div>完成率: ${project.completionRate}%</div>${project.isOverdue ? '<div style="color: #f56c6c;">已逾期</div>' : ''}</div></div>`
      }
    })
  } catch (error) {
    ElMessage.error('渲染甘特图失败')
  }
}

const handleSearch = () => fetchProjectList()
const handleReset = () => {
  queryParams.value = { projectTitle: '', leader: undefined }
  showCompletedProjects.value = false
  fetchProjectList()
}
const handleViewModeChange = () => {
  if (ganttInstance) ganttInstance.change_view_mode(viewMode.value)
}

onMounted(() => fetchProjectList())
</script>

<style>
/* 导入 frappe-gantt 样式 - 使用非scoped样式 */
@import '@/assets/styles/frappe-gantt.css';
</style>

<style scoped lang="scss">
.filter-card :deep(.el-card__body) { padding: 16px; }
.filter-content { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.filter-item { display: flex; align-items: center; gap: 8px; }
.filter-input { width: 240px; }
.filter-select { width: 200px; }
.view-mode-selector { display: flex; justify-content: center; }
.gantt-container { height: 100%; overflow: auto; padding: 20px; }
.gantt-chart { min-height: 400px; }
.empty-state { display: flex; align-items: center; justify-content: center; height: 400px; }
:deep(.gantt-task-overdue .bar) { fill: #f56c6c !important; }
:deep(.gantt .bar-progress) { fill: #409eff; }
:deep(.gantt .bar) { fill: #e4e7ed; }
:deep(.gantt-popup) { background: white; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15); padding: 12px; min-width: 250px; }
:deep(.popup-title) { font-size: 16px; font-weight: 600; margin-bottom: 8px; color: #303133; }
:deep(.popup-content div) { margin: 4px 0; font-size: 14px; color: #606266; }
</style>