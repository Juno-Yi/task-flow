<!-- 通知中心 - 通知发送日志 -->
<template>
  <div class="art-full-height">
    <PublishLogSearch
        v-show="showSearchBar"
        v-model="searchForm"
        @search="handleSearch"
        @reset="handleReset"
    />

    <ElCard
        class="art-table-card"
        shadow="never"
        :style="{ 'margin-top': showSearchBar ? '12px' : '0' }"
    >
      <ArtTableHeader
          v-model:columns="columnChecks"
          v-model:showSearchBar="showSearchBar"
          :loading="loading"
          @refresh="refreshData"
      />

      <ArtTable
          :loading="loading"
          :data="data"
          :columns="columns"
          :pagination="pagination"
          @pagination:size-change="handleSizeChange"
          @pagination:current-change="handleCurrentChange"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
import { h } from 'vue'
import { ElTag } from 'element-plus'
import { useTable } from '@/hooks/core/useTable'
import { fetchGetNotificationPublishLogList } from '@/api/notification/notification-publish-log'
import PublishLogSearch from './modules/publish-log-search.vue'

defineOptions({ name: 'NotificationPublishLog' })

type NotificationPublishLogVO = Api.Notification.NotificationPublishLogVO

const searchForm = ref({
  notificationId: undefined,
  publishUserId: undefined,
  startTime: undefined,
  endTime: undefined,
  timeRange: undefined
})

const showSearchBar = ref(true)

const formatTime = (time?: string): string => {
  if (!time) return '-'
  const date = new Date(time)
  if (isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const {
  columns,
  columnChecks,
  data,
  loading,
  pagination,
  searchParams,
  getData,
  resetSearchParams,
  handleSizeChange,
  handleCurrentChange,
  refreshData
} = useTable({
  core: {
    apiFn: fetchGetNotificationPublishLogList,
    columnsFactory: () => [
      {
        prop: 'id',
        label: 'ID',
        align: 'center',
        headerAlign: 'center',
        width: 80
      },
      {
        prop: 'notificationId',
        label: '通知ID',
        align: 'center',
        headerAlign: 'center',
        width: 100
      },
      {
        prop: 'notificationTitle',
        label: '通知标题',
        align: 'left',
        headerAlign: 'center',
        minWidth: 200,
        showOverflowTooltip: true
      },
      {
        prop: 'notificationSummary',
        label: '通知摘要',
        align: 'left',
        headerAlign: 'center',
        minWidth: 220,
        showOverflowTooltip: true
      },
      {
        prop: 'publishUserNickName',
        label: '发布者',
        align: 'center',
        headerAlign: 'center',
        width: 120,
        formatter: (row: NotificationPublishLogVO) => {
          const name = row.publishUserNickName || '系统'
          const isSystem = name === '系统'
          return h(ElTag, { type: isSystem ? 'info' : 'success', size: 'small' }, () => name)
        }
      },
      {
        prop: 'publishTime',
        label: '发布时间',
        align: 'center',
        headerAlign: 'center',
        width: 180,
        formatter: (row: NotificationPublishLogVO) => formatTime(row.publishTime)
      },
    ]
  }
})

const handleSearch = (params?: Record<string, any>) => {
  const currentParams = params || searchForm.value
  const { timeRange, ...rest } = currentParams

  Object.assign(searchParams, {
    ...rest,
    startTime: Array.isArray(timeRange) ? timeRange[0] : undefined,
    endTime: Array.isArray(timeRange) ? timeRange[1] : undefined
  })

  getData()
}

const handleReset = () => {
  Object.assign(searchForm.value, {
    notificationId: undefined,
    publishUserId: undefined,
    startTime: undefined,
    endTime: undefined,
    timeRange: undefined
  })

  resetSearchParams()
  getData()
}
</script>

<style scoped>

</style>