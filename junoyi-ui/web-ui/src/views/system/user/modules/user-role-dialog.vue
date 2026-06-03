<!-- 用户分配角色弹窗 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="分配角色"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <!-- 提示信息 -->
    <div class="tip-box mb-5" :style="tipBoxStyle">
      <ArtSvgIcon icon="ri:information-line" class="mr-2 flex-shrink-0" :style="{ color: primaryColor }" />
      <span>操作方式：拖拽左侧角色到右侧完成绑定，点击右侧角色的 × 按钮或拖回左侧完成解绑</span>
    </div>

    <div class="flex gap-4 h-80">
      <!-- 左侧：可分配角色列表 -->
      <div class="flex-1 flex flex-col border border-gray-200 dark:border-gray-600 rounded-lg overflow-hidden">
        <div class="px-3 py-2 bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-600">
          <span class="font-medium text-sm">可分配角色</span>
          <span class="text-gray-400 text-xs ml-2">({{ availableRoles.length }})</span>
        </div>
        <div
          ref="availableListRef"
          class="flex-1 overflow-auto p-2 drop-zone"
          @dragover.prevent
          @drop="handleDropToAvailable"
        >
          <div
            v-for="role in availableRoles"
            :key="role.id"
            class="role-item"
            draggable="true"
            @dragstart="handleDragStart($event, role, 'available')"
            @dragend="handleDragEnd"
            @touchstart="onTouchStart($event, role, 'available')"
            @touchmove="onTouchMove"
            @touchend="onTouchEnd"
          >
            <ArtSvgIcon icon="ri:shield-user-line" class="mr-2 text-gray-500" />
            <span class="flex-1 truncate">{{ role.roleName }}</span>
            <span class="text-xs text-gray-400">{{ role.roleKey }}</span>
          </div>
          <div v-if="availableRoles.length === 0" class="text-center text-gray-400 py-8">
            暂无可分配角色
          </div>
        </div>
      </div>

      <!-- 右侧：已绑定角色列表 -->
      <div class="flex-1 flex flex-col border border-gray-200 dark:border-gray-600 rounded-lg overflow-hidden">
        <div class="px-3 py-2 border-b border-gray-200 dark:border-gray-600" :style="{ backgroundColor: primaryColorLight }">
          <span class="font-medium text-sm" :style="{ color: primaryColor }">已绑定角色</span>
          <span class="text-xs ml-2" :style="{ color: primaryColor, opacity: 0.7 }">({{ boundRoles.length }})</span>
        </div>
        <div
          ref="boundListRef"
          class="flex-1 overflow-auto p-2 drop-zone"
          @dragover.prevent
          @drop="handleDropToBound"
        >
          <div
            v-for="role in boundRoles"
            :key="role.id"
            class="role-item role-item-bound"
            draggable="true"
            @dragstart="handleDragStart($event, role, 'bound')"
            @dragend="handleDragEnd"
            @touchstart="onTouchStart($event, role, 'bound')"
            @touchmove="onTouchMove"
            @touchend="onTouchEnd"
          >
            <ArtSvgIcon icon="ri:shield-check-line" class="mr-2" :style="{ color: primaryColor }" />
            <span class="flex-1 truncate">{{ role.roleName }}</span>
            <span class="text-xs text-gray-400 mr-2">{{ role.roleKey }}</span>
            <ElButton 
              link 
              size="small" 
              class="remove-btn"
              @click.stop="removeRole(role)"
            >
              <ArtSvgIcon icon="ri:close-line" class="text-gray-400 hover:text-red-500" />
            </ElButton>
          </div>
          <div v-if="boundRoles.length === 0" class="text-center text-gray-400 py-8">
            暂无已绑定角色
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
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { fetchGetRoleOptions } from '@/api/system/role'
  import { fetchGetUserRoles, fetchUpdateUserRoles } from '@/api/system/user'
  import { useSettingStore } from '@/store/modules/setting'

  interface RoleItem {
    id: number
    roleName: string
    roleKey: string
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

  // 所有角色列表
  const allRoles = ref<RoleItem[]>([])
  // 已绑定角色ID列表
  const boundRoleIds = ref<number[]>([])
  // 提交中
  const submitting = ref(false)

  // 可分配角色（排除已绑定的）
  const availableRoles = computed(() => {
    return allRoles.value.filter(role => !boundRoleIds.value.includes(role.id))
  })

  // 已绑定角色
  const boundRoles = computed(() => {
    return allRoles.value.filter(role => boundRoleIds.value.includes(role.id))
  })

  // 放置区域引用
  const availableListRef = ref<HTMLElement>()
  const boundListRef = ref<HTMLElement>()

  // 拖拽相关
  let draggedRole: RoleItem | null = null
  let dragSource: 'available' | 'bound' | null = null
  let dragClone: HTMLElement | null = null
  let touchStartX = 0
  let touchStartY = 0

  const handleDragStart = (event: DragEvent, role: RoleItem, source: 'available' | 'bound') => {
    draggedRole = role
    dragSource = source
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move'
    }
  }

  const handleDragEnd = () => {
    draggedRole = null
    dragSource = null
  }

  // 拖到可分配列表（解绑）
  const handleDropToAvailable = () => {
    if (draggedRole && dragSource === 'bound') {
      boundRoleIds.value = boundRoleIds.value.filter(id => id !== draggedRole!.id)
    }
  }

  // 拖到已绑定列表（绑定）
  const handleDropToBound = () => {
    if (draggedRole && dragSource === 'available') {
      if (!boundRoleIds.value.includes(draggedRole.id)) {
        boundRoleIds.value.push(draggedRole.id)
      }
    }
  }

  // 移动端触摸拖拽
  const onTouchStart = (event: TouchEvent, role: RoleItem, source: 'available' | 'bound') => {
    if (event.touches.length !== 1) return
    
    const touch = event.touches[0]
    touchStartX = touch.clientX
    touchStartY = touch.clientY
    
    draggedRole = role
    dragSource = source
    
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
    if (!draggedRole || event.touches.length !== 1) return
    
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
    boundListRef.value?.classList.toggle('drop-zone-active', isOverBound && dragSource === 'available')
    
    event.preventDefault()
  }

  const onTouchEnd = (event: TouchEvent) => {
    if (!draggedRole) return
    
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
      const isOverBound = isPointInElement(touch.clientX, touch.clientY, boundListRef.value)
      
      if (isOverBound && dragSource === 'available') {
        if (!boundRoleIds.value.includes(draggedRole.id)) {
          boundRoleIds.value.push(draggedRole.id)
        }
      } else if (isOverAvailable && dragSource === 'bound') {
        boundRoleIds.value = boundRoleIds.value.filter(id => id !== draggedRole!.id)
      }
    }
    
    draggedRole = null
    dragSource = null
  }

  const isPointInElement = (x: number, y: number, element?: HTMLElement): boolean => {
    if (!element) return false
    const rect = element.getBoundingClientRect()
    return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
  }

  // 移除角色（点击删除按钮）
  const removeRole = (role: RoleItem) => {
    boundRoleIds.value = boundRoleIds.value.filter(id => id !== role.id)
  }

  // 加载数据
  const loadData = async () => {
    if (!props.userId) return
    
    try {
      // 获取所有角色
      const roles = await fetchGetRoleOptions()
      allRoles.value = (roles || []).map(r => ({
        id: r.id,
        roleName: r.roleName,
        roleKey: r.roleKey
      }))

      // 获取用户已绑定的角色
      const userRoles = await fetchGetUserRoles(props.userId)
      boundRoleIds.value = (userRoles || []).map(r => r.id)
    } catch (error) {
      console.error('加载角色数据失败:', error)
    }
  }

  // 提交
  const handleSubmit = async () => {
    if (!props.userId) return

    submitting.value = true
    try {
      await fetchUpdateUserRoles(props.userId, boundRoleIds.value)
      ElMessage.success('分配角色成功')
      emit('submit')
      handleClose()
    } catch (error) {
      console.error('分配角色失败:', error)
    } finally {
      submitting.value = false
    }
  }

  const handleClose = () => {
    dialogVisible.value = false
    boundRoleIds.value = []
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

  .role-item {
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

  .role-item:hover {
    background: var(--el-fill-color);
  }

  .role-item:active {
    cursor: grabbing;
  }

  .role-item-bound {
    background: v-bind(primaryColorLight);
  }

  .role-item-bound:hover {
    background: v-bind(primaryColorLight);
    filter: brightness(0.95);
  }

  .role-item-bound:hover .remove-btn {
    opacity: 1;
  }

  .remove-btn {
    opacity: 0;
    transition: opacity 0.2s;
    padding: 2px;
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
