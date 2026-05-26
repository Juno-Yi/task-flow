<!-- 项目协作 - 项目动态 -->
<template>
  <div class="project-record-page">
    <!-- 筛选区域 -->
    <ElCard shadow="never" class="filter-card">
      <div class="filter-wrapper">
        <div class="filter-item">
          <span class="filter-label">项目筛选</span>
          <ElSelect
            v-model="queryParams.projectNo"
            placeholder="请选择项目（不选则显示全部）"
            clearable
            filterable
            :loading="projectLoading"
            class="filter-select"
            @change="handleProjectChange"
          >
            <ElOption
              v-for="item in projectOptions"
              :key="item.no"
              :label="item.name"
              :value="item.no"
            >
              <div class="project-option">
                <span class="project-name">{{ item.name }}</span>
                <span class="project-no">{{ item.no }}</span>
              </div>
            </ElOption>
          </ElSelect>
        </div>
      </div>
    </ElCard>

    <!-- 时间线内容 -->
    <ElCard shadow="never" class="timeline-card">
      <div
        v-loading="loading"
        class="timeline-container"
        @scroll="handleScroll"
      >
        <div v-if="recordList.length === 0 && !loading" class="empty-state">
          <ElEmpty description="暂无项目动态" />
        </div>

        <div v-else class="timeline-wrapper">
          <div
            v-for="(group, date) in groupedRecords"
            :key="date"
            class="timeline-date-group"
          >
            <!-- 日期标签 -->
            <div class="date-label">
              <span class="date-text">{{ formatDateLabel(date) }}</span>
            </div>

            <!-- 该日期下的动态列表 -->
            <div class="timeline-items">
              <div
                v-for="record in group"
                :key="record.id"
                class="timeline-item"
              >
                <!-- 时间线节点 -->
                <div class="timeline-node">
                  <div class="node-dot"></div>
                  <div class="node-line"></div>
                </div>

                <!-- 动态内容 -->
                <div class="timeline-content">
                  <div class="content-header">
                    <span class="operator-name">{{ record.operatorNickName }}</span>
                    <span class="action-text">{{ record.content }}</span>
                  </div>

                  <div class="content-meta">
                    <ElTag size="small" type="info" effect="plain">
                      {{ record.projectTitle }}
                    </ElTag>
                    <ElTag size="small" type="success" effect="plain">
                      {{ record.typeLabel }}
                    </ElTag>
                    <ElTag v-if="record.targetTypeLabel" size="small" effect="plain">
                      {{ record.targetTypeLabel }}
                    </ElTag>
                    <span class="time-text">{{ formatTime(record.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载更多提示 -->
          <div v-if="hasMore" class="loading-more">
            <ElIcon v-if="loadingMore" class="is-loading">
              <Loading />
            </ElIcon>
            <span v-if="loadingMore">加载中...</span>
            <span v-else>滚动加载更多</span>
          </div>

          <!-- 没有更多数据提示 -->
          <div v-if="!hasMore && recordList.length > 0" class="no-more">
            <span>没有更多数据了</span>
          </div>
        </div>
      </div>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { fetchGetProjectRecordList } from '@/api/project/record'
import { fetchGetProjectOptions } from '@/api/project/list'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

defineOptions({ name: 'Record' })

// 查询参数
const queryParams = ref<Api.Project.ProjectRecordQueryDTO>({
  projectNo: undefined,
  current: 1,
  size: 20
})

// 数据状态
const loading = ref(false)
const loadingMore = ref(false)
const recordList = ref<Api.Project.ProjectRecordVO[]>([])
const total = ref(0)
const hasMore = computed(() => recordList.value.length < total.value)

// 项目下拉选项
const projectOptions = ref<Api.Project.ProjectOptionVO[]>([])
const projectLoading = ref(false)

// 按日期分组的动态记录
const groupedRecords = computed(() => {
  const groups: Record<string, Api.Project.ProjectRecordVO[]> = {}

  recordList.value.forEach(record => {
    const date = formatDate(new Date(record.createTime), 'YYYY-MM-DD')
    if (!groups[date]) {
      groups[date] = []
    }
    groups[date].push(record)
  })

  return groups
})

// 获取项目动态列表
const getRecordList = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
  }

  try {
    const res = await fetchGetProjectRecordList(queryParams.value)

    if (isLoadMore) {
      // 加载更多，追加数据
      recordList.value = [...recordList.value, ...res.list]
    } else {
      // 首次加载或刷新，替换数据
      recordList.value = res.list
    }

    total.value = res.total
  } catch (error) {
    ElMessage.error('获取项目动态失败')
  } finally {
    if (isLoadMore) {
      loadingMore.value = false
    } else {
      loading.value = false
    }
  }
}

// 获取项目下拉选项
const getProjectOptions = async () => {
  projectLoading.value = true
  try {
    const res = await fetchGetProjectOptions({} as Api.Project.ProjectOptionQueryDTO)
    projectOptions.value = res
  } catch (error) {
    ElMessage.error('获取项目列表失败')
  } finally {
    projectLoading.value = false
  }
}

// 项目切换
const handleProjectChange = () => {
  queryParams.value.current = 1
  recordList.value = []
  getRecordList()
}

// 滚动加载
const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement
  const scrollTop = target.scrollTop
  const scrollHeight = target.scrollHeight
  const clientHeight = target.clientHeight

  // 滚动到底部前 100px 时触发加载
  if (scrollHeight - scrollTop - clientHeight < 100 && hasMore.value && !loadingMore.value && !loading.value) {
    queryParams.value.current = (queryParams.value.current || 1) + 1
    getRecordList(true)
  }
}

// 日期格式化工具函数
const formatDate = (date: Date, format: string) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

// 格式化日期标签
const formatDateLabel = (dateStr: string) => {
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  const isSameDay = (d1: Date, d2: Date) => {
    return d1.getFullYear() === d2.getFullYear() &&
           d1.getMonth() === d2.getMonth() &&
           d1.getDate() === d2.getDate()
  }

  if (isSameDay(date, today)) {
    return '今天'
  } else if (isSameDay(date, yesterday)) {
    return '昨天'
  } else {
    return formatDate(date, 'YYYY年MM月DD日')
  }
}

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  return formatDate(date, 'HH:mm:ss')
}

onMounted(() => {
  getProjectOptions()
  getRecordList()
})
</script>

<style scoped lang="scss">
.project-record-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  padding: 16px;
}

.filter-card {
  flex-shrink: 0;
}

.filter-wrapper {
  display: flex;
  gap: 16px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
}

.filter-select {
  width: 320px;
}

.project-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.project-name {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.project-no {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.timeline-card {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.timeline-container {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.timeline-wrapper {
  flex: 1;
  padding: 24px 0;
}

.timeline-date-group {
  position: relative;
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;

    .timeline-item:last-child .node-line {
      display: none;
    }
  }
}

.date-label {
  position: sticky;
  top: 0;
  z-index: 10;
  margin-bottom: 16px;
  padding: 8px 16px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
  display: inline-block;
}

.date-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.timeline-items {
  padding-left: 24px;
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 16px;
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.timeline-node {
  position: relative;
  flex-shrink: 0;
  width: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.node-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--el-color-primary);
  border: 3px solid var(--el-color-primary-light-7);
  flex-shrink: 0;
  z-index: 2;
  transition: all 0.3s ease;
}

.timeline-item:hover .node-dot {
  transform: scale(1.3);
  box-shadow: 0 0 0 4px var(--el-color-primary-light-9);
}

.node-line {
  flex: 1;
  width: 2px;
  background: var(--el-border-color-light);
  margin-top: 4px;
}

.timeline-content {
  flex: 1;
  padding: 12px 16px;
  background: var(--el-fill-color-blank);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    border-color: var(--el-color-primary-light-5);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transform: translateX(4px);
  }
}

.content-header {
  margin-bottom: 8px;
  line-height: 1.6;
}

.operator-name {
  font-weight: 600;
  color: var(--el-color-primary);
  margin-right: 4px;
}

.action-text {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.time-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-left: auto;
}

.loading-more,
.no-more {
  padding: 24px 0;
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.loading-more .el-icon {
  font-size: 16px;
}

.no-more {
  color: var(--el-text-color-placeholder);
  font-size: 13px;
}

// 暗色主题适配
:global(html.dark) {
  .date-label {
    background: var(--el-fill-color-dark);
  }

  .timeline-content {
    background: var(--el-fill-color-light);
    border-color: var(--el-border-color);

    &:hover {
      border-color: var(--el-color-primary);
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
    }
  }

  .node-line {
    background: var(--el-border-color);
  }
}
</style>