<!-- 页面路径显示 -->
<template>
  <div 
    v-if="pathText" 
    class="ml-3 flex-c h-8 px-3 bg-g-100 dark:bg-g-300/30 rounded-md max-lg:!hidden"
  >
    <span class="text-sm text-g-600 dark:text-g-800 whitespace-nowrap">
      {{ pathText }}
    </span>
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'
  import { useRoute } from 'vue-router'
  import { formatMenuTitle } from '@/utils/router'

  defineOptions({ name: 'ArtPagePath' })

  const route = useRoute()

  // 生成路径文本
  const pathText = computed(() => {
    const { matched } = route
    
    if (!matched.length) {
      return ''
    }

    // 过滤并格式化路径
    const paths = matched
      .filter(item => {
        // 过滤掉隐藏的、包裹容器的路由
        return item.meta?.title && 
               !item.meta?.isHide && 
               item.path !== '/outside'
      })
      .map(item => formatMenuTitle(item.meta?.title as string))

    // 如果是一级菜单，只显示当前页
    if (matched[0]?.meta?.isFirstLevel) {
      const currentRoute = matched[matched.length - 1]
      return formatMenuTitle(currentRoute.meta?.title as string)
    }

    return paths.join(' / ')
  })
</script>
