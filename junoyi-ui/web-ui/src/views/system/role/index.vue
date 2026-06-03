<!-- 角色管理页面 -->
<template>
  <div class="art-full-height">
    <RoleSearch
      v-show="showSearchBar"
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    ></RoleSearch>

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
            <ElButton v-permission="'system.ui.role.button.add'" @click="showDialog('add')" v-ripple>新增角色</ElButton>
            <ElButton 
              v-permission="'system.ui.role.button.delete'" 
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
      >
      </ArtTable>
    </ElCard>

    <!-- 角色编辑弹窗 -->
    <RoleEditDialog
      v-model="dialogVisible"
      :dialog-type="dialogType"
      :role-data="currentRoleData"
      @success="refreshData"
    />

    <!-- 菜单权限弹窗 -->
    <RolePermissionDialog
      v-model="permissionDialog"
      :role-data="currentRoleData"
      @success="refreshData"
    />

    <!-- 分配权限组弹窗 -->
    <RolePermissionGroupDialog
      v-model:visible="permissionGroupDialog"
      :role-data="currentRoleData"
      @success="refreshData"
    />
  </div>
</template>

<script setup lang="ts">
  import { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { usePermission } from '@/hooks/core/usePermission'
  import { fetchGetRoleList, fetchDeleteRole, fetchDeleteRoleBatch } from '@/api/system/role'
  import ArtButtonMore from '@/components/core/forms/art-button-more/index.vue'
  import RoleSearch from './modules/role-search.vue'
  import RoleEditDialog from './modules/role-edit-dialog.vue'
  import RolePermissionDialog from './modules/role-permission-dialog.vue'
  import RolePermissionGroupDialog from './modules/role-permission-group-dialog.vue'
  import { ElTag, ElMessageBox } from 'element-plus'

  defineOptions({ name: 'Role' })

  const { hasPermission } = usePermission()

  type RoleVO = Api.System.RoleVO

  // 搜索表单
  const searchForm = ref({
    roleName: undefined,
    roleKey: undefined,
    status: undefined,
  })

  const showSearchBar = ref(true)

  const dialogVisible = ref(false)
  const permissionDialog = ref(false)
  const permissionGroupDialog = ref(false)
  const currentRoleData = ref<RoleVO | undefined>(undefined)
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
      apiFn: fetchGetRoleList,
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
          prop: 'roleName',
          label: '角色名称',
          headerAlign: 'center',
          minWidth: 120
        },
        {
          prop: 'roleKey',
          label: '角色标识',
          headerAlign: 'center',
          minWidth: 120
        },
        {
          prop: 'sort',
          label: '排序',
          align: 'center',
          headerAlign: 'center',
          width: 80,
        },
        {
          prop: 'statusLabel',
          label: '状态',
          align: 'center',
          headerAlign: 'center',
          width: 100,
          formatter: (row: RoleVO) => {
            return h(ElTag, { 
              type: (row.statusType || 'info') as 'success' | 'info' | 'warning' | 'danger', 
              size: 'small' 
            }, () => row.statusLabel || '-')
          }
        },
        {
          prop: 'createTime',
          label: '创建时间',
          headerAlign: 'center',
          width: 180,
          formatter: (row: RoleVO) => formatTime(row.createTime)
        },
        {
          prop: 'updateTime',
          label: '更新时间',
          width: 180,
          formatter: (row: RoleVO) => formatTime(row.updateTime)
        },
        {
          prop: 'remark',
          label: '备注',
          width: 150,
          showOverflowTooltip: true,
          formatter: (row: RoleVO) => row.remark || '-'
        },
        {
          prop: 'operation',
          label: '操作',
          width: 80,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: RoleVO) => {
            const list: ButtonMoreItem[] = []

            if (hasPermission('system.ui.role.button.edit')) {
              list.push({
                key: 'edit',
                label: '编辑角色',
                icon: 'ri:edit-2-line'
              })
            }

            if (hasPermission('system.ui.role.button.permission')){
              list.push({
                key: 'assignPermission',
                label: '分配权限组',
                icon: 'ri:folder-lock-line'
              })
            }
            
            if (hasPermission('system.ui.role.button.delete')) {
              list.push({
                key: 'delete',
                label: '删除角色',
                icon: 'ri:delete-bin-4-line',
                color: '#f56c6c'
              })
            }


            
            if (list.length === 0) return '-'
            
            return h(ArtButtonMore, {
              list,
              onClick: (item: ButtonMoreItem) => handleButtonMoreClick(item, row)
            })
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

  const dialogType = ref<'add' | 'edit'>('add')

  const showDialog = (type: 'add' | 'edit', row?: RoleVO) => {
    dialogVisible.value = true
    dialogType.value = type
    currentRoleData.value = row
  }

  /**
   * 搜索处理
   */
  const handleSearch = (params: Record<string, any>) => {
    Object.assign(searchParams, params)
    getData()
  }

  const showPermissionDialog = (row?: RoleVO) => {
    permissionDialog.value = true
    currentRoleData.value = row
  }

  /**
   * 显示分配权限组弹窗
   */
  const showPermissionGroupDialog = (row?: RoleVO) => {
    permissionGroupDialog.value = true
    currentRoleData.value = row
  }

  /**
   * 操作按钮点击
   */
  const handleButtonMoreClick = (item: ButtonMoreItem, row: RoleVO) => {
    switch (item.key) {
      case 'permission':
        showPermissionDialog(row)
        break
      case 'assignPermission':
        showPermissionGroupDialog(row)
        break
      case 'edit':
        showDialog('edit', row)
        break
      case 'delete':
        handleDeleteRole(row)
        break
    }
  }

  /**
   * 表格选择变化
   */
  const handleSelectionChange = (selection: RoleVO[]) => {
    selectedIds.value = selection.map(item => item.id)
  }

  /**
   * 删除单个角色
   */
  const handleDeleteRole = async (row: RoleVO) => {
    try {
      await ElMessageBox.confirm(`确定删除角色「${row.roleName}」吗？此操作不可恢复！`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await fetchDeleteRole(row.id)
      refreshData()
    } catch {
      // 用户取消
    }
  }

  /**
   * 批量删除角色
   */
  const handleBatchDelete = async () => {
    if (selectedIds.value.length === 0) {
      ElMessage.warning('请选择要删除的角色')
      return
    }

    try {
      await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个角色吗？此操作不可恢复！`, '批量删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await fetchDeleteRoleBatch(selectedIds.value)
      selectedIds.value = []
      refreshData()
    } catch {
      // 用户取消
    }
  }
</script>
