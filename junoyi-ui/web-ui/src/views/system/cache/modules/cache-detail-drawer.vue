<!-- 缓存详情抽屉组件 -->
<template>
  <ElDrawer
    v-model="visible"
    title="缓存详情"
    size="500px"
    :destroy-on-close="true"
  >
    <div v-if="loading" class="flex-cc py-10">
      <ElIcon class="is-loading" :size="24"><Loading /></ElIcon>
    </div>
    <div v-else-if="detail" class="space-y-4">
      <!-- 基本信息 -->
      <div class="bg-g-100 rounded-lg p-4">
        <div class="text-sm font-medium text-g-700 mb-3 pb-2 border-b-d">基本信息</div>
        <div class="flex items-start gap-3 py-2">
          <span class="text-sm text-g-500 w-20 flex-shrink-0">键名</span>
          <span class="text-sm text-g-800 flex-1 font-mono break-all">{{ detail.key }}</span>
        </div>
        <div class="flex items-start gap-3 py-2">
          <span class="text-sm text-g-500 w-20 flex-shrink-0">类型</span>
          <ElTag :type="getTypeTagType(detail.type)" size="small">{{ detail.type }}</ElTag>
        </div>
        <div class="flex items-start gap-3 py-2">
          <span class="text-sm text-g-500 w-20 flex-shrink-0">TTL</span>
          <span class="text-sm text-g-800">{{ formatTTL(detail.ttl) }}</span>
        </div>
        <div v-if="detail.memoryUsage" class="flex items-start gap-3 py-2">
          <span class="text-sm text-g-500 w-20 flex-shrink-0">内存占用</span>
          <span class="text-sm text-g-800">{{ formatBytes(detail.memoryUsage) }}</span>
        </div>
      </div>
      <!-- 值内容 -->
      <div class="bg-g-100 rounded-lg p-4">
        <div class="text-sm font-medium text-g-700 mb-3 pb-2 border-b-d flex-cb">
          <span>值内容</span>
          <ElButton size="small" text @click="copyValue">
            <ArtSvgIcon icon="ri:file-copy-line" class="mr-1" />复制
          </ElButton>
        </div>
        <div class="bg-g-200 rounded-lg p-3 max-h-96 overflow-auto">
          <pre class="text-sm font-mono text-g-800 whitespace-pre-wrap break-all m-0">{{ formatValue(detail.value) }}</pre>
        </div>
      </div>
    </div>
  </ElDrawer>
</template>

<script setup lang="ts">
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { ElDrawer, ElIcon, ElButton, ElTag } from 'element-plus'
  import { Loading } from '@element-plus/icons-vue'
  import { useClipboard } from '@vueuse/core'

  defineOptions({ name: 'CacheDetailDrawer' })

  type CacheKeyDetailVO = Api.System.CacheKeyDetailVO

  const visible = defineModel<boolean>({ default: false })

  const props = defineProps<{
    detail: CacheKeyDetailVO | null
    loading: boolean
  }>()

  const { copy } = useClipboard()

  /**
   * 格式化 TTL
   */
  const formatTTL = (ttl: number): string => {
    if (ttl === -1) return '永不过期'
    if (ttl === -2) return '已过期'
    if (ttl < 60) return `${ttl}秒`
    if (ttl < 3600) return `${Math.floor(ttl / 60)}分钟`
    if (ttl < 86400) return `${Math.floor(ttl / 3600)}小时`
    return `${Math.floor(ttl / 86400)}天`
  }

  /**
   * 格式化字节
   */
  const formatBytes = (bytes: number): string => {
    if (bytes < 1024) return `${bytes} B`
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB`
    return `${(bytes / 1024 / 1024).toFixed(2)} MB`
  }

  /**
   * 获取类型标签类型
   */
  const getTypeTagType = (type: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    const map: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
      OBJECT: 'primary',
      MAP: 'info',
      LIST: 'success',
      SET: 'warning',
      ZSET: 'danger'
    }
    return map[type] || 'info'
  }

  /**
   * 格式化值
   */
  const formatValue = (value: string | string[] | Record<string, string>): string => {
    if (typeof value === 'string') {
      try {
        return JSON.stringify(JSON.parse(value), null, 2)
      } catch {
        return value
      }
    }
    return JSON.stringify(value, null, 2)
  }

  /**
   * 复制值
   */
  const copyValue = () => {
    if (props.detail) {
      const text = formatValue(props.detail.value)
      copy(text)
      ElMessage.success('已复制到剪贴板')
    }
  }
</script>
