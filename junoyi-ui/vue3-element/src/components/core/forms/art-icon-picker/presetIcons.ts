/**
 * 图标选择器预设图标配置
 * 图标来源：https://icones.js.org/
 *
 * @author Fan
 */
import remixIconData from "@/components/core/forms/art-icon-picker/icon/remixIcon";
import materialIconThemeData from "@/components/core/forms/art-icon-picker/icon/materialIconTheme";
import fluentColorData from "@/components/core/forms/art-icon-picker/icon/FluentColor";
import rivetIconsData from "@/components/core/forms/art-icon-picker/icon/RivetIcons";
import streamlinePlumpColorData from "@/components/core/forms/art-icon-picker/icon/StreamlinePlumpColor";

/** 图标分类 */
export const iconCategories = [
  { key: 'RemixIcon', label: 'RemixIcon' },
  { key: 'RivetIcons', label: 'RivetIcons'},
  { key: 'MaterialIconTheme', label: 'MaterialIconTheme'},
  { key: 'FluentColor', label: 'FluentColor'},
  { key: 'StreamlinePlumpColor', label: 'StreamlinePlumpColor'}

] as const

export type IconCategoryKey = (typeof iconCategories)[number]['key']

/** 预设图标列表 */
export const presetIcons: Record<IconCategoryKey, string[]> = {
  RemixIcon: remixIconData,
  RivetIcons: rivetIconsData,
  MaterialIconTheme: materialIconThemeData,
  FluentColor: fluentColorData,
  StreamlinePlumpColor: streamlinePlumpColorData
}
