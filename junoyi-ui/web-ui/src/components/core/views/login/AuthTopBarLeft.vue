<!-- 授权页顶部栏 - 左右布局（默认） -->
<template>
  <div class="absolute w-full flex-cb top-4.5 z-10 flex-c !justify-end max-[1180px]:!justify-between">
    <div class="flex-cc !hidden max-[1180px]:!flex ml-2 max-sm:ml-6">
      <ArtLogo class="icon" size="46" />
      <h1 class="text-xl font-medium ml-2">{{ systemName }}</h1>
    </div>

    <div class="flex-cc gap-1.5 mr-2 max-sm:mr-5">
      <!-- 主题切换 -->
      <div
        v-if="shouldShowThemeToggle"
        class="btn theme-btn h-8 w-8 c-p flex-cc tad-300"
        @click="themeAnimation"
      >
        <ArtSvgIcon
          :icon="isDark ? 'ri:sun-fill' : 'ri:moon-line'"
          class="text-xl text-g-800 transition-colors duration-300"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import { useSettingStore } from '@/store/modules/setting'
  import { useUserStore } from '@/store/modules/user'
  import { useHeaderBar } from '@/hooks/core/useHeaderBar'
  import { themeAnimation } from '@/utils/ui/animation'
  import { languageOptions } from '@/locales'
  import { LanguageEnum } from '@/enums/appEnum'
  import AppConfig from '@/config'

  defineOptions({ name: 'AuthTopBarLeft' })

  const settingStore = useSettingStore()
  const userStore = useUserStore()
  const { isDark, systemThemeColor, authLayout, systemInfo } = storeToRefs(settingStore)
  const { shouldShowThemeToggle, shouldShowLanguage } = useHeaderBar()
  const { locale } = useI18n()

  // 优先使用接口返回的系统名称，如果没有则使用配置文件的
  const systemName = computed(() => systemInfo.value?.name || AppConfig.systemInfo.name)

  const mainColors = AppConfig.systemMainColor
  const color = systemThemeColor // css v-bind 使用

  const layoutOptions = [
    { label: '左右布局', value: 'left-right', icon: 'ri:layout-left-2-line' },
    { label: '右左布局', value: 'right-left', icon: 'ri:layout-right-2-line' },
    { label: '居中布局', value: 'center', icon: 'ri:layout-top-2-line' }
  ]

  const changeLanguage = (lang: LanguageEnum) => {
    if (locale.value === lang) return
    locale.value = lang
    userStore.setLanguage(lang)
  }

  const changeThemeColor = (color: string) => {
    if (systemThemeColor.value === color) return
    settingStore.setElementTheme(color)
    settingStore.reload()
  }

  const changeLayout = (layout: string) => {
    if (authLayout.value === layout) return
    settingStore.setAuthLayout(layout)
  }
</script>

<style scoped>
  .color-dots {
    pointer-events: none;
    backdrop-filter: blur(10px);
    box-shadow: 0 2px 12px var(--art-gray-300);
    transition:
      opacity 0.3s ease,
      transform 0.3s ease;
    transform: translateX(10px);
  }

  .color-dot {
    box-shadow: 0 2px 4px rgb(0 0 0 / 15%);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    transition-delay: calc(var(--index) * 0.05s);
    transform: translateX(20px) scale(0.8);
  }

  .color-dot:hover {
    box-shadow: 0 4px 8px rgb(0 0 0 / 20%);
    transform: translateX(0) scale(1.1);
  }

  .color-picker-expandable:hover .color-dots {
    pointer-events: auto;
    opacity: 1;
    transform: translateX(0);
  }

  .color-picker-expandable:hover .color-dot {
    opacity: 1;
    transform: translateX(0) scale(1);
  }

  .dark .color-dots {
    background-color: var(--art-gray-200);
    box-shadow: none;
  }

  .color-picker-expandable:hover .palette-btn :deep(.art-svg-icon) {
    color: v-bind(color);
  }

  .layout-btn-item :deep(.el-dropdown-menu__item) {
    display: flex;
    align-items: center;
    min-width: 120px;
  }
</style>
