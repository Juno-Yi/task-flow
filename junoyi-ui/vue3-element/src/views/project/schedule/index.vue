<!-- 项目协作 - 项目排期 -->
<template>
  <div class="art-full-height">
    <div class="flex flex-col h-full">
      <!-- 搜索栏和视图切换 -->
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
          <!-- 视图切换 -->
          <div class="filter-item ml-auto">
            <ElRadioGroup v-model="viewMode" size="default" @change="handleViewModeChange">
              <ElRadioButton value="Day">日</ElRadioButton>
              <ElRadioButton value="Week">周</ElRadioButton>
              <ElRadioButton value="Month">月</ElRadioButton>
              <ElRadioButton value="Year">年</ElRadioButton>
            </ElRadioGroup>
          </div>
        </div>
      </ElCard>

      <!-- 甘特图 -->
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
const viewMode = ref<'Day' | 'Week' | 'Month' | 'Year'>('Month')
const loading = ref(false)
const projectList = ref<Api.Project.ProjectGanttVO[]>([])
const leaderOptions = ref<Array<{ userId: number; nickName: string }>>([])
const ganttChartRef = ref<HTMLElement | null>(null)
let ganttInstance: any = null

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

  // 打印数据用于调试
  console.log('项目列表数据:', projectList.value)

  const tasks = projectList.value.map(project => ({
    id: `project-${project.projectId}`,
    name: project.projectTitle,
    start: (project.planStartTime ? new Date(project.planStartTime) : new Date()).toISOString().split('T')[0],
    end: (project.planEndTime ? new Date(project.planEndTime) : new Date()).toISOString().split('T')[0],
    progress: Number(project.completionRate) || 0,
    custom_class: project.overdue ? 'gantt-task-overdue' : ''  // 修改：isOverdue -> overdue
  }))

  try {
    ganttInstance = new Gantt(ganttChartRef.value, tasks, {
      view_mode: viewMode.value,
      language: 'zh',
      bar_height: 40,
      bar_corner_radius: 4,
      arrow_curve: 5,
      padding: 20,
      date_format: 'YYYY-MM-DD',
      custom_popup_html: task => {
        const project = projectList.value.find(p => `project-${p.projectId}` === task.id)
        if (!project) return ''

        console.log('弹窗项目数据:', project)

        // 格式化时间
        const formatDate = (dateStr: string | undefined): string => {
          if (!dateStr) return '-'
          if (typeof dateStr === 'string') {
            return dateStr.split(' ')[0]
          }
          return dateStr
        }

        // 格式化完成率
        const formatCompletionRate = (rate: number | undefined): string => {
          if (!rate) return '0'
          return Number(rate).toFixed(2)
        }

        // 构建详细信息
        const details = []
        details.push(`<div><strong>项目编号：</strong>${project.projectNo || '-'}</div>`)
        details.push(`<div><strong>负责人：</strong>${project.leaderName || '-'}</div>`)
        details.push(`<div><strong>项目状态：</strong>${project.statusLabel || '-'}</div>`)
        details.push(`<div><strong>项目类型：</strong>${project.typeLabel || '-'}</div>`)
        details.push(`<div><strong>优先级：</strong>${project.priorityLabel || '-'}</div>`)
        details.push(`<div><strong>完成率：</strong>${formatCompletionRate(project.completionRate)}%</div>`)
        details.push(`<div><strong>计划开始：</strong>${formatDate(project.planStartTime)}</div>`)
        details.push(`<div><strong>计划结束：</strong>${formatDate(project.planEndTime)}</div>`)

        if (project.overdue) {
          details.push('<div style="color: #f56c6c; font-weight: bold; margin-top: 8px;">⚠️ 已逾期</div>')
        }

        return `
          <div class="gantt-popup">
            <div class="popup-title">${project.projectTitle}</div>
            <div class="popup-content">
              ${details.join('')}
            </div>
          </div>
        `
      }
    })
  } catch (error) {
    console.error('渲染甘特图失败:', error)
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
.filter-card :deep(.el-card__body) {
  padding: 16px;
}

.filter-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-input {
  width: 240px;
}

.filter-select {
  width: 200px;
}

.ml-auto {
  margin-left: auto;
}

.gantt-container {
  height: 100%;
  overflow: auto;
  padding: 20px;

  // 确保甘特图容器占满剩余空间
  display: flex;
  flex-direction: column;
}

.gantt-chart {
  flex: 1;
  min-height: 500px;
  height: 100%;

  // 让甘特图内容自适应高度
  :deep(.gantt-container) {
    height: 100% !important;
  }
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

// 甘特图样式优化
:deep(.gantt-task-overdue .bar) {
  fill: #f56c6c !important;
}

:deep(.gantt .bar-progress) {
  fill: #409eff;
}

:deep(.gantt .bar) {
  fill: #e4e7ed;
}

:deep(.gantt-popup) {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  padding: 16px;
  min-width: 300px;
  max-width: 400px;
}

:deep(.popup-title) {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 8px;
}

:deep(.popup-content div) {
  margin: 6px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;

  strong {
    color: #303133;
    margin-right: 8px;
  }
}
</style>