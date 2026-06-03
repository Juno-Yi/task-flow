<!-- 开发中页面 -->
<template>
  <div class="page-content !border-0 !bg-transparent min-h-screen flex-cc">
    <div class="flex-cc flex-col text-center">
      <div class="icon-wrapper mb-8">
        <ArtSvgIcon icon="ri:tools-line" class="text-8xl text-primary animate-pulse" />
      </div>
      <h1 class="text-3xl font-bold text-g-800 mb-4">{{ title }}</h1>
      <p class="text-lg text-g-500 mb-8 max-w-md">{{ description }}</p>
      <div class="flex gap-4">
        <ElButton type="primary" size="large" @click="goBack" v-ripple>
          <ArtSvgIcon icon="ri:arrow-left-line" class="mr-2" />
          返回上一页
        </ElButton>
        <ElButton size="large" @click="goHome" v-ripple>
          <ArtSvgIcon icon="ri:home-line" class="mr-2" />
          返回首页
        </ElButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useCommon } from '@/hooks/core/useCommon'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'

  defineOptions({ name: 'UnderDevelopment' })

  interface Props {
    title?: string
    description?: string
  }

  withDefaults(defineProps<Props>(), {
    title: '功能开发中',
    description: '该功能正在紧锣密鼓地开发中，敬请期待...'
  })

  const router = useRouter()
  const { homePath } = useCommon()

  const goBack = () => {
    router.back()
  }

  const goHome = () => {
    router.push(homePath.value)
  }
</script>

<style scoped>
  .icon-wrapper {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-primary-light-7) 100%);
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>
