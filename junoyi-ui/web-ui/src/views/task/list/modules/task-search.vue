<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    :showExpand="false"
    :isExpand="true"
    @reset="handleReset"
    @search="handleSearch"
  />
</template>

<script setup lang="ts">
import { fetchGetDictDataByType } from '@/api/system/dict'
import { fetchGetUserOptions } from '@/api/system/user'

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
const statusOptions = ref<{ label: string; value: number | string }[]>([])
const priorityOptions = ref<{ label: string; value: number | string }[]>([])
const userOptions = ref<{ label: string; value: number; avatar?: string }[]>([])
const userLoading = ref(false)

const formData = computed({
  get: () => props.modelValue,
  set: val => emit('update:modelValue', val)
})

const loadDictOptions = async () => {
  const [statusRes, priorityRes] = await Promise.all([
    fetchGetDictDataByType('task_status'),
    fetchGetDictDataByType('task_priority')
  ])

  statusOptions.value = (statusRes || []).map(item => ({
    label: item.dictLabel,
    value: Number(item.dictValue)
  }))

  priorityOptions.value = (priorityRes || []).map(item => ({
    label: item.dictLabel,
    value: Number(item.dictValue)
  }))
}

const loadUserOptions = async (keyword?: string) => {
  userLoading.value = true
  try {
    const res = await fetchGetUserOptions({
      nickName: keyword || undefined
    })
    userOptions.value = (res || []).map(item => ({
      label: item.nickName || item.userName,
      value: item.userId,
      avatar: item.avatar
    }))
  } finally {
    userLoading.value = false
  }
}

const handleUserRemoteSearch = (keyword: string) => {
  loadUserOptions(keyword)
}

onMounted(() => {
  loadDictOptions()
  loadUserOptions()
})

const formItems = computed(() => [
  {
    label: '任务标题',
    key: 'title',
    type: 'input',
    props: {
      placeholder: '请输入任务标题',
      clearable: true
    }
  },
  {
    label: '任务状态',
    key: 'status',
    type: 'select',
    props: {
      placeholder: '请选择状态',
      options: statusOptions.value,
      clearable: true
    }
  },
  {
    label: '优先级',
    key: 'priority',
    type: 'select',
    props: {
      placeholder: '请选择优先级',
      options: priorityOptions.value,
      clearable: true
    }
  },
  {
    label: '执行人',
    key: 'userId',
    type: 'select',
    props: {
      placeholder: '请输入昵称搜索执行人',
      options: userOptions.value,
      clearable: true,
      filterable: true,
      remote: true,
      reserveKeyword: true,
      loading: userLoading.value,
      remoteMethod: handleUserRemoteSearch
    }
  },
  {
    label: '任务时间',
    key: 'timeRange',
    type: 'daterange',
    props: {
      clearable: true,
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      type: 'datetimerange',
      valueFormat: 'YYYY-MM-DD HH:mm:ss'
    }
  }
])

const handleReset = () => emit('reset')
const handleSearch = async () => emit('search', formData.value)
</script>

