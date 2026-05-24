<template>
  <ElDrawer v-model="visible" title="需求详情" size="60%" direction="rtl">
    <div v-if="requirement" class="requirement-detail">
      <div class="detail-section">
        <div class="section-title">基础信息</div>
        <div class="section-content">
          <div class="detail-item"><div class="item-label">需求编号</div><div class="item-value">{{ requirement.requirementNo }}</div></div>
          <div class="detail-item"><div class="item-label">需求标题</div><div class="item-value">{{ requirement.title }}</div></div>
          <div class="detail-item"><div class="item-label">需求描述</div><div class="item-value">{{ requirement.description || '暂无描述' }}</div></div>
          <div class="detail-item"><div class="item-label">优先级</div><div class="item-value"><ElTag :type="requirement.priorityType as any" size="small">{{ requirement.priorityLabel }}</ElTag></div></div>
          <div class="detail-item"><div class="item-label">状态</div><div class="item-value"><ElTag :type="requirement.statusType as any" size="small">{{ requirement.statusLabel }}</ElTag></div></div>
          <div class="detail-item"><div class="item-label">需求来源</div><div class="item-value"><ElTag :type="requirement.sourceType as any" size="small" effect="plain">{{ requirement.sourceLabel }}</ElTag></div></div>
          <div class="detail-item"><div class="item-label">需求类型</div><div class="item-value"><ElTag :type="requirement.typeLabelType as any" size="small" effect="plain">{{ requirement.typeLabel }}</ElTag></div></div>
          <div class="detail-item"><div class="item-label">创建时间</div><div class="item-value">{{ formatDate(requirement.createTime) }}</div></div>
          <div class="detail-item"><div class="item-label">更新时间</div><div class="item-value">{{ formatDate(requirement.updateTime) }}</div></div>
        </div>
      </div>

      <div class="detail-section"><div class="section-title">需求详情</div><div class="section-content"><ElEmpty description="核心内容待开发" /></div></div>
      <div class="detail-section"><div class="section-title">附件</div><div class="section-content"><ElEmpty description="附件功能待开发" /></div></div>
      <div class="detail-section"><div class="section-title">评论</div><div class="section-content"><ElEmpty description="评论功能待开发" /></div></div>
      <div class="detail-section"><div class="section-title">操作历史</div><div class="section-content"><ElEmpty description="操作历史待开发" /></div></div>
    </div>
  </ElDrawer>
</template>

<script setup lang="ts">
defineOptions({ name: 'RequirementDetailDrawer' })

interface Props {
  modelValue: boolean
  requirement: Api.Project.ProjectRequirementVO | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
})

const formatDate = (dateStr: string | undefined): string => {
  if (!dateStr) return '-'
  if (dateStr.includes('T')) {
    return dateStr.replace('T', ' ').substring(0, 19)
  }
  return dateStr.substring(0, 19)
}
</script>

<style scoped lang="scss">
.requirement-detail {
  .detail-section {
    margin-bottom: 24px;
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 1px solid #e5e7eb;
    }
    .section-content {
      .detail-item {
        display: flex;
        margin-bottom: 16px;
        .item-label {
          width: 100px;
          flex-shrink: 0;
          color: #606266;
          font-size: 14px;
        }
        .item-value {
          flex: 1;
          color: #303133;
          font-size: 14px;
          word-break: break-word;
        }
      }
    }
  }
}
</style>
