<!-- 缓存监控页面 -->
<template>
  <div class="art-full-height">
    <!-- Redis 信息卡片 -->
    <RedisInfoCard :info="redisInfo" />

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
        @refresh="handleRefreshAll"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton
              v-permission="'system.ui.cache.button.delete'"
              :disabled="selectedRows.length === 0"
              @click="handleBatchDelete"
              v-ripple
            >
              <ArtSvgIcon icon="ri:delete-bin-line" class="mr-1" />
              批量删除
            </ElButton>
            <ElButton
              v-permission="'system.ui.cache.button.clear'"
              plain
              @click="handleClearAll"
              v-ripple
            >
              <ArtSvgIcon icon="ri:delete-bin-7-line" class="mr-1" />
              清空缓存
            </ElButton>
            <ElDivider direction="vertical" />
            <div class="flex-c gap-3 text-sm">
              <span class="text-g-600">
                共 <b :style="{ color: primaryColor }">{{ pagination.total }}</b> 个键
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

    <!-- 键值详情抽屉 -->
    <CacheDetailDrawer
      v-model="detailDrawerVisible"
      :detail="currentDetail"
      :loading="detailLoading"
    />
  </div>
</template>

<script setup lang="ts">
  import { useTable } from '@/hooks/core/useTable'
  import { usePermission } from '@/hooks/core/usePermission'
  import { useSettingStore } from '@/store/modules/setting'
  import {
    fetchGetRedisInfo,
    fetchGetCacheKeys,
    fetchGetCacheKeyDetail,
    fetchDeleteCacheKey,
    fetchBatchDeleteCacheKeys,
    fetchClearAllCache
  } from '@/api/system/cache'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import RedisInfoCard from './modules/redis-info-card.vue'
  import CacheDetailDrawer from './modules/cache-detail-drawer.vue'
  import { ElTag, ElMessageBox, ElButton, ElCard } from 'element-plus'

  defineOptions({ name: 'Cache' })

  const { hasPermission } = usePermission()
  const settingStore = useSettingStore()

  type CacheKeyVO = Api.System.CacheKeyVO
  type CacheKeyDetailVO = Api.System.CacheKeyDetailVO
  type RedisInfo = Api.System.RedisInfo

  // 主题色
  const primaryColor = computed(() => settingStore.systemThemeColor || '#409eff')

  // Redis 信息
  const redisInfo = ref<RedisInfo | null>(null)

  // 搜索表单
  const initialSearchState = {
    pattern: undefined as string | undefined,
    type: undefined as string | undefined
  }

  const formFilters = reactive({ ...initialSearchState })

  // 类型选项 (对应后端 RType 枚举)
  const typeOptions = [
    { label: 'OBJECT', value: 'OBJECT' },
    { label: 'MAP', value: 'MAP' },
    { label: 'LIST', value: 'LIST' },
    { label: 'SET', value: 'SET' },
    { label: 'ZSET', value: 'ZSET' }
  ]

  const formItems = computed(() => [
    {
      label: '键名',
      key: 'pattern',
      type: 'input',
      props: { clearable: true, placeholder: '支持通配符，如 user:*' }
    },
    {
      label: '类型',
      key: 'type',
      type: 'select',
      props: { clearable: true, options: typeOptions }
    }
  ])

  // 选中行
  const selectedRows = ref<CacheKeyVO[]>([])

  // 详情抽屉
  const detailDrawerVisible = ref(false)
  const detailLoading = ref(false)
  const currentDetail = ref<CacheKeyDetailVO | null>(null)

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
  } = useTable<typeof fetchGetCacheKeys>({
    core: {
      apiFn: fetchGetCacheKeys,
      apiParams: { current: 1, size: 20 },
      columnsFactory: () => [
        { type: 'selection', width: 50, align: 'center' },
        {
          prop: 'key',
          label: '键名',
          minWidth: 300,
          headerAlign: 'center',
          showOverflowTooltip: true,
          formatter: (row: CacheKeyVO) => {
            return h('span', {
              class: 'font-mono text-sm cursor-pointer hover:underline',
              style: { color: primaryColor.value },
              onClick: () => showDetail(row)
            }, row.key)
          }
        },
        {
          prop: 'type',
          label: '类型',
          width: 100,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: CacheKeyVO) => {
            return h(ElTag, { type: getTypeTagType(row.type), size: 'small' }, () => row.type)
          }
        },
        {
          prop: 'ttl',
          label: 'TTL',
          width: 140,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: CacheKeyVO) => {
            const ttlText = formatTTL(row.ttl)
            const color = row.ttl === -1 ? 'var(--art-gray-500)' : row.ttl < 60 ? '#f56c6c' : row.ttl < 3600 ? '#e6a23c' : '#67c23a'
            return h('span', { style: { color } }, ttlText)
          }
        },
        {
          prop: 'memoryUsage',
          label: '内存占用',
          width: 120,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: CacheKeyVO) => row.memoryUsage ? formatBytes(row.memoryUsage) : '-'
        },
        {
          prop: 'operation',
          label: '操作',
          width: 120,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: CacheKeyVO) => {
            const buttons = []
            if (hasPermission('system.ui.cache.button.detail'))
            buttons.push(
              h(ArtButtonTable, {
                type: 'view',
                onClick: () => showDetail(row)
              })
            )
            if (hasPermission('system.ui.cache.button.delete')) {
              buttons.push(
                h(ArtButtonTable, {
                  type: 'delete',
                  onClick: () => handleDelete(row)
                })
              )
            }
            return buttons.length ? h('div', { style: 'text-align: center' }, buttons) : '-'
          }
        }
      ]
    }
  })

  /**
   * 获取 Redis 信息
   */
  const loadRedisInfo = async () => {
    try {
      redisInfo.value = await fetchGetRedisInfo()
    } catch {
      // 忽略错误
    }
  }

  /**
   * 刷新所有数据
   */
  const handleRefreshAll = () => {
    loadRedisInfo()
    refreshData()
  }

  /**
   * 格式化 TTL
   */
  const formatTTL = (ttl: number): string => {
    if (ttl === -1) return '永不过期'
    if (ttl === -2) return '已过期'
    if (ttl < 60) return `${ttl}秒`
    if (ttl < 3600) return `${Math.floor(ttl / 60)}分钟`
    if (ttl < 86400) return `${Math.floor(ttl / 3600)}小时`
    return `${Math.floor(ttl / 86400)}天`
  }

  /**
   * 格式化字节
   */
  const formatBytes = (bytes: number): string => {
    if (bytes < 1024) return `${bytes} B`
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB`
    return `${(bytes / 1024 / 1024).toFixed(2)} MB`
  }

  /**
   * 获取类型标签类型
   */
  const getTypeTagType = (type: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    const map: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
      OBJECT: 'primary',
      MAP: 'info',
      LIST: 'success',
      SET: 'warning',
      ZSET: 'danger'
    }
    return map[type] || 'info'
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
  const handleSelectionChange = (selection: CacheKeyVO[]) => {
    selectedRows.value = selection
  }

  /**
   * 查看详情
   */
  const showDetail = async (row: CacheKeyVO) => {
    detailDrawerVisible.value = true
    detailLoading.value = true
    try {
      currentDetail.value = await fetchGetCacheKeyDetail(row.key)
    } finally {
      detailLoading.value = false
    }
  }

  /**
   * 删除缓存键
   */
  const handleDelete = async (row: CacheKeyVO) => {
    try {
      await ElMessageBox.confirm(`确定要删除键「${row.key}」吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await fetchDeleteCacheKey(row.key)
      handleRefreshAll()
    } catch {
      // 用户取消
    }
  }

  /**
   * 批量删除
   */
  const handleBatchDelete = async () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请选择要删除的缓存键')
      return
    }
    try {
      await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个缓存键吗？`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      const keys = selectedRows.value.map(item => item.key)
      await fetchBatchDeleteCacheKeys(keys)
      selectedRows.value = []
      handleRefreshAll()
    } catch {
      // 用户取消
    }
  }

  /**
   * 清空所有缓存
   */
  const handleClearAll = async () => {
    try {
      await ElMessageBox.confirm('确定要清空所有缓存吗？此操作不可恢复！', '清空缓存', {
        confirmButtonText: '确定清空',
        cancelButtonText: '取消',
        type: 'error'
      })
      await fetchClearAllCache()
      handleRefreshAll()
    } catch {
      // 用户取消
    }
  }

  onMounted(() => {
    loadRedisInfo()
  })
</script>
