<!-- 部门管理页面 -->
<template>
  <div class="dept-page art-full-height">
    <!-- 搜索栏 -->
    <ArtSearchBar
      v-model="formFilters"
      :items="formItems"
      :showExpand="true"
      @reset="handleReset"
      @search="handleSearch"
    />

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader
        :showZebra="false"
        :loading="loading"
        v-model:columns="columnChecks"
        @refresh="handleRefresh"
      >
        <template #left>
          <ElButton v-permission="'system.ui.dept.button.add'" @click="handleAddDept" v-ripple>
            添加部门
          </ElButton>
          <ElButton @click="toggleExpand" v-ripple>
            {{ isExpanded ? '收起' : '展开' }}
          </ElButton>
          <ElButton 
            v-if="hasPermission('system.ui.dept.button.edit')"
            :type="isDragMode ? 'primary' : 'default'"
            @click="toggleDragMode" 
            v-ripple
          >
            <ArtSvgIcon :icon="isDragMode ? 'ri:check-line' : 'ri:drag-move-2-fill'" class="mr-1" />
            {{ isDragMode ? '保存排序' : '拖拽排序' }}
          </ElButton>
          <ElButton v-if="isDragMode" @click="cancelDragMode" v-ripple>
            取消
          </ElButton>
        </template>
      </ArtTableHeader>

      <!-- 拖拽模式提示 -->
      <div v-if="isDragMode" class="drag-tip mt-4 mb-4">
        <ArtSvgIcon icon="ri:information-line" class="mr-1" />
        拖拽模式：拖动部门可调整排序，拖到其他部门上可移动到该部门下。完成后点击「保存排序」。
      </div>

      <!-- 普通模式表格 -->
      <ArtTable
        v-if="!isDragMode"
        ref="tableRef"
        rowKey="id"
        :loading="loading"
        :columns="columns"
        :data="tableData"
        :stripe="false"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
      />

      <!-- 拖拽模式 - 树形列表 -->
      <div v-else class="drag-tree-wrapper">
        <div class="drag-tree-header">
          <span class="col-name">部门名称</span>
          <span class="col-leader">负责人</span>
          <span class="col-sort">排序</span>
          <span class="col-status">状态</span>
        </div>
        <div class="drag-tree-body" ref="dragTreeRef">
          <DragDeptNode
            v-for="item in dragTableData"
            :key="item.id"
            :node="item"
            :level="0"
            :dragNodeId="treeDrag.dragNodeId.value"
            :dropTargetId="treeDrag.dropTargetId.value"
            :dropPosition="treeDrag.dropPosition.value"
            :getNodeDragHandlers="treeDrag.getNodeDragHandlers"
          />
        </div>
      </div>

      <!-- 部门弹窗 -->
      <DeptDialog
        v-model:visible="dialogVisible"
        :editData="editData"
        :defaultParentId="parentId"
        @success="getDeptList"
      />

      <!-- 分配权限组弹窗 -->
      <DeptPermissionDialog
        v-model:visible="permissionDialogVisible"
        :dept-data="currentDeptData"
        @success="getDeptList"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import ArtButtonMore, { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { useTableColumns } from '@/hooks/core/useTableColumns'
  import { usePermission } from '@/hooks/core/usePermission'
  import { useTreeDrag } from '@/hooks/core/useTouchDrag'
  import DeptDialog from './modules/dept-dialog.vue'
  import DeptPermissionDialog from './modules/dept-permission-dialog.vue'
  import DragDeptNode from './modules/drag-dept-node.vue'
  import { ElTag, ElMessageBox } from 'element-plus'
  import { fetchGetDeptTree, fetchDeleteDept, fetchUpdateDeptSort } from '@/api/system/department'

  defineOptions({ name: 'Department' })

  type DeptVO = Api.System.DeptVO

  // 权限
  const { hasPermission } = usePermission()

  // 状态管理
  const loading = ref(false)
  const isExpanded = ref(false)
  const tableRef = ref()
  const dragTreeRef = ref()

  // 拖拽模式
  const isDragMode = ref(false)
  const dragTableData = ref<DeptVO[]>([])

  // 使用树形拖拽 hook
  const treeDrag = useTreeDrag({
    canBeParent: () => true, // 部门都可以作为父级
    onDrop: handleDrop
  })

  // 弹窗相关
  const dialogVisible = ref(false)
  const permissionDialogVisible = ref(false)
  const editData = ref<DeptVO | null>(null)
  const currentDeptData = ref<DeptVO | null>(null)
  const parentId = ref<number>(0)

  // 搜索相关
  const initialSearchState = {
    name: '',
    leader: '',
    phonenumber: '',
    email: '',
    status: undefined as number | undefined
  }

  const formFilters = reactive({ ...initialSearchState })

  // 状态选项
  const statusOptions = [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 }
  ]

  const formItems = computed(() => [
    {
      label: '部门名称',
      key: 'name',
      type: 'input',
      props: { clearable: true }
    },
    {
      label: '负责人',
      key: 'leader',
      type: 'input',
      props: { clearable: true }
    },
    {
      label: '联系电话',
      key: 'phonenumber',
      type: 'input',
      props: { clearable: true }
    },
    {
      label: '邮箱',
      key: 'email',
      type: 'input',
      props: { clearable: true }
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

  // 数据相关
  const tableData = ref<DeptVO[]>([])

  onMounted(() => {
    getDeptList()
  })

  /**
   * 获取部门列表数据
   */
  const getDeptList = async (): Promise<void> => {
    loading.value = true
    try {
      const list = await fetchGetDeptTree(formFilters)
      tableData.value = list || []
    } catch (error) {
      console.error('获取部门失败:', error)
    } finally {
      loading.value = false
    }
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

  // 表格列配置
  const { columnChecks, columns } = useTableColumns(() => [
    {
      type: 'selection',
      width: 50,
      align: 'center'
    },
    {
      prop: 'name',
      label: '部门名称',
      width: 200,
      headerAlign: 'center'
    },
    {
      prop: 'leader',
      label: '负责人',
      width: 120,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: DeptVO) => row.leader || '-'
    },
    {
      prop: 'phonenumber',
      label: '联系电话',
      width: 160,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: DeptVO) => row.phonenumber || '-'
    },
    {
      prop: 'email',
      label: '邮箱',
      width: 160,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: DeptVO) => row.email || '-'
    },
    {
      prop: 'sort',
      label: '排序',
      width: 80,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: DeptVO) => row.sort ?? 0
    },
    {
      prop: 'statusLabel',
      label: '状态',
      width: 100,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: DeptVO) => {
        return h(ElTag, { 
          type: (row.statusType || 'info') as 'success' | 'info' | 'warning' | 'danger', 
          size: 'small' 
        }, () => row.statusLabel || '-')
      }
    },
    {
      prop: 'createTime',
      label: '创建时间',
      width: 180,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: DeptVO) => formatTime(row.createTime)
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      width: 180,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: DeptVO) => formatTime(row.updateTime)
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: '200',
      headerAlign: 'center'
    },
    {
      prop: 'operation',
      label: '操作',
      width: 80,
      align: 'center',
      headerAlign: 'center',
      fixed: 'right',
      formatter: (row: DeptVO) => {
        const list: ButtonMoreItem[] = []
        
        if (hasPermission('system.ui.dept.button.add')) {
          list.push({
            key: 'addChild',
            label: '添加子部门',
            icon: 'ri:add-line'
          })
        }
        
        if (hasPermission('system.ui.dept.button.edit')) {
          list.push({
            key: 'edit',
            label: '编辑部门',
            icon: 'ri:edit-2-line'
          })
        }

        if (hasPermission('system.ui.dept.button.permission')){
          list.push({
            key: 'assignPermission',
            label: '分配权限组',
            icon: 'ri:folder-lock-line'
          })
        }
        
        if (hasPermission('system.ui.dept.button.delete')) {
          list.push({
            key: 'delete',
            label: '删除部门',
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
  ])

  /**
   * 深度克隆对象
   */
  const deepClone = <T,>(obj: T): T => {
    if (obj === null || typeof obj !== 'object') return obj
    if (obj instanceof Date) return new Date(obj) as T
    if (Array.isArray(obj)) return obj.map((item) => deepClone(item)) as T
    const cloned = {} as T
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        cloned[key] = deepClone(obj[key])
      }
    }
    return cloned
  }

  /**
   * 重置搜索条件
   */
  const handleReset = (): void => {
    Object.assign(formFilters, { ...initialSearchState })
    isExpanded.value = false
    getDeptList()
  }

  /**
   * 执行搜索
   */
  const handleSearch = (): void => {
    getDeptList()
  }

  /**
   * 刷新部门列表
   */
  const handleRefresh = (): void => {
    getDeptList()
  }

  /**
   * 添加部门
   */
  const handleAddDept = (): void => {
    editData.value = null
    parentId.value = 0
    dialogVisible.value = true
  }

  /**
   * 操作按钮点击
   */
  const handleButtonMoreClick = (item: ButtonMoreItem, row: DeptVO) => {
    switch (item.key) {
      case 'addChild':
        handleAddChildDept(row)
        break
      case 'edit':
        handleEditDept(row)
        break
      case 'assignPermission':
        showPermissionDialog(row)
        break
      case 'delete':
        handleDeleteDept(row)
        break
    }
  }

  /**
   * 显示分配权限组弹窗
   */
  const showPermissionDialog = (row: DeptVO): void => {
    currentDeptData.value = row
    permissionDialogVisible.value = true
  }

  /**
   * 添加子部门
   */
  const handleAddChildDept = (row: DeptVO): void => {
    editData.value = null
    parentId.value = row.id
    dialogVisible.value = true
  }

  /**
   * 编辑部门
   */
  const handleEditDept = (row: DeptVO): void => {
    editData.value = row
    parentId.value = 0
    dialogVisible.value = true
  }

  /**
   * 删除部门
   */
  const handleDeleteDept = async (row: DeptVO): Promise<void> => {
    const hasChildren = row.children && row.children.length > 0
    
    if (hasChildren) {
      ElMessageBox.alert(`部门「${row.name}」下有子部门，请先删除子部门后再删除`, '提示', {
        confirmButtonText: '知道了',
        type: 'warning'
      })
      return
    }
    
    try {
      await ElMessageBox.confirm(`确定要删除部门「${row.name}」吗？删除后无法恢复`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await fetchDeleteDept(row.id)
      getDeptList()
    } catch {
      // 用户取消操作
    }
  }

  /**
   * 切换展开/收起所有部门
   */
  const toggleExpand = (): void => {
    isExpanded.value = !isExpanded.value
    nextTick(() => {
      if (tableRef.value?.elTableRef && tableData.value) {
        const processRows = (rows: DeptVO[]) => {
          rows.forEach((row) => {
            if (row.children?.length) {
              tableRef.value.elTableRef.toggleRowExpansion(row, isExpanded.value)
              processRows(row.children)
            }
          })
        }
        processRows(tableData.value)
      }
    })
  }

  // ==================== 拖拽排序相关 ====================

  /**
   * 进入/保存拖拽模式
   */
  const toggleDragMode = async (): Promise<void> => {
    if (isDragMode.value) {
      // 保存排序
      await saveSortChanges()
    } else {
      // 进入拖拽模式
      dragTableData.value = deepClone(tableData.value)
      isDragMode.value = true
    }
  }

  /**
   * 取消拖拽模式
   */
  const cancelDragMode = (): void => {
    isDragMode.value = false
    dragTableData.value = []
    // 重置展开状态
    isExpanded.value = false
  }

  /**
   * 处理拖拽放置
   */
  function handleDrop(dragId: number, targetId: number, position: 'before' | 'after' | 'inside'): void {
    const dragNode = findNodeById(dragTableData.value, dragId)
    if (!dragNode) return

    // 不能拖到自己的子节点里
    if (isDescendant(dragNode, targetId)) {
      ElMessage.warning('不能将节点拖到自己的子节点中')
      return
    }

    // 从原位置移除
    removeNode(dragTableData.value, dragId)

    // 插入到新位置
    if (position === 'inside') {
      // 放入部门内部
      const targetNode = findNodeById(dragTableData.value, targetId)
      if (targetNode) {
        dragNode.parentId = targetId
        if (!targetNode.children) {
          targetNode.children = []
        }
        targetNode.children.push(dragNode)
        recalculateSort(targetNode.children)
      }
    } else {
      // 放在目标前面或后面
      insertNode(dragTableData.value, dragNode, targetId, position)
    }
  }

  /**
   * 查找节点
   */
  const findNodeById = (tree: DeptVO[], id: number): DeptVO | null => {
    for (const node of tree) {
      if (node.id === id) return node
      if (node.children?.length) {
        const found = findNodeById(node.children, id)
        if (found) return found
      }
    }
    return null
  }

  /**
   * 检查是否是后代节点
   */
  const isDescendant = (node: DeptVO, targetId: number): boolean => {
    if (!node.children?.length) return false
    for (const child of node.children) {
      if (child.id === targetId) return true
      if (isDescendant(child, targetId)) return true
    }
    return false
  }

  /**
   * 从树中移除节点
   */
  const removeNode = (tree: DeptVO[], id: number): boolean => {
    for (let i = 0; i < tree.length; i++) {
      if (tree[i].id === id) {
        tree.splice(i, 1)
        return true
      }
      if (tree[i].children?.length) {
        if (removeNode(tree[i].children!, id)) {
          return true
        }
      }
    }
    return false
  }

  /**
   * 插入节点到指定位置
   */
  const insertNode = (
    tree: DeptVO[],
    node: DeptVO,
    targetId: number,
    position: 'before' | 'after'
  ): boolean => {
    for (let i = 0; i < tree.length; i++) {
      if (tree[i].id === targetId) {
        const insertIndex = position === 'before' ? i : i + 1
        node.parentId = tree[i].parentId
        tree.splice(insertIndex, 0, node)
        recalculateSort(tree)
        return true
      }
      if (tree[i].children?.length) {
        if (insertNode(tree[i].children!, node, targetId, position)) {
          return true
        }
      }
    }
    return false
  }

  /**
   * 重新计算排序号
   */
  const recalculateSort = (items: DeptVO[]): void => {
    items.forEach((item, index) => {
      item.sort = index + 1
    })
  }

  /**
   * 收集所有排序数据
   */
  const collectSortData = (tree: DeptVO[]): Api.System.DeptSortItem[] => {
    const result: Api.System.DeptSortItem[] = []
    
    const traverse = (items: DeptVO[]) => {
      items.forEach((item, index) => {
        result.push({
          id: item.id,
          parentId: item.parentId,
          sort: index + 1
        })
        
        if (item.children?.length) {
          traverse(item.children)
        }
      })
    }
    
    traverse(tree)
    return result
  }

  /**
   * 保存排序更改
   */
  const saveSortChanges = async (): Promise<void> => {
    try {
      loading.value = true
      const items = collectSortData(dragTableData.value)
      await fetchUpdateDeptSort({ items })
      await getDeptList()
      isDragMode.value = false
      dragTableData.value = []
      // 重置展开状态
      isExpanded.value = false
    } catch (error) {
      console.error('保存排序失败:', error)
    } finally {
      loading.value = false
    }
  }
</script>

<style scoped>
  .drag-tip {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    border-radius: 6px;
    font-size: 14px;
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    border: 1px solid var(--el-color-primary-light-7);
  }

  .drag-tree-wrapper {
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    overflow: hidden;
  }

  .drag-tree-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: var(--el-fill-color-light);
    border-bottom: 1px solid var(--el-border-color);
    font-weight: 500;
    font-size: 14px;
    color: var(--el-text-color-primary);
  }

  .drag-tree-header .col-name {
    flex: 1;
    min-width: 200px;
  }

  .drag-tree-header .col-leader {
    width: 100px;
    text-align: center;
  }

  .drag-tree-header .col-sort {
    width: 60px;
    text-align: center;
  }

  .drag-tree-header .col-status {
    width: 80px;
    text-align: center;
  }

  .drag-tree-body {
    max-height: 800px;
    overflow-y: auto;
  }
</style>
