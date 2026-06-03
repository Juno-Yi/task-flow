<!-- 图标组件 -->
<template>
  <!-- 如果是图片URL，使用img标签 -->
  <img 
    v-if="isImageUrl" 
    :src="processedIcon" 
    :class="['art-svg-icon', 'inline', attrs.class as string]" 
    :style="attrs.style as string"
    alt="icon"
  />
  <!-- 否则使用Iconify图标 -->
  <Icon v-else-if="icon" :icon="icon" v-bind="bindAttrs" class="art-svg-icon inline" />
</template>

<script setup lang="ts">
  import { Icon } from '@iconify/vue'

  defineOptions({ name: 'ArtSvgIcon', inheritAttrs: false })

  interface Props {
    /** Iconify icon name or image URL */
    icon?: string
  }

  const props = defineProps<Props>()
  const attrs = useAttrs()

  const bindAttrs = computed<{ class: string; style: string }>(() => ({
    class: (attrs.class as string) || '',
    style: (attrs.style as string) || ''
  }))

  /**
   * 获取完整的 API 基础路径
   */
  const getBaseURL = (): string => {
    const apiUrl = (import.meta.env.VITE_API_URL || '').trim()
    const apiPrefix = (import.meta.env.VITE_API_PREFIX || '').trim()
    
    if (!apiPrefix) return apiUrl
    const normalizedUrl = apiUrl.endsWith('/') ? apiUrl.slice(0, -1) : apiUrl
    const normalizedPrefix = apiPrefix.startsWith('/') ? apiPrefix : `/${apiPrefix}`
    return `${normalizedUrl}${normalizedPrefix}`
  }

  /**
   * 判断是否为图片URL
   * 支持：http://, https://, 或以 / 开头的相对路径（且包含图片扩展名）
   */
  const isImageUrl = computed(() => {
    if (!props.icon) return false
    
    const icon = props.icon.toLowerCase()
    
    // 完整URL
    if (icon.startsWith('http://') || icon.startsWith('https://')) {
      return true
    }
    
    // 相对路径且包含图片扩展名
    if (icon.startsWith('/') && /\.(png|jpg|jpeg|gif|svg|webp|ico)(\?.*)?$/.test(icon)) {
      return true
    }
    
    return false
  })

  /**
   * 处理图标路径
   */
  const processedIcon = computed(() => {
    if (!props.icon || !isImageUrl.value) return props.icon
    
    const icon = props.icon
    
    // 完整URL直接返回
    if (icon.startsWith('http://') || icon.startsWith('https://')) {
      return icon
    }
    
    // 相对路径，拼接API基础路径
    const baseURL = getBaseURL()
    const normalizedIcon = icon.startsWith('/') ? icon : `/${icon}`
    
    if (!baseURL || baseURL === '/') {
      return normalizedIcon
    }
    
    return `${baseURL}${normalizedIcon}`
  })
</script>

<style scoped>
  .art-svg-icon {
    display: inline-block;
    vertical-align: middle;
  }
  
  /* 图片图标样式 */
  img.art-svg-icon {
    width: 1em;
    height: 1em;
    object-fit: contain;
  }
</style>
