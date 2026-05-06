<!-- 登录背景 - 根据布局自动选择对应组件 -->
<template>
  <LoginLeftView v-if="authLayout === 'left-right'" />
  <LoginRightView v-else-if="authLayout === 'right-left'" />
  <LoginBackgroundCenter v-else />
</template>

<script setup lang="ts">
  import { useSettingStore } from '@/store/modules/setting'
  import LoginLeftView from './LoginLeftView.vue'
  import LoginRightView from './LoginRightView.vue'
  import LoginBackgroundCenter from './LoginBackgroundCenter.vue'

  defineOptions({ name: 'LoginBackground' })

  const settingStore = useSettingStore()
  const { authLayout } = storeToRefs(settingStore)
</script>
