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
        v-loading="loading"
        class="min-h-0 flex-1 [&_.el-scrollbar__wrap]:overflow-x-hidden [&_.el-scrollbar__view]:min-h-full"
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
                v-if="!item.isRead"
                class="mt-1 size-2 shrink-0 rounded-full bg-danger"
              ></span>
            </div>

            <div class="mt-2 line-clamp-2 text-xs leading-5 text-g-500">
              {{ item.summary || '点击查看完整消息内容' }}
            </div>

            <div class="mt-3 flex items-center justify-between gap-3">
              <span
                class="rounded-full px-2 py-1 text-[11px] font-medium"
                :class="getTypeSoftClass(item.type)"
              >
                {{ getTypeText(item.type) }}
              </span>
              <span class="shrink-0 text-xs text-g-500">{{
                formatDateTime(item.publishedAt)
              }}</span>
            </div>
          </button>
        </div>

        <div v-else class="flex min-h-full items-center justify-center p-6">
          <ElEmpty
            :description="searchQuery ? '没有找到匹配的消息' : '暂无消息'"
            :image-size="120"
          />
        </div>
      </ElScrollbar>
    </aside>

    <section class="flex min-h-0 flex-1 flex-col">
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

            <ElSpace wrap>
              <ElButton
                v-if="!detail.isRead"
                type="primary"
                plain
                @click="markAsRead(detail)"
              >
                标记已读
              </ElButton>
            </ElSpace>
          </div>

          <div class="mt-5 grid grid-cols-2 gap-3 xl:grid-cols-4">
            <div class="art-surface-muted p-3">
              <div class="text-xs text-g-500">消息类型</div>
              <div class="mt-2 flex items-center gap-2 text-sm font-medium text-g-900">
                <span class="size-2 rounded-full" :class="getTypeDotClass(detail.type)"></span>
                {{ getTypeText(detail.type) }}
              </div>
            </div>
            <div class="art-surface-muted p-3">
              <div class="text-xs text-g-500">阅读状态</div>
              <div class="mt-2 text-sm font-medium text-g-900">
                {{ detail.isRead ? '已读' : '未读' }}
              </div>
            </div>
            <div class="art-surface-muted p-3">
              <div class="text-xs text-g-500">发送人</div>
              <div class="mt-2 text-sm font-medium text-g-900">
                {{ detail.createdBy || '系统' }}
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
              <ElTag :type="getTypeTagType(detail.type)">{{ getTypeText(detail.type) }}</ElTag>
              <ElTag :type="detail.isRead ? 'info' : 'danger'">
                {{ detail.isRead ? '已读' : '未读' }}
              </ElTag>
              <ElTag v-if="detail.readAt" type="info">
                阅读于：{{ formatDateTime(detail.readAt) }}
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
  import { Search } from '@element-plus/icons-vue'
  import Vditor from 'vditor'
  import 'vditor/dist/index.css'
  import { useAutoLayoutHeight } from '@/hooks/core/useLayoutHeight'

  defineOptions({ name: 'DashboardNotice' })

  const { containerMinHeight } = useAutoLayoutHeight()

  type NotificationType = 'SYSTEM' | 'ANNOUNCEMENT' | 'ALERT' | 'UPDATE'

  interface NotificationItem {
    id: number
    title: string
    summary: string
    content: string
    type: NotificationType
    isRead: boolean
    publishedAt: string
    readAt?: string
    createdBy: string
  }

  const searchQuery = ref('')
  const onlyUnread = ref(false)
  const loading = ref(false)
  const activeId = ref<number>()
  const detail = ref<NotificationItem>()
  const previewRef = ref<HTMLDivElement>()

  // Mock 数据 - 改为 Markdown 格式
  const mockData: NotificationItem[] = [
    {
      id: 1,
      title: '系统维护通知',
      summary: '系统将于今晚22:00-24:00进行例行维护，届时服务将暂时不可用',
      content: `## 系统维护通知

尊敬的用户：

为了给您提供更好的服务体验，我们将于**今晚22:00-24:00**进行系统例行维护。

### 维护内容

- 服务器性能优化
- 数据库索引重建
- 安全补丁更新

维护期间系统将暂时不可用，给您带来的不便敬请谅解。`,
      type: 'SYSTEM',
      isRead: false,
      publishedAt: '2024-01-15 14:30:00',
      createdBy: '系统管理员'
    },
    {
      id: 2,
      title: '新功能上线公告',
      summary: '任务流程管理新增自动化审批功能，提升工作效率',
      content: `## 新功能上线公告

我们很高兴地宣布，任务流程管理模块已上线全新的**自动化审批功能**！

### 主要特性

- 智能审批规则配置
- 多级审批流程支持
- 审批进度实时追踪
- 消息通知及时推送

欢迎体验使用，如有任何问题请联系技术支持团队。`,
      type: 'ANNOUNCEMENT',
      isRead: true,
      publishedAt: '2024-01-14 10:00:00',
      readAt: '2024-01-14 11:23:00',
      createdBy: '产品团队'
    },
    {
      id: 3,
      title: '存储空间预警',
      summary: '您的存储空间使用率已达85%，请及时清理或扩容',
      content: `## 存储空间预警

您好，检测到您的存储空间使用情况如下：

| 项目 | 已使用 | 总容量 | 使用率 |
|------|--------|--------|--------|
| 文件存储 | 42.5 GB | 50 GB | 85% |

建议您：

1. 清理不需要的文件
2. 归档历史数据
3. 联系管理员扩容`,
      type: 'ALERT',
      isRead: false,
      publishedAt: '2024-01-15 09:15:00',
      createdBy: '监控系统'
    },
    {
      id: 4,
      title: '版本更新提醒',
      summary: 'v2.5.0 版本已发布，包含多项性能优化和bug修复',
      content: `## 版本更新 v2.5.0

### 新增功能

- 支持批量操作任务
- 新增数据导出功能
- 优化移动端适配

### 性能优化

- 列表加载速度提升40%
- 内存占用减少30%

### Bug修复

- 修复文件上传偶现失败问题
- 修复时间选择器显示异常`,
      type: 'UPDATE',
      isRead: true,
      publishedAt: '2024-01-13 16:00:00',
      readAt: '2024-01-13 16:45:00',
      createdBy: '开发团队'
    },
    {
      id: 5,
      title: '安全提醒',
      summary: '检测到您的密码已超过90天未更改，建议定期更新密码',
      content: `## 安全提醒

为了保障您的账号安全，我们建议您定期更新密码。

### 安全建议

- 密码长度至少8位
- 包含大小写字母、数字和特殊符号
- 不要使用常见密码
- 不要在多个网站使用相同密码
- 定期更换密码（建议90天）

您可以在**个人中心 > 安全设置**中修改密码。`,
      type: 'SYSTEM',
      isRead: false,
      publishedAt: '2024-01-12 08:00:00',
      createdBy: '安全中心'
    },
    {
      id: 6,
      title: '活动通知',
      summary: '春节假期服务安排及优惠活动通知',
      content: `## 春节假期服务安排

尊敬的用户，春节将至，特此通知假期服务安排：

### 假期时间

2024年2月10日至2月17日

### 服务安排

- 系统正常运行，7x24小时可用
- 在线客服工作时间：10:00-18:00
- 紧急技术支持保持在线

### 优惠活动

春节期间购买年度套餐享**8折优惠**，详情请咨询客服。`,
      type: 'ANNOUNCEMENT',
      isRead: true,
      publishedAt: '2024-01-10 14:00:00',
      readAt: '2024-01-10 15:20:00',
      createdBy: '运营团队'
    }
  ]

  const records = ref<NotificationItem[]>([...mockData])


  const totalCount = computed(() => mockData.length)
  const unreadCount = computed(() => records.value.filter((item) => !item.isRead).length)

  /**
   * 根据搜索关键词和筛选条件过滤消息列表
   */
  const filteredRecords = computed(() => {
    let filtered = records.value

    // 筛选未读
    if (onlyUnread.value) {
      filtered = filtered.filter((item) => !item.isRead)
    }

    // 搜索关键词
    const keyword = searchQuery.value.trim().toLowerCase()
    if (keyword) {
      filtered = filtered.filter((item) => {
        return [item.title, item.summary || '', getTypeText(item.type)].some((field) =>
          String(field).toLowerCase().includes(keyword)
        )
      })
    }

    return filtered
  })

  onMounted(() => {
    // 不自动选中第一条消息，保持空状态
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
  function openDetail(item: NotificationItem) {
    activeId.value = item.id
    detail.value = item

    // 自动标记为已读
    if (!item.isRead) {
      item.isRead = true
      item.readAt = new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      })
    }

    // 确保 DOM 更新后再渲染 Markdown
    nextTick(() => {
      if (previewRef.value && item.content) {
        renderMarkdown(item.content)
      }
    })
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
   * 标记单条消息为已读
   */
  function markAsRead(item: NotificationItem) {
    if (!item.isRead) {
      item.isRead = true
      item.readAt = new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      })
    }
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
      if (!item.isRead) {
        item.isRead = true
        item.readAt = now
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

  /**
   * 获取消息类型文案
   */
  function getTypeText(type: NotificationType) {
    return (
      {
        SYSTEM: '系统通知',
        ANNOUNCEMENT: '公告通知',
        ALERT: '预警通知',
        UPDATE: '更新通知'
      }[type] || type
    )
  }

  /**
   * 获取消息类型图标
   */
  function getTypeIcon(type: NotificationType) {
    return (
      {
        SYSTEM: 'ri:notification-3-line',
        ANNOUNCEMENT: 'ri:megaphone-line',
        ALERT: 'ri:alarm-warning-line',
        UPDATE: 'ri:rocket-line'
      }[type] || 'ri:notification-3-line'
    )
  }

  /**
   * 获取消息列表项的类型主样式
   */
  function getTypeClass(type: NotificationType) {
    return (
      {
        SYSTEM: 'bg-theme/12 text-theme',
        ANNOUNCEMENT: 'bg-success/12 text-success',
        ALERT: 'bg-danger/12 text-danger',
        UPDATE: 'bg-warning/12 text-warning'
      }[type] || 'bg-theme/12 text-theme'
    )
  }

  /**
   * 获取消息详情区的类型浅色样式
   */
  function getTypeSoftClass(type: NotificationType) {
    return (
      {
        SYSTEM: 'bg-theme/10 text-theme',
        ANNOUNCEMENT: 'bg-success/10 text-success',
        ALERT: 'bg-danger/10 text-danger',
        UPDATE: 'bg-warning/10 text-warning'
      }[type] || 'bg-theme/10 text-theme'
    )
  }

  /**
   * 获取未读圆点的类型颜色样式
   */
  function getTypeDotClass(type: NotificationType) {
    return (
      {
        SYSTEM: 'bg-theme',
        ANNOUNCEMENT: 'bg-success',
        ALERT: 'bg-danger',
        UPDATE: 'bg-warning'
      }[type] || 'bg-theme'
    )
  }

  /**
   * 获取消息类型对应的标签颜色
   */
  function getTypeTagType(
    type: NotificationType
  ): 'primary' | 'success' | 'warning' | 'danger' {
    switch (type) {
      case 'ANNOUNCEMENT':
        return 'success'
      case 'ALERT':
        return 'danger'
      case 'UPDATE':
        return 'warning'
      default:
        return 'primary'
    }
  }
</script>

<style scoped>
  /* Vditor 预览样式会自动应用 */
</style>