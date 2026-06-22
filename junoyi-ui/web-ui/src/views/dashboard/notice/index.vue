<!-- 仪表盘 - 消息通知 -->
<template>
  <div class="page-content flex !p-0 max-lg:flex-col" :style="{ height: containerMinHeight }">
    <aside
      class="box-border flex w-[360px] shrink-0 flex-col border-r-d max-lg:w-full max-lg:border-r-0 max-lg:border-b-d"
    >
      <div class="border-b-d px-5 pb-4 pt-5">
        <div class="flex-cb gap-3">
          <div>
            <div class="text-lg font-semibold text-g-900">消息通知</div>
            <div class="mt-1 text-sm text-g-500">查看系统消息与重要提醒</div>
          </div>
        </div>

        <div class="mt-4">
          <ElInput
            v-model="searchQuery"
            placeholder="搜索消息标题或内容"
            clearable
            :prefix-icon="Search"
          />
        </div>

        <div class="mt-4 grid grid-cols-2 gap-3">
          <div class="art-surface-muted p-3">
            <div class="text-xs text-g-500">全部消息</div>
            <div class="mt-2 text-2xl font-semibold text-g-900">{{ totalCount }}</div>
          </div>
          <div class="art-surface-muted p-3">
            <div class="text-xs text-g-500">未读消息</div>
            <div class="mt-2 text-2xl font-semibold text-g-900">{{ unreadCount }}</div>
          </div>
        </div>

        <div class="mt-4 flex flex-wrap items-center gap-3">
          <ElRadioGroup v-model="onlyUnread" @change="handleFilterChange">
            <ElRadioButton :label="false">全部</ElRadioButton>
            <ElRadioButton :label="true">未读</ElRadioButton>
          </ElRadioGroup>
          <ElButton text type="primary" @click="handleReadAll" :disabled="!unreadCount">
            全部已读
          </ElButton>
        </div>

        <div class="mt-3 text-xs text-g-500">
          当前展示 {{ filteredRecords.length }} 条{{ onlyUnread ? '未读' : '' }}消息
        </div>
      </div>

      <ElScrollbar
        ref="scrollbarRef"
        v-loading="loading"
        class="min-h-0 flex-1 [&_.el-scrollbar__wrap]:overflow-x-hidden [&_.el-scrollbar__view]:min-h-full"
        @scroll="handleScroll"
      >
        <div v-if="filteredRecords.length" class="p-3">
          <button
            v-for="item in filteredRecords"
            :key="item.id"
            type="button"
            class="relative mb-2 w-full appearance-none art-surface-muted p-4 text-left tad-200 c-p hover:bg-active-color/30"
            :class="item.id === activeId ? '!border-[var(--default-border)] !bg-active-color' : ''"
            @click="openDetail(item)"
          >
            <div class="flex items-start gap-2">
              <div class="line-clamp-1 flex-1 text-sm font-semibold leading-6 text-g-900">
                {{ item.title }}
              </div>
              <span
                v-if="!item.read"
                class="mt-1 size-2 shrink-0 rounded-full bg-danger"
              ></span>
            </div>

            <div class="mt-2 line-clamp-2 text-xs leading-5 text-g-500">
              {{ item.summary || '点击查看完整消息内容' }}
            </div>

            <div class="mt-3 flex items-center justify-between gap-3">
              <span
                class="rounded-full px-2 py-1 text-[11px] font-medium"
                :class="item.typeType"
              >
                {{ item.typeLabel }}
              </span>
              <span class="shrink-0 text-xs text-g-500">{{
                formatDateTime(item.publishedAt)
              }}</span>
            </div>
          </button>

          <!-- 加载更多提示 -->
          <div v-if="loadingMore" class="py-4 text-center text-sm text-g-500">
            <ElIcon class="is-loading mr-2"><Loading /></ElIcon>
            加载中...
          </div>
          <div v-else-if="!hasMore && records.length > 0" class="py-4 text-center text-xs text-g-400">
            没有更多了
          </div>
        </div>

        <div v-else class="flex min-h-full items-center justify-center p-6">
          <ElEmpty
            :description="searchQuery ? '没有找到匹配的消息' : '暂无消息'"
            :image-size="120"
          />
        </div>
      </ElScrollbar>
    </aside>

    <section class="flex min-h-0 flex-1 flex-col" v-loading="detailLoading">
      <template v-if="detail">
        <div class="border-b-d px-6 pb-5 pt-5 max-md:px-4">
          <div class="flex flex-wrap items-start justify-between gap-4">
            <div class="min-w-0">
              <div class="flex items-center gap-3">
                <div class="min-w-0">
                  <div class="line-clamp-2 text-xl font-semibold text-g-900 max-md:text-lg">
                    {{ detail.title }}
                  </div>
                  <div class="mt-1 text-sm text-g-500">
                    {{ detail.summary || '这里展示消息的详细内容' }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-5 grid grid-cols-2 gap-3 xl:grid-cols-4">
            <div class="art-surface-muted p-3">
              <div class="text-xs text-g-500">消息类型</div>
              <div class="mt-2 flex items-center gap-2 text-sm font-medium text-g-900">
                <span class="size-2 rounded-full bg-theme"></span>
                {{ detail.typeLabel }}
              </div>
            </div>
            <div class="art-surface-muted p-3">
              <div class="text-xs text-g-500">阅读状态</div>
              <div class="mt-2 text-sm font-medium text-g-900">
                {{ detail.read ? '已读' : '未读' }}
              </div>
            </div>
            <div class="art-surface-muted p-3">
              <div class="text-xs text-g-500">发送人</div>
              <div class="mt-2 text-sm font-medium text-g-900">
                {{ detail.publishedBy || '系统' }}
              </div>
            </div>
            <div class="art-surface-muted p-3">
              <div class="text-xs text-g-500">发布时间</div>
              <div class="mt-2 text-sm font-medium text-g-900">
                {{ formatDateTime(detail.publishedAt) }}
              </div>
            </div>
          </div>
        </div>

        <ElScrollbar class="min-h-0 flex-1">
          <div class="px-6 py-5 max-md:px-4">
            <div class="mb-4 flex flex-wrap gap-2">
              <ElTag :type="detail.typeType as any">{{ detail.typeLabel }}</ElTag>
              <ElTag :type="detail.read ? 'info' : 'danger'">
                {{ detail.read ? '已读' : '未读' }}
              </ElTag>
              <ElTag v-if="detail.readTime" type="info">
                阅读于：{{ formatDateTime(detail.readTime) }}
              </ElTag>
            </div>

            <!-- Markdown 渲染器 -->
            <div ref="previewRef" class="art-surface-sm px-5 py-5"></div>
          </div>
        </ElScrollbar>
      </template>

      <div v-else class="flex flex-1 items-center justify-center">
        <ElEmpty description="请选择左侧消息查看详情" :image-size="120" />
      </div>
    </section>
  </div>
</template>


<script setup lang="ts">
  import { Search, Loading } from '@element-plus/icons-vue'
  import Vditor from 'vditor'
  import 'vditor/dist/index.css'
  import { useAutoLayoutHeight } from '@/hooks/core/useLayoutHeight'
  import { fetchGetMyNotificationList, fetchGetMyNotificationDetail } from '@/api/notification/notice'

  defineOptions({ name: 'DashboardNotice' })

  const { containerMinHeight } = useAutoLayoutHeight()

  interface NotificationItem {
    id: number
    title: string
    summary: string
    type: number
    typeLabel: string
    typeType: string
    read: boolean
    readTime: string
    publishedBy: string
    publishedAt: string
  }

  interface NotificationDetail extends NotificationItem {
    content: string
  }

  const searchQuery = ref('')
  const onlyUnread = ref(false)
  const loading = ref(false)
  const activeId = ref<number>()
  const detail = ref<NotificationDetail>()
  const previewRef = ref<HTMLDivElement>()
  const scrollbarRef = ref<any>()

  const records = ref<NotificationItem[]>([])
  const currentPage = ref(1)
  const pageSize = ref(20)
  const hasMore = ref(true)
  const loadingMore = ref(false)
  const detailLoading = ref(false)

  const totalCount = computed(() => records.value.length)
  const unreadCount = computed(() => records.value.filter((item) => !item.read).length)

  /**
   * 根据搜索关键词和筛选条件过滤消息列表
   */
  const filteredRecords = computed(() => {
    let filtered = records.value

    // 筛选未读
    if (onlyUnread.value) {
      filtered = filtered.filter((item) => !item.read)
    }

    // 搜索关键词
    const keyword = searchQuery.value.trim().toLowerCase()
    if (keyword) {
      filtered = filtered.filter((item) => {
        return [item.title, item.summary || '', item.typeLabel].some((field) =>
          String(field).toLowerCase().includes(keyword)
        )
      })
    }

    return filtered
  })

  /**
   * 加载通知列表
   */
  async function loadNotifications(isLoadMore = false) {
    if (loadingMore.value || (!hasMore.value && isLoadMore)) {
      return
    }

    try {
      if (isLoadMore) {
        loadingMore.value = true
      } else {
        loading.value = true
      }

      const res = await fetchGetMyNotificationList(currentPage.value, pageSize.value)
      const newList = res.list || []

      if (isLoadMore) {
        records.value = [...records.value, ...newList]
      } else {
        records.value = newList
      }

      // 判断是否还有更多数据
      hasMore.value = newList.length >= pageSize.value

      if (hasMore.value) {
        currentPage.value++
      }
    } catch (error) {
      console.error('加载通知列表失败:', error)
      ElMessage.error('加载通知列表失败')
    } finally {
      loading.value = false
      loadingMore.value = false
    }
  }

  /**
   * 滚动监听
   */
  function handleScroll({ scrollTop }: { scrollTop: number, scrollLeft: number }) {
    const wrapElement = scrollbarRef.value?.wrapRef as HTMLElement
    if (!wrapElement) return

    const scrollHeight = wrapElement.scrollHeight
    const clientHeight = wrapElement.clientHeight
    const threshold = 50 // 距离底部50px时开始加载

    if (scrollTop + clientHeight >= scrollHeight - threshold) {
      handleScrollToBottom()
    }
  }

  /**
   * 滚动到底部加载更多
   */
  function handleScrollToBottom() {
    if (!loading.value && hasMore.value && !loadingMore.value) {
      loadNotifications(true)
    }
  }

  onMounted(() => {
    loadNotifications()
  })

  /**
   * 监听 detail 变化，重新渲染 Markdown（用于非点击触发的场景）
   */
  watch(
    () => detail.value?.content,
    (newContent) => {
      if (newContent) {
        nextTick(() => {
          if (previewRef.value) {
            renderMarkdown(newContent)
          }
        })
      }
    }
  )

  /**
   * 打开消息详情
   */
  async function openDetail(item: NotificationItem) {
    activeId.value = item.id
    detail.value = undefined

    try {
      detailLoading.value = true

      // 调用详情接口（会自动标记为已读）
      const res = await fetchGetMyNotificationDetail(item.id)
      detail.value = res

      // 更新列表中的已读状态
      item.read = res.read
      item.readTime = res.readTime

      // 渲染 Markdown
      nextTick(() => {
        if (previewRef.value && res.content) {
          renderMarkdown(res.content)
        }
      })
    } catch (error: any) {
      console.error('获取通知详情失败:', error)
      ElMessage.error('获取通知详情失败')
    } finally {
      detailLoading.value = false
    }
  }

  /**
   * 渲染 Markdown 内容
   */
  function renderMarkdown(content: string) {
    if (!previewRef.value) return

    // 清空之前的内容
    previewRef.value.innerHTML = ''

    Vditor.preview(previewRef.value, content, {
      mode: 'light',
      markdown: {
        toc: false,
      },
      hljs: {
        style: 'github',
        enable: true
      }
    })
  }

  /**
   * 全部标记为已读
   */
  function handleReadAll() {
    const now = new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    })

    records.value.forEach((item) => {
      if (!item.read) {
        item.read = true
        item.readTime = now
      }
    })

    ElMessage.success('已全部标记为已读')
  }

  /**
   * 切换未读筛选
   */
  function handleFilterChange() {
    searchQuery.value = ''

    // 切换筛选时清空选中状态
    detail.value = undefined
    activeId.value = undefined
  }

  /**
   * 格式化日期时间
   */
  function formatDateTime(dateStr: string | undefined): string {
    if (!dateStr) return '-'
    if (dateStr.includes('T')) {
      return dateStr.replace('T', ' ').substring(0, 19)
    }
    return dateStr
  }
</script>

<style scoped>
  /* Vditor 预览样式会自动应用 */
</style>