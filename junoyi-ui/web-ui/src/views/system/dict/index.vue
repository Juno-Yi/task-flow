<template>
  <div class="dict-page art-full-height">
    <ElRow :gutter="20" class="h-full">
      <!-- 左侧：字典类型列表 -->
      <ElCol :span="8" class="h-full">
        <ElCard class="h-full dict-type-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <ArtSvgIcon icon="ri:book-2-line" class="mr-2" />
                字典类型
              </span>
              <ElButton
                  v-permission="'system.ui.dict.button.add'"
                  type="primary"
                  size="small"
                  @click="handleAddDictType"
              >
                <ArtSvgIcon icon="ri:add-line" class="mr-1" />
                新增
              </ElButton>
            </div>
          </template>

          <!-- 搜索 -->
          <div class="search-wrapper">
            <ElInput
                v-model="typeSearchText"
                placeholder="搜索字典名称或类型"
                clearable
                @input="handleTypeSearch"
            >
              <template #prefix>
                <ArtSvgIcon icon="ri:search-line" />
              </template>
            </ElInput>
          </div>

          <!-- 字典类型列表 -->
          <div class="dict-type-list">
            <ElEmpty
                v-if="filteredDictTypes.length === 0"
                description="暂无数据"
                :image-size="80"
            />
            <div
                v-for="item in filteredDictTypes"
                :key="item.dictId"
                :class="['dict-type-item', { active: currentDictType?.dictId === item.dictId }]"
                @click="handleSelectDictType(item)"
            >
              <div class="dict-type-content">
                <div class="dict-type-header">
                  <ArtSvgIcon
                      icon="ri:folder-3-line"
                      :class="['type-icon', { active: currentDictType?.dictId === item.dictId }]"
                  />
                  <div class="dict-type-info">
                    <div class="dict-type-name">{{ item.dictName }}</div>
                    <div class="dict-type-code">
                      <ArtSvgIcon icon="ri:code-line" :size="12" class="mr-1" />
                      {{ item.dictType }}
                    </div>
                  </div>
                </div>
                <div class="dict-type-actions" @click.stop>
                  <ElTooltip content="编辑" placement="top">
                    <ElButton
                        v-permission="'system.ui.dict.button.edit'"
                        type="primary"
                        text
                        circle
                        size="small"
                        @click="handleEditDictType(item)"
                    >
                      <ArtSvgIcon icon="ri:edit-line" />
                    </ElButton>
                  </ElTooltip>
                  <ElTooltip content="删除" placement="top">
                    <ElButton
                        v-permission="'system.ui.dict.button.delete'"
                        type="danger"
                        text
                        circle
                        size="small"
                        @click="handleDeleteDictType(item.dictId)"
                    >
                      <ArtSvgIcon icon="ri:delete-bin-line" />
                    </ElButton>
                  </ElTooltip>
                </div>
              </div>
            </div>
          </div>
        </ElCard>
      </ElCol>

      <!-- 右侧：字典数据列表 -->
      <ElCol :span="16" class="h-full">
        <ElCard class="h-full" shadow="never">
          <template #header>
            <div class="card-header">
              <span>字典数据 {{ currentDictType ? `- ${currentDictType.dictName}` : '' }}</span>
              <ElButton
                  v-if="currentDictType"
                  v-permission="'system.ui.dict.button.add'"
                  type="primary"
                  size="small"
                  @click="handleAddDictData"
              >
                新增
              </ElButton>
            </div>
          </template>

          <div v-if="!currentDictType" class="empty-tip">
            <ArtSvgIcon icon="ri:folder-open-line" :size="64" class="empty-icon" />
            <p class="empty-title">请选择字典类型</p>
            <p class="empty-desc">从左侧列表中选择一个字典类型以查看和管理字典数据</p>
          </div>

          <div v-else class="dict-data-container">
            <!-- 搜索栏 -->
            <div class="search-bar">
              <ElForm :inline="true" :model="dataSearchForm" class="search-form">
                <ElFormItem label="字典标签">
                  <ElInput
                      v-model="dataSearchForm.dictLabel"
                      placeholder="请输入字典标签"
                      clearable
                      style="width: 200px"
                  />
                </ElFormItem>
                <ElFormItem label="状态">
                  <ElSelect
                      v-model="dataSearchForm.status"
                      placeholder="请选择状态"
                      clearable
                      style="width: 120px"
                  >
                    <ElOption label="正常" value="0" />
                    <ElOption label="停用" value="1" />
                  </ElSelect>
                </ElFormItem>
                <ElFormItem>
                  <ElButton type="primary" @click="handleDataSearch">
                    <ArtSvgIcon icon="ri:search-line" class="mr-1" />
                    搜索
                  </ElButton>
                  <ElButton @click="handleDataReset">
                    <ArtSvgIcon icon="ri:refresh-line" class="mr-1" />
                    重置
                  </ElButton>
                </ElFormItem>
              </ElForm>
            </div>

            <!-- 字典数据表格 -->
            <ArtTable
                ref="tableRef"
                rowKey="dictCode"
                :loading="dataLoading"
                :columns="dataColumns"
                :data="dictDataList"
                :pagination="dataPagination"
                :paginationOptions="{ hideOnSinglePage: false }"
                @pagination:size-change="handleDataSizeChange"
                @pagination:current-change="handleDataCurrentChange"
            />
          </div>
        </ElCard>
      </ElCol>
    </ElRow>

    <!-- 字典类型弹窗 -->
    <DictTypeDialog
        v-model:visible="typeDialogVisible"
        :editData="editTypeData"
        @success="getDictTypeList"
    />

    <!-- 字典数据弹窗 -->
    <DictDataDialog
        v-model:visible="dataDialogVisible"
        :editData="editDataData"
        :dictType="currentDictType?.dictType"
        @success="getDictDataList"
    />
  </div>
</template>

<script setup lang="ts">
import { h } from 'vue'
import { ElMessageBox, ElMessage, ElTag, ElButton } from 'element-plus'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import ArtTable from '@/components/core/tables/art-table/index.vue'
import ArtButtonMore, { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
import DictTypeDialog from './modules/dict-type-dialog.vue'
import DictDataDialog from './modules/dict-data-dialog.vue'
import { usePermission } from '@/hooks/core/usePermission'
import {
  fetchGetAllDictTypes,
  fetchGetDictTypeById,
  fetchDeleteDictType,
  fetchGetDictDataList,
  fetchGetDictDataById,
  fetchDeleteDictData
} from '@/api/system/dict'

defineOptions({ name: 'DictManage' })

type DictTypeVO = Api.System.DictTypeVO
type DictDataVO = Api.System.DictDataVO

// 权限检查
const { hasPermission } = usePermission()

// 字典类型相关
const dictTypes = ref<DictTypeVO[]>([])
const filteredDictTypes = ref<DictTypeVO[]>([])
const typeSearchText = ref('')
const currentDictType = ref<DictTypeVO | null>(null)
const typeDialogVisible = ref(false)
const editTypeData = ref<DictTypeVO | null>(null)

// 字典数据相关
const dictDataList = ref<DictDataVO[]>([])
const dataLoading = ref(false)
const dataDialogVisible = ref(false)
const editDataData = ref<DictDataVO | null>(null)
const dataPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 字典数据搜索表单
const dataSearchForm = reactive({
  dictLabel: '',
  status: ''
})

// 字典数据表格列
const dataColumns: any[] = [
  { prop: 'dictLabel', label: '字典标签', minWidth: 120 },
  { prop: 'dictValue', label: '字典键值', minWidth: 120 },
  { prop: 'dictSort', label: '排序', width: 80 },
  {
    prop: 'status',
    label: '状态',
    width: 80,
    formatter: (row: DictDataVO) => {
      return h(ElTag, { type: row.status === '0' ? 'success' : 'danger' }, () =>
          row.status === '0' ? '正常' : '停用'
      )
    }
  },
  { prop: 'remark', label: '备注', minWidth: 150, showOverflowTooltip: true },
  {
    prop: 'action',
    label: '操作',
    width: 80,
    align: 'center',
    headerAlign: 'center',
    fixed: 'right' as const,
    formatter: (row: DictDataVO) => {
      const list: ButtonMoreItem[] = []

      if (hasPermission('system.ui.dict.button.edit')) {
        list.push({
          key: 'edit',
          label: '编辑',
          icon: 'ri:edit-2-line'
        })
      }

      if (hasPermission('system.ui.dict.button.delete')) {
        list.push({
          key: 'delete',
          label: '删除',
          icon: 'ri:delete-bin-4-line',
          color: '#f56c6c'
        })
      }

      if (list.length === 0) return '-'

      return h(ArtButtonMore, {
        list,
        onClick: (item: ButtonMoreItem) => handleDictDataAction(item, row)
      })
    }
  }
]

/**
 * 处理字典数据操作按钮点击
 */
const handleDictDataAction = (item: ButtonMoreItem, row: DictDataVO) => {
  switch (item.key) {
    case 'edit':
      handleEditDictData(row)
      break
    case 'delete':
      handleDeleteDictData(row.dictCode)
      break
  }
}

// 获取字典类型列表
const getDictTypeList = async () => {
  try {
    const res = await fetchGetAllDictTypes()

    // API封装已经提取了data字段，所以res直接就是数据
    if (Array.isArray(res)) {
      dictTypes.value = res
    } else {
      dictTypes.value = []
    }

    filteredDictTypes.value = dictTypes.value

    if (currentDictType.value) {
      const found = dictTypes.value.find((item) => item.dictId === currentDictType.value?.dictId)
      if (found) {
        currentDictType.value = found
      }
    }
  } catch (error) {
    console.error('获取字典类型列表失败:', error)
    ElMessage.error('获取字典类型列表失败')
  }
}

// 搜索字典类型
const handleTypeSearch = () => {
  if (!typeSearchText.value) {
    filteredDictTypes.value = dictTypes.value
  } else {
    const keyword = typeSearchText.value.toLowerCase()
    filteredDictTypes.value = dictTypes.value.filter(
        (item) =>
            item.dictName.toLowerCase().includes(keyword) ||
            item.dictType.toLowerCase().includes(keyword)
    )
  }
}

// 选择字典类型
const handleSelectDictType = (item: DictTypeVO) => {
  currentDictType.value = item
  dataPagination.current = 1
  getDictDataList()
}

// 新增字典类型
const handleAddDictType = () => {
  editTypeData.value = null
  typeDialogVisible.value = true
}

// 编辑字典类型
const handleEditDictType = async (item: DictTypeVO) => {
  try {
    // 通过ID查询详细信息
    const detail = await fetchGetDictTypeById(item.dictId)
    editTypeData.value = detail
    typeDialogVisible.value = true
  } catch (error) {
    console.error('获取字典类型详情失败:', error)
    ElMessage.error('获取字典类型详情失败')
  }
}

// 删除字典类型
const handleDeleteDictType = async (dictId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该字典类型吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await fetchDeleteDictType(dictId)
    ElMessage.success('删除成功')
    if (currentDictType.value?.dictId === dictId) {
      currentDictType.value = null
      dictDataList.value = []
    }
    getDictTypeList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除字典类型失败:', error)
    }
  }
}

// 获取字典数据列表
const getDictDataList = async () => {
  if (!currentDictType.value) return
  dataLoading.value = true
  try {
    const res = await fetchGetDictDataList({
      dictType: currentDictType.value.dictType,
      dictLabel: dataSearchForm.dictLabel || undefined,
      status: dataSearchForm.status || undefined,
      current: dataPagination.current,
      size: dataPagination.size
    })
    dictDataList.value = res?.list || []
    dataPagination.total = res?.total || 0
    dataPagination.current = res?.current || dataPagination.current
    dataPagination.size = res?.size || dataPagination.size

  } catch (error) {
    console.error('获取字典数据列表失败:', error)
  } finally {
    dataLoading.value = false
  }
}

// 搜索字典数据
const handleDataSearch = () => {
  dataPagination.current = 1
  getDictDataList()
}

// 重置搜索
const handleDataReset = () => {
  dataSearchForm.dictLabel = ''
  dataSearchForm.status = ''
  dataPagination.current = 1
  getDictDataList()
}

// 新增字典数据
const handleAddDictData = () => {
  editDataData.value = null
  dataDialogVisible.value = true
}

// 编辑字典数据
const handleEditDictData = async (item: DictDataVO) => {
  try {
    // 通过ID查询详细信息
    const detail = await fetchGetDictDataById(item.dictCode)
    editDataData.value = detail
    dataDialogVisible.value = true
  } catch (error) {
    console.error('获取字典数据详情失败:', error)
    ElMessage.error('获取字典数据详情失败')
  }
}

// 删除字典数据
const handleDeleteDictData = async (dictCode: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该字典数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await fetchDeleteDictData(dictCode)
    ElMessage.success('删除成功')
    getDictDataList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除字典数据失败:', error)
    }
  }
}

// 分页大小变化
const handleDataSizeChange = (size: number) => {
  dataPagination.size = size
  dataPagination.current = 1
  getDictDataList()
}

// 分页页码变化
const handleDataCurrentChange = (page: number) => {
  dataPagination.current = page
  getDictDataList()
}

// 初始化
onMounted(() => {
  getDictTypeList()
})
</script>

<style scoped lang="scss">
.dict-page {
  padding: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .card-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 500;
  }
}

// 左侧字典类型卡片
.dict-type-card {
  :deep(.el-card__body) {
    display: flex;
    flex-direction: column;
    height: calc(100% - 56px);
  }
}

// 搜索框
.search-wrapper {
  margin-bottom: 16px;
}

// 字典类型列表
.dict-type-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;

  // 滚动条样式
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: var(--el-border-color);
    border-radius: 3px;

    &:hover {
      background-color: var(--el-border-color-dark);
    }
  }
}

// 字典类型项
.dict-type-item {
  padding: 12px;
  margin-bottom: 8px;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
  }

  &.active {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);

    .type-icon {
      color: var(--el-color-primary);
    }
  }

  &:last-child {
    margin-bottom: 0;
  }
}

.dict-type-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.dict-type-header {
  display: flex;
  align-items: flex-start;
  flex: 1;
  min-width: 0;
}

.type-icon {
  font-size: 20px;
  color: var(--el-text-color-secondary);
  margin-right: 12px;
  margin-top: 2px;
  flex-shrink: 0;
  transition: color 0.2s ease;

  &.active {
    color: var(--el-color-primary);
  }
}

.dict-type-info {
  flex: 1;
  min-width: 0;
}

.dict-type-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dict-type-code {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  font-family: 'Courier New', monospace;
}

.dict-type-actions {
  display: flex;
  gap: 4px;
  margin-left: 8px;
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.2s ease;

  .dict-type-item:hover & {
    opacity: 1;
  }
}

// 右侧字典数据区域
.dict-data-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;

  .art-table {
    flex: 1;
    min-height: 0;
    display: flex;
    flex-direction: column;
  }
}

// 右侧卡片样式调整
:deep(.h-full > .el-card) {
  display: flex;
  flex-direction: column;

  .el-card__body {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
    overflow: auto;
  }
}

.search-bar {
  flex-shrink: 0;
  margin-bottom: 16px;
  padding: 16px;
  background-color: var(--el-fill-color-light);
  border-radius: 6px;
}

.search-form {
  margin-bottom: 0;

  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: calc(100vh - 240px);
  color: var(--el-text-color-secondary);

  .empty-icon {
    color: var(--el-text-color-placeholder);
    margin-bottom: 16px;
    opacity: 0.6;
  }

  .empty-title {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 500;
    color: var(--el-text-color-regular);
  }

  .empty-desc {
    margin: 0;
    font-size: 14px;
    color: var(--el-text-color-secondary);
  }
}
</style>
