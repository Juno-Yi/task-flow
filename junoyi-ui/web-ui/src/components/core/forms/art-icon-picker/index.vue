<!-- 图标选择器组件 -->
<template>
  <div class="art-icon-picker">
    <ElInput
      v-model="inputValue"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      @input="handleInput"
      @clear="handleClear"
    >
      <template #prefix>
        <ArtSvgIcon v-if="modelValue" :icon="modelValue" class="text-base" />
        <ArtSvgIcon v-else icon="ri:image-line" class="text-base text-gray-400" />
      </template>
      <template #suffix>
        <ElButton
          :disabled="disabled"
          link
          @click="showPopover = true"
        >
          <ArtSvgIcon icon="ri:apps-line" class="text-base" />
        </ElButton>
      </template>
    </ElInput>

    <!-- 图标选择弹窗 -->
    <ElDialog
      v-model="showPopover"
      title="选择图标"
      width="680px"
      :close-on-click-modal="true"
      append-to-body
    >
      <!-- 搜索框 -->
      <ElInput
        v-model="searchKeyword"
        placeholder="搜索图标..."
        clearable
        class="mb-4"
      >
        <template #prefix>
          <ArtSvgIcon icon="ri:search-line" />
        </template>
      </ElInput>

      <!-- 图标分类 Tabs -->
      <ElTabs v-model="activeTab" class="icon-tabs">
        <ElTabPane
          v-for="category in iconCategories"
          :key="category.key"
          :label="category.label"
          :name="category.key"
        />
      </ElTabs>

      <!-- 图标列表 -->
      <div class="icon-grid">
        <div
          v-for="icon in filteredIcons"
          :key="icon"
          class="icon-item"
          :class="{ 'is-selected': modelValue === icon }"
          :title="icon"
          @click="selectIcon(icon)"
        >
          <ArtSvgIcon :icon="icon" class="text-xl" />
        </div>
        <div v-if="filteredIcons.length === 0" class="no-icons">
          暂无匹配的图标
        </div>
      </div>

      <template #footer>
        <ElButton @click="showPopover = false">取消</ElButton>
        <ElButton type="primary" @click="confirmSelect">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { ElInput, ElButton, ElDialog, ElTabs, ElTabPane } from 'element-plus'
  import { iconCategories, presetIcons, type IconCategoryKey } from './presetIcons'

  defineOptions({ name: 'ArtIconPicker' })

  interface Props {
    modelValue?: string
    placeholder?: string
    clearable?: boolean
    disabled?: boolean
  }

  interface Emits {
    (e: 'update:modelValue', value: string): void
    (e: 'change', value: string): void
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: '',
    placeholder: '请输入或选择图标',
    clearable: true,
    disabled: false
  })

  const emit = defineEmits<Emits>()

  const inputValue = ref(props.modelValue)
  const showPopover = ref(false)
  const searchKeyword = ref('')
  const activeTab = ref<IconCategoryKey>('RemixIcon')
  const tempSelected = ref('')

  // 当前分类的图标
  const currentCategoryIcons = computed(() => {
    return presetIcons[activeTab.value] || []
  })

  // 过滤后的图标
  const filteredIcons = computed(() => {
    if (!searchKeyword.value) {
      return currentCategoryIcons.value
    }
    const keyword = searchKeyword.value.toLowerCase()
    return currentCategoryIcons.value.filter(icon => 
      icon.toLowerCase().includes(keyword)
    )
  })

  // 监听 props 变化
  watch(() => props.modelValue, (val) => {
    inputValue.value = val
  })

  // 打开弹窗时初始化
  watch(showPopover, (val) => {
    if (val) {
      tempSelected.value = props.modelValue
      searchKeyword.value = ''
    }
  })

  const handleInput = (val: string): void => {
    emit('update:modelValue', val)
    emit('change', val)
  }

  const handleClear = (): void => {
    emit('update:modelValue', '')
    emit('change', '')
  }

  const selectIcon = (icon: string): void => {
    tempSelected.value = icon
    inputValue.value = icon
    emit('update:modelValue', icon)
  }

  const confirmSelect = (): void => {
    emit('change', tempSelected.value)
    showPopover.value = false
  }
</script>

<style scoped>
  .art-icon-picker {
    width: 100%;
  }

  .icon-tabs {
    margin-bottom: 16px;
  }

  .icon-grid {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    gap: 8px;
    max-height: 320px;
    overflow-y: auto;
    padding: 4px;
  }

  .icon-item {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    aspect-ratio: 1;
    border: 1px solid var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;
    color: var(--el-text-color-regular);
  }

  .icon-item:hover {
    border-color: var(--el-color-primary);
    color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
  }

  .icon-item.is-selected {
    border-color: var(--el-color-primary);
    color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-8);
  }

  .no-icons {
    grid-column: 1 / -1;
    text-align: center;
    padding: 40px 0;
    color: var(--el-text-color-placeholder);
  }
</style>
