<!-- 通知中心 - 通知管理 -->
<template>
  <div class="art-full-height">
    <ElCard
      class="art-table-card"
      shadow="never"
    >
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="refreshData"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton type="primary" @click="handleAdd" v-permission="'notification.ui.manage.publish.button'" v-ripple>发布通知</ElButton>
            <ElButton @click="goMyNotification" v-ripple>我的收信箱</ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>

    <!-- 发布通知弹窗 -->
    <NotificationDialog ref="dialogRef" @success="refreshData" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElTag, ElMessageBox, ElMessage } from 'element-plus'
import { useTable } from '@/hooks/core/useTable'
import { fetchGetNotificationList, fetchPublishNotification } from '@/api/notification/manage'
import { router } from "@/router"
import NotificationDialog from './modules/notification-dialog.vue'
import ArtButtonMore, {ButtonMoreItem} from "@/components/core/forms/art-button-more/index.vue";

defineOptions({ name: 'NotificationManager' })

type NotificationListVO = Api.Notification.NotificationListVO

const formatTime = (time?: string): string => {
  if (!time) return '-'
  const date = new Date(time)
  if (isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const {
  columns,
  columnChecks,
  data,
  loading,
  pagination,
  handleSizeChange,
  handleCurrentChange,
  refreshData
} = useTable({
  core: {
    apiFn: fetchGetNotificationList,
    apiParams: {
      current: 1,
      size: 20
    },
    columnsFactory: () => [
      {
        prop: 'id',
        label: 'ID',
        align: 'center',
        headerAlign: 'center',
        width: 80
      },
      {
        prop: 'title',
        label: '通知标题',
        align: 'left',
        headerAlign: 'center',
        minWidth: 200,
        showOverflowTooltip: true
      },
      {
        prop: 'content',
        label: '通知内容',
        align: 'left',
        headerAlign: 'center',
        minWidth: 260,
        showOverflowTooltip: true
      },
      {
        prop: 'typeLabel',
        label: '通知类型',
        align: 'center',
        headerAlign: 'center',
        width: 120,
        formatter: (row: NotificationListVO) => {
          if (!row.typeLabel) return '-'
          const type = (row.typeType || 'info') as 'success' | 'info' | 'warning' | 'danger'
          return h(ElTag, { type, size: 'small' }, () => row.typeLabel)
        }
      },
      {
        prop: 'statusLabel',
        label: '状态',
        align: 'center',
        headerAlign: 'center',
        width: 100,
        formatter: (row: NotificationListVO) => {
          if (!row.statusLabel) return '-'
          const type = (row.statusType || 'info') as 'success' | 'info' | 'warning' | 'danger'
          return h(ElTag, { type, size: 'small' }, () => row.statusLabel)
        }
      },
      {
        prop: 'senderNickName',
        label: '发送者',
        align: 'center',
        headerAlign: 'center',
        width: 120,
        formatter: (row: NotificationListVO) => row.senderNickName || '-'
      },
      {
        prop: 'publishTime',
        label: '发布时间',
        align: 'center',
        headerAlign: 'center',
        width: 180,
        formatter: (row: NotificationListVO) => formatTime(row.publishTime)
      },
      {
        prop: 'updateTime',
        label: '更新时间',
        align: 'center',
        headerAlign: 'center',
        width: 180,
        formatter: (row: NotificationListVO) => formatTime(row.updateTime)
      },
      {
        prop: 'operation',
        label: '操作',
        width: 80,
        align: 'center',
        headerAlign: 'center',
        fixed: 'right',
        formatter: (row: any) => {
          const list: ButtonMoreItem[] = []

          // 草稿状态显示发布按钮
          if (row.status === 0) {
            list.push({
              key: 'publish',
              label: '发布',
              icon: 'ri:send-plane-line',
              auth: 'notification.ui.manage.publish.button'
            })
          }

          list.push(
            {
              key: 'edit',
              label: '编辑',
              icon: 'ri:edit-line',
              auth: 'notification.ui.manage.edit.button'
            },
            {
              key: 'delete',
              label: '删除',
              icon: 'ri:delete-bin-line',
              auth: 'notification.ui.manage.delete.button',
            }
          )

          return h(ArtButtonMore, {
            list,
            onClick: (item: ButtonMoreItem) => handleButtonMoreClick(item, row)
          })
        }
      }
    ]
  }
})

const handleButtonMoreClick = (item: ButtonMoreItem, row: NotificationListVO) => {
  if (item.key === 'publish') {
    handlePublish(row)
  } else if (item.key === 'edit') {
    handleEdit(row)
  } else if (item.key === 'delete') {
    handleDelete(row)
  }
}

const goMyNotification = () => {
  router.push('/dashboard/notice')
}

const dialogRef = ref<InstanceType<typeof NotificationDialog>>()

const handleAdd = () => {
  dialogRef.value?.open()
}

const handleEdit = (row: NotificationListVO) => {
  dialogRef.value?.open(row)
}

const handlePublish = async (row: NotificationListVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布通知「${row.title}」吗？发布后将通知到目标用户。`,
      '发布通知',
      {
        confirmButtonText: '确定发布',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await fetchPublishNotification(row.id)
    ElMessage.success('发布成功')
    refreshData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布通知失败', error)
    }
  }
}

const handleDelete = (row: NotificationListVO) => {
  // TODO: 实现删除功能
  console.log('删除通知', row.id)
}
</script>

<style scoped lang="scss">
</style>