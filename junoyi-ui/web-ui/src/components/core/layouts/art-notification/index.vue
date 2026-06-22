<!-- 通知组件 -->
<template>
  <div
    class="art-notification-panel art-card-sm !shadow-xl"
    :style="{
      transform: show ? 'scaleY(1)' : 'scaleY(0.9)',
      opacity: show ? 1 : 0
    }"
    v-show="visible"
    @click.stop
  >
    <div class="flex-cb px-3.5 mt-3.5">
      <span class="text-base font-medium text-g-800">{{ $t('notice.title') }}</span>
      <span class="text-xs text-g-800 px-1.5 py-1 c-p select-none rounded hover:bg-g-200">
        {{ $t('notice.btnRead') }}
      </span>
    </div>

    <ul class="box-border flex items-end w-full h-12.5 px-3.5 border-b-d">
      <li
        v-for="(item, index) in barList"
        :key="index"
        class="h-12 leading-12 mr-5 overflow-hidden text-[13px] text-g-700 c-p select-none"
        :class="{ 'bar-active': barActiveIndex === index }"
        @click="changeBar(index)"
      >
        {{ item.name }} ({{ item.num }})
      </li>
    </ul>

    <div class="w-full h-[calc(100%-95px)]">
      <div class="h-[calc(100%-60px)] overflow-y-scroll scrollbar-thin" v-loading="loading">
        <!-- 通知 -->
        <ul v-show="barActiveIndex === 0">
          <li
            v-for="(item, index) in noticeList"
            :key="index"
            class="box-border flex-c px-3.5 py-3.5 c-p last:border-b-0 hover:bg-g-200/60"
            @click="router.push('/dashboard/notice')"
          >
            <div
              class="size-9 leading-9 text-center rounded-lg flex-cc"
              :class="[getNoticeStyle(item.type).iconClass]"
            >
              <ArtSvgIcon class="text-lg !bg-transparent" :icon="getNoticeStyle(item.type).icon" />
            </div>
            <div class="w-[calc(100%-45px)] ml-3.5">
              <div class="flex items-center justify-between">
                <h4 class="flex-1 text-sm font-normal leading-5.5 text-g-900">{{ item.title }}</h4>
                <span v-if="!item.read" class="ml-2 size-2 shrink-0 rounded-full bg-danger"></span>
              </div>
              <p class="mt-1.5 text-xs text-g-500">{{ item.time }}</p>
            </div>
          </li>
        </ul>

        <!-- 消息 -->
        <ul v-show="barActiveIndex === 1">
          <li
            v-for="(item, index) in msgList"
            :key="index"
            class="box-border flex-c px-3.5 py-3.5 c-p last:border-b-0 hover:bg-g-200/60"
          >
            <div class="w-9 h-9">
              <img :src="item.avatar" class="w-full h-full rounded-lg" />
            </div>
            <div class="w-[calc(100%-45px)] ml-3.5">
              <h4 class="text-xs font-normal leading-5.5">{{ item.title }}</h4>
              <p class="mt-1.5 text-xs text-g-500">{{ item.time }}</p>
            </div>
          </li>
        </ul>

        <!-- 待办 -->
        <ul v-show="barActiveIndex === 2">
          <li
            v-for="(item, index) in pendingList"
            :key="index"
            class="box-border px-5 py-3.5 last:border-b-0"
          >
            <h4>{{ item.title }}</h4>
            <p class="text-xs text-g-500">{{ item.time }}</p>
          </li>
        </ul>

        <!-- 空状态 -->
        <div
          v-show="currentTabIsEmpty"
          class="relative top-25 h-full text-g-500 text-center !bg-transparent"
        >
          <ArtSvgIcon icon="system-uicons:inbox" class="text-5xl" />
          <p class="mt-3.5 text-xs !bg-transparent"
            >{{ $t('notice.text[0]') }}{{ barList[barActiveIndex].name }}</p
          >
        </div>
      </div>

      <div class="relative box-border w-full px-3.5">
        <ElButton class="w-full mt-3" @click="handleViewAll" v-ripple>
          {{ $t('notice.viewAll') }}
        </ElButton>
      </div>
    </div>

    <div class="h-25"></div>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref, watch, type Ref, type ComputedRef, onMounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { useRouter } from 'vue-router'
  import { fetchGetMyNotificationList } from '@/api/notification/notice'

  defineOptions({ name: 'ArtNotification' })

  interface NoticeItem {
    /** ID */
    id: number
    /** 标题 */
    title: string
    /** 摘要 */
    summary: string
    /** 时间 */
    time: string
    /** 类型 */
    type: string
    /** 类型标签 */
    typeLabel: string
    /** 是否已读 */
    read: boolean
  }

  interface MessageItem {
    /** 标题 */
    title: string
    /** 时间 */
    time: string
    /** 头像 */
    avatar: string
  }

  interface PendingItem {
    /** 标题 */
    title: string
    /** 时间 */
    time: string
  }

  interface BarItem {
    /** 名称 */
    name: ComputedRef<string>
    /** 数量 */
    num: number
  }

  interface NoticeStyle {
    /** 图标 */
    icon: string
    /** icon 样式 */
    iconClass: string
  }

  const { t } = useI18n()
  const router = useRouter()

  const props = defineProps<{
    value: boolean
  }>()

  const emit = defineEmits<{
    'update:value': [value: boolean]
  }>()

  const show = ref(false)
  const visible = ref(false)
  const barActiveIndex = ref(0)

  const useNotificationData = () => {
    // 通知数据
    const noticeList = ref<NoticeItem[]>([])
    const loading = ref(false)

    // 加载通知列表
    const loadNotifications = async () => {
      try {
        loading.value = true
        const res = await fetchGetMyNotificationList(1, 10) // 只加载最新 10 条
        noticeList.value = (res.list || []).map((item: any) => ({
          id: item.id,
          title: item.title,
          summary: item.summary || '点击查看详情',
          time: formatTime(item.publishedAt),
          type: getNoticeType(item.type),
          typeLabel: item.typeLabel,
          read: item.read
        }))
      } catch (error) {
        console.error('[Notification] 加载通知列表失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 格式化时间
    const formatTime = (dateStr: string): string => {
      if (!dateStr) return ''
      if (dateStr.includes('T')) {
        return dateStr.replace('T', ' ').substring(0, 16)
      }
      return dateStr.substring(0, 16)
    }

    // 根据类型获取通知样式类型
    const getNoticeType = (type: number): string => {
      // 根据后端的类型映射到前端样式类型
      const typeMap: Record<number, string> = {
        1: 'notice',   // 系统通知
        2: 'email',    // 公告通知
        3: 'message',  // 预警通知
        4: 'collection' // 更新通知
      }
      return typeMap[type] || 'notice'
    }

    // 消息数据（暂时保留为空）
    const msgList = ref<MessageItem[]>([])

    // 待办数据（暂时保留为空）
    const pendingList = ref<PendingItem[]>([])

    // 标签栏数据
    const barList = computed<BarItem[]>(() => [
      {
        name: computed(() => t('notice.bar[0]')),
        num: noticeList.value.length
      },
      {
        name: computed(() => t('notice.bar[1]')),
        num: msgList.value.length
      },
      {
        name: computed(() => t('notice.bar[2]')),
        num: pendingList.value.length
      }
    ])

    return {
      noticeList,
      msgList,
      pendingList,
      barList,
      loading,
      loadNotifications
    }
  }

  // 样式管理
  const useNotificationStyles = () => {
    const noticeStyleMap: Record<string, NoticeStyle> = {
      email: {
        icon: 'ri:mail-line',
        iconClass: 'bg-warning/12 text-warning'
      },
      message: {
        icon: 'ri:volume-down-line',
        iconClass: 'bg-success/12 text-success'
      },
      collection: {
        icon: 'ri:heart-3-line',
        iconClass: 'bg-danger/12 text-danger'
      },
      notice: {
        icon: 'ri:notification-3-line',
        iconClass: 'bg-theme/12 text-theme'
      }
    }

    const getNoticeStyle = (type: string): NoticeStyle => {
      const defaultStyle: NoticeStyle = {
        icon: 'ri:arrow-right-circle-line',
        iconClass: 'bg-theme/12 text-theme'
      }

      return noticeStyleMap[type] || defaultStyle
    }

    return {
      getNoticeStyle
    }
  }

  // 动画管理
  const useNotificationAnimation = () => {
    const showNotice = (open: boolean) => {
      if (open) {
        visible.value = true
        setTimeout(() => {
          show.value = true
        }, 5)
      } else {
        show.value = false
        setTimeout(() => {
          visible.value = false
        }, 350)
      }
    }

    return {
      showNotice
    }
  }

  // 标签页管理
  const useTabManagement = (
    noticeList: Ref<NoticeItem[]>,
    msgList: Ref<MessageItem[]>,
    pendingList: Ref<PendingItem[]>,
    businessHandlers: {
      handleNoticeAll: () => void
      handleMsgAll: () => void
      handlePendingAll: () => void
    }
  ) => {
    const changeBar = (index: number) => {
      barActiveIndex.value = index
    }

    // 检查当前标签页是否为空
    const currentTabIsEmpty = computed(() => {
      const tabDataMap = [noticeList.value, msgList.value, pendingList.value]

      const currentData = tabDataMap[barActiveIndex.value]
      return currentData && currentData.length === 0
    })

    const handleViewAll = () => {
      // 查看全部处理器映射
      const viewAllHandlers: Record<number, () => void> = {
        0: businessHandlers.handleNoticeAll,
        1: businessHandlers.handleMsgAll,
        2: businessHandlers.handlePendingAll
      }

      const handler = viewAllHandlers[barActiveIndex.value]
      handler?.()

      // 关闭通知面板
      emit('update:value', false)
    }

    return {
      changeBar,
      currentTabIsEmpty,
      handleViewAll
    }
  }

  // 业务逻辑处理
  const useBusinessLogic = () => {
    const handleNoticeAll = () => {
      // 跳转到通知页面
      router.push('/dashboard/notice')
    }

    const handleMsgAll = () => {
      // 处理查看全部消息（暂时不处理）
      console.log('查看全部消息')
    }

    const handlePendingAll = () => {
      // 处理查看全部待办（暂时不处理）
      console.log('查看全部待办')
    }

    return {
      handleNoticeAll,
      handleMsgAll,
      handlePendingAll
    }
  }

  // 组合所有逻辑
  const { noticeList, msgList, pendingList, barList, loading, loadNotifications } = useNotificationData()
  const { getNoticeStyle } = useNotificationStyles()
  const { showNotice } = useNotificationAnimation()
  const { handleNoticeAll, handleMsgAll, handlePendingAll } = useBusinessLogic()
  const { changeBar, currentTabIsEmpty, handleViewAll } = useTabManagement(
    noticeList,
    msgList,
    pendingList,
    { handleNoticeAll, handleMsgAll, handlePendingAll }
  )

  // 监听属性变化，打开时加载数据
  watch(
    () => props.value,
    (newValue) => {
      showNotice(newValue)
      if (newValue) {
        loadNotifications()
      }
    }
  )

  // 组件挂载时加载数据
  onMounted(() => {
    loadNotifications()
  })
</script>

<style scoped>
  @reference '@styles/core/tailwind.css';

  .art-notification-panel {
    @apply absolute 
    top-14.5 
    right-5 
    w-90 
    h-125 
    overflow-hidden 
    transition-all 
    duration-300
    origin-top 
    will-change-[top,left] 
    max-[640px]:top-[65px]
    max-[640px]:right-0
    max-[640px]:w-full 
    max-[640px]:h-[80vh];
  }

  .bar-active {
    color: var(--theme-color) !important;
    border-bottom: 2px solid var(--theme-color);
  }

  .scrollbar-thin::-webkit-scrollbar {
    width: 5px !important;
  }

  .dark .scrollbar-thin::-webkit-scrollbar-track {
    background-color: var(--default-box-color);
  }

  .dark .scrollbar-thin::-webkit-scrollbar-thumb {
    background-color: #222 !important;
  }
</style>
