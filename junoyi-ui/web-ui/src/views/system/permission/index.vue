<!-- 权限组管理页面 -->
<template>
  <div class="permission-page art-full-height">
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
          <ElButton 
            v-permission="'system.ui.permission.button.add'" 
            @click="handleAdd" 
            v-ripple
          >
            新增权限组
          </ElButton>
          <ElButton 
            @click="goToPermissionPool" 
            v-permission="'system.ui.permission.pool.view'"
            v-ripple
          >
            权限池
          </ElButton>
          <ElButton 
            v-permission="'system.ui.permission.button.delete'"
            :disabled="selectedRows.length === 0"
            @click="handleBatchDelete" 
            v-ripple
          >
            批量删除
          </ElButton>

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

    <!-- 编辑弹窗 -->
    <PermissionGroupDialog
      v-model:visible="dialogVisible"
      :editData="editData"
      @success="handleDialogSuccess"
    />

    <!-- 预览弹窗 -->
    <PermissionPreviewDialog
      v-model:visible="previewVisible"
      :data="previewData"
    />
  </div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { usePermission } from '@/hooks/core/usePermission'
  import { ElTag, ElMessageBox } from 'element-plus'
  import { 
    fetchGetPermissionGroupList, 
    fetchDeletePermissionGroup,
    fetchDeletePermissionGroupBatch
  } from '@/api/system/permission'
  import PermissionGroupDialog from './modules/permission-group-dialog.vue'
  import PermissionPreviewDialog from './modules/permission-preview-dialog.vue'

  defineOptions({ name: 'PermissionGroup' })

  const router = useRouter()

  type PermissionGroupVO = Api.System.PermissionGroupVO

  const { hasPermission } = usePermission()

  // 弹窗相关
  const dialogVisible = ref(false)
  const editData = ref<PermissionGroupVO | null>(null)

  // 预览弹窗
  const previewVisible = ref(false)
  const previewData = ref<PermissionGroupVO | null>(null)

  // 选中行
  const tableRef = ref()
  const selectedRows = ref<PermissionGroupVO[]>([])

  // 搜索相关
  const initialSearchState = {
    groupName: '',
    groupCode: '',
    status: undefined as number | undefined
  }

  const formFilters = reactive({ ...initialSearchState })

  // 状态选项（1=正常 0=禁用）
  const statusOptions = [
    { label: '正常', value: 1 },
    { label: '禁用', value: 0 }
  ]

  const formItems = computed(() => [
    {
      label: '名称',
      key: 'groupName',
      type: 'input',
      props: { clearable: true, placeholder: '权限组名称' }
    },
    {
      label: '编码',
      key: 'groupCode',
      type: 'input',
      props: { clearable: true, placeholder: '权限组编码' }
    },
    {
      label: '状态',
      key: 'status',
      type: 'select',
      props: {
        clearable: true,
        options: statusOptions
      }
    }
  ])

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    searchParams,
    resetSearchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    core: {
      apiFn: fetchGetPermissionGroupList,
      apiParams: {
        current: 1,
        size: 20,
        ...formFilters
      },
      columnsFactory: () => [
        {
          type: 'selection',
          width: 50,
          align: 'center',
          headerAlign: 'center',
          reserveSelection: true
        },
        {
          prop: 'groupName',
          label: '权限组名称',
          minWidth: 150,
          headerAlign: 'center'
        },
        {
          prop: 'groupCode',
          label: '权限组编码',
          minWidth: 180,
          headerAlign: 'center',
          formatter: (row: PermissionGroupVO) => {
            return h('code', { 
              style: { 
                padding: '2px 8px', 
                background: 'var(--el-fill-color)', 
                borderRadius: '4px',
                fontFamily: 'monospace',
                fontSize: '12px'
              } 
            }, row.groupCode)
          }
        },
        {
          prop: 'description',
          label: '描述',
          minWidth: 180,
          headerAlign: 'center',
          showOverflowTooltip: true,
          formatter: (row: PermissionGroupVO) => row.description || '-'
        },
        {
          prop: 'priority',
          label: '优先级',
          width: 90,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: PermissionGroupVO) => row.priority ?? 0
        },
        {
          prop: 'permissions',
          label: '权限',
          width: 100,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: PermissionGroupVO) => {
            const perms = row.permissions || []
            const count = perms.length
            const isSuperAdmin = count === 1 && perms[0] === '*'
            const type = count === 0 ? 'info' : isSuperAdmin ? 'warning' : 'primary'
            return h(ElTag, { 
              type, 
              size: 'small',
              effect: 'light',
              style: { cursor: 'pointer' },
              onClick: () => handlePreview(row)
            }, () => isSuperAdmin ? '超级权限' : `${count} 个`)
          }
        },
        {
          prop: 'status',
          label: '状态',
          width: 80,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: PermissionGroupVO) => {
            const status = row.status ?? 1
            return h(ElTag, { 
              type: status === 1 ? 'success' : 'danger', 
              size: 'small',
              effect: 'light'
            }, () => status === 1 ? '正常' : '禁用')
          }
        },
        {
          prop: 'createTime',
          label: '创建时间',
          width: 170,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: PermissionGroupVO) => formatTime(row.updateTime || row.createTime)
        },
        {
          prop: 'updateTime',
          label: '更新时间',
          width: 170,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: PermissionGroupVO) => formatTime(row.updateTime || row.updateTime)
        },
        {
          prop: 'operation',
          label: '操作',
          width: 160,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: PermissionGroupVO) => {
            const buttons = []
            
            buttons.push(
              h(ArtButtonTable, {
                type: 'view',
                onClick: () => handlePreview(row)
              })
            )
            
            if (hasPermission('system.ui.permission.button.edit')) {
              buttons.push(
                h(ArtButtonTable, {
                  type: 'edit',
                  onClick: () => handleEdit(row)
                })
              )
            }
            
            if (hasPermission('system.ui.permission.button.delete')) {
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
   * 选择变化
   */
  const handleSelectionChange = (rows: PermissionGroupVO[]) => {
    selectedRows.value = rows
  }

  /**
   * 重置搜索
   */
  const handleReset = () => {
    Object.assign(formFilters, { ...initialSearchState })
    resetSearchParams()
  }

  /**
   * 搜索
   */
  const handleSearch = () => {
    const validParams: Record<string, unknown> = {}
    Object.entries(formFilters).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        validParams[key] = value
      }
    })
    Object.assign(searchParams, validParams)
    refreshData()
  }

  /**
   * 新增
   */
  const handleAdd = () => {
    editData.value = null
    dialogVisible.value = true
  }

  /**
   * 跳转到权限池
   */
  const goToPermissionPool = () => {
    router.push('/system/permission-pool')
  }

  /**
   * 编辑
   */
  const handleEdit = (row: PermissionGroupVO) => {
    editData.value = row
    dialogVisible.value = true
  }

  /**
   * 预览
   */
  const handlePreview = (row: PermissionGroupVO) => {
    previewData.value = row
    previewVisible.value = true
  }

  /**
   * 删除
   */
  const handleDelete = async (row: PermissionGroupVO) => {
    try {
      await ElMessageBox.confirm(
        `确定要删除权限组「${row.groupName}」吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      await fetchDeletePermissionGroup(row.id)
      refreshData()
    } catch {
      // 用户取消
    }
  }

  /**
   * 批量删除
   */
  const handleBatchDelete = async () => {
    if (selectedRows.value.length === 0) return
    
    try {
      await ElMessageBox.confirm(
        `确定要删除选中的 ${selectedRows.value.length} 个权限组吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      const ids = selectedRows.value.map(row => row.id)
      await fetchDeletePermissionGroupBatch(ids)
      selectedRows.value = []
      refreshData()
    } catch {
      // 用户取消
    }
  }

  /**
   * 弹窗成功回调
   */
  const handleDialogSuccess = () => {
    refreshData()
  }
</script>
