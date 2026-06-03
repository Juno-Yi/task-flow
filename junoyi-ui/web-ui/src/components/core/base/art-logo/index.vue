<!-- 系统logo -->
<template>
  <div class="flex-cc">
    <img :style="logoStyle" :src="logoSrc" alt="logo" class="w-full h-full" />
  </div>
</template>

<script setup lang="ts">
  import { useSettingStore } from '@/store/modules/setting'
  import logoDefault from '@imgs/common/logo.webp'

  defineOptions({ name: 'ArtLogo' })

  interface Props {
    /** logo 大小 */
    size?: number | string
  }

  const props = withDefaults(defineProps<Props>(), {
    size: 36
  })

  const settingStore = useSettingStore()
  const { systemInfo } = storeToRefs(settingStore)

  const logoStyle = computed(() => ({ width: `${props.size}px` }))
  
  /**
   * 获取完整的 API 基础路径（与 HTTP 请求保持一致）
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
   * 处理 logo 路径
   * 如果是相对路径，拼接 API 基础路径
   * 如果是完整 URL，直接使用
   * 如果没有，使用默认 logo
   */
  const logoSrc = computed(() => {
    const logo = systemInfo.value?.logo
    
    if (!logo) {
      return logoDefault
    }
    
    // 如果是完整的 URL（http:// 或 https://），直接使用
    if (logo.startsWith('http://') || logo.startsWith('https://')) {
      return logo
    }
    
    // 如果是相对路径，拼接 API 基础路径
    const baseURL = getBaseURL()
    const normalizedLogo = logo.startsWith('/') ? logo : `/${logo}`
    
    // 如果 baseURL 为空或只是 /，直接返回 logo 路径
    if (!baseURL || baseURL === '/') {
      return normalizedLogo
    }
    
    return `${baseURL}${normalizedLogo}`
  })
</script>
