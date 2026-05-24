<template>
  <ElDrawer v-model="visible" title="需求详情" size="60%" direction="rtl">
    <div v-if="requirement" class="requirement-detail">
      <div class="detail-section">
        <div class="section-title">基础信息</div>
        <div class="section-content">
          <div class="detail-item"><div class="item-label">需求编号</div><div class="item-value">{{ requirement.requirementNo }}</div></div>
          <div class="detail-item"><div class="item-label">需求标题</div><div class="item-value">{{ requirement.title }}</div></div>
          <div class="detail-item"><div class="item-label">优先级</div><div class="item-value"><ElTag :type="requirement.priorityType as any" size="small">{{ requirement.priorityLabel }}</ElTag></div></div>
          <div class="detail-item"><div class="item-label">状态</div><div class="item-value"><ElTag :type="requirement.statusType as any" size="small">{{ requirement.statusLabel }}</ElTag></div></div>
          <div class="detail-item"><div class="item-label">需求来源</div><div class="item-value"><ElTag :type="requirement.sourceType as any" size="small" effect="plain">{{ requirement.sourceLabel }}</ElTag></div></div>
          <div class="detail-item"><div class="item-label">需求类型</div><div class="item-value"><ElTag :type="requirement.typeLabelType as any" size="small" effect="plain">{{ requirement.typeLabel }}</ElTag></div></div>
          <div class="detail-item"><div class="item-label">创建时间</div><div class="item-value">{{ formatDate(requirement.createTime) }}</div></div>
          <div class="detail-item"><div class="item-label">更新时间</div><div class="item-value">{{ formatDate(requirement.updateTime) }}</div></div>
        </div>
      </div>

      <div class="detail-section">
        <div class="section-title">需求描述</div>
        <div class="section-content">
          <div v-if="requirement.description" class="markdown-wrapper">
            <div class="markdown-body" v-html="renderedDescription"></div>
          </div>
          <ElEmpty v-else description="暂无描述" />
        </div>
      </div>
      <div class="detail-section"><div class="section-title">附件</div><div class="section-content"><ElEmpty description="附件功能待开发" /></div></div>
      <div class="detail-section"><div class="section-title">评论</div><div class="section-content"><ElEmpty description="评论功能待开发" /></div></div>
      <div class="detail-section"><div class="section-title">操作历史</div><div class="section-content"><ElEmpty description="操作历史待开发" /></div></div>
    </div>
  </ElDrawer>
</template>

<script setup lang="ts">
import { marked } from 'marked'

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

marked.setOptions({
  breaks: true,
  gfm: true
})

const renderedDescription = computed(() => {
  const text = props.requirement?.description || ''
  if (!text) return ''
  return marked.parse(text) as string
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

.markdown-wrapper {
  max-height: 420px;
  overflow-y: auto;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fafafa;
}

.markdown-body {
  color: #303133;
  font-size: 14px;
  line-height: 1.8;
  word-break: break-word;

  :deep(*:first-child) {
    margin-top: 0 !important;
  }

  :deep(*:last-child) {
    margin-bottom: 0 !important;
  }

  :deep(h1),
  :deep(h2),
  :deep(h3),
  :deep(h4),
  :deep(h5),
  :deep(h6) {
    margin: 18px 0 12px;
    font-weight: 600;
    line-height: 1.4;
    color: #1f2937;
  }

  :deep(h1) {
    font-size: 26px;
  }

  :deep(h2) {
    font-size: 22px;
  }

  :deep(h3) {
    font-size: 18px;
  }

  :deep(h4),
  :deep(h5),
  :deep(h6) {
    font-size: 16px;
  }

  :deep(p) {
    margin: 10px 0;
  }

  :deep(ul),
  :deep(ol) {
    padding-left: 22px;
    margin: 10px 0;
  }

  :deep(li) {
    margin: 6px 0;
  }

  :deep(blockquote) {
    margin: 12px 0;
    padding: 12px 16px;
    border-left: 4px solid #409eff;
    background: #ecf5ff;
    color: #606266;
    border-radius: 4px;
  }

  :deep(a) {
    color: #409eff;
    text-decoration: none;
  }

  :deep(a:hover) {
    text-decoration: underline;
  }

  :deep(hr) {
    margin: 16px 0;
    border: none;
    border-top: 1px solid #e5e7eb;
  }

  :deep(table) {
    width: 100%;
    border-collapse: collapse;
    margin: 12px 0;
    background: #fff;
  }

  :deep(th),
  :deep(td) {
    padding: 10px 12px;
    border: 1px solid #e5e7eb;
    text-align: left;
  }

  :deep(th) {
    background: #f5f7fa;
    font-weight: 600;
  }

  :deep(img) {
    max-width: 100%;
    border-radius: 6px;
  }

  :deep(code) {
    padding: 2px 6px;
    border-radius: 4px;
    background: #f4f4f5;
    font-size: 13px;
    color: #e11d48;
  }

  :deep(pre) {
    padding: 14px 16px;
    border-radius: 8px;
    background: #111827;
    overflow-x: auto;
    margin: 12px 0;
  }

  :deep(pre code) {
    padding: 0;
    background: transparent;
    color: #e5e7eb;
    font-size: 13px;
    line-height: 1.7;
  }
}
</style>
