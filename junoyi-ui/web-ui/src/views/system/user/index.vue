<!-- 用户管理页面 -->
<template>
  <div class="art-full-height">
    <div class="box-border flex gap-4 h-full max-md:block max-md:gap-0 max-md:h-auto">
      <!-- 左侧部门树 -->
      <div class="flex-shrink-0 h-full max-md:w-full max-md:h-auto max-md:mb-5" :class="isTreeCollapsed ? 'w-12' : 'w-58'">
        <ElCard class="tree-card art-card-xs flex flex-col h-full mt-0" shadow="never">
          <template #header>
            <div class="flex items-center justify-between">
              <b v-if="!isTreeCollapsed">部门</b>
              <ElButton link @click="toggleTreeCollapse">
                <ArtSvgIcon :icon="isTreeCollapsed ? 'ri:menu-unfold-line' : 'ri:menu-fold-line'" />
              </ElButton>
            </div>
          </template>
          <template v-if="!isTreeCollapsed">
            <ElInput
              v-model="deptSearchKey"
              placeholder="搜索部门"
              clearable
              class="mb-3 px-2"
              :prefix-icon="Search"
            />
            <ElScrollbar>
              <ElTree
                ref="deptTreeRef"
                :data="deptTreeData"
                :props="{ label: 'name', children: 'children' }"
                node-key="id"
                highlight-current
                default-expand-all
                :filter-node-method="filterDeptNode"
                @node-click="handleDeptClick"
              >
                <template #default="{ node, data }">
                  <span class="dept-tree-node">
                    <ArtSvgIcon 
                      :icon="data.id === 0 ? 'ri:building-line' : 'ri:folder-line'" 
                      class="mr-1.5"
                    />
                    <span>{{ node.label }}</span>
                  </span>
                </template>
              </ElTree>
            </ElScrollbar>
          </template>
        </ElCard>
      </div>

      <!-- 右侧用户列表 -->
      <div class="flex flex-col flex-grow min-w-0">
        <UserSearch v-model="searchForm" @search="handleSearch" @reset="resetSearchParams" />

        <ElCard class="flex flex-col flex-1 min-h-0 art-table-card" shadow="never">
          <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
            <template #left>
              <ElSpace wrap>
                <ElButton @click="showDialog('add')" v-permission="'system.ui.user.button.add'" v-ripple>新增用户</ElButton>
                <ElButton 
                  :disabled="selectedRows.length === 0"
                  @click="batchDeleteUsers"
                  v-permission="'system.ui.user.button.delete'"
                  v-ripple
                >
                  批量删除
                </ElButton>
              </ElSpace>
            </template>
          </ArtTableHeader>

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
    </div>

    <!-- 用户弹窗 -->
    <UserDialog
      v-model:visible="dialogVisible"
      :type="dialogType"
      :user-data="currentUserData"
      @submit="handleDialogSubmit"
    />

    <!-- 分配角色弹窗 -->
    <UserRoleDialog
      v-model:visible="roleDialogVisible"
      :user-id="currentUserId"
      :user-name="currentUserName"
      @submit="handleRoleDialogSubmit"
    />

    <!-- 分配部门弹窗 -->
    <UserDeptDialog
      v-model:visible="deptDialogVisible"
      :user-id="currentUserId"
      :user-name="currentUserName"
      @submit="handleDeptDialogSubmit"
    />

    <!-- 重置密码弹窗 -->
    <UserPasswordDialog
      v-model:visible="passwordDialogVisible"
      :user-id="currentUserId"
      :user-name="currentUserName"
      @submit="handlePasswordDialogSubmit"
    />

    <!-- 分配权限组弹窗 -->
    <UserPermissionDialog
      v-model:visible="permissionDialogVisible"
      :user-id="currentUserId"
      :user-name="currentUserName"
      @submit="handlePermissionDialogSubmit"
    />

    <!-- 独立权限设置弹窗 -->
    <UserIndividualPermDialog
      v-model:visible="individualPermDialogVisible"
      :user-id="currentUserId"
      :user-name="currentUserName"
      @submit="handleIndividualPermDialogSubmit"
    />
  </div>
</template>

<script setup lang="ts">
  import { Search } from '@element-plus/icons-vue'
  import ArtButtonMore, { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { usePermission } from '@/hooks/core/usePermission'
  import { fetchGetUserList, fetchDeleteUser, fetchDeleteUserBatch } from '@/api/system/user'
  import { fetchGetDeptTree } from '@/api/system/department'
  import UserSearch from './modules/user-search.vue'
  import UserDialog from './modules/user-dialog.vue'
  import UserRoleDialog from './modules/user-role-dialog.vue'
  import UserDeptDialog from './modules/user-dept-dialog.vue'
  import UserPasswordDialog from './modules/user-password-dialog.vue'
  import UserPermissionDialog from './modules/user-permission-dialog.vue'
  import UserIndividualPermDialog from './modules/user-individual-perm-dialog.vue'
  import { ElTag, ElMessageBox, ElTree } from 'element-plus'
  import { DialogType } from '@/types'

  defineOptions({ name: 'User' })

  const { hasPermission, isSuperAdmin } = usePermission()

  type SysUserVO = Api.System.SysUserVO

  // 部门树相关
  const deptTreeRef = ref<InstanceType<typeof ElTree>>()
  const deptTreeData = ref<any[]>([])
  const deptSearchKey = ref('')
  const selectedDeptId = ref<number | null>(null)
  const isTreeCollapsed = ref(false)

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentUserData = ref<Partial<SysUserVO>>({})

  // 分配角色弹窗
  const roleDialogVisible = ref(false)
  const currentUserId = ref<number | null>(null)
  const currentUserName = ref('')

  // 分配部门弹窗
  const deptDialogVisible = ref(false)

  // 重置密码弹窗
  const passwordDialogVisible = ref(false)

  // 分配权限组弹窗
  const permissionDialogVisible = ref(false)

  // 独立权限设置弹窗
  const individualPermDialogVisible = ref(false)

  // 选中行
  const selectedRows = ref<SysUserVO[]>([])

  // 搜索表单
  const searchForm = ref({
    userName: undefined,
    nickName: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
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
    resetSearchParams: _resetSearchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    core: {
      apiFn: fetchGetUserList,
      apiParams: {
        current: 1,
        size: 20,
        ...searchForm.value
      },
      columnsFactory: () => [
        { type: 'selection' },
        {
          prop: 'userId',
          label: 'ID',
          width: 60,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'userName',
          label: '用户名',
          width: 120,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'nickName',
          label: '昵称',
          width: 120,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'sexLabel',
          label: '性别',
          width: 80,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SysUserVO) => {
            return h(ElTag, { 
              type: (row.sexType || 'info') as 'success' | 'info' | 'warning' | 'danger', 
              size: 'small' 
            }, () => row.sexLabel || '-')
          }
        },
        {
          prop: 'phonenumber',
          label: '手机号',
          width: 130,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'email',
          label: '邮箱',
          minWidth: 180,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'statusLabel',
          label: '状态',
          width: 80,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: SysUserVO) => {
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
          formatter: (row: SysUserVO) => formatTime(row.createTime)
        },
        {
          prop: 'updateTime',
          label: '更新时间',
          width: 180,
          headerAlign: 'center',
          formatter: (row: SysUserVO) => formatTime(row.updateTime)
        },
        {
          prop: 'operation',
          label: '操作',
          width: 80,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: SysUserVO) => {
            const list: ButtonMoreItem[] = []
            
            if (hasPermission('system.ui.user.button.edit')) {
              list.push({
                key: 'edit',
                label: '编辑用户',
                icon: 'ri:edit-2-line'
              })
            }

            // 只有超级管理员能重置用户密码
            if (isSuperAdmin.value){
              list.push({
                key: 'restPassword',
                label: '重置密码',
                icon: 'ri:lock-fill',
              })
            }
            
            if (hasPermission('system.ui.user.button.role')) {
              list.push({
                key: 'assignRole',
                label: '分配角色',
                icon: 'ri:user-settings-line'
              })
            }
            
            if (hasPermission('system.ui.user.button.dept')) {
              list.push({
                key: 'assignDept',
                label: '分配部门',
                icon: 'ri:building-2-line'
              })
            }

            if (hasPermission('system.ui.user.button.permission')){
              list.push({
                key: 'assignPermission',
                label: '分配权限组',
                icon: 'ri:folder-lock-line'
              })
            }

            if (hasPermission('system.ui.user.button.individual-perm')){
              list.push({
                key: 'individualPerm',
                label: '独立权限',
                icon: 'ri:key-2-line'
              })
            }
            
            if (hasPermission('system.ui.user.button.delete')) {
              list.push({
                key: 'delete',
                label: '删除用户',
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

  /**
   * 获取部门树数据
   */
  const loadDeptTree = async () => {
    try {
      const list = await fetchGetDeptTree()
      // 添加"全部用户"节点
      deptTreeData.value = [
        { id: 0, name: '全部用户', children: [] },
        ...(list || [])
      ]
    } catch (error) {
      console.error('获取部门树失败:', error)
    }
  }

  /**
   * 部门树过滤
   */
  const filterDeptNode = (value: string, data: any) => {
    if (!value) return true
    return data.name?.toLowerCase().includes(value.toLowerCase())
  }

  /**
   * 监听部门搜索关键字变化
   */
  watch(deptSearchKey, (val) => {
    deptTreeRef.value?.filter(val)
  })

  /**
   * 点击部门节点
   */
  const handleDeptClick = (data: any) => {
    selectedDeptId.value = data.id === 0 ? null : data.id
    // 更新搜索参数并刷新数据
    Object.assign(searchParams, { deptId: selectedDeptId.value })
    getData()
  }

  /**
   * 切换部门树折叠状态
   */
  const toggleTreeCollapse = () => {
    isTreeCollapsed.value = !isTreeCollapsed.value
  }

  /**
   * 搜索处理
   */
  const handleSearch = (params: Record<string, any>) => {
    Object.assign(searchParams, params, { deptId: selectedDeptId.value })
    getData()
  }

  /**
   * 重置搜索参数
   */
  const resetSearchParams = () => {
    _resetSearchParams()
    // 保留部门筛选
    if (selectedDeptId.value) {
      Object.assign(searchParams, { deptId: selectedDeptId.value })
    }
    getData()
  }

  /**
   * 显示用户弹窗
   */
  const showDialog = (type: DialogType, row?: SysUserVO): void => {
    dialogType.value = type
    currentUserData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 操作按钮点击
   */
  const handleButtonMoreClick = (item: ButtonMoreItem, row: SysUserVO) => {
    switch (item.key) {
      case 'edit':
        showDialog('edit', row)
        break
      case 'restPassword':
        showPasswordDialog(row)
        break
      case 'assignRole':
        showRoleDialog(row)
        break
      case 'assignDept':
        showDeptDialog(row)
        break
      case 'assignPermission':
        showPermissionDialog(row)
        break
      case 'individualPerm':
        showIndividualPermDialog(row)
        break
      case 'delete':
        deleteUser(row)
        break
    }
  }

  /**
   * 显示分配角色弹窗
   */
  const showRoleDialog = (row: SysUserVO): void => {
    currentUserId.value = row.userId
    currentUserName.value = row.userName
    roleDialogVisible.value = true
  }

  /**
   * 分配角色弹窗提交
   */
  const handleRoleDialogSubmit = () => {
    // 角色分配成功后可以刷新数据
    getData()
  }

  /**
   * 显示分配部门弹窗
   */
  const showDeptDialog = (row: SysUserVO): void => {
    currentUserId.value = row.userId
    currentUserName.value = row.userName
    deptDialogVisible.value = true
  }

  /**
   * 分配部门弹窗提交
   */
  const handleDeptDialogSubmit = () => {
    // 部门分配成功后可以刷新数据
    getData()
  }

  /**
   * 显示重置密码弹窗
   */
  const showPasswordDialog = (row: SysUserVO): void => {
    currentUserId.value = row.userId
    currentUserName.value = row.userName
    passwordDialogVisible.value = true
  }

  /**
   * 重置密码弹窗提交
   */
  const handlePasswordDialogSubmit = () => {
    // 密码重置成功
  }

  /**
   * 显示分配权限组弹窗
   */
  const showPermissionDialog = (row: SysUserVO): void => {
    currentUserId.value = row.userId
    currentUserName.value = row.userName
    permissionDialogVisible.value = true
  }

  /**
   * 分配权限组弹窗提交
   */
  const handlePermissionDialogSubmit = () => {
    // 权限组分配成功后可以刷新数据
    getData()
  }

  /**
   * 显示独立权限设置弹窗
   */
  const showIndividualPermDialog = (row: SysUserVO): void => {
    currentUserId.value = row.userId
    currentUserName.value = row.userName
    individualPermDialogVisible.value = true
  }

  /**
   * 独立权限弹窗提交
   */
  const handleIndividualPermDialogSubmit = () => {
    // 独立权限设置成功后可以刷新数据
    getData()
  }

  /**
   * 删除用户
   */
  const deleteUser = async (row: SysUserVO): Promise<void> => {
    try {
      await ElMessageBox.confirm(`确定要删除用户 "${row.userName}" 吗？`, '删除用户', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await fetchDeleteUser(row.userId)
      getData()
    } catch (error) {
      // 用户取消或删除失败
    }
  }

  /**
   * 批量删除用户
   */
  const batchDeleteUsers = async (): Promise<void> => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请先选择要删除的用户')
      return
    }
    try {
      await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个用户吗？`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      const ids = selectedRows.value.map(item => item.userId)
      await fetchDeleteUserBatch(ids)
      selectedRows.value = []
      getData()
    } catch (error) {
      // 用户取消或删除失败
    }
  }

  /**
   * 处理弹窗提交事件
   */
  const handleDialogSubmit = async () => {
    try {
      dialogVisible.value = false
      currentUserData.value = {}
      getData()
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: SysUserVO[]): void => {
    selectedRows.value = selection
  }

  onMounted(() => {
    loadDeptTree()
  })
</script>

<style scoped>
  .tree-card :deep(.el-card__body) {
    flex: 1;
    min-height: 0;
    padding: 10px 2px 10px 10px;
    display: flex;
    flex-direction: column;
  }

  .dept-tree-node {
    display: flex;
    align-items: center;
    font-size: 14px;
  }
</style>
