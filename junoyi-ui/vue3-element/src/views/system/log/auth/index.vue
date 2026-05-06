<!-- 登录日志页面 -->
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
                v-permission="'system.ui.auth-log.button.delete'"
                :disabled="selectedRows.length === 0"
                @click="handleBatchDelete"
                v-ripple
            >
              批量删除
            </ElButton>
            <ElButton
                v-permission="'system.ui.auth-log.button.clear'"
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
  </div>
</template>

<script setup lang="ts">
import { useTable } from '@/hooks/core/useTable'
import { fetchGetAuthLogList, fetchDeleteAuthLog, fetchClearAuthLog } from '@/api/system/authLog'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { ElTag, ElMessageBox } from 'element-plus'
import AuthLogVO = Api.System.AuthLogVO;

defineOptions({ name: 'LoginLog' })

// type LoginLogVO = Api.System.LoginLogVO

// 搜索表单
const initialSearchState = {
  userName: undefined as string | undefined,
  loginIp: undefined as string | undefined,
  loginType: undefined as string | undefined,
  status: undefined as number | undefined
}

const formFilters = reactive({ ...initialSearchState })

// 登录方式选项
const loginTypeOptions = [
  { label: '账号密码登录', value: 'password' },
  { label: '企业微信登录', value: 'wechat_work' }
]

// 状态选项
const statusOptions = [
  { label: '成功', value: 1 },
  { label: '失败', value: 0 }
]

const formItems = computed(() => [
  {
    label: '用户名',
    key: 'userName',
    type: 'input',
    props: { clearable: true, placeholder: '请输入用户名' }
  },
  {
    label: '登录IP',
    key: 'loginIp',
    type: 'input',
    props: { clearable: true, placeholder: '请输入IP地址' }
  },
  {
    label: '登录方式',
    key: 'loginType',
    type: 'select',
    props: { clearable: true, options: loginTypeOptions }
  },
  {
    label: '状态',
    key: 'status',
    type: 'select',
    props: { clearable: true, options: statusOptions }
  }
])

// 选中行
const selectedRows = ref<AuthLogVO[]>([])

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
    apiFn: fetchGetAuthLogList,
    apiParams: { current: 1, size: 20 },
    columnsFactory: () => [
      { type: 'selection' as const, width: 50, align: 'center' },
      {
        prop: 'userName',
        label: '用户信息',
        minWidth: 140,
        headerAlign: 'center',
        formatter: (row: AuthLogVO) => {
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
        prop: 'loginIp',
        label: '登录IP',
        width: 160,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: AuthLogVO) => {
          return h('div', {}, [
            h('div', { class: 'font-mono text-sm' }, row.loginIp || '-'),
            row.ipRegion ? h('div', { class: 'text-xs text-gray-400' }, row.ipRegion) : null
          ])
        }
      },
      {
        prop: 'sessionId',
        label: '会话ID',
        minWidth: 180,
        align: 'center',
        headerAlign: 'center',
        showOverflowTooltip: true,
        formatter: (row: AuthLogVO) => row.sessionId || '-'
      },
      {
        prop: 'identity',
        label: '身份',
        width: 100,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: AuthLogVO) => row.identity || '-'
      },
      {
        prop: 'loginType',
        label: '登录方式',
        width: 120,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: AuthLogVO) => row.loginTypeName || row.loginType || '-'
      },
      {
        prop: 'browser',
        label: '浏览器',
        width: 120,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: AuthLogVO) => row.browser || '-'
      },
      {
        prop: 'os',
        label: '操作系统',
        width: 120,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: AuthLogVO) => row.os || '-'
      },
      {
        prop: 'status',
        label: '状态',
        width: 80,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: AuthLogVO) => {
          return h(ElTag, {
            type: row.status === 1 ? 'success' : 'danger',
            size: 'small'
          }, () => row.status === 1 ? '成功' : '失败')
        }
      },
      {
        prop: 'msg',
        label: '消息',
        minWidth: 120,
        align: 'center',
        headerAlign: 'center',
        showOverflowTooltip: true,
        formatter: (row: AuthLogVO) => row.msg || '-'
      },
      {
        prop: 'loginTime',
        label: '登录时间',
        width: 170,
        align: 'center',
        headerAlign: 'center',
        formatter: (row: AuthLogVO) => formatTime(row.loginTime)
      }
    ]
  }
})

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
const handleSelectionChange = (rows: AuthLogVO[]) => {
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
  await fetchDeleteAuthLog(ids)
  selectedRows.value = []
  getData()
}

/**
 * 清空日志
 */
const handleClear = async () => {
  await ElMessageBox.confirm(
      '确定要清空所有登录日志吗？此操作不可恢复！',
      '清空确认',
      { type: 'warning' }
  )

  await fetchClearAuthLog()
  getData()
}
</script>
