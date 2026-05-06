<!-- 角色分配权限组弹窗 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="分配权限组"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="tip-box mb-5" :style="tipBoxStyle">
      <ArtSvgIcon icon="ri:information-line" class="mr-2 flex-shrink-0" :style="{ color: primaryColor }" />
      <span>操作方式：拖拽或双击左侧权限组完成绑定，点击右侧 × 按钮或双击/拖回左侧完成解绑</span>
    </div>
    <div class="flex gap-4 h-80">
      <div class="flex-1 flex flex-col border border-gray-200 dark:border-gray-600 rounded-lg overflow-hidden">
        <div class="px-3 py-2 bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-600">
          <span class="font-medium text-sm">可分配权限组</span>
          <span class="text-gray-400 text-xs ml-2">({{ availableGroups.length }})</span>
        </div>
        <div ref="availableListRef" class="flex-1 overflow-auto p-2 drop-zone" @dragover.prevent @drop="handleDropToAvailable">
          <div v-for="group in availableGroups" :key="group.id" class="perm-item" draggable="true"
            @dragstart="handleDragStart($event, group, 'available')" @dragend="handleDragEnd"
            @touchstart="onTouchStart($event, group, 'available')" @touchmove="onTouchMove" @touchend="onTouchEnd"
            @dblclick="addGroup(group)">
            <ArtSvgIcon icon="ri:folder-shield-line" class="mr-2 text-gray-500" />
            <span class="flex-1 truncate">{{ group.groupName }}</span>
            <ElTag v-if="isSuperGroup(group)" type="warning" size="small" effect="light">超级</ElTag>
            <span v-else class="text-xs text-gray-400">{{ group.groupCode }}</span>
          </div>
          <div v-if="availableGroups.length === 0" class="text-center text-gray-400 py-8">暂无可分配权限组</div>
        </div>
      </div>
      <div class="flex-1 flex flex-col border border-gray-200 dark:border-gray-600 rounded-lg overflow-hidden">
        <div class="px-3 py-2 border-b border-gray-200 dark:border-gray-600" :style="{ backgroundColor: primaryColorLight }">
          <span class="font-medium text-sm" :style="{ color: primaryColor }">已绑定权限组</span>
          <span class="text-xs ml-2" :style="{ color: primaryColor, opacity: 0.7 }">({{ boundGroups.length }})</span>
        </div>
        <div ref="boundListRef" class="flex-1 overflow-auto p-2 drop-zone" @dragover.prevent @drop="handleDropToBound">
          <div v-for="group in boundGroups" :key="group.id" class="perm-item perm-item-bound" draggable="true"
            @dragstart="handleDragStart($event, group, 'bound')" @dragend="handleDragEnd"
            @touchstart="onTouchStart($event, group, 'bound')" @touchmove="onTouchMove" @touchend="onTouchEnd"
            @dblclick="removeGroup(group)">
            <ArtSvgIcon icon="ri:folder-shield-2-line" class="mr-2" :style="{ color: primaryColor }" />
            <span class="flex-1 truncate">{{ group.groupName }}</span>
            <ElTag v-if="isSuperGroup(group)" type="warning" size="small" effect="light" class="mr-2">超级</ElTag>
            <span v-else class="text-xs text-gray-400 mr-2">{{ group.groupCode }}</span>
            <ElButton link size="small" class="remove-btn" @click.stop="removeGroup(group)">
              <ArtSvgIcon icon="ri:close-line" class="text-gray-400 hover:text-red-500" />
            </ElButton>
          </div>
          <div v-if="boundGroups.length === 0" class="text-center text-gray-400 py-8">暂无已绑定权限组</div>
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
import { fetchGetPermissionGroupOptions } from '@/api/system/permission'
import { fetchGetRolePermissionGroups, fetchUpdateRolePermissionGroups } from '@/api/system/role'
import { useSettingStore } from '@/store/modules/setting'

type PermissionGroupVO = Api.System.PermissionGroupVO
type RoleVO = Api.System.RoleVO

interface Props {
  visible: boolean
  roleData?: RoleVO | null
}

const props = defineProps<Props>()
const emit = defineEmits<{ 'update:visible': [value: boolean]; 'success': [] }>()
const settingStore = useSettingStore()

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

const allGroups = ref<PermissionGroupVO[]>([])
const boundGroupIds = ref<number[]>([])
const submitting = ref(false)
const availableListRef = ref<HTMLElement>()
const boundListRef = ref<HTMLElement>()

const availableGroups = computed(() => allGroups.value.filter(g => !boundGroupIds.value.includes(g.id)))
const boundGroups = computed(() => allGroups.value.filter(g => boundGroupIds.value.includes(g.id)))
const isSuperGroup = (group: PermissionGroupVO) => group.permissions?.length === 1 && group.permissions[0] === '*'

let draggedGroup: PermissionGroupVO | null = null
let dragSource: 'available' | 'bound' | null = null
let dragClone: HTMLElement | null = null

const handleDragStart = (event: DragEvent, group: PermissionGroupVO, source: 'available' | 'bound') => {
  draggedGroup = group
  dragSource = source
  if (event.dataTransfer) event.dataTransfer.effectAllowed = 'move'
}

const handleDragEnd = () => {
  draggedGroup = null
  dragSource = null
}

const handleDropToAvailable = () => {
  if (draggedGroup && dragSource === 'bound') {
    boundGroupIds.value = boundGroupIds.value.filter(id => id !== draggedGroup!.id)
  }
}

const handleDropToBound = () => {
  if (draggedGroup && dragSource === 'available' && !boundGroupIds.value.includes(draggedGroup.id)) {
    boundGroupIds.value.push(draggedGroup.id)
  }
}

const onTouchStart = (event: TouchEvent, group: PermissionGroupVO, source: 'available' | 'bound') => {
  if (event.touches.length !== 1) return
  const touch = event.touches[0]
  draggedGroup = group
  dragSource = source
  const target = event.currentTarget as HTMLElement
  dragClone = target.cloneNode(true) as HTMLElement
  dragClone.style.cssText = `position:fixed;z-index:9999;pointer-events:none;opacity:0.9;transform:scale(1.02);box-shadow:0 4px 12px rgba(0,0,0,0.15);width:${target.offsetWidth}px;left:${touch.clientX - target.offsetWidth / 2}px;top:${touch.clientY - 20}px`
  document.body.appendChild(dragClone)
  target.style.opacity = '0.4'
  event.preventDefault()
}

const onTouchMove = (event: TouchEvent) => {
  if (!draggedGroup || event.touches.length !== 1) return
  const touch = event.touches[0]
  if (dragClone) {
    dragClone.style.left = `${touch.clientX - dragClone.offsetWidth / 2}px`
    dragClone.style.top = `${touch.clientY - 20}px`
  }
  const isOverAvailable = isPointInElement(touch.clientX, touch.clientY, availableListRef.value)
  const isOverBound = isPointInElement(touch.clientX, touch.clientY, boundListRef.value)
  availableListRef.value?.classList.toggle('drop-zone-active', isOverAvailable && dragSource === 'bound')
  boundListRef.value?.classList.toggle('drop-zone-active', isOverBound && dragSource === 'available')
  event.preventDefault()
}

const onTouchEnd = (event: TouchEvent) => {
  if (!draggedGroup) return
  const target = event.currentTarget as HTMLElement
  target.style.opacity = ''
  if (dragClone) { dragClone.remove(); dragClone = null }
  availableListRef.value?.classList.remove('drop-zone-active')
  boundListRef.value?.classList.remove('drop-zone-active')
  const touch = event.changedTouches[0]
  if (touch) {
    const isOverAvailable = isPointInElement(touch.clientX, touch.clientY, availableListRef.value)
    const isOverBound = isPointInElement(touch.clientX, touch.clientY, boundListRef.value)
    if (isOverBound && dragSource === 'available' && !boundGroupIds.value.includes(draggedGroup.id)) {
      boundGroupIds.value.push(draggedGroup.id)
    } else if (isOverAvailable && dragSource === 'bound') {
      boundGroupIds.value = boundGroupIds.value.filter(id => id !== draggedGroup!.id)
    }
  }
  draggedGroup = null
  dragSource = null
}

const isPointInElement = (x: number, y: number, element?: HTMLElement): boolean => {
  if (!element) return false
  const rect = element.getBoundingClientRect()
  return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
}

const addGroup = (group: PermissionGroupVO) => {
  if (!boundGroupIds.value.includes(group.id)) {
    boundGroupIds.value.push(group.id)
  }
}

const removeGroup = (group: PermissionGroupVO) => {
  boundGroupIds.value = boundGroupIds.value.filter(id => id !== group.id)
}

const loadData = async () => {
  if (!props.roleData?.id) return
  try {
    const groups = await fetchGetPermissionGroupOptions()
    allGroups.value = groups || []
    const roleGroups = await fetchGetRolePermissionGroups(props.roleData.id)
    boundGroupIds.value = (roleGroups || []).map(g => g.id)
  } catch (error) {
    console.error('加载权限组数据失败:', error)
  }
}

const handleSubmit = async () => {
  if (!props.roleData?.id) return
  submitting.value = true
  try {
    await fetchUpdateRolePermissionGroups(props.roleData.id, boundGroupIds.value)
    ElMessage.success('分配权限组成功')
    emit('success')
    handleClose()
  } catch (error) {
    console.error('分配权限组失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleClose = () => {
  dialogVisible.value = false
  boundGroupIds.value = []
}

watch(() => props.visible, (val) => { if (val) loadData() })
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

.perm-item:hover {
  background: var(--el-fill-color);
}

.perm-item:active {
  cursor: grabbing;
}

.perm-item-bound {
  background: v-bind(primaryColorLight);
}

.perm-item-bound:hover {
  background: v-bind(primaryColorLight);
  filter: brightness(0.95);
}

.perm-item-bound:hover .remove-btn {
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
