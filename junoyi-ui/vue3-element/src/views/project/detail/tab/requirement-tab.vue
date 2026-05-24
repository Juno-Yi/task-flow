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
            <ElOption
              v-for="item in priorityDictList"
              :key="item.dictCode"
              :label="item.dictLabel"
              :value="Number(item.dictValue)"
            />
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
            <ElOption
              v-for="item in statusDictList"
              :key="item.dictCode"
              :label="item.dictLabel"
              :value="Number(item.dictValue)"
            />
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
            <ElOption
              v-for="item in sourceDictList"
              :key="item.dictCode"
              :label="item.dictLabel"
              :value="Number(item.dictValue)"
            />
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
            <ElOption
              v-for="item in typeDictList"
              :key="item.dictCode"
              :label="item.dictLabel"
              :value="Number(item.dictValue)"
            />
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
        <ElTableColumn prop="requirementNo" label="需求编号" width="140">
          <template #default="{ row }">
            <span class="link-text" @click="handleView(row)">
              {{ row.requirementNo }}
            </span>
          </template>
        </ElTableColumn>

        <ElTableColumn prop="title" label="需求标题" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="link-text" @click="handleView(row)">
              {{ row.title }}
            </span>
          </template>
        </ElTableColumn>

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

        <ElTableColumn label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <ElDropdown trigger="click" @command="(command: string) => handleCommand(command, row)">
              <ElButton text type="primary" size="small">
                更多
                <ArtSvgIcon icon="ri:arrow-down-s-line" class="ml-1" />
              </ElButton>
              <template #dropdown>
                <ElDropdownMenu>
                  <ElDropdownItem command="view">
                    <ArtSvgIcon icon="ri:eye-line" class="mr-2" />
                    查看详情
                  </ElDropdownItem>
                  <ElDropdownItem
                    v-if="projectRole.isOwner.value || projectRole.isAdmin.value"
                    command="edit"
                  >
                    <ArtSvgIcon icon="ri:edit-line" class="mr-2" />
                    编辑
                  </ElDropdownItem>
                  <ElDropdownItem
                    v-if="projectRole.isOwner.value"
                    command="delete"
                    divided
                  >
                    <span class="text-red-500">
                      <ArtSvgIcon icon="ri:delete-bin-line" class="mr-2" />
                      删除
                    </span>
                  </ElDropdownItem>
                </ElDropdownMenu>
              </template>
            </ElDropdown>
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

    <!-- 添加/编辑需求对话框 -->
    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <ElFormItem label="需求标题" prop="title">
          <ElInput
            v-model="formData.title"
            placeholder="请输入需求标题"
            maxlength="100"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem label="需求描述" prop="description">
          <ElInput
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入需求描述"
            maxlength="500"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem label="优先级" prop="priority">
          <ElSelect
            v-model="formData.priority"
            placeholder="请选择优先级"
            style="width: 100%"
          >
            <ElOption
              v-for="item in priorityDictList"
              :key="item.dictCode"
              :label="item.dictLabel"
              :value="Number(item.dictValue)"
            />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="需求来源" prop="source">
          <ElSelect
            v-model="formData.source"
            placeholder="请选择需求来源"
            style="width: 100%"
          >
            <ElOption
              v-for="item in sourceDictList"
              :key="item.dictCode"
              :label="item.dictLabel"
              :value="Number(item.dictValue)"
            />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="需求类型" prop="type">
          <ElSelect
            v-model="formData.type"
            placeholder="请选择需求类型"
            style="width: 100%"
          >
            <ElOption
              v-for="item in typeDictList"
              :key="item.dictCode"
              :label="item.dictLabel"
              :value="Number(item.dictValue)"
            />
          </ElSelect>
        </ElFormItem>
      </ElForm>

      <template #footer>
        <div class="flex justify-end gap-2">
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </ElButton>
        </div>
      </template>
    </ElDialog>

    <!-- 需求详情抽屉 -->
    <ElDrawer
      v-model="drawerVisible"
      title="需求详情"
      size="60%"
      direction="rtl"
    >
      <div v-if="currentRequirement" class="requirement-detail">
        <!-- 基础信息区域 -->
        <div class="detail-section">
          <div class="section-title">基础信息</div>
          <div class="section-content">
            <!-- 需求编号 -->
            <div class="detail-item">
              <div class="item-label">需求编号</div>
              <div class="item-value">{{ currentRequirement.requirementNo }}</div>
            </div>

            <!-- 需求标题 -->
            <div class="detail-item">
              <div class="item-label">需求标题</div>
              <div class="item-value">{{ currentRequirement.title }}</div>
            </div>

            <!-- 需求描述 -->
            <div class="detail-item">
              <div class="item-label">需求描述</div>
              <div class="item-value">
                {{ currentRequirement.description || '暂无描述' }}
              </div>
            </div>

            <!-- 优先级 -->
            <div class="detail-item">
              <div class="item-label">优先级</div>
              <div class="item-value">
                <ElTag :type="currentRequirement.priorityType as any" size="small">
                  {{ currentRequirement.priorityLabel }}
                </ElTag>
              </div>
            </div>

            <!-- 状态 -->
            <div class="detail-item">
              <div class="item-label">状态</div>
              <div class="item-value">
                <ElTag :type="currentRequirement.statusType as any" size="small">
                  {{ currentRequirement.statusLabel }}
                </ElTag>
              </div>
            </div>

            <!-- 需求来源 -->
            <div class="detail-item">
              <div class="item-label">需求来源</div>
              <div class="item-value">
                <ElTag :type="currentRequirement.sourceType as any" size="small" effect="plain">
                  {{ currentRequirement.sourceLabel }}
                </ElTag>
              </div>
            </div>

            <!-- 需求类型 -->
            <div class="detail-item">
              <div class="item-label">需求类型</div>
              <div class="item-value">
                <ElTag :type="currentRequirement.typeLabelType as any" size="small" effect="plain">
                  {{ currentRequirement.typeLabel }}
                </ElTag>
              </div>
            </div>

            <!-- 创建时间 -->
            <div class="detail-item">
              <div class="item-label">创建时间</div>
              <div class="item-value">{{ formatDate(currentRequirement.createTime) }}</div>
            </div>

            <!-- 更新时间 -->
            <div class="detail-item">
              <div class="item-label">更新时间</div>
              <div class="item-value">{{ formatDate(currentRequirement.updateTime) }}</div>
            </div>
          </div>
        </div>

        <!-- 核心内容区域（留空） -->
        <div class="detail-section">
          <div class="section-title">需求详情</div>
          <div class="section-content">
            <ElEmpty description="核心内容待开发" />
          </div>
        </div>

        <!-- 附件区域（留空） -->
        <div class="detail-section">
          <div class="section-title">附件</div>
          <div class="section-content">
            <ElEmpty description="附件功能待开发" />
          </div>
        </div>

        <!-- 评论区域（留空） -->
        <div class="detail-section">
          <div class="section-title">评论</div>
          <div class="section-content">
            <ElEmpty description="评论功能待开发" />
          </div>
        </div>

        <!-- 操作历史区域（留空） -->
        <div class="detail-section">
          <div class="section-title">操作历史</div>
          <div class="section-content">
            <ElEmpty description="操作历史待开发" />
          </div>
        </div>
      </div>
    </ElDrawer>
  </div>
</template>


<script setup lang="ts">
import {
  fetchGetProjectRequirementList,
  fetchAddProjectRequirement,
  fetchUpdateProjectRequirement, fetchDeleteProjectRequirement
} from "@/api/project/requirement"
import { fetchGetDictDataByType } from "@/api/system/dict"
import { useProjectRole } from "@/hooks/useProjectRole"
import ArtSvgIcon from "@/components/core/base/art-svg-icon/index.vue"
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

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

// 字典数据
const priorityDictList = ref<Api.System.DictDataVO[]>([])
const statusDictList = ref<Api.System.DictDataVO[]>([])
const sourceDictList = ref<Api.System.DictDataVO[]>([])
const typeDictList = ref<Api.System.DictDataVO[]>([])

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

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑需求' : '添加需求'))
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 抽屉相关
const drawerVisible = ref(false)
const currentRequirement = ref<Api.Project.ProjectRequirementVO | null>(null)

// 表单数据
const formData = ref<Api.Project.ProjectRequirementDTO>({
  id: undefined,
  title: '',
  description: '',
  priority: undefined,
  source: undefined,
  type: undefined
})

// 表单验证规则
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入需求标题', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  source: [
    { required: true, message: '请选择需求来源', trigger: 'change' }
  ],
  type: [
    { required: true, message: '请选择需求类型', trigger: 'change' }
  ]
}

/**
 * 加载字典数据
 */
const loadDictData = async () => {
  try {
    const [priority, status, source, type] = await Promise.all([
      fetchGetDictDataByType('project_requirement_priority'),
      fetchGetDictDataByType('project_requirement_status'),
      fetchGetDictDataByType('project_requirement_source'),
      fetchGetDictDataByType('project_requirement_type')
    ])
    priorityDictList.value = priority
    statusDictList.value = status
    sourceDictList.value = source
    typeDictList.value = type
  } catch (error) {
    console.error('加载字典数据失败：', error)
  }
}

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
    priority: undefined as any,
    status: undefined as any,
    source: undefined as any,
    type: undefined as any
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
 * 处理下拉菜单命令
 */
const handleCommand = (command: string, requirement: Api.Project.ProjectRequirementVO) => {
  switch (command) {
    case 'view':
      handleView(requirement)
      break
    case 'edit':
      handleEdit(requirement)
      break
    case 'delete':
      handleDelete(requirement)
      break
  }
}

/**
 * 查看需求详情
 */
const handleView = (requirement: Api.Project.ProjectRequirementVO) => {
  currentRequirement.value = requirement
  drawerVisible.value = true
}

/**
 * 添加需求
 */
const handleAdd = () => {
  isEdit.value = false
  formData.value = {
    id: undefined,
    title: '',
    description: '',
    priority: undefined as any,
    source: undefined as any,
    type: undefined as any
  }
  dialogVisible.value = true
}

/**
 * 编辑需求
 */
const handleEdit = (requirement: Api.Project.ProjectRequirementVO) => {
  isEdit.value = true
  formData.value = {
    id: requirement.id,
    title: requirement.title,
    description: requirement.description,
    priority: requirement.priority,
    source: requirement.source,
    type: requirement.type
  }
  dialogVisible.value = true
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await fetchUpdateProjectRequirement(props.projectInfo.id,formData.value)
      ElMessage.success('更新成功')
      dialogVisible.value = false
      await loadRequirementList()
    } else {
      await fetchAddProjectRequirement(props.projectInfo.id, formData.value)
      ElMessage.success('添加成功')
      dialogVisible.value = false
      await loadRequirementList()
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error !== false) {
      ElMessage.error('提交失败')
    }
  } finally {
    submitLoading.value = false
  }
}

/**
 * 关闭对话框
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
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

    console.log('删除需求：', requirement)
    await fetchDeleteProjectRequirement(props.projectInfo.id, requirement.id)
    ElMessage.success('删除成功')
    await loadRequirementList()
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

// 初始化时加载字典数据
onMounted(() => {
  loadDictData()
})
</script>

<style scoped lang="scss">
:deep(.el-form--inline .el-form-item) {
  margin-right: 16px;
  margin-bottom: 12px;
}

// 表格可点击文本
.link-text {
  color: #303133;
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: #409eff;
  }
}

// 需求详情样式
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