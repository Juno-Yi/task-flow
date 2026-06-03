<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    :showExpand="false"
    @reset="handleReset"
    @search="handleSearch"
  >
  </ArtSearchBar>
</template>

<script setup lang="ts">
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

  /**
   * 表单数据双向绑定
   */
  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  /**
   * 角色状态选项
   */
  const statusOptions = [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 }
  ]

  /**
   * 搜索表单配置项
   */
  const formItems = computed(() => [
    {
      label: '角色名称',
      key: 'roleName',
      type: 'input',
      props: {
        placeholder: '请输入角色名称',
        clearable: true
      }
    },
    {
      label: '角色标识',
      key: 'roleKey',
      type: 'input',
      props: {
        placeholder: '请输入角色标识',
        clearable: true
      }
    },
    {
      label: '状态',
      key: 'status',
      type: 'select',
      props: {
        placeholder: '请选择',
        options: statusOptions,
        clearable: true
      }
    }
  ])

  /**
   * 处理重置事件
   */
  const handleReset = () => {
    emit('reset')
  }

  /**
   * 处理搜索事件
   */
  const handleSearch = async () => {
    emit('search', formData.value)
  }
</script>
