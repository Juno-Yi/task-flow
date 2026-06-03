<!-- 菜单拖拽树节点组件 -->
<template>
  <div
    class="drag-tree-node"
    :class="{
      'is-directory': node.menuType === 0,
      'drag-over-before': currentDropPosition === 'before',
      'drag-over-after': currentDropPosition === 'after',
      'drag-over-inside': currentDropPosition === 'inside',
      'is-dragging': dragNodeId === node.id
    }"
    :style="{ paddingLeft: `${level * 24 + 16}px` }"
    :data-node-id="node.id"
    draggable="true"
    v-bind="getNodeDragHandlers(node.id)"
  >
    <div class="node-content">
      <span class="drag-handle">
        <ArtSvgIcon icon="ri:drag-move-2-fill" />
      </span>

      <span v-if="node.children?.length" class="expand-icon" @click.stop="toggleExpand">
        <ArtSvgIcon :icon="isExpanded ? 'ri:arrow-down-s-line' : 'ri:arrow-right-s-line'" />
      </span>
      <span v-else class="expand-placeholder"></span>

      <ArtSvgIcon v-if="node.icon" :icon="node.icon" class="node-icon" />

      <span class="node-title">{{ formatMenuTitle(node.title) }}</span>
    </div>

    <div class="node-type">
      <ElTag :type="node.menuType === 0 ? 'info' : 'primary'" size="small">
        {{ node.menuType === 0 ? '目录' : '菜单' }}
      </ElTag>
    </div>

    <div class="node-sort">{{ node.sort }}</div>

    <div class="node-path">{{ node.path || '-' }}</div>
  </div>

  <!-- 子节点 -->
  <template v-if="isExpanded && node.children?.length">
    <DragTreeNode
      v-for="child in node.children"
      :key="child.id"
      :node="child"
      :level="level + 1"
      :dragNodeId="dragNodeId"
      :dropTargetId="dropTargetId"
      :dropPosition="dropPosition"
      :getNodeDragHandlers="getNodeDragHandlers"
    />
  </template>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
  import { formatMenuTitle } from '@/utils/router'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { ElTag } from 'element-plus'
  import type { DropPosition } from '@/hooks/core/useTouchDrag'

  interface Props {
    node: Api.System.MenuVO
    level: number
    /** 当前拖拽的节点 ID（从父组件传入） */
    dragNodeId?: number | null
    /** 当前放置目标节点 ID（从父组件传入） */
    dropTargetId?: number | null
    /** 放置位置（从父组件传入） */
    dropPosition?: DropPosition | null
    /** 获取节点拖拽事件处理器（从父组件传入） */
    getNodeDragHandlers: (nodeId: number) => Record<string, unknown>
  }

  const props = withDefaults(defineProps<Props>(), {
    dragNodeId: null,
    dropTargetId: null,
    dropPosition: null
  })

  const isExpanded = ref(true)

  // 当前节点的放置位置
  const currentDropPosition = computed(() => {
    return props.dropTargetId === props.node.id ? props.dropPosition : null
  })

  const toggleExpand = (): void => {
    isExpanded.value = !isExpanded.value
  }
</script>

<style scoped>
  .drag-tree-node {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    cursor: move;
    transition: background-color 0.2s;
    position: relative;
    user-select: none;
    touch-action: none;
  }

  .drag-tree-node:hover {
    background-color: var(--el-fill-color-light);
  }

  .drag-tree-node.is-directory {
    background-color: var(--el-fill-color-lighter);
  }

  .drag-tree-node.is-dragging {
    opacity: 0.5;
  }

  .drag-tree-node.drag-over-before::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background-color: var(--el-color-primary);
    pointer-events: none;
    z-index: 10;
  }

  .drag-tree-node.drag-over-after::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 3px;
    background-color: var(--el-color-primary);
    pointer-events: none;
    z-index: 10;
  }

  .drag-tree-node.drag-over-inside {
    background-color: var(--el-color-primary-light-9);
    outline: 2px dashed var(--el-color-primary);
    outline-offset: -2px;
  }

  .node-content {
    flex: 1;
    min-width: 200px;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .drag-handle {
    color: var(--el-text-color-placeholder);
    cursor: move;
  }

  .drag-handle:hover {
    color: var(--el-color-primary);
  }

  .expand-icon {
    cursor: pointer;
    color: var(--el-text-color-secondary);
    width: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .expand-icon:hover {
    color: var(--el-color-primary);
  }

  .expand-placeholder {
    width: 16px;
  }

  .node-icon {
    font-size: 16px;
    color: var(--el-text-color-secondary);
  }

  .node-title {
    font-size: 14px;
    color: var(--el-text-color-primary);
  }

  .node-type {
    width: 80px;
    text-align: center;
  }

  .node-sort {
    width: 60px;
    text-align: center;
    font-size: 14px;
    color: var(--el-text-color-secondary);
  }

  .node-path {
    width: 200px;
    font-size: 13px;
    color: var(--el-text-color-secondary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
</style>
