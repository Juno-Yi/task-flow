<!-- 用户独立权限设置弹窗 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="设置独立权限"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <!-- 提示信息 -->
    <div class="tip-box mb-4" :style="tipBoxStyle">
      <ArtSvgIcon icon="ri:information-line" class="mr-2 flex-shrink-0" :style="{ color: primaryColor }" />
      <span>操作方式：拖拽或双击左侧权限完成添加，点击右侧 × 按钮或双击/拖回左侧完成移除</span>
    </div>

    <!-- 穿梭框 -->
    <div class="permission-transfer">
      <!-- 左侧：权限池 -->
      <div class="transfer-panel">
        <div class="panel-header">
          <span class="font-medium text-sm">权限池</span>
          <span class="text-gray-400 text-xs ml-2">({{ filteredPoolList.length }})</span>
        </div>
        <div class="panel-search">
          <ElInput
            v-model="poolSearch"
            placeholder="搜索权限..."
            clearable
            size="small"
          >
            <template #prefix>
              <ArtSvgIcon icon="ri:search-line" />
            </template>
          </ElInput>
        </div>
        <div 
          ref="poolListRef" 
          class="panel-content drop-zone"
          @dragover.prevent 
          @drop="handleDropToPool"
        >
          <div v-if="poolLoading" class="text-center text-gray-400 py-8">
            <ElIcon class="is-loading"><Loading /></ElIcon>
            <span class="ml-2">加载中...</span>
          </div>
          <template v-else-if="filteredPoolList.length > 0">
            <div
              v-for="item in filteredPoolList"
              :key="item.id"
              class="perm-item"
              draggable="true"
              @dragstart="handleDragStart($event, item.permission, 'pool')"
              @dragend="handleDragEnd"
              @touchstart="onTouchStart($event, item.permission, 'pool')"
              @touchmove="onTouchMove"
              @touchend="onTouchEnd"
              @dblclick="addToSelected(item.permission)"
            >
              <ArtSvgIcon :icon="getPermIcon(item.permission)" class="mr-2" :style="{ color: getPermColor(item.permission) }" />
              <div class="flex-1 min-w-0">
                <div class="text-sm truncate">{{ item.permission }}</div>
                <div class="text-xs text-gray-400 truncate" v-if="item.description">{{ item.description }}</div>
              </div>
              <ElTag :type="getPermType(item.permission)" size="small" effect="light">
                {{ getPermLabel(item.permission) }}
              </ElTag>
            </div>
          </template>
          <div v-else class="text-center text-gray-400 py-8">
            {{ poolSearch ? '无匹配权限' : '暂无可用权限' }}
          </div>
        </div>
      </div>

      <!-- 右侧：已选权限 -->
      <div class="transfer-panel">
        <div class="panel-header panel-header-selected" :style="{ backgroundColor: primaryColorLight }">
          <span class="font-medium text-sm" :style="{ color: primaryColor }">已选独立权限</span>
          <span class="text-xs ml-2" :style="{ color: primaryColor, opacity: 0.7 }">({{ selectedPermissions.length }})</span>
        </div>
        <div class="panel-search">
          <ElInput
            v-model="selectedSearch"
            placeholder="搜索已选..."
            clearable
            size="small"
          >
            <template #prefix>
              <ArtSvgIcon icon="ri:search-line" />
            </template>
          </ElInput>
        </div>
        <div 
          ref="selectedListRef" 
          class="panel-content drop-zone"
          @dragover.prevent 
          @drop="handleDropToSelected"
        >
          <div v-if="loading" class="text-center text-gray-400 py-8">
            <ElIcon class="is-loading"><Loading /></ElIcon>
            <span class="ml-2">加载中...</span>
          </div>
          <template v-else-if="filteredSelectedList.length > 0">
            <div
              v-for="perm in filteredSelectedList"
              :key="perm"
              class="perm-item perm-item-selected"
              draggable="true"
              @dragstart="handleDragStart($event, perm, 'selected')"
              @dragend="handleDragEnd"
              @touchstart="onTouchStart($event, perm, 'selected')"
              @touchmove="onTouchMove"
              @touchend="onTouchEnd"
              @dblclick="removeFromSelected(perm)"
            >
              <ArtSvgIcon :icon="getPermIcon(perm)" class="mr-2" :style="{ color: getPermColor(perm) }" />
              <div class="flex-1 min-w-0">
                <div class="text-sm truncate">{{ perm }}</div>
                <div class="text-xs text-gray-400 truncate" v-if="getPermDescription(perm)">{{ getPermDescription(perm) }}</div>
              </div>
              <ElTag :type="getPermType(perm)" size="small" effect="light" class="mr-2">
                {{ getPermLabel(perm) }}
              </ElTag>
              <ElButton link size="small" class="remove-btn" @click.stop="removeFromSelected(perm)">
                <ArtSvgIcon icon="ri:close-line" class="text-gray-400 hover:text-red-500" />
              </ElButton>
            </div>
          </template>
          <div v-else class="text-center text-gray-400 py-8">
            {{ selectedSearch ? '无匹配权限' : '拖拽或双击添加权限' }}
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
  import { ElMessage } from 'element-plus'
  import { Loading } from '@element-plus/icons-vue'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { 
    fetchGetUserPermissions, 
    fetchAddUserPermissions,
    fetchDeleteUserPermission
  } from '@/api/system/user'
  import { fetchGetPermissionPoolOptions } from '@/api/system/permissionPool'
  import { useSettingStore } from '@/store/modules/setting'

  type SysUserPermVO = Api.System.SysUserPermVO
  type PermissionPoolVO = Api.System.PermissionPoolVO

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

  const tipBoxStyle = computed(() => ({
    backgroundColor: primaryColorLight.value,
    borderColor: primaryColor.value,
    color: primaryColor.value
  }))

  const dialogVisible = computed({
    get: () => props.visible,
    set: (val) => emit('update:visible', val)
  })

  // 状态
  const loading = ref(false)
  const poolLoading = ref(false)
  const submitting = ref(false)
  const poolList = ref<PermissionPoolVO[]>([])
  const userPermList = ref<SysUserPermVO[]>([])  // 原始用户权限列表（带id）
  const selectedPermissions = ref<string[]>([])  // 当前选中的权限标识
  const initialPermissions = ref<string[]>([])   // 初始权限（用于对比变化）
  const poolSearch = ref('')
  const selectedSearch = ref('')
  const poolListRef = ref<HTMLElement>()
  const selectedListRef = ref<HTMLElement>()

  // 拖拽相关
  let draggedPermission: string | null = null
  let dragSource: 'pool' | 'selected' | null = null
  let dragClone: HTMLElement | null = null

  // 过滤后的权限池列表（排除已选）
  const filteredPoolList = computed(() => {
    let list = poolList.value.filter(item => 
      item.status === 1 && !selectedPermissions.value.includes(item.permission)
    )
    if (poolSearch.value) {
      const keyword = poolSearch.value.toLowerCase()
      list = list.filter(item => 
        item.permission.toLowerCase().includes(keyword) ||
        (item.description && item.description.toLowerCase().includes(keyword))
      )
    }
    return list
  })

  // 过滤后的已选列表
  const filteredSelectedList = computed(() => {
    if (!selectedSearch.value) return selectedPermissions.value
    const keyword = selectedSearch.value.toLowerCase()
    return selectedPermissions.value.filter(perm => perm.toLowerCase().includes(keyword))
  })

  /**
   * 获取权限描述
   */
  const getPermDescription = (perm: string): string => {
    const item = poolList.value.find(p => p.permission === perm)
    return item?.description || ''
  }

  /**
   * 获取权限图标
   */
  const getPermIcon = (perm: string): string => {
    if (perm === '*') return 'ri:vip-crown-line'
    if (perm.includes('*')) return 'ri:asterisk'
    if (perm.includes('.ui.')) return 'ri:layout-line'
    if (perm.includes('.api.')) return 'ri:code-s-slash-line'
    if (perm.includes('.data.')) return 'ri:database-2-line'
    return 'ri:key-2-line'
  }

  /**
   * 获取权限颜色
   */
  const getPermColor = (perm: string): string => {
    if (perm === '*' || perm.includes('*')) return 'var(--el-color-warning)'
    if (perm.includes('.ui.')) return 'var(--el-color-primary)'
    if (perm.includes('.api.')) return 'var(--el-color-success)'
    if (perm.includes('.data.')) return 'var(--el-color-danger)'
    return 'var(--el-text-color-secondary)'
  }

  /**
   * 获取权限类型
   */
  const getPermType = (perm: string): 'warning' | 'primary' | 'success' | 'info' | 'danger' => {
    if (perm === '*' || perm.includes('*')) return 'warning'
    if (perm.includes('.ui.')) return 'primary'
    if (perm.includes('.api.')) return 'success'
    if (perm.includes('.data.')) return 'danger'
    return 'info'
  }

  /**
   * 获取权限标签
   */
  const getPermLabel = (perm: string): string => {
    if (perm === '*' || perm.includes('*')) return '通配符'
    if (perm.includes('.ui.')) return 'UI'
    if (perm.includes('.api.')) return 'API'
    if (perm.includes('.data.')) return 'DATA'
    return '其他'
  }

  /**
   * 拖拽开始
   */
  const handleDragStart = (event: DragEvent, permission: string, source: 'pool' | 'selected') => {
    draggedPermission = permission
    dragSource = source
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move'
    }
  }

  /**
   * 拖拽结束
   */
  const handleDragEnd = () => {
    draggedPermission = null
    dragSource = null
  }

  /**
   * 移动端触摸开始
   */
  const onTouchStart = (event: TouchEvent, permission: string, source: 'pool' | 'selected') => {
    if (event.touches.length !== 1) return
    
    const touch = event.touches[0]
    draggedPermission = permission
    dragSource = source
    
    const target = event.currentTarget as HTMLElement
    
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

  /**
   * 移动端触摸移动
   */
  const onTouchMove = (event: TouchEvent) => {
    if (!draggedPermission || event.touches.length !== 1) return
    
    const touch = event.touches[0]
    
    if (dragClone) {
      dragClone.style.left = `${touch.clientX - dragClone.offsetWidth / 2}px`
      dragClone.style.top = `${touch.clientY - 20}px`
    }
    
    const isOverPool = isPointInElement(touch.clientX, touch.clientY, poolListRef.value)
    const isOverSelected = isPointInElement(touch.clientX, touch.clientY, selectedListRef.value)
    
    poolListRef.value?.classList.toggle('drop-zone-active', isOverPool && dragSource === 'selected')
    selectedListRef.value?.classList.toggle('drop-zone-active', isOverSelected && dragSource === 'pool')
    
    event.preventDefault()
  }

  /**
   * 移动端触摸结束
   */
  const onTouchEnd = (event: TouchEvent) => {
    if (!draggedPermission) return
    
    const target = event.currentTarget as HTMLElement
    target.style.opacity = ''
    
    if (dragClone) {
      dragClone.remove()
      dragClone = null
    }
    
    poolListRef.value?.classList.remove('drop-zone-active')
    selectedListRef.value?.classList.remove('drop-zone-active')
    
    const touch = event.changedTouches[0]
    if (touch) {
      const isOverPool = isPointInElement(touch.clientX, touch.clientY, poolListRef.value)
      const isOverSelected = isPointInElement(touch.clientX, touch.clientY, selectedListRef.value)
      
      if (isOverSelected && dragSource === 'pool') {
        addToSelected(draggedPermission)
      } else if (isOverPool && dragSource === 'selected') {
        removeFromSelected(draggedPermission)
      }
    }
    
    draggedPermission = null
    dragSource = null
  }

  /**
   * 判断点是否在元素内
   */
  const isPointInElement = (x: number, y: number, element?: HTMLElement): boolean => {
    if (!element) return false
    const rect = element.getBoundingClientRect()
    return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
  }

  /**
   * 拖放到已选区域
   */
  const handleDropToSelected = () => {
    if (draggedPermission && dragSource === 'pool') {
      addToSelected(draggedPermission)
    }
  }

  /**
   * 拖放到权限池区域
   */
  const handleDropToPool = () => {
    if (draggedPermission && dragSource === 'selected') {
      removeFromSelected(draggedPermission)
    }
  }

  /**
   * 添加到已选
   */
  const addToSelected = (permission: string) => {
    if (!selectedPermissions.value.includes(permission)) {
      selectedPermissions.value.push(permission)
    }
  }

  /**
   * 从已选移除
   */
  const removeFromSelected = (permission: string) => {
    const index = selectedPermissions.value.indexOf(permission)
    if (index > -1) {
      selectedPermissions.value.splice(index, 1)
    }
  }

  /**
   * 加载权限池数据
   */
  const loadPoolData = async () => {
    poolLoading.value = true
    try {
      const result = await fetchGetPermissionPoolOptions()
      poolList.value = result || []
    } catch (error) {
      console.error('获取权限池失败:', error)
    } finally {
      poolLoading.value = false
    }
  }

  /**
   * 加载用户已有权限
   */
  const loadUserPermissions = async () => {
    if (!props.userId) return
    
    loading.value = true
    try {
      const list = await fetchGetUserPermissions(props.userId)
      userPermList.value = list || []
      selectedPermissions.value = (list || []).map((p: SysUserPermVO) => p.permission)
      initialPermissions.value = [...selectedPermissions.value]
    } catch (error) {
      console.error('加载用户独立权限失败:', error)
    } finally {
      loading.value = false
    }
  }

  /**
   * 提交 - 计算差异，增量添加和删除
   */
  const handleSubmit = async () => {
    if (!props.userId) return

    // 计算需要添加的权限
    const toAdd = selectedPermissions.value.filter(p => !initialPermissions.value.includes(p))
    // 计算需要删除的权限
    const toRemove = initialPermissions.value.filter(p => !selectedPermissions.value.includes(p))

    if (toAdd.length === 0 && toRemove.length === 0) {
      ElMessage.info('没有变更')
      handleClose()
      return
    }

    submitting.value = true
    try {
      // 删除权限
      for (const perm of toRemove) {
        const permItem = userPermList.value.find(p => p.permission === perm)
        if (permItem) {
          await fetchDeleteUserPermission(props.userId, permItem.id)
        }
      }

      // 添加权限
      if (toAdd.length > 0) {
        await fetchAddUserPermissions(props.userId, toAdd)
      }

      ElMessage.success('设置独立权限成功')
      emit('submit')
      handleClose()
    } catch (error) {
      console.error('设置独立权限失败:', error)
    } finally {
      submitting.value = false
    }
  }

  const handleClose = () => {
    dialogVisible.value = false
    selectedPermissions.value = []
    initialPermissions.value = []
    userPermList.value = []
    poolSearch.value = ''
    selectedSearch.value = ''
  }

  watch(() => props.visible, (val) => {
    if (val) {
      loadPoolData()
      loadUserPermissions()
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
    font-size: 13px;
    width: 100%;
  }

  .permission-transfer {
    display: flex;
    gap: 16px;
    width: 100%;
  }

  .transfer-panel {
    flex: 1;
    display: flex;
    flex-direction: column;
    border: 1px solid var(--el-border-color);
    border-radius: 8px;
    overflow: hidden;
    min-width: 0;
  }

  .panel-header {
    display: flex;
    align-items: center;
    padding: 10px 12px;
    background: var(--el-fill-color-light);
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .panel-search {
    padding: 8px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .panel-content {
    flex: 1;
    overflow-y: auto;
    padding: 8px;
    min-height: 320px;
    max-height: 320px;
  }

  .perm-item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    margin-bottom: 6px;
    background: var(--el-fill-color-light);
    border-radius: 6px;
    cursor: grab;
    transition: all 0.2s;
    user-select: none;
    touch-action: none;
  }

  .perm-item:last-child {
    margin-bottom: 0;
  }

  .perm-item:hover {
    background: var(--el-fill-color);
  }

  .perm-item:active {
    cursor: grabbing;
  }

  .perm-item-selected {
    background: v-bind(primaryColorLight);
  }

  .perm-item-selected:hover {
    background: v-bind(primaryColorLight);
    filter: brightness(0.95);
  }

  .perm-item-selected:hover .remove-btn {
    opacity: 1;
  }

  .remove-btn {
    opacity: 0;
    transition: opacity 0.2s;
    padding: 2px;
  }

  @media (max-width: 768px) {
    .remove-btn {
      opacity: 1;
    }
  }

  .drop-zone {
    transition: background-color 0.2s, border-color 0.2s;
  }

  .drop-zone-active {
    background-color: var(--el-color-primary-light-9) !important;
    border: 2px dashed var(--el-color-primary) !important;
  }
</style>
