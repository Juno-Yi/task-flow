<!-- 项目协作 - 项目排期 (dhtmlx-gantt) -->
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
            <ElButton type="primary" :icon="Search" @click="handleSearch">查询</ElButton>
            <ElButton :icon="Refresh" @click="handleReset">重置</ElButton>
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
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { fetchGetProjectScheduleGantList } from '@/api/project/schedule'
import { gantt } from 'dhtmlx-gantt'
import 'dhtmlx-gantt/codebase/dhtmlxgantt.css'

const queryParams = ref({ projectTitle: '', leader: undefined })
const showCompletedProjects = ref(false)
const loading = ref(false)
const projectList = ref<Api.Project.ProjectGanttVO[]>([])
const leaderOptions = ref<Array<{ userId: number; nickName: string }>>([])
const ganttChartRef = ref<HTMLElement | null>(null)
let isGanttInitialized = false

// 初始化甘特图配置
const initGanttConfig = () => {
  gantt.i18n.setLocale('cn')

  // 配置列 - 调整宽度更紧凑
  gantt.config.columns = [
    { name: 'text', label: '项目名称', tree: true, width: 150 },
    { name: 'leader', label: '负责人', align: 'center', width: 80 },
    { name: 'status', label: '状态', align: 'center', width: 70 },
    { name: 'priority', label: '优先级', align: 'center', width: 70 },
    { name: 'progress', label: '完成率', align: 'center', width: 70, template: (task: any) => Math.round(task.progress * 100) + '%' }
  ]

  gantt.config.date_format = '%Y-%m-%d %H:%i:%s'
  gantt.config.scale_height = 60
  gantt.config.row_height = 40
  gantt.config.bar_height = 24
  gantt.config.readonly = true
  gantt.config.show_progress = true
  gantt.config.autosize = false  // 关闭自动调整大小，启用滚动
  gantt.config.fit_tasks = true  // 自动适应任务时间范围
  gantt.config.show_tasks_outside_timescale = true  // 显示时间范围外的任务

  // 固定使用月视图
  gantt.config.scale_unit = 'month'
  gantt.config.subscales = [
    { unit: 'day', step: 1, date: '%d' }
  ]

  // 配置工具提示
  gantt.templates.tooltip_text = (_start: Date, _end: Date, task: any) => {
    const project = projectList.value.find(p => p.projectId === task.id)
    if (!project) return ''

    const lines = []
    lines.push('<div style="padding: 8px;">')
    lines.push('<div style="font-weight: bold; margin-bottom: 8px; font-size: 14px;">' + project.projectTitle + '</div>')
    lines.push('<div style="margin: 4px 0;"><strong>项目编号：</strong>' + (project.projectNo || '-') + '</div>')
    lines.push('<div style="margin: 4px 0;"><strong>负责人：</strong>' + (project.leaderName || '-') + '</div>')
    lines.push('<div style="margin: 4px 0;"><strong>项目状态：</strong>' + (project.statusLabel || '-') + '</div>')
    lines.push('<div style="margin: 4px 0;"><strong>项目类型：</strong>' + (project.typeLabel || '-') + '</div>')
    lines.push('<div style="margin: 4px 0;"><strong>优先级：</strong>' + (project.priorityLabel || '-') + '</div>')
    lines.push('<div style="margin: 4px 0;"><strong>完成率：</strong>' + Number(project.completionRate || 0).toFixed(2) + '%</div>')
    lines.push('<div style="margin: 4px 0;"><strong>计划开始：</strong>' + (project.planStartTime?.split(' ')[0] || '-') + '</div>')
    lines.push('<div style="margin: 4px 0;"><strong>计划结束：</strong>' + (project.planEndTime?.split(' ')[0] || '-') + '</div>')
    if (project.isOverdue) {
      lines.push('<div style="color: #f56c6c; font-weight: bold; margin-top: 8px;">⚠️ 已逾期</div>')
    }
    lines.push('</div>')
    return lines.join('')
  }

  // 配置任务颜色
  gantt.templates.task_class = (_start: Date, _end: Date, task: any) => {
    const project = projectList.value.find(p => p.projectId === task.id)
    return project?.isOverdue ? 'gantt-task-overdue' : ''
  }
}

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

    // 直接使用所有数据，不筛选
    projectList.value = allProjects

    console.log('项目列表数据:', projectList.value)
    console.log('项目数量:', projectList.value.length)

    await nextTick()
    renderGanttChart()
  } catch (error) {
    console.error('获取项目列表失败:', error)
    ElMessage.error('获取项目列表失败')
  } finally {
    loading.value = false
  }
}

const renderGanttChart = () => {
  if (!ganttChartRef.value || projectList.value.length === 0) {
    console.log('无法渲染甘特图:', { hasRef: !!ganttChartRef.value, projectCount: projectList.value.length })
    return
  }

  console.log('开始渲染甘特图，项目数量:', projectList.value.length)

  gantt.clearAll()

  // 计算所有项目的时间范围
  let minDate: Date | null = null
  let maxDate: Date | null = null

  projectList.value.forEach(project => {
    console.log('项目:', project.projectTitle, '开始:', project.planStartTime, '结束:', project.planEndTime)

    if (project.planStartTime) {
      const startDate = new Date(project.planStartTime)
      if (!minDate || startDate < minDate) {
        minDate = startDate
      }
    }
    if (project.planEndTime) {
      const endDate = new Date(project.planEndTime)
      if (!maxDate || endDate > maxDate) {
        maxDate = endDate
      }
    }
  })

  console.log('计算的时间范围:', { minDate, maxDate })

  // 构建任务数据
  const tasks = {
    data: projectList.value.map(project => {
      const startDate = project.planStartTime ? project.planStartTime.split(' ')[0] : new Date().toISOString().split('T')[0]
      const endDate = project.planEndTime ? project.planEndTime.split(' ')[0] : new Date().toISOString().split('T')[0]

      // 计算持续天数
      const start = new Date(startDate)
      const end = new Date(endDate)
      const duration = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24))

      return {
        id: project.projectId,
        text: project.projectTitle,
        leader: project.leaderName || '-',
        status: project.statusLabel || '-',
        priority: project.priorityLabel || '-',
        start_date: startDate,
        end_date: endDate,
        progress: Number(project.completionRate || 0) / 100,
        duration: duration > 0 ? duration : 1  // 至少1天
      }
    })
  }

  console.log('任务数据:', tasks)

  if (!isGanttInitialized) {
    initGanttConfig()
    gantt.init(ganttChartRef.value)
    isGanttInitialized = true
  }

  // 清空并重新加载数据
  gantt.clearAll()
  gantt.parse(tasks)

  console.log('gantt 内部任务数量:', gantt.getTaskCount())
  console.log('gantt 所有任务:', gantt.getTaskByTime())

  // 强制渲染
  gantt.render()

  console.log('甘特图渲染完成')
  console.log('最终 gantt 配置:', {
    start_date: gantt.config.start_date,
    end_date: gantt.config.end_date,
    scale_unit: gantt.config.scale_unit,
    fit_tasks: gantt.config.fit_tasks
  })
}

const handleSearch = () => fetchProjectList()

const handleReset = () => {
  queryParams.value = { projectTitle: '', leader: undefined }
  showCompletedProjects.value = false
  fetchProjectList()
}

onMounted(() => {
  fetchProjectList()
})

onBeforeUnmount(() => {
  if (isGanttInitialized) {
    gantt.clearAll()
  }
})
</script>

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
  overflow: auto;  // 允许滚动
  padding: 0;
}

.gantt-chart {
  height: 100%;
  width: 100%;
  min-height: 500px;  // 设置最小高度
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}
</style>

<style>
/* 甘特图全局样式 */
.gantt-task-overdue .gantt_task_progress {
  background-color: #f56c6c !important;
}

.gantt-task-overdue .gantt_task_line {
  background-color: #f56c6c !important;
  border-color: #f56c6c !important;
}

/* 确保甘特图容器可以横向滚动 */
.gantt_container {
  overflow-x: auto !important;
  overflow-y: auto !important;
}

.gantt_task {
  overflow-x: auto !important;
}
</style>

