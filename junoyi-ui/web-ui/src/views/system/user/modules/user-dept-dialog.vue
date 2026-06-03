<!-- 用户分配部门弹窗 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="分配部门"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <!-- 提示信息 -->
    <div class="tip-box mb-5" :style="tipBoxStyle">
      <ArtSvgIcon icon="ri:information-line" class="mr-2 flex-shrink-0" :style="{ color: primaryColor }" />
      <span>操作方式：拖拽左侧部门到右侧完成绑定，点击右侧部门的 × 按钮或拖回左侧完成解绑</span>
    </div>

    <div class="flex gap-4 h-80">
      <!-- 左侧：可分配部门树 -->
      <div class="flex-1 flex flex-col border border-gray-200 dark:border-gray-600 rounded-lg overflow-hidden">
        <div class="px-3 py-2 bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-600">
          <span class="font-medium text-sm">可分配部门</span>
          <span class="text-gray-400 text-xs ml-2">({{ availableDeptCount }})</span>
        </div>
        <div class="px-2 py-2 border-b border-gray-200 dark:border-gray-600">
          <ElInput
            v-model="deptSearchKey"
            placeholder="搜索部门"
            clearable
            size="small"
            :prefix-icon="Search"
          />
        </div>
        <div
          ref="availableListRef"
          class="flex-1 overflow-auto p-2 drop-zone"
          @dragover.prevent
          @drop="handleDropToAvailable"
        >
          <ElTree
            ref="deptTreeRef"
            :data="availableDeptTree"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            default-expand-all
            :filter-node-method="filterDeptNode"
            :draggable="true"
            :allow-drop="() => false"
            @node-drag-start="handleTreeDragStart"
            @node-drag-end="handleTreeDragEnd"
          >
            <template #default="{ node, data }">
              <span class="dept-tree-node">
                <ArtSvgIcon icon="ri:folder-line" class="mr-1.5 text-gray-500" />
                <span>{{ node.label }}</span>
              </span>
            </template>
          </ElTree>
          <div v-if="availableDeptTree.length === 0" class="text-center text-gray-400 py-8">
            暂无可分配部门
          </div>
        </div>
      </div>

      <!-- 右侧：已绑定部门列表 -->
      <div class="flex-1 flex flex-col border border-gray-200 dark:border-gray-600 rounded-lg overflow-hidden">
        <div class="px-3 py-2 border-b border-gray-200 dark:border-gray-600" :style="{ backgroundColor: primaryColorLight }">
          <span class="font-medium text-sm" :style="{ color: primaryColor }">已绑定部门</span>
          <span class="text-xs ml-2" :style="{ color: primaryColor, opacity: 0.7 }">({{ boundDepts.length }})</span>
        </div>
        <div
          ref="boundListRef"
          class="flex-1 overflow-auto p-2 drop-zone"
          @dragover.prevent
          @drop="handleDropToBound"
        >
          <div
            v-for="dept in boundDepts"
            :key="dept.id"
            class="dept-item"
            draggable="true"
            @dragstart="handleDeptDragStart($event, dept)"
            @dragend="handleDragEnd"
            @touchstart="onTouchStart($event, dept)"
            @touchmove="onTouchMove"
            @touchend="onTouchEnd"
          >
            <ArtSvgIcon icon="ri:building-2-line" class="mr-2" :style="{ color: primaryColor }" />
            <span class="flex-1 truncate">{{ dept.name }}</span>
            <ElButton 
              link 
              size="small" 
              class="remove-btn"
              @click.stop="removeDept(dept)"
            >
              <ArtSvgIcon icon="ri:close-line" class="text-gray-400 hover:text-red-500" />
            </ElButton>
          </div>
          <div v-if="boundDepts.length === 0" class="text-center text-gray-400 py-8">
            暂无已绑定部门
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="submitting" @click="handleSubmit">确定</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import { Search } from '@element-plus/icons-vue'
  import { ElMessage, ElTree } from 'element-plus'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { fetchGetDeptTree } from '@/api/system/department'
  import { fetchGetUserDepts, fetchUpdateUserDepts } from '@/api/system/user'
  import { useSettingStore } from '@/store/modules/setting'

  interface DeptItem {
    id: number
    name: string
    parentId?: number
    children?: DeptItem[]
  }

  interface Props {
    visible: boolean
    userId: number | null
    userName?: string
  }

  const props = defineProps<Props>()
  const emit = defineEmits<{
    'update:visible': [value: boolean]
    'submit': []
  }>()

  const settingStore = useSettingStore()

  // 主题色
  const primaryColor = computed(() => settingStore.systemThemeColor || '#409eff')
  const primaryColorLight = computed(() => {
    const hex = primaryColor.value.replace('#', '')
    const r = parseInt(hex.substring(0, 2), 16)
    const g = parseInt(hex.substring(2, 4), 16)
    const b = parseInt(hex.substring(4, 6), 16)
    return `rgba(${r}, ${g}, ${b}, 0.1)`
  })

  // 提示框样式
  const tipBoxStyle = computed(() => ({
    backgroundColor: primaryColorLight.value,
    borderColor: primaryColor.value,
    color: primaryColor.value
  }))

  const dialogVisible = computed({
    get: () => props.visible,
    set: (val) => emit('update:visible', val)
  })

  // 部门树
  const deptTreeRef = ref<InstanceType<typeof ElTree>>()
  const allDeptTree = ref<DeptItem[]>([])
  const deptSearchKey = ref('')
  // 已绑定部门ID列表
  const boundDeptIds = ref<number[]>([])
  // 提交中
  const submitting = ref(false)

  // 扁平化部门列表（用于查找）
  const flatDeptList = computed(() => {
    const list: DeptItem[] = []
    const flatten = (items: DeptItem[]) => {
      items.forEach(item => {
        list.push({ id: item.id, name: item.name, parentId: item.parentId })
        if (item.children?.length) {
          flatten(item.children)
        }
      })
    }
    flatten(allDeptTree.value)
    return list
  })

  // 可分配部门树（过滤掉已绑定的）
  const availableDeptTree = computed(() => {
    const filterTree = (items: DeptItem[]): DeptItem[] => {
      return items
        .filter(item => !boundDeptIds.value.includes(item.id))
        .map(item => ({
          ...item,
          children: item.children ? filterTree(item.children) : []
        }))
    }
    return filterTree(allDeptTree.value)
  })

  // 可分配部门数量
  const availableDeptCount = computed(() => {
    return flatDeptList.value.filter(d => !boundDeptIds.value.includes(d.id)).length
  })

  // 已绑定部门
  const boundDepts = computed(() => {
    return flatDeptList.value.filter(d => boundDeptIds.value.includes(d.id))
  })

  // 放置区域引用
  const availableListRef = ref<HTMLElement>()
  const boundListRef = ref<HTMLElement>()

  // 拖拽相关
  let draggedDept: DeptItem | null = null
  let dragSource: 'tree' | 'bound' | null = null
  let dragClone: HTMLElement | null = null
  let touchStartX = 0
  let touchStartY = 0

  // 树节点拖拽开始
  const handleTreeDragStart = (node: any) => {
    draggedDept = { id: node.data.id, name: node.data.name }
    dragSource = 'tree'
  }

  const handleTreeDragEnd = () => {
    // 延迟清除，让 drop 事件先处理
    setTimeout(() => {
      draggedDept = null
      dragSource = null
    }, 100)
  }

  // 已绑定部门拖拽开始
  const handleDeptDragStart = (event: DragEvent, dept: DeptItem) => {
    draggedDept = dept
    dragSource = 'bound'
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move'
    }
  }

  const handleDragEnd = () => {
    draggedDept = null
    dragSource = null
  }

  // 拖到可分配列表（解绑）
  const handleDropToAvailable = () => {
    if (draggedDept && dragSource === 'bound') {
      boundDeptIds.value = boundDeptIds.value.filter(id => id !== draggedDept!.id)
    }
  }

  // 拖到已绑定列表（绑定）
  const handleDropToBound = () => {
    if (draggedDept && dragSource === 'tree') {
      if (!boundDeptIds.value.includes(draggedDept.id)) {
        boundDeptIds.value.push(draggedDept.id)
      }
    }
  }

  // 移动端触摸拖拽（已绑定部门）
  const onTouchStart = (event: TouchEvent, dept: DeptItem) => {
    if (event.touches.length !== 1) return
    
    const touch = event.touches[0]
    touchStartX = touch.clientX
    touchStartY = touch.clientY
    
    draggedDept = dept
    dragSource = 'bound'
    
    const target = event.currentTarget as HTMLElement
    
    // 创建拖拽克隆
    dragClone = target.cloneNode(true) as HTMLElement
    dragClone.style.position = 'fixed'
    dragClone.style.zIndex = '9999'
    dragClone.style.pointerEvents = 'none'
    dragClone.style.opacity = '0.9'
    dragClone.style.transform = 'scale(1.02)'
    dragClone.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)'
    dragClone.style.width = `${target.offsetWidth}px`
    dragClone.style.left = `${touch.clientX - target.offsetWidth / 2}px`
    dragClone.style.top = `${touch.clientY - 20}px`
    document.body.appendChild(dragClone)
    
    target.style.opacity = '0.4'
    
    event.preventDefault()
  }

  const onTouchMove = (event: TouchEvent) => {
    if (!draggedDept || event.touches.length !== 1) return
    
    const touch = event.touches[0]
    touchStartX = touch.clientX
    touchStartY = touch.clientY
    
    if (dragClone) {
      dragClone.style.left = `${touch.clientX - dragClone.offsetWidth / 2}px`
      dragClone.style.top = `${touch.clientY - 20}px`
    }
    
    // 高亮放置区域
    const isOverAvailable = isPointInElement(touch.clientX, touch.clientY, availableListRef.value)
    const isOverBound = isPointInElement(touch.clientX, touch.clientY, boundListRef.value)
    
    availableListRef.value?.classList.toggle('drop-zone-active', isOverAvailable && dragSource === 'bound')
    boundListRef.value?.classList.toggle('drop-zone-active', isOverBound && dragSource === 'tree')
    
    event.preventDefault()
  }

  const onTouchEnd = (event: TouchEvent) => {
    if (!draggedDept) return
    
    const target = event.currentTarget as HTMLElement
    target.style.opacity = ''
    
    // 移除克隆
    if (dragClone) {
      dragClone.remove()
      dragClone = null
    }
    
    // 移除高亮
    availableListRef.value?.classList.remove('drop-zone-active')
    boundListRef.value?.classList.remove('drop-zone-active')
    
    // 检查放置位置
    const touch = event.changedTouches[0]
    if (touch) {
      const isOverAvailable = isPointInElement(touch.clientX, touch.clientY, availableListRef.value)
      
      if (isOverAvailable && dragSource === 'bound') {
        boundDeptIds.value = boundDeptIds.value.filter(id => id !== draggedDept!.id)
      }
    }
    
    draggedDept = null
    dragSource = null
  }

  const isPointInElement = (x: number, y: number, element?: HTMLElement): boolean => {
    if (!element) return false
    const rect = element.getBoundingClientRect()
    return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
  }

  // 移除部门（点击删除按钮）
  const removeDept = (dept: DeptItem) => {
    boundDeptIds.value = boundDeptIds.value.filter(id => id !== dept.id)
  }

  // 部门树过滤
  const filterDeptNode = (value: string, data: any) => {
    if (!value) return true
    return data.name?.toLowerCase().includes(value.toLowerCase())
  }

  // 监听搜索关键字
  watch(deptSearchKey, (val) => {
    deptTreeRef.value?.filter(val)
  })

  // 加载数据
  const loadData = async () => {
    if (!props.userId) return
    
    try {
      // 获取所有部门树
      const deptTree = await fetchGetDeptTree()
      allDeptTree.value = deptTree || []

      // 获取用户已绑定的部门
      const userDepts = await fetchGetUserDepts(props.userId)
      boundDeptIds.value = (userDepts || []).map(d => d.id)
    } catch (error) {
      console.error('加载部门数据失败:', error)
    }
  }

  // 提交
  const handleSubmit = async () => {
    if (!props.userId) return

    submitting.value = true
    try {
      await fetchUpdateUserDepts(props.userId, boundDeptIds.value)
      ElMessage.success('分配部门成功')
      emit('submit')
      handleClose()
    } catch (error) {
      console.error('分配部门失败:', error)
    } finally {
      submitting.value = false
    }
  }

  const handleClose = () => {
    dialogVisible.value = false
    boundDeptIds.value = []
    deptSearchKey.value = ''
  }

  watch(() => props.visible, (val) => {
    if (val) {
      loadData()
    }
  })
</script>

<style scoped>
  .tip-box {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    border-radius: 8px;
    border: 1px solid;
    font-size: 14px;
  }

  .dept-tree-node {
    display: flex;
    align-items: center;
    font-size: 14px;
  }

  .dept-item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    margin-bottom: 6px;
    background: v-bind(primaryColorLight);
    border-radius: 6px;
    cursor: grab;
    transition: all 0.2s;
    user-select: none;
    touch-action: none;
  }

  .dept-item:hover {
    filter: brightness(0.95);
  }

  .dept-item:hover .remove-btn {
    opacity: 1;
  }

  .remove-btn {
    opacity: 0;
    transition: opacity 0.2s;
    padding: 2px;
  }

  .dept-item:active {
    cursor: grabbing;
  }

  :deep(.el-tree-node__content) {
    cursor: grab;
  }

  :deep(.el-tree-node__content:active) {
    cursor: grabbing;
  }

  /* 移动端始终显示删除按钮 */
  @media (max-width: 768px) {
    .remove-btn {
      opacity: 1;
    }
  }

  /* 放置区域激活状态 */
  .drop-zone {
    transition: background-color 0.2s, border-color 0.2s;
  }

  .drop-zone-active {
    background-color: var(--el-color-primary-light-9) !important;
    border: 2px dashed var(--el-color-primary) !important;
  }
</style>
