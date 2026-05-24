<template>
  <ElDrawer v-model="visible" title="需求详情" size="70%" direction="rtl" class="requirement-drawer">
    <div v-if="requirement" class="flex h-full flex-col overflow-hidden">
      <div class="shrink-0">
        <div class="mb-4 border-b border-gray-200 pb-2 text-base font-semibold text-gray-800">基础信息</div>
        <div class="space-y-4">
          <div class="flex"><div class="w-[100px] shrink-0 text-sm text-gray-500">需求编号</div><div class="flex-1 break-words text-sm text-gray-800">{{ requirement.requirementNo }}</div></div>
          <div class="flex"><div class="w-[100px] shrink-0 text-sm text-gray-500">需求标题</div><div class="flex-1 break-words text-sm text-gray-800">{{ requirement.title }}</div></div>
          <div class="flex"><div class="w-[100px] shrink-0 text-sm text-gray-500">优先级</div><div class="flex-1 break-words text-sm text-gray-800"><ElTag :type="requirement.priorityType as any" size="small">{{ requirement.priorityLabel }}</ElTag></div></div>
          <div class="flex"><div class="w-[100px] shrink-0 text-sm text-gray-500">状态</div><div class="flex-1 break-words text-sm text-gray-800"><ElTag :type="requirement.statusType as any" size="small">{{ requirement.statusLabel }}</ElTag></div></div>
          <div class="flex"><div class="w-[100px] shrink-0 text-sm text-gray-500">需求来源</div><div class="flex-1 break-words text-sm text-gray-800"><ElTag :type="requirement.sourceType as any" size="small" effect="plain">{{ requirement.sourceLabel }}</ElTag></div></div>
          <div class="flex"><div class="w-[100px] shrink-0 text-sm text-gray-500">需求类型</div><div class="flex-1 break-words text-sm text-gray-800"><ElTag :type="requirement.typeLabelType as any" size="small" effect="plain">{{ requirement.typeLabel }}</ElTag></div></div>
          <div class="flex"><div class="w-[100px] shrink-0 text-sm text-gray-500">创建时间</div><div class="flex-1 break-words text-sm text-gray-800">{{ formatDate(requirement.createTime) }}</div></div>
          <div class="flex"><div class="w-[100px] shrink-0 text-sm text-gray-500">更新时间</div><div class="flex-1 break-words text-sm text-gray-800">{{ formatDate(requirement.updateTime) }}</div></div>
        </div>
      </div>

      <div class="mt-6 shrink-0">
        <div class="mb-4 border-b border-gray-200 pb-2 text-base font-semibold text-gray-800">需求描述</div>
        <div>
          <div v-if="requirement.description" class="h-[320px] overflow-y-auto rounded-lg border border-gray-200 bg-gray-50 p-4">
            <div class="markdown-body" v-html="renderedDescription"></div>
          </div>
          <div v-else class="flex h-[320px] items-center justify-center rounded-lg border border-gray-200 bg-gray-50">
            <ElEmpty description="暂无描述" />
          </div>
        </div>
      </div>

      <div class="mt-6 min-h-0 flex-1 overflow-hidden rounded-lg border border-gray-200 bg-white px-5 pb-5">
        <ElTabs v-model="activeTab" class="flex h-full flex-col">
          <ElTabPane label="附件" name="attachment">
            <div class="h-[320px] overflow-y-auto pt-3">
              <ElEmpty description="附件功能待开发" />
            </div>
          </ElTabPane>
          <ElTabPane label="评论" name="comment">
            <div class="h-[320px] overflow-y-auto pt-3">
              <ElEmpty description="评论功能待开发" />
            </div>
          </ElTabPane>
          <ElTabPane label="操作历史" name="history">
            <div class="h-[320px] overflow-y-auto pt-3">
              <ElEmpty description="操作历史待开发" />
            </div>
          </ElTabPane>
        </ElTabs>
      </div>
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

const activeTab = ref('attachment')

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
:deep(.requirement-drawer .el-drawer__body) {
  height: calc(100% - 55px);
  overflow: hidden;
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
