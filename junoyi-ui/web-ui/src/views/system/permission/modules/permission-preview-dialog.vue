<!-- 权限预览弹窗 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="权限详情"
    width="520px"
    :close-on-click-modal="true"
  >
    <div class="preview-content">
      <!-- 基本信息 -->
      <div class="info-section">
        <div class="info-row">
          <span class="info-label">权限组名称</span>
          <span class="info-value">{{ data?.groupName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">权限组编码</span>
          <code class="info-code">{{ data?.groupCode }}</code>
        </div>
        <div class="info-row">
          <span class="info-label">优先级</span>
          <span class="info-value">{{ data?.priority ?? 0 }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">状态</span>
          <ElTag :type="data?.status === 1 ? 'success' : 'danger'" size="small" effect="light">
            {{ data?.status === 1 ? '正常' : '禁用' }}
          </ElTag>
        </div>
        <div v-if="data?.description" class="info-row">
          <span class="info-label">描述</span>
          <span class="info-value desc">{{ data.description }}</span>
        </div>
      </div>

      <!-- 权限列表 -->
      <div class="permissions-section">
        <div class="section-header">
          <ArtSvgIcon icon="ri:key-2-line" />
          <span>权限列表</span>
          <ElTag type="primary" size="small" effect="light" round>
            {{ data?.permissions?.length || 0 }}
          </ElTag>
        </div>
        
        <div class="permissions-list">
          <template v-if="data?.permissions?.length">
            <div
              v-for="perm in data.permissions"
              :key="perm"
              class="perm-item"
            >
              <ArtSvgIcon 
                :icon="getPermIcon(perm)" 
                class="perm-icon"
                :style="{ color: getPermColor(perm) }"
              />
              <span class="perm-text">{{ perm }}</span>
              <ElTag 
                :type="getPermType(perm)" 
                size="small"
                effect="light"
                class="perm-tag"
              >
                {{ getPermLabel(perm) }}
              </ElTag>
            </div>
          </template>
          <div v-else class="permissions-empty">
            <ArtSvgIcon icon="ri:inbox-line" class="empty-icon" />
            <span>暂无权限</span>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <ElButton type="primary" @click="dialogVisible = false">关闭</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { computed } from 'vue'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'

  type PermissionGroupVO = Api.System.PermissionGroupVO

  interface Props {
    visible: boolean
    data?: PermissionGroupVO | null
  }

  const props = withDefaults(defineProps<Props>(), {
    data: null
  })

  const emit = defineEmits<{
    'update:visible': [value: boolean]
  }>()

  const dialogVisible = computed({
    get: () => props.visible,
    set: (val) => emit('update:visible', val)
  })

  const getPermIcon = (perm: string): string => {
    if (perm === '*') return 'ri:vip-crown-line'
    if (perm.includes('.ui.')) return 'ri:layout-line'
    if (perm.includes('.api.')) return 'ri:code-s-slash-line'
    if (perm.includes('.data.')) return 'ri:database-2-line'
    if (perm.endsWith('*')) return 'ri:folder-line'
    return 'ri:key-2-line'
  }

  const getPermColor = (perm: string): string => {
    if (perm === '*') return 'var(--el-color-warning)'
    if (perm.includes('.ui.')) return 'var(--el-color-primary)'
    if (perm.includes('.api.')) return 'var(--el-color-success)'
    if (perm.includes('.data.')) return 'var(--el-color-info)'
    return 'var(--el-text-color-secondary)'
  }

  const getPermType = (perm: string): 'warning' | 'primary' | 'success' | 'info' | 'danger' => {
    if (perm === '*') return 'warning'
    if (perm.includes('.ui.')) return 'primary'
    if (perm.includes('.api.')) return 'success'
    if (perm.includes('.data.')) return 'info'
    return 'info'
  }

  const getPermLabel = (perm: string): string => {
    if (perm === '*') return '超级权限'
    if (perm.endsWith('*')) return '通配符'
    if (perm.includes('.ui.')) return 'UI权限'
    if (perm.includes('.api.')) return 'API权限'
    if (perm.includes('.data.')) return '数据权限'
    return '其他'
  }
</script>

<style scoped>
  .preview-content {
    padding: 0 4px;
  }

  .info-section {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    margin-bottom: 16px;
  }

  .info-row {
    display: flex;
    align-items: flex-start;
  }

  .info-label {
    width: 90px;
    flex-shrink: 0;
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }

  .info-value {
    flex: 1;
    color: var(--el-text-color-primary);
    font-size: 13px;
  }

  .info-value.desc {
    color: var(--el-text-color-regular);
    line-height: 1.5;
  }

  .info-code {
    padding: 2px 8px;
    background: var(--el-fill-color);
    border-radius: 4px;
    font-family: 'SF Mono', Monaco, Consolas, monospace;
    font-size: 12px;
  }

  .permissions-section {
    /* empty */
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 500;
    margin-bottom: 12px;
    color: var(--el-text-color-primary);
    font-size: 14px;
  }

  .permissions-list {
    max-height: 280px;
    overflow-y: auto;
    background: var(--el-fill-color-lighter);
    border-radius: 8px;
    padding: 8px;
  }

  .perm-item {
    display: flex;
    align-items: center;
    padding: 10px 12px;
    margin-bottom: 6px;
    background: var(--el-bg-color);
    border-radius: 6px;
    border: 1px solid var(--el-border-color-lighter);
  }

  .perm-item:last-child {
    margin-bottom: 0;
  }

  .perm-icon {
    margin-right: 10px;
    flex-shrink: 0;
    font-size: 16px;
  }

  .perm-text {
    flex: 1;
    font-family: 'SF Mono', Monaco, Consolas, monospace;
    font-size: 12px;
    color: var(--el-text-color-primary);
    word-break: break-all;
  }

  .perm-tag {
    flex-shrink: 0;
    margin-left: 8px;
  }

  .permissions-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
    color: var(--el-text-color-placeholder);
    font-size: 13px;
  }

  .empty-icon {
    font-size: 32px;
    margin-bottom: 8px;
    opacity: 0.5;
  }
</style>
