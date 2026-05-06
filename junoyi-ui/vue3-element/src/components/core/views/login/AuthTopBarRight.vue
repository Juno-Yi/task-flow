<!-- 授权页顶部栏 - 右左布局 -->
<template>
  <div class="absolute w-full flex-cb top-4.5 z-10 flex-c !justify-start max-[1180px]:!justify-between">
    <!-- 操作按钮组 - 左侧 -->
    <div class="flex-cc gap-1.5 ml-2 max-sm:ml-5">
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

      <!-- 语言切换 -->
      <ElDropdown
        v-if="shouldShowLanguage"
        @command="changeLanguage"
        popper-class="langDropDownStyle"
      >
        <div class="btn language-btn h-8 w-8 c-p flex-cc tad-300">
          <ArtSvgIcon
            icon="ri:translate-2"
            class="text-[19px] text-g-800 transition-colors duration-300"
          />
        </div>
        <template #dropdown>
          <ElDropdownMenu>
            <div v-for="lang in languageOptions" :key="lang.value" class="lang-btn-item">
              <ElDropdownItem
                :command="lang.value"
                :class="{ 'is-selected': locale === lang.value }"
              >
                <span class="menu-txt">{{ lang.label }}</span>
                <ArtSvgIcon icon="ri:check-fill" class="text-base" v-if="locale === lang.value" />
              </ElDropdownItem>
            </div>
          </ElDropdownMenu>
        </template>
      </ElDropdown>

      <!-- 布局切换 -->
      <ElDropdown @command="changeLayout" popper-class="layoutDropDownStyle" class="max-sm:!hidden">
        <div class="btn layout-btn h-8 w-8 c-p flex-cc tad-300">
          <ArtSvgIcon
            icon="ri:layout-4-line"
            class="text-xl text-g-800 transition-colors duration-300"
          />
        </div>
        <template #dropdown>
          <ElDropdownMenu>
            <div v-for="layout in layoutOptions" :key="layout.value" class="layout-btn-item">
              <ElDropdownItem
                :command="layout.value"
                :class="{ 'is-selected': authLayout === layout.value }"
              >
                <ArtSvgIcon :icon="layout.icon" class="text-base mr-2" />
                <span class="menu-txt">{{ layout.label }}</span>
                <ArtSvgIcon icon="ri:check-fill" class="text-base ml-auto" v-if="authLayout === layout.value" />
              </ElDropdownItem>
            </div>
          </ElDropdownMenu>
        </template>
      </ElDropdown>

      <!-- 主题色选择 - 放最后，向右展开 -->
      <div class="color-picker-expandable relative flex-c max-sm:!hidden">
        <div class="btn palette-btn relative z-[2] h-8 w-8 c-p flex-cc tad-300">
          <ArtSvgIcon
            icon="ri:palette-line"
            class="text-xl text-g-800 transition-colors duration-300"
          />
        </div>
        <div
          class="color-dots absolute left-full ml-1 rounded-full flex-c gap-2 rounded-5 px-2.5 py-2 opacity-0"
        >
          <div
            v-for="(color, index) in mainColors"
            :key="color"
            class="color-dot relative size-5 c-p flex-cc rounded-full opacity-0"
            :class="{ active: color === systemThemeColor }"
            :style="{ background: color, '--index': index }"
            @click="changeThemeColor(color)"
          >
            <ArtSvgIcon v-if="color === systemThemeColor" icon="ri:check-fill" class="text-white" />
          </div>
        </div>
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

  defineOptions({ name: 'AuthTopBarRight' })

  const settingStore = useSettingStore()
  const userStore = useUserStore()
  const { isDark, systemThemeColor, authLayout } = storeToRefs(settingStore)
  const { shouldShowThemeToggle, shouldShowLanguage } = useHeaderBar()
  const { locale } = useI18n()

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
    transform: translateX(-10px);
  }

  .color-dot {
    box-shadow: 0 2px 4px rgb(0 0 0 / 15%);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    transition-delay: calc(var(--index) * 0.05s);
    transform: translateX(-20px) scale(0.8);
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
