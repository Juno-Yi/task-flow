<!-- Redis 信息卡片组件 -->
<template>
  <ElCard class="mb-4" shadow="never">
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
      <div v-for="item in infoCards" :key="item.label" class="flex-c gap-3">
        <div
          class="w-10 h-10 rounded-lg flex-cc flex-shrink-0"
          :style="{ backgroundColor: `color-mix(in srgb, ${primaryColor} 15%, transparent)` }"
        >
          <ArtSvgIcon :icon="item.icon" :style="{ color: primaryColor }" />
        </div>
        <div class="flex-1 min-w-0">
          <div class="text-lg font-semibold text-g-900 truncate">{{ item.value }}</div>
          <div class="text-xs text-g-500">{{ item.label }}</div>
        </div>
      </div>
    </div>
  </ElCard>
</template>

<script setup lang="ts">
  import { useSettingStore } from '@/store/modules/setting'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { ElCard } from 'element-plus'

  defineOptions({ name: 'RedisInfoCard' })

  type RedisInfo = Api.System.RedisInfo

  const props = defineProps<{
    info: RedisInfo | null
  }>()

  const settingStore = useSettingStore()

  // 主题色
  const primaryColor = computed(() => settingStore.systemThemeColor || '#409eff')

  /**
   * 格式化运行时间
   */
  const formatUptime = (seconds: number): string => {
    const days = Math.floor(seconds / 86400)
    const hours = Math.floor((seconds % 86400) / 3600)
    if (days > 0) return `${days}天${hours}小时`
    const minutes = Math.floor((seconds % 3600) / 60)
    if (hours > 0) return `${hours}小时${minutes}分钟`
    return `${minutes}分钟`
  }

  // 信息卡片
  const infoCards = computed(() => {
    const info = props.info
    if (!info) return []
    return [
      { label: 'Redis版本', value: info.version, icon: 'ri:server-line' },
      { label: '运行时间', value: formatUptime(info.uptimeInSeconds), icon: 'ri:time-line' },
      { label: '已用内存', value: info.usedMemoryHuman, icon: 'ri:database-2-line' },
      { label: '键数量', value: String(info.dbSize), icon: 'ri:key-2-line' },
      { label: '命中率', value: info.hitRate, icon: 'ri:focus-3-line' },
      { label: '连接数', value: String(info.connectedClients), icon: 'ri:links-line' }
    ]
  })
</script>
