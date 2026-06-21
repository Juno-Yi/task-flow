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
            class="relative mb-2 w-full appearance-none art-surface-muted p-3 text-left tad-200 c-p hover:bg-active-color/30"
            :class="item.id === activeId ? '!border-[var(--default-border)] !bg-active-color' : ''"
            @click="openDetail(item)"
          >
            <div class="flex items-start gap-3">
              <div
                class="flex size-11 shrink-0 items-center justify-center rounded-custom-sm"
                :class="getTypeClass(item.type)"
              >
                <ArtSvgIcon :icon="getTypeIcon(item.type)" class="text-lg" />
              </div>

              <div class="min-w-0 flex-1">
                <div class="flex items-start gap-2">
                  <div class="line-clamp-1 flex-1 text-sm font-semibold leading-6 text-g-900">
                    {{ item.title }}
                  </div>
                  <span
                    v-if="!item.isRead"
                    class="mt-2 size-2 shrink-0 rounded-full bg-danger"
                  ></span>
                </div>

                <div class="mt-1 line-clamp-2 text-xs leading-5 text-g-500">
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
              </div>
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
                <div
                  class="flex size-12 shrink-0 items-center justify-center rounded-custom-sm"
                  :class="getTypeClass(detail.type)"
                >
                  <ArtSvgIcon :icon="getTypeIcon(detail.type)" class="text-xl" />
                </div>
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

            <div
              v-if="detail.summary"
              class="mb-5 art-surface-muted px-4 py-3 text-sm leading-6 text-g-600"
            >
              {{ detail.summary }}
            </div>

            <div
              class="detail-content art-surface-sm px-5 py-5 markdown-body [&_img]:h-auto [&_img]:max-w-full [&_img]:rounded-[8px] [&_table]:table [&_table]:w-full"
              v-html="detail.content"
            ></div>
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

  // Mock 数据
  const mockData: NotificationItem[] = [
    {
      id: 1,
      title: '系统维护通知',
      summary: '系统将于今晚22:00-24:00进行例行维护，届时服务将暂时不可用',
      content: `
        <h2>系统维护通知</h2>
        <p>尊敬的用户：</p>
        <p>为了给您提供更好的服务体验，我们将于<strong>今晚22:00-24:00</strong>进行系统例行维护。</p>
        <h3>维护内容</h3>
        <ul>
          <li>服务器性能优化</li>
          <li>数据库索引重建</li>
          <li>安全补丁更新</li>
        </ul>
        <p>维护期间系统将暂时不可用，给您带来的不便敬请谅解。</p>
      `,
      type: 'SYSTEM',
      isRead: false,
      publishedAt: '2024-01-15 14:30:00',
      createdBy: '系统管理员'
    },
    {
      id: 2,
      title: '新功能上线公告',
      summary: '任务流程管理新增自动化审批功能，提升工作效率',
      content: `
        <h2>新功能上线公告</h2>
        <p>我们很高兴地宣布，任务流程管理模块已上线全新的<strong>自动化审批功能</strong>！</p>
        <h3>主要特性</h3>
        <ul>
          <li>智能审批规则配置</li>
          <li>多级审批流程支持</li>
          <li>审批进度实时追踪</li>
          <li>消息通知及时推送</li>
        </ul>
        <p>欢迎体验使用，如有任何问题请联系技术支持团队。</p>
      `,
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
      content: `
        <h2>存储空间预警</h2>
        <p>您好，检测到您的存储空间使用情况如下：</p>
        <table>
          <tr>
            <th>项目</th>
            <th>已使用</th>
            <th>总容量</th>
            <th>使用率</th>
          </tr>
          <tr>
            <td>文件存储</td>
            <td>42.5 GB</td>
            <td>50 GB</td>
            <td>85%</td>
          </tr>
        </table>
        <p>建议您：</p>
        <ol>
          <li>清理不需要的文件</li>
          <li>归档历史数据</li>
          <li>联系管理员扩容</li>
        </ol>
      `,
      type: 'ALERT',
      isRead: false,
      publishedAt: '2024-01-15 09:15:00',
      createdBy: '监控系统'
    },
    {
      id: 4,
      title: '版本更新提醒',
      summary: 'v2.5.0 版本已发布，包含多项性能优化和bug修复',
      content: `
        <h2>版本更新 v2.5.0</h2>
        <h3>新增功能</h3>
        <ul>
          <li>支持批量操作任务</li>
          <li>新增数据导出功能</li>
          <li>优化移动端适配</li>
        </ul>
        <h3>性能优化</h3>
        <ul>
          <li>列表加载速度提升40%</li>
          <li>内存占用减少30%</li>
        </ul>
        <h3>Bug修复</h3>
        <ul>
          <li>修复文件上传偶现失败问题</li>
          <li>修复时间选择器显示异常</li>
        </ul>
      `,
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
      content: `
        <h2>安全提醒</h2>
        <p>为了保障您的账号安全，我们建议您定期更新密码。</p>
        <h3>安全建议</h3>
        <ul>
          <li>密码长度至少8位</li>
          <li>包含大小写字母、数字和特殊符号</li>
          <li>不要使用常见密码</li>
          <li>不要在多个网站使用相同密码</li>
          <li>定期更换密码（建议90天）</li>
        </ul>
        <p>您可以在<strong>个人中心 > 安全设置</strong>中修改密码。</p>
      `,
      type: 'SYSTEM',
      isRead: false,
      publishedAt: '2024-01-12 08:00:00',
      createdBy: '安全中心'
    },
    {
      id: 6,
      title: '活动通知',
      summary: '春节假期服务安排及优惠活动通知',
      content: `
        <h2>春节假期服务安排</h2>
        <p>尊敬的用户，春节将至，特此通知假期服务安排：</p>
        <h3>假期时间</h3>
        <p>2024年2月10日至2月17日</p>
        <h3>服务安排</h3>
        <ul>
          <li>系统正常运行，7x24小时可用</li>
          <li>在线客服工作时间：10:00-18:00</li>
          <li>紧急技术支持保持在线</li>
        </ul>
        <h3>优惠活动</h3>
        <p>春节期间购买年度套餐享<strong>8折优惠</strong>，详情请咨询客服。</p>
      `,
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
    // 默认选中第一条消息
    if (records.value.length > 0) {
      openDetail(records.value[0])
    }
  })

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

    // 重新选中第一条
    if (filteredRecords.value.length > 0) {
      openDetail(filteredRecords.value[0])
    } else {
      detail.value = undefined
      activeId.value = undefined
    }
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
  .markdown-body {
    font-size: 14px;
    line-height: 1.8;
  }

  .markdown-body h2 {
    font-size: 20px;
    font-weight: 600;
    margin-top: 24px;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid var(--default-border);
  }

  .markdown-body h3 {
    font-size: 16px;
    font-weight: 600;
    margin-top: 20px;
    margin-bottom: 12px;
  }

  .markdown-body p {
    margin-bottom: 12px;
  }

  .markdown-body ul,
  .markdown-body ol {
    margin-bottom: 12px;
    padding-left: 24px;
  }

  .markdown-body li {
    margin-bottom: 6px;
  }

  .markdown-body table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 16px;
  }

  .markdown-body table th,
  .markdown-body table td {
    padding: 10px 12px;
    border: 1px solid var(--default-border);
    text-align: left;
  }

  .markdown-body table th {
    background-color: var(--muted-bg);
    font-weight: 600;
  }

  .markdown-body strong {
    font-weight: 600;
    color: var(--primary-color);
  }
</style>