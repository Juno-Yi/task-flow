<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    :rules="rules"
    @reset="handleReset"
    @search="handleSearch"
  />
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue'
  import { fetchGetDictDataByType } from '@/api/system/dict'

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

  // 表单数据双向绑定
  const searchBarRef = ref()
  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  // 校验规则
  const rules = {}

  // 字典数据
  const platformOptions = ref<Array<{ label: string; value: string }>>([])
  const statusOptions = ref<Array<{ label: string; value: number }>>([])

  // 加载字典数据
  const loadDictData = async () => {
    try {
      // 加载平台字典
      const platformDict = await fetchGetDictDataByType('oauth_platform')
      platformOptions.value = platformDict.map((item) => ({
        label: item.dictLabel,
        value: item.dictValue
      }))

      // 加载状态字典
      const statusDict = await fetchGetDictDataByType('oauth_status')
      statusOptions.value = statusDict.map((item) => ({
        label: item.dictLabel,
        value: parseInt(item.dictValue)
      }))
    } catch (error) {
      console.error('加载字典数据失败:', error)
    }
  }

  // 组件挂载时加载字典
  loadDictData()

  // 表单配置
  const formItems = computed(() => [
    {
      label: '平台',
      key: 'platform',
      type: 'select',
      props: {
        placeholder: '请选择平台',
        clearable: true,
        options: platformOptions.value
      }
    },
    {
      label: '状态',
      key: 'status',
      type: 'select',
      props: {
        placeholder: '请选择状态',
        clearable: true,
        options: statusOptions.value
      }
    }
  ])

  // 搜索
  const handleSearch = () => {
    emit('search', formData.value)
  }

  // 重置
  const handleReset = () => {
    emit('reset')
  }
</script>

