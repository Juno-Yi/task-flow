<!-- 需求Tab -->
<template>
  <div class="h-full flex flex-col">
    <!-- 搜索筛选栏 -->
    <div class="mb-4">
      <ElForm :model="queryParams" inline>
        <ElFormItem label="需求标题">
          <ElInput
            v-model="queryParams.title"
            placeholder="请输入需求标题"
            clearable
            style="width: 200px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </ElFormItem>

        <ElFormItem label="优先级">
          <ElSelect
            v-model="queryParams.priority"
            placeholder="请选择优先级"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <ElOption label="低" :value="1" />
            <ElOption label="中" :value="2" />
            <ElOption label="高" :value="3" />
            <ElOption label="紧急" :value="4" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="状态">
          <ElSelect
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <ElOption label="待评审" :value="1" />
            <ElOption label="已通过" :value="2" />
            <ElOption label="开发中" :value="3" />
            <ElOption label="已完成" :value="4" />
            <ElOption label="已拒绝" :value="5" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="来源">
          <ElSelect
            v-model="queryParams.source"
            placeholder="请选择来源"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <ElOption label="客户" :value="1" />
            <ElOption label="内部" :value="2" />
            <ElOption label="市场" :value="3" />
            <ElOption label="其他" :value="4" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="类型">
          <ElSelect
            v-model="queryParams.type"
            placeholder="请选择类型"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <ElOption label="功能" :value="1" />
            <ElOption label="优化" :value="2" />
            <ElOption label="修复" :value="3" />
            <ElOption label="其他" :value="4" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem>
          <ElButton type="primary" @click="handleSearch">
            <ArtSvgIcon icon="ri:search-line" class="mr-1" />
            搜索
          </ElButton>
          <ElButton @click="handleReset">
            <ArtSvgIcon icon="ri:refresh-line" class="mr-1" />
            重置
          </ElButton>
        </ElFormItem>
      </ElForm>
    </div>

    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div class="text-sm text-gray-500">
        共 {{ pagination.total }} 个需求
      </div>
      <ElButton v-if="projectRole.isOwner.value || projectRole.isAdmin.value" type="primary" @click="handleAdd">
        <ArtSvgIcon icon="ri:add-line" class="mr-1" />
        添加需求
      </ElButton>
    </div>

    <!-- 需求列表表格 -->
    <div class="flex-1 overflow-auto">
      <ElTable
        :data="requirementList"
        v-loading="loading"
        stripe
        style="width: 100%"
        height="100%"
      >
        <ElTableColumn type="index" label="序号" width="60" align="center" />

        <ElTableColumn prop="requirementNo" label="需求编号" width="140" />

        <ElTableColumn prop="title" label="需求标题" min-width="200" show-overflow-tooltip />

        <ElTableColumn prop="priority" label="优先级" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="row.priorityType as any" size="small">
              {{ row.priorityLabel }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="row.statusType as any" size="small">
              {{ row.statusLabel }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="source" label="来源" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="row.sourceType as any" size="small" effect="plain">
              {{ row.sourceLabel }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <ElTag :type="row.typeLabelType as any" size="small" effect="plain">
              {{ row.typeLabel }}
            </ElTag>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </ElTableColumn>

        <ElTableColumn label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <ElButton text type="primary" size="small" @click="handleView(row)">
              查看
            </ElButton>
            <ElButton
              v-if="projectRole.isOwner.value || projectRole.isAdmin.value"
              text
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              v-if="projectRole.isOwner.value"
              text
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </div>

    <!-- 分页器 -->
    <div class="mt-4 flex justify-end">
      <ElPagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>


<script setup lang="ts">
import { fetchGetProjectRequirementList } from "@/api/project/requirement"
import { useProjectRole } from "@/hooks/useProjectRole"
import ArtSvgIcon from "@/components/core/base/art-svg-icon/index.vue"
import { ElMessage, ElMessageBox } from 'element-plus'

defineOptions({ name: 'RequirementTab' })

interface Props {
  projectInfo: Api.Project.ProjectDetailVO
}

const props = defineProps<Props>()

// 使用项目角色权限
const projectRole = useProjectRole(computed(() => props.projectInfo.currentUserRole))

// 需求列表
const requirementList = ref<Api.Project.ProjectRequirementVO[]>([])
const loading = ref(false)

// 查询参数
const queryParams = ref<Api.Project.ProjectRequirementQueryDTO>({
  title: undefined,
  priority: undefined,
  status: undefined,
  source: undefined,
  type: undefined
})

// 分页参数
const pagination = ref({
  current: 1,
  size: 50,
  total: 0
})

/**
 * 加载需求列表数据
 */
const loadRequirementList = async () => {
  try {
    loading.value = true
    const data = await fetchGetProjectRequirementList(props.projectInfo.id, {
      ...queryParams.value,
      current: pagination.value.current,
      size: pagination.value.size
    })
    requirementList.value = data.list || []
    pagination.value.total = data.total || 0
  } catch (error) {
    console.error('加载需求列表失败：', error)
    ElMessage.error('加载需求列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.value.current = 1
  loadRequirementList()
}

/**
 * 重置
 */
const handleReset = () => {
  queryParams.value = {
    title: undefined,
    priority: undefined,
    status: undefined,
    source: undefined,
    type: undefined
  }
  pagination.value.current = 1
  loadRequirementList()
}

/**
 * 分页大小改变
 */
const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  loadRequirementList()
}

/**
 * 当前页改变
 */
const handleCurrentChange = (current: number) => {
  pagination.value.current = current
  loadRequirementList()
}

/**
 * 格式化日期
 */
const formatDate = (dateStr: string | undefined): string => {
  if (!dateStr) return '-'
  if (dateStr.includes('T')) {
    return dateStr.replace('T', ' ').substring(0, 19)
  }
  return dateStr.substring(0, 19)
}

/**
 * 查看需求详情
 */
const handleView = (requirement: Api.Project.ProjectRequirementVO) => {
  ElMessage.info('查看需求详情功能待实现')
  console.log('查看需求：', requirement)
}

/**
 * 添加需求
 */
const handleAdd = () => {
  ElMessage.info('添加需求功能待实现')
}

/**
 * 编辑需求
 */
const handleEdit = (requirement: Api.Project.ProjectRequirementVO) => {
  ElMessage.info('编辑需求功能待实现')
  console.log('编辑需求：', requirement)
}

/**
 * 删除需求
 */
const handleDelete = async (requirement: Api.Project.ProjectRequirementVO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除需求「${requirement.title}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    ElMessage.info('删除需求功能待实现')
    console.log('删除需求：', requirement)
    // TODO: 调用删除接口
    // await fetchDeleteProjectRequirement(props.projectInfo.id, requirement.id)
    // ElMessage.success('删除成功')
    // await loadRequirementList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 监听项目信息变化，重新加载数据
watch(() => props.projectInfo.id, (newId) => {
  if (newId) {
    // 重置查询参数和分页
    queryParams.value = {
      title: undefined,
      priority: undefined,
      status: undefined,
      source: undefined,
      type: undefined
    }
    pagination.value.current = 1
    loadRequirementList()
  }
}, { immediate: true })
</script>

<style scoped lang="scss">
:deep(.el-form--inline .el-form-item) {
  margin-right: 16px;
  margin-bottom: 12px;
}
</style>