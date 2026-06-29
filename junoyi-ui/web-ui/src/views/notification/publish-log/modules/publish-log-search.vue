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
import { fetchGetUserOptions } from '@/api/system/user'
import { fetchGetNotificationOptions } from '@/api/notification/manage'

interface Props {
  modelValue: Record<string, any>
}

interface Emits {
  (e: 'update:modelValue', value: Record<string, any>): void
  (e: 'search', params: Record<string, any>): void
  (e: 'reset'): void
}

defineOptions({ name: 'PublishLogSearch' })

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formData = computed({
  get: () => props.modelValue,
  set: val => emit('update:modelValue', val)
})

const notificationOptions = ref<{ label: string; value: number }[]>([])

const loadNotificationOptions = async () => {
  const res = await fetchGetNotificationOptions()
  notificationOptions.value = (res || []).map((item: Api.Notification.NotificationOptionVO) => ({
    label: item.title,
    value: item.id
  }))
}

const userOptions = ref<{ label: string; value: number }[]>([])
const userLoading = ref(false)

const loadUserOptions = async (keyword?: string) => {
  userLoading.value = true
  try {
    const res = await fetchGetUserOptions({
      nickName: keyword || undefined
    })
    userOptions.value = (res || []).map((item: Api.System.SysUserVO) => ({
      label: item.nickName || item.userName,
      value: item.userId
    }))
  } finally {
    userLoading.value = false
  }
}

const handleUserRemoteSearch = (keyword: string) => {
  loadUserOptions(keyword)
}

onMounted(() => {
  loadNotificationOptions()
  loadUserOptions()
})

const formItems = computed(() => [
  {
    label: '通知',
    key: 'notificationId',
    type: 'select',
    props: {
      placeholder: '请选择通知',
      options: notificationOptions.value,
      clearable: true,
      filterable: true
    }
  },
  {
    label: '发布者',
    key: 'publishUserId',
    type: 'select',
    props: {
      placeholder: '请输入发布者昵称搜索',
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
    label: '发布时间',
    key: 'timeRange',
    type: 'daterange',
    props: {
      clearable: true,
      startPlaceholder: '开始日期',
      endPlaceholder: '结束日期',
      type: 'daterange',
      valueFormat: 'YYYY-MM-DD'
    }
  }
])

const handleReset = () => emit('reset')
const handleSearch = async () => emit('search', formData.value)
</script>
