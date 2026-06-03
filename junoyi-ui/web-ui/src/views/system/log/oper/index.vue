<!-- 操作日志页面 -->
<template>
  <div class="art-full-height">
    <!-- 搜索栏 -->
    <ArtSearchBar
        v-model="formFilters"
        :items="formItems"
        :showExpand="false"
        @reset="handleReset"
        @search="handleSearch"
    />

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader
          :loading="loading"
          v-model:columns="columnChecks"
          @refresh="refreshData"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton
                v-permission="'system.ui.oper-log.button.delete'"
                :disabled="selectedRows.length === 0"
                @click="handleBatchDelete"
                v-ripple
            >
              批量删除
            </ElButton>
            <ElButton
                v-permission="'system.ui.oper-log.button.clear'"
                @click="handleClear"
                v-ripple
            >
              清空日志
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <!-- 表格 -->
      <ArtTable
          :loading="loading"
          :data="data"
          :columns="columns"
          :pagination="pagination"
          @selection-change="handleSelectionChange"
          @pagination:size-change="handleSizeChange"
          @pagination:current-change="handleCurrentChange"
      />
    </ElCard>

    <!-- 日志详情弹窗 -->
    <ElDialog
        v-model="rawDialogVisible"
        title="日志详情"
        width="700px"
        destroy-on-close
    >
      <div v-if="currentLogDetail" class="log-detail-container">
        <ElDescriptions :column="2" border>
          <ElDescriptionsItem label="级别">
            <ElTag :type="getLevelTagType(currentLogDetail.level)" size="small">
              {{ getLevelText(currentLogDetail.level) }}
            </ElTag>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="动作">
            {{ currentLogDetail.actionName || currentLogDetail.action || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="模块">
            {{ currentLogDetail.moduleName || currentLogDetail.module || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="用户">
            {{ currentLogDetail.userName || '-' }} ({{ currentLogDetail.nickName || '-' }})
          </ElDescriptionsItem>
          <ElDescriptionsItem label="详情" :span="2">
            {{ currentLogDetail.message || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="对象ID">
            {{ currentLogDetail.targetId || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="对象名称">
            {{ currentLogDetail.targetName || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="请求路径">
            {{ currentLogDetail.path || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="请求方法">
            {{ currentLogDetail.method || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="IP地址">
            {{ currentLogDetail.ip || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作时间">
            {{ formatTime(currentLogDetail.createTime) }}
          </ElDescriptionsItem>
        </ElDescriptions>

        <div v-if="currentLogDetail.rawData" class="raw-data-section">
          <div class="raw-data-title">原始数据</div>
          <div class="raw-data-container">
            <pre class="raw-data-content">{{ formattedRawData }}</pre>
          </div>
        </div>
      </div>
      <template #footer>
        <ElButton @click="rawDialogVisible = false">关闭</ElButton>
        <ElButton type="primary" @click="copyRawData">复制</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { useTable } from '@/hooks/core/useTable'
import { fetchGetOperLogList, fetchDeleteOperLog, fetchClearOperLog } from '@/api/system/operLog'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { ElTag, ElMessageBox, ElMessage, ElDescriptions, ElDescriptionsItem } from 'element-plus'

defineOptions({ name: 'OperationLog' })

type OperationLogVO = Api.System.SysOperLogVO

// 搜索表单
const initialSearchState = {
  level: undefined as string | undefined,
  action: undefined as string | undefined,
  module: undefined as string | undefined,
  userName: undefined as string | undefined,
  targetId: undefined as string | undefined,
  message: undefined as string | undefined,
  startTime: undefined as string | undefined,
  endTime: undefined as string | undefined
}

const formFilters = reactive({ ...initialSearchState })

// 日志级别选项
const levelOptions = [
  { label: '信息', value: 'info' },
  { label: '警告', value: 'warn' },
  { label: '错误', value: 'error' }
]

// 动作选项
const actionOptions = [
  { label: '查看', value: 'view' },
  { label: '创建', value: 'create' },
  { label: '更新', value: 'update' },
  { label: '删除', value: 'delete' },
  { label: '下载', value: 'download' },
  { label: '连接', value: 'connect' },
  { label: '导出', value: 'export' },
  { label: '导入', value: 'import' },
  { label: '执行', value: 'execute' }
]

// 模块选项
const moduleOptions = [
  { label: '漏洞', value: 'vulnerability' },
  { label: 'Webshell', value: 'webshell' },
  { label: '项目', value: 'project' },
  { label: '用户', value: 'user' },
  { label: '角色', value: 'role' },
  { label: '部门', value: 'dept' },
  { label: '菜单', value: 'menu' },
  { label: '权限', value: 'permission' },
  { label: '文件', value: 'file' },
  { label: '系统', value: 'system' }
]

const formItems = computed(() => [
  {
    label: '时间',
    key: 'timeRange',
    type: 'date-range',
    props: {
      clearable: true,
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      type: 'datetimerange'
    }
  },
  {
    label: '用户',
    key: 'userName',
    type: 'input',
    props: { clearable: true, placeholder: '请输入用户名' }
  },
  {
    label: '日志级别',
    key: 'level',
    type: 'select',
    props: { clearable: true, options: levelOptions }
  },
  {
    label: '动作',
    key: 'action',
    type: 'select',
    props: { clearable: true, options: actionOptions }
  },
  {
    label: '模块',
    key: 'module',
    type: 'select',
    props: { clearable: true, options: moduleOptions }
  },
  {
    label: '详情',
    key: 'message',
    type: 'input',
    props: { clearable: true, placeholder: '请输入关键字' }
  }
])

// 选中行
const selectedRows = ref<OperationLogVO[]>([])

// 详情弹窗
const rawDialogVisible = ref(false)
const currentLogDetail = ref<OperationLogVO | null>(null)

const formattedRawData = computed(() => {
  if (!currentLogDetail.value?.rawData) return ''
  try {
    return JSON.stringify(JSON.parse(currentLogDetail.value.rawData), null, 2)
  } catch {
    return currentLogDetail.value.rawData
  }
})

/**
 * 格式化时间
 */
const formatTime = (time: string | undefined): string => {
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

/**
 * 获取级别标签类型
 */
const getLevelTagType = (level: string) => {
  switch (level) {
    case 'info': return 'success'
    case 'warn': return 'warning'
    case 'error': return 'danger'
    default: return 'info'
  }
}

/**
 * 获取级别显示文本
 */
const getLevelText = (level: string) => {
  switch (level) {
    case 'info': return '信息'
    case 'warn': return '警告'
    case 'error': return '错误'
    default: return level
  }
}

const {
  columns,
  columnChecks,
  data,
  loading,
  pagination,
  getData,
  searchParams,
  resetSearchParams,
  handleSizeChange,
  handleCurrentChange,
  refreshData
} = useTable({
  core: {
    apiFn: fetchGetOperLogList,
    apiParams: { current: 1, size: 20 },
    columnsFactory: () => [
      { type: 'selection' as const, width: 50, align: 'center' },
      {
        prop: 'level',
        label: '级别',
        width: 80,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: OperationLogVO) => {
          return h(ElTag, {
            type: getLevelTagType(row.level) as any,
            size: 'small'
          }, () => getLevelText(row.level))
        }
      },
      {
        prop: 'action',
        label: '动作',
        width: 100,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: OperationLogVO) => row.actionName || row.action || '-'
      },
      {
        prop: 'module',
        label: '模块',
        width: 100,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: OperationLogVO) => row.moduleName || row.module || '-'
      },
      {
        prop: 'userName',
        label: '用户',
        minWidth: 140,
        headerAlign: 'center',
        formatter: (row: OperationLogVO) => {
          return h('div', { class: 'flex items-center gap-2' }, [
            h('div', { class: 'w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center' }, [
              h(ArtSvgIcon, { icon: 'ri:user-line', class: 'text-primary' })
            ]),
            h('div', {}, [
              h('div', { class: 'font-medium' }, row.userName || '-'),
              h('div', { class: 'text-xs text-gray-400' }, row.nickName || '-')
            ])
          ])
        }
      },
      {
        prop: 'message',
        label: '详情',
        minWidth: 250,
        headerAlign: 'center',
        showOverflowTooltip: true,
        formatter: (row: OperationLogVO) => row.message || '-'
      },
      {
        prop: 'targetId',
        label: '对象ID',
        width: 120,
        align: 'center',
        headerAlign: 'center',
        showOverflowTooltip: true,
        formatter: (row: OperationLogVO) => row.targetId || '-'
      },
      {
        prop: 'createTime',
        label: '时间',
        width: 170,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: OperationLogVO) => formatTime(row.createTime)
      },
      {
        prop: 'operation',
        label: '日志操作',
        width: 100,
        align: 'center',
        headerAlign: 'center',
        fixed: 'right',
        formatter: (row: OperationLogVO) => {
          return h(ElButton, {
            link: true,
            type: 'primary',
            onClick: () => showRawData(row)
          }, () => '查看详情')
        }
      }
    ]
  }
})

/**
 * 显示日志详情
 */
const showRawData = (row: OperationLogVO) => {
  currentLogDetail.value = row
  rawDialogVisible.value = true
}

/**
 * 复制日志详情
 */
const copyRawData = async () => {
  if (!currentLogDetail.value) return
  const detail = currentLogDetail.value
  const text = `级别: ${getLevelText(detail.level)}
动作: ${detail.actionName || detail.action || '-'}
模块: ${detail.moduleName || detail.module || '-'}
用户: ${detail.userName || '-'} (${detail.nickName || '-'})
详情: ${detail.message || '-'}
对象ID: ${detail.targetId || '-'}
对象名称: ${detail.targetName || '-'}
请求路径: ${detail.path || '-'}
请求方法: ${detail.method || '-'}
IP地址: ${detail.ip || '-'}
操作时间: ${formatTime(detail.createTime)}
${detail.rawData ? '\n原始数据:\n' + formattedRawData.value : ''}`
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('复制成功')
  } catch {
    ElMessage.error('复制失败')
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  Object.assign(searchParams, formFilters)
  getData()
}

/**
 * 重置
 */
const handleReset = () => {
  Object.assign(formFilters, initialSearchState)
  resetSearchParams()
  getData()
}

/**
 * 选择变化
 */
const handleSelectionChange = (rows: OperationLogVO[]) => {
  selectedRows.value = rows
}

/**
 * 批量删除
 */
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return

  await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条日志吗？`,
      '删除确认',
      { type: 'warning' }
  )

  const ids = selectedRows.value.map(row => row.id)
  await fetchDeleteOperLog(ids)
  selectedRows.value = []
  getData()
}

/**
 * 清空日志
 */
const handleClear = async () => {
  await ElMessageBox.confirm(
      '确定要清空所有操作日志吗？此操作不可恢复！',
      '清空确认',
      { type: 'warning' }
  )

  await fetchClearOperLog()
  getData()
}
</script>

<style scoped>
.log-detail-container {
  padding: 0;
}

.raw-data-section {
  margin-top: 16px;
}

.raw-data-title {
  font-weight: 500;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}

.raw-data-container {
  max-height: 300px;
  overflow: auto;
  background-color: #1e1e1e;
  border-radius: 8px;
  padding: 16px;
}

.raw-data-content {
  margin: 0;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #9cdcfe;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
