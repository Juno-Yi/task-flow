<!--系统管理-系统参数-->
<template>
  <div class="art-full-height">
    <!-- 搜索栏 -->
    <ConfigSearch
      v-show="showSearchBar"
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
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
      >
        <template #left>
          <ElSpace wrap>
            <ElButton v-permission="'system.ui.config.button.add'" @click="showDialog('add')" v-ripple>
              新增参数
            </ElButton>
            <ElButton 
              v-permission="'system.ui.config.button.delete'" 
              :disabled="selectedIds.length === 0"
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
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>

    <!-- 参数编辑/添加弹窗 -->
    <ConfigDialog
      v-model="dialogVisible"
      :dialog-type="dialogType"
      :config-data="currentConfigData"
      @success="refreshData"
    />
  </div>
</template>

<script setup lang="ts">
  import { ElTag, ElMessageBox } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import { usePermission } from '@/hooks/core/usePermission'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ConfigSearch from './modules/config-search.vue'
  import ConfigDialog from './modules/config-dialog.vue'
  import { fetchGetConfigList, fetchDeleteConfig, fetchDeleteConfigBatch } from '@/api/system/config'

  defineOptions({ name: 'SystemConfig' })

  const { hasPermission } = usePermission()

  type ConfigVO = Api.System.ConfigVO

  // 搜索表单
  const searchForm = ref<Api.System.ConfigQueryDTO>({
    configName: undefined,
    configKey: undefined,
    configType: undefined,
    isSystem: undefined
  })

  const showSearchBar = ref(true)
  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const currentConfigData = ref<ConfigVO | undefined>(undefined)
  const selectedIds = ref<number[]>([])

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
    // 核心配置
    core: {
      apiFn: fetchGetConfigList,
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
          prop: 'id',
          label: 'ID',
          align: 'center',
          headerAlign: 'center',
          width: 80
        },
        {
          prop: 'configName',
          label: '参数名称',
          align: 'center',
          headerAlign: 'center',
          minWidth: 150
        },
        {
          prop: 'configKey',
          label: '参数键名',
          align: 'center',
          headerAlign: 'center',
          minWidth: 180
        },
        {
          prop: 'configValue',
          label: '参数键值',
          align: 'center',
          headerAlign: 'center',
          minWidth: 150,
          showOverflowTooltip: true
        },
        {
          prop: 'configType',
          label: '参数类型',
          align: 'center',
          headerAlign: 'center',
          width: 100,
          formatter: (row: ConfigVO) => {
            const typeMap: Record<string, string> = {
              text: '文本',
              number: '数字',
              boolean: '布尔',
              json: 'JSON'
            }
            return typeMap[row.configType] || row.configType || '-'
          }
        },
        {
          prop: 'sort',
          label: '排序',
          align: 'center',
          headerAlign: 'center',
          width: 80
        },
        {
          prop: 'isSystem',
          label: '系统内置',
          align: 'center',
          headerAlign: 'center',
          width: 100,
          formatter: (row: ConfigVO) => {
            const isBuiltIn = row.isSystem === 'Y'
            return h(
              ElTag,
              { type: isBuiltIn ? 'danger' : 'success', size: 'small' },
              () => isBuiltIn ? '是' : '否'
            )
          }
        },
        {
          prop: 'status',
          label: '状态',
          align: 'center',
          headerAlign: 'center',
          width: 80,
          formatter: (row: ConfigVO) => {
            const isNormal = row.status === 0
            return h(
              ElTag,
              { type: isNormal ? 'success' : 'info', size: 'small' },
              () => isNormal ? '正常' : '停用'
            )
          }
        },
        {
          prop: 'remark',
          label: '备注',
          headerAlign: 'center',
          minWidth: 150,
          showOverflowTooltip: true,
          formatter: (row: ConfigVO) => row.remark || '-'
        },
        {
          prop: 'createTime',
          label: '创建时间',
          headerAlign: 'center',
          width: 180,
          formatter: (row: ConfigVO) => formatTime(row.createTime)
        },
        {
          prop: 'operation',
          label: '操作',
          width: 150,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: ConfigVO) => {
            const buttons = []

            // 编辑按钮
            if (hasPermission('system.ui.config.button.edit')) {
              buttons.push(
                h(ArtButtonTable, {
                  type: 'edit',
                  onClick: () => showDialog('edit', row)
                })
              )
            }

            // 删除按钮（系统内置参数不允许删除）
            const isBuiltIn = row.isSystem === 'Y'
            if (hasPermission('system.ui.config.button.delete') && !isBuiltIn) {
              buttons.push(
                h(ArtButtonTable, {
                  type: 'delete',
                  onClick: () => handleDeleteConfig(row)
                })
              )
            }

            return h('div', { class: 'flex items-center justify-center' }, buttons)
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
   * 显示弹窗
   */
  const showDialog = (type: 'add' | 'edit', row?: ConfigVO) => {
    dialogVisible.value = true
    dialogType.value = type
    currentConfigData.value = row
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
  const handleSelectionChange = (selection: ConfigVO[]) => {
    selectedIds.value = selection.map(item => item.id)
  }

  /**
   * 删除单个参数
   */
  const handleDeleteConfig = async (row: ConfigVO) => {
    // 系统内置参数不允许删除
    if (row.isSystem === 'Y') {
      ElMessage.warning('系统内置参数不允许删除')
      return
    }

    try {
      await ElMessageBox.confirm(
        `确定删除参数「${row.configName}」吗？此操作不可恢复！`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      await fetchDeleteConfig(row.id)
      ElMessage.success('删除参数成功')
      refreshData()
    } catch {
      // 用户取消
    }
  }

  /**
   * 批量删除参数
   */
  const handleBatchDelete = async () => {
    if (selectedIds.value.length === 0) {
      ElMessage.warning('请选择要删除的参数')
      return
    }

    // 检查是否包含系统内置参数
    const selectedRows = data.value.filter((item: Api.System.ConfigVO) => selectedIds.value.includes(item.id))
    const hasSystemBuiltIn = selectedRows.some((item: Api.System.ConfigVO) => item.isSystem === 'Y')
    
    if (hasSystemBuiltIn) {
      ElMessage.warning('选中的参数中包含系统内置参数，不允许删除')
      return
    }

    try {
      await ElMessageBox.confirm(
        `确定删除选中的 ${selectedIds.value.length} 个参数吗？此操作不可恢复！`,
        '批量删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      await fetchDeleteConfigBatch(selectedIds.value)
      ElMessage.success(`成功删除 ${selectedIds.value.length} 个参数`)
      selectedIds.value = []
      refreshData()
    } catch {
      // 用户取消
    }
  }
</script>
