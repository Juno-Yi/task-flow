<!-- 授权页顶部栏 - 根据布局自动选择对应组件 -->
<template>
  <AuthTopBarLeft v-if="authLayout === 'left-right'" />
  <AuthTopBarRight v-else-if="authLayout === 'right-left'" />
  <AuthTopBarCenter v-else />
</template>

<script setup lang="ts">
  import { useSettingStore } from '@/store/modules/setting'
  import AuthTopBarLeft from './AuthTopBarLeft.vue'
  import AuthTopBarRight from './AuthTopBarRight.vue'
  import AuthTopBarCenter from './AuthTopBarCenter.vue'

  defineOptions({ name: 'AuthTopBar' })

  const settingStore = useSettingStore()
  const { authLayout } = storeToRefs(settingStore)
</script>
