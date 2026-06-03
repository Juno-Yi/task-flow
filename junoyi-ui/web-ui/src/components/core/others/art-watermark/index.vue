<!-- 水印组件 -->
<template>
  <div
    v-if="finalWatermarkVisible"
    class="fixed left-0 top-0 h-screen w-screen pointer-events-none"
    :style="{ zIndex: zIndex }"
  >
    <ElWatermark
      :content="finalContent"
      :font="{ fontSize: fontSize, color: fontColor }"
      :rotate="rotate"
      :gap="[gapX, gapY]"
      :offset="[offsetX, offsetY]"
    >
      <div style="height: 100vh"></div>
    </ElWatermark>
  </div>
</template>

<script setup lang="ts">
  import AppConfig from '@/config'
  import { useSettingStore } from '@/store/modules/setting'
  import { useUserStore } from '@/store/modules/user'
  import { replaceWatermarkPlaceholders } from '@/utils/watermark'

  defineOptions({ name: 'ArtWatermark' })

  const settingStore = useSettingStore()
  const userStore = useUserStore()
  const { systemInfo } = storeToRefs(settingStore)

  // 优先使用接口返回的系统名称，如果没有则使用配置文件的
  const defaultContent = computed(() => systemInfo.value?.name || AppConfig.systemInfo.name)

  interface WatermarkProps {
    /** 水印内容 */
    content?: string
    /** 水印是否可见 */
    visible?: boolean
    /** 水印字体大小 */
    fontSize?: number
    /** 水印字体颜色 */
    fontColor?: string
    /** 水印旋转角度 */
    rotate?: number
    /** 水印间距X */
    gapX?: number
    /** 水印间距Y */
    gapY?: number
    /** 水印偏移X */
    offsetX?: number
    /** 水印偏移Y */
    offsetY?: number
    /** 水印层级 */
    zIndex?: number
  }

  const props = withDefaults(defineProps<WatermarkProps>(), {
    visible: false,
    fontSize: 16,
    fontColor: 'rgba(128, 128, 128, 0.2)',
    rotate: -22,
    gapX: 100,
    gapY: 100,
    offsetX: 50,
    offsetY: 50,
    zIndex: 3100
  })

  /**
   * 最终的水印可见性
   * 直接由系统配置控制，不需要用户手动开关
   */
  const finalWatermarkVisible = computed(() => {
    // 直接使用系统配置控制水印显示
    return settingStore.isSystemAppConfigEnabled('sys.watermark.enabled')
  })

  /**
   * 最终的水印内容
   * 优先级：props.content > 系统配置 > 默认内容
   * 支持占位符替换：{username}, {nickname}, {date}, {time}, {datetime}, {year}, {month}, {day}
   */
  const finalContent = computed(() => {
    const text = props.content || settingStore.getSystemAppConfig('sys.watermark.text') || defaultContent.value
    
    // 使用工具函数替换占位符
    return replaceWatermarkPlaceholders(text, userStore.info)
  })
</script>
