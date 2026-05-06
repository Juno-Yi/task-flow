<!-- 会话监控页面 -->
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
              v-permission="'system.ui.session.button.logout'"
              :disabled="selectedRows.length === 0"
              @click="handleBatchLogout"
              v-ripple
            >
              批量下线
            </ElButton>
            <ElDivider direction="vertical" />
            <div class="flex items-center gap-3 text-sm">
              <span class="inline-flex items-center gap-1.5">
                <span class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></span>
                <span class="text-gray-600 dark:text-gray-300">在线 <b class="text-green-600">{{ pagination.total }}</b></span>
              </span>
            </div>
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
  import { usePermission } from '@/hooks/core/usePermission'
  import { fetchGetSessionList, fetchForceLogout, fetchBatchKickOut } from '@/api/system/session'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { ElTag, ElMessageBox } from 'element-plus'

  defineOptions({ name: 'Session' })

  const { hasPermission } = usePermission()

  type SessionVO = Api.System.SessionVO

  // 搜索表单
  const initialSearchState = {
    userName: undefined as string | undefined,
    nickName: undefined as string | undefined,
    loginIp: undefined as string | undefined,
    platformType: undefined as string | undefined
  }

  const formFilters = reactive({ ...initialSearchState })

  // 平台选项 (对应后端 PlatformType 枚举名称)
  const platformOptions = [
    { label: '后台网站', value: 'ADMIN_WEB' },
    { label: '前台网站', value: 'FRONT_DESK_WEB' },
    { label: '小程序', value: 'MINI_PROGRAM' },
    { label: 'APP', value: 'APP' },
    { label: '桌面应用', value: 'DESKTOP_APP' }
  ]

  const formItems = computed(() => [
    {
      label: '用户名',
      key: 'userName',
      type: 'input',
      props: { clearable: true, placeholder: '请输入用户名' }
    },
    {
      label: '昵称',
      key: 'nickName',
      type: 'input',
      props: { clearable: true, placeholder: '请输入昵称' }
    },
    {
      label: '登录IP',
      key: 'loginIp',
      type: 'input',
      props: { clearable: true, placeholder: '请输入IP地址' }
    },
    {
      label: '平台类型',
      key: 'platformType',
      type: 'select',
      props: { clearable: true, options: platformOptions }
    }
  ])

  // 选中行
  const selectedRows = ref<SessionVO[]>([])

  // 平台配置 (对应后端 PlatformType 枚举名称)
  const platformConfig: Record<string, { icon: string; color: string; label: string }> = {
    ADMIN_WEB: { icon: 'ri:dashboard-line', color: '#409eff', label: '后台网站' },
    FRONT_DESK_WEB: { icon: 'ri:global-line', color: '#67c23a', label: '前台网站' },
    MINI_PROGRAM: { icon: 'ri:wechat-line', color: '#07c160', label: '小程序' },
    APP: { icon: 'ri:smartphone-line', color: '#e6a23c', label: 'APP' },
    DESKTOP_APP: { icon: 'ri:computer-line', color: '#909399', label: '桌面应用' }
  }

  // 设备类型配置
  const deviceConfig: Record<string, { icon: string; label: string }> = {
    Mobile: { icon: 'ri:smartphone-line', label: '手机' },
    Tablet: { icon: 'ri:tablet-line', label: '平板' },
    Desktop: { icon: 'ri:computer-line', label: '电脑' },
    Unknown: { icon: 'ri:device-line', label: '未知' }
  }

  /**
   * 获取平台配置
   */
  const getPlatformConfig = (platformType: string) => {
    return platformConfig[platformType] || { icon: 'ri:question-line', color: '#909399', label: '未知' }
  }

  /**
   * 获取设备配置
   */
  const getDeviceConfig = (deviceType: string | null) => {
    return deviceConfig[deviceType || 'Unknown'] || deviceConfig.Unknown
  }

  /**
   * 格式化剩余时间
   */
  const formatRemainingTime = (expireTime: number): { text: string; type: 'success' | 'warning' | 'danger' } => {
    const now = Date.now()
    const diff = expireTime - now
    if (diff <= 0) return { text: '已过期', type: 'danger' }
    
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    
    let text = ''
    if (days > 0) {
      text = `${days}天${hours}小时${minutes}分钟`
    } else if (hours > 0) {
      text = `${hours}小时${minutes}分钟`
    } else {
      text = `${minutes}分钟`
    }
    
    // 根据剩余时间返回不同状态
    let type: 'success' | 'warning' | 'danger' = 'success'
    if (days === 0 && hours === 0 && minutes < 10) {
      type = 'danger'
    } else if (days === 0 && hours < 1) {
      type = 'warning'
    }
    
    return { text, type }
  }

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
   * 格式化时间戳
   */
  const formatTimestamp = (timestamp: number | undefined): string => {
    if (!timestamp) return '-'
    return formatTime(new Date(timestamp).toISOString())
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
      apiFn: fetchGetSessionList,
      apiParams: { current: 1, size: 20 },
      columnsFactory: () => [
        { type: 'selection', width: 50, align: 'center' },
        {
          prop: 'userName',
          label: '用户信息',
          minWidth: 160,
          headerAlign: 'center',
          formatter: (row: SessionVO) => {
            return h('div', { class: 'flex items-center gap-2' }, [
              h('div', { class: 'w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center' }, [
                h(ArtSvgIcon, { icon: 'ri:user-line', class: 'text-primary' })
              ]),
              h('div', {}, [
                h('div', { class: 'font-medium' }, row.userName),
                h('div', { class: 'text-xs text-gray-400' }, row.nickName || '-')
              ])
            ])
          }
        },
        {
          prop: 'platformType',
          label: '平台类型',
          width: 120,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SessionVO) => {
            const config = getPlatformConfig(row.platformType)
            return h('div', { class: 'flex items-center justify-center gap-1' }, [
              h(ArtSvgIcon, { icon: config.icon, style: { color: config.color } }),
              h('span', {}, config.label)
            ])
          }
        },
        {
          prop: 'loginIp',
          label: '登录IP',
          width: 160,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SessionVO) => {
            return h('div', {}, [
              h('div', { class: 'font-mono text-sm' }, row.loginIp || '-'),
              row.ipRegion ? h('div', { class: 'text-xs text-gray-400' }, row.ipRegion) : null
            ])
          }
        },
        {
          prop: 'deviceType',
          label: '设备信息',
          width: 140,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SessionVO) => {
            const config = getDeviceConfig(row.deviceType)
            return h('div', { class: 'flex flex-col items-center' }, [
              h('div', { class: 'flex items-center gap-1' }, [
                h(ArtSvgIcon, { icon: config.icon, class: 'text-gray-500' }),
                h('span', {}, config.label)
              ]),
              h('div', { class: 'text-xs text-gray-400' }, row.os || '-')
            ])
          }
        },
        {
          prop: 'browser',
          label: '浏览器',
          width: 120,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SessionVO) => row.browser || '-'
        },
        {
          prop: 'loginTime',
          label: '登录时间',
          width: 170,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SessionVO) => formatTime(row.loginTime)
        },
        {
          prop: 'lastAccessTime',
          label: '最后访问',
          width: 170,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SessionVO) => formatTime(row.lastAccessTime)
        },
        {
          prop: 'accessExpireTime',
          label: '剩余时间',
          width: 160,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SessionVO) => {
            const { text, type } = formatRemainingTime(row.accessExpireTime)
            return h(ElTag, { type, size: 'small' }, () => text)
          }
        },
        {
          prop: 'operation',
          label: '操作',
          width: 100,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: SessionVO) => {
            if (!hasPermission('system.ui.session.button.logout')) return '-'
            return h(ElButton, {
              type: 'danger',
              size: 'small',
              link: true,
              onClick: () => handleForceLogout(row)
            }, () => [
              h(ArtSvgIcon, { icon: 'ri:logout-box-r-line', class: 'mr-1' }),
              '强制下线'
            ])
          }
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
  const handleSelectionChange = (selection: SessionVO[]) => {
    selectedRows.value = selection
  }

  /**
   * 强制下线
   */
  const handleForceLogout = async (row: SessionVO) => {
    try {
      await ElMessageBox.confirm(
        `确定要强制下线用户「${row.userName}」吗？`,
        '强制下线',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
      )
      await fetchForceLogout(row.sessionId)
      refreshData()
    } catch {
      // 用户取消
    }
  }

  /**
   * 批量踢出会话
   */
  const handleBatchLogout = async () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请选择要踢出的会话')
      return
    }
    try {
      await ElMessageBox.confirm(
        `确定要踢出选中的 ${selectedRows.value.length} 个会话吗？`,
        '批量踢出',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
      )
      const ids = selectedRows.value.map(item => item.sessionId)
      await fetchBatchKickOut(ids)
      selectedRows.value = []
      refreshData()
    } catch {
      // 用户取消
    }
  }
</script>

<style scoped>
  :deep(.el-progress-bar__outer) {
    border-radius: 3px;
  }
  :deep(.el-progress-bar__inner) {
    border-radius: 3px;
  }
</style>
