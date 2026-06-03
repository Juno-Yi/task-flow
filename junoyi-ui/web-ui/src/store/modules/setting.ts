/**
 * 系统设置状态管理模块
 *
 * 提供完整的系统设置状态管理
 *
 * ## 主要功能
 *
 * - 菜单布局配置（左侧、顶部、混合、双栏）
 * - 主题管理（亮色、暗色、自动）
 * - 菜单主题样式配置
 * - 界面显示开关（面包屑、标签页、语言切换等）
 * - 功能开关（手风琴模式、色弱模式、水印等）
 * - 样式配置（边框、圆角、容器宽度、页面过渡）
 * - 节日功能配置
 * - Element Plus 主题色动态设置
 * - 系统应用配置管理（从后端获取的系统级配置）
 *
 * ## 使用场景
 *
 * - 设置面板配置管理
 * - 主题切换和样式定制
 * - 界面功能开关控制
 * - 用户偏好设置持久化
 * - 系统级配置应用（如强制水印、功能开关等）
 *
 * ## 持久化
 *
 * - 使用 localStorage 存储
 * - 存储键：sys-v{version}-setting
 * - 支持跨版本数据迁移
 *
 * @module store/modules/setting
 * @author Art Design Pro Team
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { MenuThemeType } from '@/types/store'
import AppConfig from '@/config'
import { SystemThemeEnum, MenuThemeEnum, MenuTypeEnum, ContainerWidthEnum } from '@/enums/appEnum'
import { setElementThemeColor } from '@/utils/ui'
import { useCeremony } from '@/hooks/core/useCeremony'
import { StorageConfig } from '@/utils'
import { SETTING_DEFAULT_CONFIG } from '@/config/setting'
import type { SystemInfo } from '@/api/system/info'

/**
 * 系统应用配置类型
 * 用于存储从后端获取的系统级配置
 */
export interface SystemAppConfig {
  /** 配置键名 */
  configKey: string
  /** 配置键值 */
  configValue: string
  /** 配置类型 */
  configType: string
}

/**
 * 系统设置状态管理
 * 管理应用的菜单、主题、界面显示等各项设置
 */
export const useSettingStore = defineStore(
  'settingStore',
  () => {
    // 系统信息
    /** 系统信息（从接口获取） */
    const systemInfo = ref<SystemInfo | null>(null)

    // 系统应用配置
    /** 系统应用配置列表（从后端获取的系统级配置） */
    const systemAppConfigs = ref<Api.System.ConfigVO[]>([])
    /** 系统应用配置是否已加载 */
    const systemAppConfigLoaded = ref(false)

    // 菜单相关设置
    /** 菜单类型 */
    const menuType = ref(SETTING_DEFAULT_CONFIG.menuType)
    /** 菜单展开宽度 */
    const menuOpenWidth = ref(SETTING_DEFAULT_CONFIG.menuOpenWidth)
    /** 菜单是否展开 */
    const menuOpen = ref(SETTING_DEFAULT_CONFIG.menuOpen)
    /** 双菜单是否显示文本 */
    const dualMenuShowText = ref(SETTING_DEFAULT_CONFIG.dualMenuShowText)

    // 主题相关设置
    /** 系统主题类型 */
    const systemThemeType = ref(SETTING_DEFAULT_CONFIG.systemThemeType)
    /** 系统主题模式 */
    const systemThemeMode = ref(SETTING_DEFAULT_CONFIG.systemThemeMode)
    /** 菜单主题类型 */
    const menuThemeType = ref(SETTING_DEFAULT_CONFIG.menuThemeType)
    /** 系统主题颜色 */
    const systemThemeColor = ref(SETTING_DEFAULT_CONFIG.systemThemeColor)

    // 界面显示设置
    /** 是否显示菜单按钮 */
    const showMenuButton = ref(SETTING_DEFAULT_CONFIG.showMenuButton)
    /** 是否显示刷新按钮 */
    const showRefreshButton = ref(SETTING_DEFAULT_CONFIG.showRefreshButton)
    /** 是否显示面包屑 */
    const showCrumbs = ref(SETTING_DEFAULT_CONFIG.showCrumbs)
    /** 是否显示工作台标签 */
    const showWorkTab = ref(SETTING_DEFAULT_CONFIG.showWorkTab)
    /** 是否显示语言切换 */
    const showLanguage = ref(SETTING_DEFAULT_CONFIG.showLanguage)
    /** 是否显示进度条 */
    const showNprogress = ref(SETTING_DEFAULT_CONFIG.showNprogress)
    /** 是否显示设置引导 */
    const showSettingGuide = ref(SETTING_DEFAULT_CONFIG.showSettingGuide)
    /** 是否显示节日文本 */
    const showFestivalText = ref(SETTING_DEFAULT_CONFIG.showFestivalText)
    /** 是否显示水印 */
    const watermarkVisible = ref(SETTING_DEFAULT_CONFIG.watermarkVisible)

    // 功能设置
    /** 是否自动关闭 */
    const autoClose = ref(SETTING_DEFAULT_CONFIG.autoClose)
    /** 是否唯一展开 */
    const uniqueOpened = ref(SETTING_DEFAULT_CONFIG.uniqueOpened)
    /** 是否色弱模式 */
    const colorWeak = ref(SETTING_DEFAULT_CONFIG.colorWeak)
    /** 是否刷新 */
    const refresh = ref(SETTING_DEFAULT_CONFIG.refresh)
    /** 是否加载节日烟花 */
    const holidayFireworksLoaded = ref(SETTING_DEFAULT_CONFIG.holidayFireworksLoaded)

    // 样式设置
    /** 边框模式 */
    const boxBorderMode = ref(SETTING_DEFAULT_CONFIG.boxBorderMode)
    /** 页面过渡效果 */
    const pageTransition = ref(SETTING_DEFAULT_CONFIG.pageTransition)
    /** 标签页样式 */
    const tabStyle = ref(SETTING_DEFAULT_CONFIG.tabStyle)
    /** 自定义圆角 */
    const customRadius = ref(SETTING_DEFAULT_CONFIG.customRadius)
    /** 容器宽度 */
    const containerWidth = ref(SETTING_DEFAULT_CONFIG.containerWidth)

    // 节日相关
    /** 节日日期 */
    const festivalDate = ref('')

    // 授权页布局
    /** 授权页布局类型 */
    const authLayout = ref('left-right')

    /**
     * 获取菜单主题
     * 根据当前主题类型和暗色模式返回对应的主题配置
     */
    const getMenuTheme = computed((): MenuThemeType => {
      const list = AppConfig.themeList.filter((item) => item.theme === menuThemeType.value)
      if (isDark.value) {
        return AppConfig.darkMenuStyles[0]
      } else {
        return list[0]
      }
    })

    /**
     * 判断是否为暗色模式
     */
    const isDark = computed((): boolean => {
      return systemThemeType.value === SystemThemeEnum.DARK
    })

    /**
     * 获取菜单展开宽度
     */
    const getMenuOpenWidth = computed((): string => {
      return menuOpenWidth.value + 'px' || SETTING_DEFAULT_CONFIG.menuOpenWidth + 'px'
    })

    /**
     * 获取自定义圆角
     */
    const getCustomRadius = computed((): string => {
      return customRadius.value + 'rem' || SETTING_DEFAULT_CONFIG.customRadius + 'rem'
    })

    /**
     * 是否显示烟花
     * 根据当前日期和节日日期判断是否显示烟花效果
     */
    const isShowFireworks = computed((): boolean => {
      return festivalDate.value === useCeremony().currentFestivalData.value?.date ? false : true
    })

    /**
     * 是否允许编辑菜单布局
     * 根据系统配置 sys.menu.layout.editable 判断
     */
    const isMenuLayoutEditable = computed((): boolean => {
      // 如果配置未加载，默认允许编辑
      if (!systemAppConfigLoaded.value) {
        return true
      }
      
      // 获取配置值
      const configValue = getSystemAppConfig('sys.menu.layout.editable')
      
      // 如果没有配置，默认允许编辑
      if (!configValue) {
        return true
      }
      
      // 检查配置值，只有明确为 false/N/0 等才禁止编辑
      const disabledValues = ['false', 'FALSE', 'False', 'N', 'n', '0', 'no', 'NO', 'off', 'OFF']
      return !disabledValues.includes(configValue)
    })

    /**
     * 切换菜单布局
     * @param type 菜单类型
     */
    const switchMenuLayouts = (type: MenuTypeEnum) => {
      menuType.value = type
    }

    /**
     * 设置菜单展开宽度
     * @param width 宽度值
     */
    const setMenuOpenWidth = (width: number) => {
      menuOpenWidth.value = width
    }

    /**
     * 设置全局主题
     * @param theme 主题类型
     * @param themeMode 主题模式
     */
    const setGlopTheme = (theme: SystemThemeEnum, themeMode: SystemThemeEnum) => {
      systemThemeType.value = theme
      systemThemeMode.value = themeMode
      localStorage.setItem(StorageConfig.THEME_KEY, theme)
    }

    /**
     * 切换菜单样式
     * @param theme 菜单主题
     */
    const switchMenuStyles = (theme: MenuThemeEnum) => {
      menuThemeType.value = theme
    }

    /**
     * 设置Element Plus主题颜色
     * @param theme 主题颜色
     */
    const setElementTheme = (theme: string) => {
      systemThemeColor.value = theme
      setElementThemeColor(theme)
    }

    /**
     * 切换边框模式
     */
    const setBorderMode = () => {
      boxBorderMode.value = !boxBorderMode.value
    }

    /**
     * 设置容器宽度
     * @param width 容器宽度枚举值
     */
    const setContainerWidth = (width: ContainerWidthEnum) => {
      containerWidth.value = width
    }

    /**
     * 切换唯一展开模式
     */
    const setUniqueOpened = () => {
      uniqueOpened.value = !uniqueOpened.value
    }

    /**
     * 切换菜单按钮显示
     */
    const setButton = () => {
      showMenuButton.value = !showMenuButton.value
    }

    /**
     * 切换自动关闭
     */
    const setAutoClose = () => {
      autoClose.value = !autoClose.value
    }

    /**
     * 切换刷新按钮显示
     */
    const setShowRefreshButton = () => {
      showRefreshButton.value = !showRefreshButton.value
    }

    /**
     * 切换面包屑显示
     */
    const setCrumbs = () => {
      showCrumbs.value = !showCrumbs.value
    }

    /**
     * 设置工作台标签显示
     * @param show 是否显示
     */
    const setWorkTab = (show: boolean) => {
      showWorkTab.value = show
    }

    /**
     * 切换语言切换显示
     */
    const setLanguage = () => {
      showLanguage.value = !showLanguage.value
    }

    /**
     * 切换进度条显示
     */
    const setNprogress = () => {
      showNprogress.value = !showNprogress.value
    }

    /**
     * 切换色弱模式
     */
    const setColorWeak = () => {
      colorWeak.value = !colorWeak.value
    }

    /**
     * 隐藏设置引导
     */
    const hideSettingGuide = () => {
      showSettingGuide.value = false
    }

    /**
     * 显示设置引导
     */
    const openSettingGuide = () => {
      showSettingGuide.value = true
    }

    /**
     * 设置页面过渡效果
     * @param transition 过渡效果名称
     */
    const setPageTransition = (transition: string) => {
      pageTransition.value = transition
    }

    /**
     * 设置标签页样式
     * @param style 样式名称
     */
    const setTabStyle = (style: string) => {
      tabStyle.value = style
    }

    /**
     * 设置菜单展开状态
     * @param open 是否展开
     */
    const setMenuOpen = (open: boolean) => {
      menuOpen.value = open
    }

    /**
     * 刷新页面
     */
    const reload = () => {
      refresh.value = !refresh.value
    }

    /**
     * 设置水印显示
     * @param visible 是否显示
     */
    const setWatermarkVisible = (visible: boolean) => {
      watermarkVisible.value = visible
    }

    /**
     * 设置自定义圆角
     * @param radius 圆角值
     */
    const setCustomRadius = (radius: string) => {
      customRadius.value = radius
      document.documentElement.style.setProperty('--custom-radius', `${radius}rem`)
    }

    /**
     * 设置节日烟花加载状态
     * @param isLoad 是否已加载
     */
    const setholidayFireworksLoaded = (isLoad: boolean) => {
      holidayFireworksLoaded.value = isLoad
    }

    /**
     * 设置节日文本显示
     * @param show 是否显示
     */
    const setShowFestivalText = (show: boolean) => {
      showFestivalText.value = show
    }

    const setFestivalDate = (date: string) => {
      festivalDate.value = date
    }

    const setDualMenuShowText = (show: boolean) => {
      dualMenuShowText.value = show
    }

    /**
     * 设置授权页布局
     * @param layout 布局类型
     */
    const setAuthLayout = (layout: string) => {
      authLayout.value = layout
    }

    /**
     * 设置系统信息
     * @param info 系统信息
     */
    const setSystemInfo = (info: SystemInfo) => {
      systemInfo.value = info
    }

    /**
     * 设置系统应用配置
     * @param configs 系统应用配置列表
     */
    const setSystemAppConfigs = (configs: Api.System.ConfigVO[]) => {
      systemAppConfigs.value = configs
      systemAppConfigLoaded.value = true
    }

    /**
     * 获取系统应用配置值
     * @param key 配置键名
     * @param defaultValue 默认值
     * @returns 配置值
     */
    const getSystemAppConfig = (key: string, defaultValue?: string): string | undefined => {
      const config = systemAppConfigs.value.find(c => c.configKey === key)
      return config?.configValue ?? defaultValue
    }

    /**
     * 检查系统应用配置是否启用
     * @param key 配置键名
     * @returns 是否启用（Y/true/1 为启用）
     */
    const isSystemAppConfigEnabled = (key: string): boolean => {
      const value = getSystemAppConfig(key)
      if (!value) return false
      
      // 支持多种格式：'Y', 'true', '1', 'yes', 'on'
      const enabledValues = ['Y', 'y', 'true', 'TRUE', '1', 'yes', 'YES', 'on', 'ON']
      return enabledValues.includes(value)
    }

    /**
     * 应用系统级配置约束
     * 根据后端返回的系统配置，应用强制性的配置
     */
    const applySystemAppConfigConstraints = () => {
      // 应用菜单布局配置
      applyMenuLayoutConfig()
      
      // 系统水印配置直接控制显示，不需要额外处理
      // 未来可以在这里添加更多系统级配置的应用逻辑
      // 例如：
      // - 强制主题模式
      // - 禁用某些功能
      // - 设置默认值等
    }

    /**
     * 应用菜单布局配置
     * 根据后端返回的 sys.menu.layout.default 配置设置菜单布局
     */
    const applyMenuLayoutConfig = () => {
      const layoutValue = getSystemAppConfig('sys.menu.layout.default')
      if (!layoutValue) {
        return
      }

      // 后端值到前端枚举的映射
      const layoutMap: Record<string, MenuTypeEnum> = {
        'left': MenuTypeEnum.LEFT,        // 垂直（左侧）
        'top': MenuTypeEnum.TOP,          // 水平（顶部）
        'mix': MenuTypeEnum.TOP_LEFT,     // 混合（顶部+左侧）
        'double': MenuTypeEnum.DUAL_MENU  // 双列
      }

      const targetLayout = layoutMap[layoutValue.toLowerCase()]
      if (targetLayout && targetLayout !== menuType.value) {
        console.log(`[System] 应用系统菜单布局配置: ${layoutValue} -> ${targetLayout}`)
        switchMenuLayouts(targetLayout)
      }
    }

    /**
     * 重置系统应用配置
     */
    const resetSystemAppConfigs = () => {
      systemAppConfigs.value = []
      systemAppConfigLoaded.value = false
    }

    return {
      systemInfo,
      systemAppConfigs,
      systemAppConfigLoaded,
      menuType,
      menuOpenWidth,
      systemThemeType,
      systemThemeMode,
      menuThemeType,
      systemThemeColor,
      boxBorderMode,
      uniqueOpened,
      showMenuButton,
      showRefreshButton,
      showCrumbs,
      autoClose,
      showWorkTab,
      showLanguage,
      showNprogress,
      colorWeak,
      showSettingGuide,
      pageTransition,
      tabStyle,
      menuOpen,
      refresh,
      watermarkVisible,
      customRadius,
      holidayFireworksLoaded,
      showFestivalText,
      festivalDate,
      dualMenuShowText,
      containerWidth,
      authLayout,
      getMenuTheme,
      isDark,
      getMenuOpenWidth,
      getCustomRadius,
      isShowFireworks,
      isMenuLayoutEditable,
      switchMenuLayouts,
      setMenuOpenWidth,
      setGlopTheme,
      switchMenuStyles,
      setElementTheme,
      setBorderMode,
      setContainerWidth,
      setUniqueOpened,
      setButton,
      setAutoClose,
      setShowRefreshButton,
      setCrumbs,
      setWorkTab,
      setLanguage,
      setNprogress,
      setColorWeak,
      hideSettingGuide,
      openSettingGuide,
      setPageTransition,
      setTabStyle,
      setMenuOpen,
      reload,
      setWatermarkVisible,
      setCustomRadius,
      setholidayFireworksLoaded,
      setShowFestivalText,
      setFestivalDate,
      setDualMenuShowText,
      setAuthLayout,
      setSystemInfo,
      setSystemAppConfigs,
      getSystemAppConfig,
      isSystemAppConfigEnabled,
      applySystemAppConfigConstraints,
      applyMenuLayoutConfig,
      resetSystemAppConfigs
    }
  },
  {
    persist: {
      key: 'setting',
      storage: localStorage
    }
  }
)
