<!-- 菜单管理页面 -->
<template>
  <div class="menu-page art-full-height">
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
        :showZebra="false"
        :loading="loading"
        v-model:columns="columnChecks"
        @refresh="handleRefresh"
      >
        <template #left>
          <ElButton v-permission="'system.ui.menu.button.add'" @click="handleAddMenu" v-ripple>
            添加菜单
          </ElButton>
          <ElButton @click="toggleExpand" v-ripple>
            {{ isExpanded ? '收起' : '展开' }}
          </ElButton>
          <ElButton
              v-permission="'system.ui.menu.button.edit'"
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
        拖拽模式：拖动菜单可调整排序，拖到目录上可移动到该目录下。完成后点击「保存排序」。
      </div>

      <!-- 普通模式表格 -->
      <ArtTable
        v-if="!isDragMode"
        ref="tableRef"
        rowKey="id"
        :loading="loading"
        :columns="columns"
        :data="filteredTableData"
        :stripe="false"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
      />

      <!-- 拖拽模式 - 树形列表 -->
      <div v-else class="drag-tree-wrapper">
        <div class="drag-tree-header">
          <span class="col-name">菜单名称</span>
          <span class="col-type">类型</span>
          <span class="col-sort">排序</span>
          <span class="col-path">路由</span>
        </div>
        <div class="drag-tree-body" ref="dragTreeRef">
          <DragTreeNode
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

      <!-- 菜单弹窗 -->
      <MenuDialog
        v-model:visible="dialogVisible"
        :editData="editData"
        :defaultParentId="parentId"
        @success="getMenuList"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { formatMenuTitle } from '@/utils/router'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { useTableColumns } from '@/hooks/core/useTableColumns'
  import { usePermission } from '@/hooks/core/usePermission'
  import { useTreeDrag } from '@/hooks/core/useTouchDrag'
  import MenuDialog from './modules/menu-dialog.vue'
  import DragTreeNode from './modules/drag-tree-node.vue'
  import { ElTag, ElMessageBox } from 'element-plus'
  import { fetchGetMenuTree, fetchDeleteMenu, fetchUpdateMenuSort } from '@/api/system/menu'

  defineOptions({ name: 'Menus' })

  // 权限
  const { hasPermission } = usePermission()

  // 状态管理
  const loading = ref(false)
  const isExpanded = ref(false)
  const tableRef = ref()
  const dragTreeRef = ref()

  // 拖拽模式
  const isDragMode = ref(false)
  const dragTableData = ref<Api.System.MenuVO[]>([])

  // 使用树形拖拽 hook
  const treeDrag = useTreeDrag({
    canBeParent: (nodeId, element) => {
      // 只有目录（menuType === 0）可以作为父级
      return element.classList.contains('is-directory')
    },
    onDrop: handleDrop
  })

  // 弹窗相关
  const dialogVisible = ref(false)
  const editData = ref<Api.System.MenuVO | null>(null)
  const parentId = ref<number>(0)

  // 搜索相关
  const initialSearchState = {
    name: '',
    route: '',
    status: null as number | null
  }

  const formFilters = reactive({ ...initialSearchState })
  const appliedFilters = reactive({ ...initialSearchState })

  // 状态选项
  const statusOptions = [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 }
  ]

  const formItems = computed(() => [
    {
      label: '菜单名称',
      key: 'name',
      type: 'input',
      props: { clearable: true }
    },
    {
      label: '路由地址',
      key: 'route',
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
  const tableData = ref<Api.System.MenuVO[]>([])

  onMounted(() => {
    getMenuList()
  })

  /**
   * 获取菜单列表数据
   */
  const getMenuList = async (): Promise<void> => {
    loading.value = true
    try {
      const list = await fetchGetMenuTree()
      tableData.value = list || []
    } catch (error) {
      console.error('获取菜单失败:', error)
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取菜单类型标签颜色
   */
  const getMenuTypeTag = (row: Api.System.MenuVO): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    if (row.menuType === 0) return 'info'
    if (row.isIframe === 1) return 'success'
    if (row.link) return 'warning'
    return 'primary'
  }

  /**
   * 获取菜单类型文本
   */
  const getMenuTypeText = (row: Api.System.MenuVO): string => {
    if (row.menuType === 0) return '目录'
    if (row.isIframe === 1) return '内嵌'
    if (row.link && row.menuType === 1) return '外链'
    return '菜单'
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
      prop: 'title',
      label: '菜单名称',
      minWidth: 180,
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => formatMenuTitle(row.title) || '-'
    },
    {
      prop: 'icon',
      label: '菜单图标',
      width: 90,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => {
        if (!row.icon) return '-'
        return h(ArtSvgIcon, { icon: row.icon, style: 'font-size: 18px' })
      }
    },
    {
      prop: 'menuType',
      label: '菜单类型',
      width: 90,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => {
        return h(ElTag, { type: getMenuTypeTag(row), size: 'small' }, () => getMenuTypeText(row))
      }
    },
    {
      prop: 'sort',
      label: '排序',
      width: 60,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => row.sort ?? 0
    },
    {
      prop: 'path',
      label: '路由',
      minWidth: 160,
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => {
        if (row.menuType === 2) return '-'
        return row.link || row.path || '-'
      }
    },
    {
      prop: 'component',
      label: '组件',
      minWidth: 140,
      formatter: (row: Api.System.MenuVO) => {
        return row.component || '-'
      }
    },
    {
      prop: 'permission',
      label: '权限标识符',
      minWidth: 140,
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => row.permission || '-'
    },
    {
      prop: 'isHide',
      label: '显示',
      width: 70,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => {
        const isHide = row.isHide ?? 1
        return h(ElTag, { type: isHide === 1 ? 'danger' : 'success', size: 'small' }, () => isHide === 1 ? '隐藏' : '显示')
      }
    },
    {
      prop: 'status',
      label: '状态',
      width: 70,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => {
        const status = row.status ?? 1
        return h(ElTag, { type: status === 1 ? 'success' : 'danger', size: 'small' }, () => status === 1 ? '启用' : '禁用')
      }
    },
    {
      prop: 'createTime',
      label: '创建时间',
      width: 170,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => formatTime(row.createTime)
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      width: 170,
      align: 'center',
      headerAlign: 'center',
      formatter: (row: Api.System.MenuVO) => formatTime(row.updateTime)
    },
    {
      prop: 'operation',
      label: '操作',
      width: 160,
      align: 'center',
      headerAlign: 'center',
      fixed: 'right',
      formatter: (row: Api.System.MenuVO) => {
        const buttons = []
        if (row.menuType === 0 && hasPermission('system.ui.menu.button.add')) {
          buttons.push(
            h(ArtButtonTable, {
              type: 'add',
              onClick: () => handleAddChildMenu(row)
            })
          )
        }
        if (hasPermission('system.ui.menu.button.edit')) {
          buttons.push(
            h(ArtButtonTable, {
              type: 'edit',
              onClick: () => handleEditMenu(row)
            })
          )
        }
        if (hasPermission('system.ui.menu.button.delete')) {
          buttons.push(
            h(ArtButtonTable, {
              type: 'delete',
              onClick: () => handleDeleteMenu(row)
            })
          )
        }
        return buttons.length ? h('div', { style: 'text-align: center' }, buttons) : '-'
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
   * 搜索菜单
   */
  const searchMenu = (items: Api.System.MenuVO[]): Api.System.MenuVO[] => {
    const results: Api.System.MenuVO[] = []
    for (const item of items) {
      const searchName = appliedFilters.name?.toLowerCase().trim() || ''
      const searchRoute = appliedFilters.route?.toLowerCase().trim() || ''
      const searchStatus = appliedFilters.status
      const menuTitle = formatMenuTitle(item.title || '').toLowerCase()
      const menuPath = (item.path || '').toLowerCase()
      const nameMatch = !searchName || menuTitle.includes(searchName)
      const routeMatch = !searchRoute || menuPath.includes(searchRoute)
      const statusMatch = searchStatus === null || item.status === searchStatus

      if (item.children?.length) {
        const matchedChildren = searchMenu(item.children)
        if (matchedChildren.length > 0) {
          const clonedItem = deepClone(item)
          clonedItem.children = matchedChildren
          results.push(clonedItem)
          continue
        }
      }

      if (nameMatch && routeMatch && statusMatch) {
        results.push(deepClone(item))
      }
    }
    return results
  }

  // 过滤后的表格数据
  const filteredTableData = computed(() => {
    return searchMenu(tableData.value)
  })

  /**
   * 重置搜索条件
   */
  const handleReset = (): void => {
    Object.assign(formFilters, { ...initialSearchState })
    Object.assign(appliedFilters, { ...initialSearchState })
    getMenuList()
  }

  /**
   * 执行搜索
   */
  const handleSearch = (): void => {
    Object.assign(appliedFilters, { ...formFilters })
  }

  /**
   * 刷新菜单列表
   */
  const handleRefresh = (): void => {
    getMenuList()
  }

  /**
   * 添加菜单
   */
  const handleAddMenu = (): void => {
    editData.value = null
    parentId.value = 0
    dialogVisible.value = true
  }

  /**
   * 添加子菜单（在目录下添加）
   */
  const handleAddChildMenu = (row: Api.System.MenuVO): void => {
    editData.value = null
    parentId.value = row.id
    dialogVisible.value = true
  }

  /**
   * 编辑菜单
   */
  const handleEditMenu = (row: Api.System.MenuVO): void => {
    editData.value = row
    parentId.value = 0
    dialogVisible.value = true
  }

  /**
   * 删除菜单
   */
  const handleDeleteMenu = async (row: Api.System.MenuVO): Promise<void> => {
    const title = formatMenuTitle(row.title)
    const hasChildren = row.children && row.children.length > 0
    
    if (hasChildren) {
      ElMessageBox.alert(`菜单「${title}」下有子菜单，请先删除子菜单后再删除`, '提示', {
        confirmButtonText: '知道了',
        type: 'warning'
      })
      return
    }
    
    try {
      await ElMessageBox.confirm(`确定要删除菜单「${title}」吗？删除后无法恢复`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await fetchDeleteMenu(row.id)
      getMenuList()
    } catch (error) {
      // 用户取消操作
    }
  }

  /**
   * 切换展开/收起所有菜单
   */
  const toggleExpand = (): void => {
    isExpanded.value = !isExpanded.value
    nextTick(() => {
      if (tableRef.value?.elTableRef && filteredTableData.value) {
        const processRows = (rows: Api.System.MenuVO[]) => {
          rows.forEach((row) => {
            if (row.children?.length) {
              tableRef.value.elTableRef.toggleRowExpansion(row, isExpanded.value)
              processRows(row.children)
            }
          })
        }
        processRows(filteredTableData.value)
      }
    })
  }

  // ==================== 拖拽排序相关 ====================

  /**
   * 进入拖拽模式
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
      // 放入目录内部
      const targetNode = findNodeById(dragTableData.value, targetId)
      if (targetNode) {
        if (targetNode.menuType !== 0) {
          ElMessage.warning('只能拖入目录中')
          // 恢复原位置
          dragTableData.value = deepClone(tableData.value)
          return
        }
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
  const findNodeById = (tree: Api.System.MenuVO[], id: number): Api.System.MenuVO | null => {
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
  const isDescendant = (node: Api.System.MenuVO, targetId: number): boolean => {
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
  const removeNode = (tree: Api.System.MenuVO[], id: number): boolean => {
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
    tree: Api.System.MenuVO[],
    node: Api.System.MenuVO,
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
  const recalculateSort = (items: Api.System.MenuVO[]): void => {
    items.forEach((item, index) => {
      item.sort = index + 1
    })
  }

  /**
   * 收集所有排序数据（包含层级变化时的路径修正）
   */
  const collectSortData = (tree: Api.System.MenuVO[]): Api.System.MenuSortItem[] => {
    const result: Api.System.MenuSortItem[] = []
    
    // 构建原始数据的 parentId 映射，用于检测层级变化
    const buildOriginalParentMap = (items: Api.System.MenuVO[], map: Map<number, number>) => {
      items.forEach(item => {
        map.set(item.id, item.parentId)
        if (item.children?.length) {
          buildOriginalParentMap(item.children, map)
        }
      })
    }
    const originalParentMap = new Map<number, number>()
    buildOriginalParentMap(tableData.value, originalParentMap)
    
    /**
     * 修正路径格式
     * - 一级菜单/目录：path 以 / 开头
     * - 二级及以下：path 不能以 / 开头
     */
    const fixPath = (path: string, isTopLevel: boolean): string => {
      if (!path) return path
      if (isTopLevel) {
        // 一级：确保以 / 开头
        return path.startsWith('/') ? path : `/${path}`
      } else {
        // 二级及以下：移除开头的 /
        return path.startsWith('/') ? path.slice(1) : path
      }
    }
    
    /**
     * 修正组件路径
     * - 一级目录：component 为 /index/index
     * - 二级及以下目录：component 为空
     * - 菜单：保持原有 component（如果是 /index/index 则需要清空）
     */
    const fixComponent = (item: Api.System.MenuVO, isTopLevel: boolean): string => {
      const isDirectory = item.menuType === 0
      
      if (isDirectory) {
        return isTopLevel ? '/index/index' : ''
      }
      
      // 菜单类型
      if (isTopLevel) {
        // 一级菜单必须有 component，如果没有则保持原样
        return item.component || ''
      } else {
        // 二级及以下菜单，如果是 /index/index 则清空
        return item.component === '/index/index' ? '' : (item.component || '')
      }
    }
    
    const traverse = (items: Api.System.MenuVO[], isTopLevel: boolean) => {
      items.forEach((item, index) => {
        const originalParentId = originalParentMap.get(item.id)
        const parentChanged = originalParentId !== item.parentId
        
        const sortItem: Api.System.MenuSortItem = {
          id: item.id,
          parentId: item.parentId,
          sort: index + 1
        }
        
        // 如果层级发生变化，需要修正 path 和 component
        if (parentChanged) {
          sortItem.path = fixPath(item.path, isTopLevel)
          sortItem.component = fixComponent(item, isTopLevel)
        }
        
        result.push(sortItem)
        
        if (item.children?.length) {
          traverse(item.children, false)
        }
      })
    }
    
    traverse(tree, true)
    return result
  }

  /**
   * 保存排序更改
   */
  const saveSortChanges = async (): Promise<void> => {
    try {
      loading.value = true
      const items = collectSortData(dragTableData.value)
      await fetchUpdateMenuSort({ items })
      await getMenuList()
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

  .drag-tree-header .col-type {
    width: 80px;
    text-align: center;
  }

  .drag-tree-header .col-sort {
    width: 60px;
    text-align: center;
  }

  .drag-tree-header .col-path {
    width: 200px;
  }

  .drag-tree-body {
    max-height: 800px;
    overflow-y: auto;
  }
</style>
