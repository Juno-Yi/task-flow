<!-- 系统参数搜索组件 -->
<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="searchItems"
    @search="handleSearch"
    @reset="handleReset"
  />
</template>

<script setup lang="ts">
  defineOptions({ name: 'ConfigSearch' })

  interface Props {
    modelValue: Record<string, any>
  }

  interface Emits {
    (e: 'update:modelValue', value: Record<string, any>): void
    (e: 'search', params: Record<string, any>): void
    (e: 'reset'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const searchBarRef = ref()

  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  // 搜索项配置
  const searchItems = computed(() => [
    {
      label: '参数名称',
      key: 'configName',
      type: 'input',
      placeholder: '请输入参数名称',
      clearable: true
    },
    {
      label: '参数键名',
      key: 'configKey',
      type: 'input',
      placeholder: '请输入参数键名',
      clearable: true
    },
    {
      label: '参数类型',
      key: 'configType',
      type: 'select',
      props: {
        placeholder: '请选择',
        clearable: true,
        options: [
          { label: '文本', value: 'text' },
          { label: '数字', value: 'number' },
          { label: '布尔', value: 'boolean' },
          { label: 'JSON', value: 'json' }
        ]
      }
    },
    {
      label: '系统内置',
      key: 'isSystem',
      type: 'select',
      props: {
        placeholder: '请选择',
        clearable: true,
        options: [
          { label: '是', value: 'Y' },
          { label: '否', value: 'N' }
        ]
      }
    }
  ])

  /**
   * 搜索
   */
  const handleSearch = async () => {
    await searchBarRef.value?.validate()
    emit('search', formData.value)
  }

  /**
   * 重置
   */
  const handleReset = () => {
    emit('reset')
  }
</script>
