/**
 * useTreeDrag - 树形结构拖拽排序 Hook
 *
 * 支持桌面端 drag 事件和移动端 touch 事件
 * 支持 before/after/inside 三种放置位置
 *
 * @module hooks/useTreeDrag
 */

import { ref, onUnmounted, type Ref } from 'vue'

export type DropPosition = 'before' | 'after' | 'inside'

export interface TreeDragState {
  /** 是否正在拖拽 */
  isDragging: Ref<boolean>
  /** 当前拖拽的节点 ID */
  dragNodeId: Ref<number | null>
  /** 当前放置目标节点 ID */
  dropTargetId: Ref<number | null>
  /** 放置位置 */
  dropPosition: Ref<DropPosition | null>
}

export interface UseTreeDragOptions {
  /** 节点选择器，用于查找拖拽目标 */
  nodeSelector?: string
  /** 节点 ID 属性名 */
  nodeIdAttr?: string
  /** 判断节点是否可以作为父级（接受 inside 放置） */
  canBeParent?: (nodeId: number, element: HTMLElement) => boolean
  /** 放置完成回调 */
  onDrop?: (dragId: number, targetId: number, position: DropPosition) => void
}

/**
 * 创建树形拖拽功能
 */
export function useTreeDrag(options: UseTreeDragOptions = {}) {
  const {
    nodeSelector = '.drag-tree-node',
    nodeIdAttr = 'data-node-id',
    canBeParent = () => true,
    onDrop
  } = options

  // 拖拽状态
  const isDragging = ref(false)
  const dragNodeId = ref<number | null>(null)
  const dropTargetId = ref<number | null>(null)
  const dropPosition = ref<DropPosition | null>(null)

  // 触摸拖拽相关
  let dragClone: HTMLElement | null = null
  let dragSourceElement: HTMLElement | null = null

  /**
   * 根据 Y 坐标计算放置位置
   */
  const calcDropPosition = (
    y: number,
    rect: DOMRect,
    targetId: number,
    targetElement: HTMLElement
  ): DropPosition => {
    const relativeY = y - rect.top
    const height = rect.height

    if (relativeY < height * 0.25) {
      return 'before'
    } else if (relativeY > height * 0.75) {
      return 'after'
    } else if (canBeParent(targetId, targetElement)) {
      return 'inside'
    }
    return 'after'
  }

  /**
   * 查找触摸点下的目标节点
   */
  const findTargetNode = (x: number, y: number, excludeElement?: HTMLElement): HTMLElement | null => {
    // 临时隐藏克隆元素以便正确检测
    if (dragClone) {
      dragClone.style.display = 'none'
    }

    const elements = document.elementsFromPoint(x, y)
    const target = elements.find(
      (el) => el.matches(nodeSelector) && el !== excludeElement
    ) as HTMLElement | null

    if (dragClone) {
      dragClone.style.display = ''
    }

    return target
  }

  /**
   * 获取节点 ID
   */
  const getNodeId = (element: HTMLElement): number | null => {
    const id = element.getAttribute(nodeIdAttr)
    return id ? Number(id) : null
  }

  /**
   * 创建拖拽克隆元素
   */
  const createDragClone = (element: HTMLElement, x: number, y: number): HTMLElement => {
    const clone = element.cloneNode(true) as HTMLElement
    clone.classList.add('drag-clone')
    clone.style.cssText = `
      position: fixed;
      z-index: 9999;
      pointer-events: none;
      opacity: 0.9;
      transform: scale(1.02);
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      width: ${element.offsetWidth}px;
      left: ${x - element.offsetWidth / 2}px;
      top: ${y - 20}px;
      background-color: var(--el-bg-color);
    `
    document.body.appendChild(clone)
    return clone
  }

  /**
   * 更新克隆元素位置
   */
  const updateClonePosition = (x: number, y: number): void => {
    if (dragClone) {
      dragClone.style.left = `${x - dragClone.offsetWidth / 2}px`
      dragClone.style.top = `${y - 20}px`
    }
  }

  /**
   * 清理拖拽状态
   */
  const cleanup = (): void => {
    if (dragClone) {
      dragClone.remove()
      dragClone = null
    }
    if (dragSourceElement) {
      dragSourceElement.style.opacity = ''
      dragSourceElement = null
    }
    isDragging.value = false
    dragNodeId.value = null
    dropTargetId.value = null
    dropPosition.value = null
  }

  /**
   * 执行放置操作
   */
  const executeDrop = (): void => {
    if (
      dragNodeId.value !== null &&
      dropTargetId.value !== null &&
      dropPosition.value !== null &&
      dragNodeId.value !== dropTargetId.value
    ) {
      onDrop?.(dragNodeId.value, dropTargetId.value, dropPosition.value)
    }
  }

  // ========== 桌面端拖拽事件 ==========

  /**
   * 拖拽开始
   */
  const handleDragStart = (e: DragEvent, nodeId: number): void => {
    e.stopPropagation()
    isDragging.value = true
    dragNodeId.value = nodeId

    if (e.dataTransfer) {
      e.dataTransfer.effectAllowed = 'move'
      e.dataTransfer.setData('text/plain', String(nodeId))
    }
  }

  /**
   * 拖拽经过
   */
  const handleDragOver = (e: DragEvent, nodeId: number, element: HTMLElement): void => {
    e.preventDefault()
    e.stopPropagation()

    // 不能拖到自己身上
    if (dragNodeId.value === nodeId) {
      dropTargetId.value = null
      dropPosition.value = null
      return
    }

    dropTargetId.value = nodeId
    const rect = element.getBoundingClientRect()
    dropPosition.value = calcDropPosition(e.clientY, rect, nodeId, element)

    if (e.dataTransfer) {
      e.dataTransfer.dropEffect = 'move'
    }
  }

  /**
   * 放置
   */
  const handleDrop = (e: DragEvent): void => {
    e.preventDefault()
    e.stopPropagation()
    executeDrop()
  }

  /**
   * 拖拽结束
   */
  const handleDragEnd = (): void => {
    cleanup()
  }

  // ========== 移动端触摸事件 ==========

  /**
   * 触摸开始
   */
  const handleTouchStart = (e: TouchEvent, nodeId: number): void => {
    if (e.touches.length !== 1) return

    e.stopPropagation()
    e.preventDefault()

    const touch = e.touches[0]
    const element = e.currentTarget as HTMLElement

    isDragging.value = true
    dragNodeId.value = nodeId
    dragSourceElement = element

    // 创建克隆元素
    dragClone = createDragClone(element, touch.clientX, touch.clientY)

    // 降低原元素透明度
    element.style.opacity = '0.5'
  }

  /**
   * 触摸移动
   */
  const handleTouchMove = (e: TouchEvent): void => {
    if (!isDragging.value || e.touches.length !== 1) return

    e.stopPropagation()
    e.preventDefault()

    const touch = e.touches[0]
    updateClonePosition(touch.clientX, touch.clientY)

    // 查找目标节点
    const targetElement = findTargetNode(touch.clientX, touch.clientY, dragSourceElement || undefined)

    if (targetElement) {
      const targetId = getNodeId(targetElement)
      if (targetId !== null && targetId !== dragNodeId.value) {
        dropTargetId.value = targetId
        const rect = targetElement.getBoundingClientRect()
        dropPosition.value = calcDropPosition(touch.clientY, rect, targetId, targetElement)
      } else {
        dropTargetId.value = null
        dropPosition.value = null
      }
    } else {
      dropTargetId.value = null
      dropPosition.value = null
    }
  }

  /**
   * 触摸结束
   */
  const handleTouchEnd = (e: TouchEvent): void => {
    if (!isDragging.value) return

    e.stopPropagation()

    const touch = e.changedTouches[0]
    if (touch) {
      // 最后一次查找目标
      const targetElement = findTargetNode(touch.clientX, touch.clientY, dragSourceElement || undefined)
      if (targetElement) {
        const targetId = getNodeId(targetElement)
        if (targetId !== null && targetId !== dragNodeId.value) {
          dropTargetId.value = targetId
          const rect = targetElement.getBoundingClientRect()
          dropPosition.value = calcDropPosition(touch.clientY, rect, targetId, targetElement)
        }
      }
    }

    executeDrop()
    cleanup()
  }

  /**
   * 获取节点的拖拽事件绑定
   */
  const getNodeDragHandlers = (nodeId: number) => {
    return {
      onDragstart: (e: DragEvent) => handleDragStart(e, nodeId),
      onDragend: handleDragEnd,
      onDragover: (e: DragEvent) => handleDragOver(e, nodeId, e.currentTarget as HTMLElement),
      onDrop: handleDrop,
      onTouchstart: (e: TouchEvent) => handleTouchStart(e, nodeId),
      onTouchmove: handleTouchMove,
      onTouchend: handleTouchEnd
    }
  }

  /**
   * 判断节点是否是当前放置目标
   */
  const isDropTarget = (nodeId: number): boolean => {
    return dropTargetId.value === nodeId
  }

  /**
   * 获取节点的放置位置（仅当是放置目标时有效）
   */
  const getDropPosition = (nodeId: number): DropPosition | null => {
    return dropTargetId.value === nodeId ? dropPosition.value : null
  }

  // 清理
  onUnmounted(cleanup)

  return {
    // 状态
    isDragging,
    dragNodeId,
    dropTargetId,
    dropPosition,
    // 方法
    getNodeDragHandlers,
    isDropTarget,
    getDropPosition,
    cleanup
  }
}
