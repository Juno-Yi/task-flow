<template>
  <div class="art-full-height">
    <!-- 快速添加区域 -->
    <PermissionPoolAdd v-permission="'system.ui.permission-pool.button.add'" @add="handleQuickAdd" />

    <!-- 搜索栏 -->
    <PermissionPoolSearch
      v-show="showSearchBar"
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    />

    <!-- 主内容卡片 -->
    <ElCard
      class="art-table-card"
      shadow="never"
      :style="{ 'margin-top': showSearchBar ? '12px' : '0' }"
    >
      <!-- 表格头部 -->
      <ArtTableHeader
        v-model:columns="columnChecks"
        v-model:showSearchBar="showSearchBar"
        :loading="loading"
        @refresh="refreshData"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton
              :disabled="selectedIds.length === 0"
              v-permission="'system.ui.permission-pool.button.delete'"
              @click="handleBatchDelete"
              v-ripple
            >
              批量删除
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <!-- 表格 -->
      <ArtTable
        ref="tableRef"
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        row-key="id"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { ElMessageBox, ElMessage, ElTag, ElSwitch, ElButton as ElBtn } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import { usePermission } from '@/hooks/core/usePermission'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import PermissionPoolAdd from './modules/permission-pool-add.vue'
  import PermissionPoolSearch from './modules/permission-pool-search.vue'
  import {
    fetchGetPermissionPoolList,
    fetchAddPermissionPool,
    fetchDeletePermissionPool,
    fetchDeletePermissionPoolBatch,
    fetchUpdatePermissionPoolStatus
  } from '@/api/system/permissionPool'

  defineOptions({ name: 'PermissionPool' })

  type PermissionPoolVO = Api.System.PermissionPoolVO

  const { hasPermission } = usePermission()

  const tableRef = ref()
  const showSearchBar = ref(true)
  const selectedIds = ref<number[]>([])

  // 搜索表单
  const searchForm = ref({
    permission: undefined,
    description: undefined,
    status: undefined
  })

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
      apiFn: fetchGetPermissionPoolList,
      apiParams: {
        current: 1,
        size: 20
      },
      columnsFactory: () => [
        {
          type: 'selection',
          width: 50,
          align: 'center'
        },
        {
          prop: 'permission',
          label: '权限标识',
          headerAlign: 'center',
          minWidth: 200,
          showOverflowTooltip: true
        },
        {
          prop: 'type',
          label: '权限类型',
          align: 'center',
          headerAlign: 'center',
          width: 100,
          formatter: (row: PermissionPoolVO) => {
            const typeInfo = getPermissionType(row.permission)
            return h(
              ElTag,
              { type: typeInfo.tagType as any, size: 'small' },
              () => typeInfo.type
            )
          }
        },
        {
          prop: 'description',
          label: '权限描述',
          headerAlign: 'center',
          minWidth: 180,
          showOverflowTooltip: true,
          formatter: (row: PermissionPoolVO) => row.description || '-'
        },
        {
          prop: 'status',
          label: '状态',
          align: 'center',
          headerAlign: 'center',
          width: 100,
          formatter: (row: PermissionPoolVO) => {
            return h(ElSwitch, {
              modelValue: row.status === 1,
              size: 'small',
              disabled: !hasPermission('system.ui.permission-pool.button.status'),
              onChange: () => handleToggleStatus(row)
            })
          }
        },
        {
          prop: 'createTime',
          label: '创建时间',
          headerAlign: 'center',
          width: 180,
          formatter: (row: PermissionPoolVO) => formatTime(row.createTime)
        },
        {
          prop: 'operation',
          label: '操作',
          width: 80,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: PermissionPoolVO) => {
            if (!hasPermission('system.ui.permission-pool.button.delete')) {
              return '-'
            }
            return h(ElBtn, {
              type: 'danger',
              size: 'small',
              text: true,
              onClick: () => handleDelete(row)
            }, () => [
              h(ArtSvgIcon, { icon: 'ri:delete-bin-4-line', size: 16, class: 'mr-1' }),
              '删除'
            ])
          }
        }
      ]
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
   * 获取权限类型
   */
  const getPermissionType = (permission: string): { type: string; tagType: string } => {
    const lowerPermission = permission.toLowerCase()

    if (permission.includes('*')) {
      return { type: '通配符', tagType: 'warning' }
    }
    if (lowerPermission.includes('.ui.')) {
      return { type: 'UI', tagType: 'primary' }
    }
    if (lowerPermission.includes('.api.')) {
      return { type: 'API', tagType: 'success' }
    }
    if (lowerPermission.includes('.data.')) {
      return { type: 'DATA', tagType: 'danger' }
    }
    return { type: '其他', tagType: 'info' }
  }

  /**
   * 快速添加权限
   */
  const handleQuickAdd = async (formData: { permission: string; description: string }) => {
    if (!formData.permission.trim()) {
      ElMessage.warning('请输入权限标识')
      return
    }

    try {
      await fetchAddPermissionPool({
        ...formData,
        status: 1
      })
      refreshData()
    } catch (error) {
      console.error('添加权限失败:', error)
    }
  }

  /**
   * 搜索处理
   */
  const handleSearch = (params: Record<string, any>) => {
    Object.assign(searchParams, params)
    getData()
  }

  /**
   * 表格选择变化
   */
  const handleSelectionChange = (selection: PermissionPoolVO[]) => {
    selectedIds.value = selection.map(item => item.id)
  }

  /**
   * 切换权限状态
   */
  const handleToggleStatus = async (item: PermissionPoolVO) => {
    const newStatus = item.status === 1 ? 0 : 1
    try {
      await fetchUpdatePermissionPoolStatus(item.id, newStatus)
      item.status = newStatus
      ElMessage.success('状态更新成功')
    } catch (error) {
      console.error('更新权限状态失败:', error)
    }
  }

  /**
   * 删除单个权限
   */
  const handleDelete = async (row: PermissionPoolVO) => {
    try {
      await ElMessageBox.confirm(
        `确定删除权限「${row.permission}」吗？此操作不可恢复！`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      await fetchDeletePermissionPool(row.id)
      refreshData()
    } catch {
      // 用户取消
    }
  }

  /**
   * 批量删除权限
   */
  const handleBatchDelete = async () => {
    if (selectedIds.value.length === 0) {
      ElMessage.warning('请选择要删除的权限')
      return
    }

    try {
      await ElMessageBox.confirm(
        `确定删除选中的 ${selectedIds.value.length} 个权限吗？此操作不可恢复！`,
        '批量删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      await fetchDeletePermissionPoolBatch(selectedIds.value)
      selectedIds.value = []
      refreshData()
    } catch {
      // 用户取消
    }
  }
</script>
