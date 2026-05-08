<!-- OAuth配置管理页面 -->
<template>
  <div class="art-full-height">
    <OauthConfigSearch
      v-show="showSearchBar"
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    />

    <ElCard
      class="art-table-card"
      shadow="never"
      :style="{ 'margin-top': showSearchBar ? '12px' : '0' }"
    >
      <ArtTableHeader
        v-model:columns="columnChecks"
        v-model:showSearchBar="showSearchBar"
        :loading="loading"
        @refresh="refreshData"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton v-permission="'oauth.ui.config.button.add'" @click="showDialog('add')" v-ripple>
              新增配置
            </ElButton>
            <ElButton
              v-permission="'oauth.ui.config.button.delete'"
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
              v-ripple
            >
              批量删除
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>

    <!-- 新增/编辑对话框 -->
    <OauthConfigDialog
      v-model="dialogVisible"
      :type="dialogType"
      :data="currentConfigData"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
  import { ref, h } from 'vue'
  import { ElTag, ElMessageBox, ElMessage } from 'element-plus'
  import ArtButtonMore, { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { usePermission } from '@/hooks/core/usePermission'
  import { fetchGetOauthConfigList, fetchDeleteOauthConfig, fetchDeleteOauthConfigBatch } from '@/api/system/oauth'
  import OauthConfigSearch from './modules/oauth-config-search.vue'
  import OauthConfigDialog from './modules/oauth-config-dialog.vue'

  defineOptions({ name: 'OauthConfig' })

  type OauthConfigVO = Api.Oauth.OauthConfigVO

  const { hasPermission } = usePermission()

  // 搜索表单
  const searchForm = ref({
    platform: undefined,
    status: undefined,
    platformName: undefined
  })

  const showSearchBar = ref(true)
  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const currentConfigData = ref<OauthConfigVO | undefined>(undefined)
  const selectedIds = ref<number[]>([])

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    searchParams,
    resetSearchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    core: {
      apiFn: fetchGetOauthConfigList,
      apiParams: {
        current: 1,
        size: 20
      },
      columnsFactory: () => [
        { type: 'selection', width: 50, align: 'center' },
        {
          prop: 'id',
          label: 'ID',
          width: 80,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'platformLabel',
          label: '平台',
          width: 120,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'platform',
          label: '平台代码',
          width: 120,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'statusLabel',
          label: '状态',
          width: 100,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: OauthConfigVO) => {
            return h(
              ElTag,
              {
                type: row.isSystem ? 'success' : 'info',
                size: 'small'
              },
              () => (row.isSystem ? '是' : '否')
            )
          }
        },
        {
          prop: 'createTime',
          label: '创建时间',
          width: 180,
          align: 'center',
          headerAlign: 'center'
        },
        {
          prop: 'remark',
          label: '备注',
          minWidth: 150,
          align: 'center',
          headerAlign: 'center',
          showOverflowTooltip: true
        },
        {
          prop: 'action',
          label: '操作',
          width: 180,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: OauthConfigVO) => {
            const items: ButtonMoreItem[] = [
              {
                label: '编辑',
                icon: 'ri:edit-line',
                permission: 'oauth.ui.config.button.edit',
                onClick: () => showDialog('edit', row)
              },
              {
                label: '删除',
                icon: 'ri:delete-bin-line',
                type: 'danger',
                permission: 'oauth.ui.config.button.delete',
                onClick: () => handleDelete(row)
              }
            ]

            return h(ArtButtonMore, { items })
          }
        }
      ]
    },
    hooks: {
      resetFormCallback: () => {
        searchForm.value = {
          platform: undefined,
          status: undefined,
          platformName: undefined
        }
      }
    }
  })

  // 搜索
  const handleSearch = () => {
    Object.assign(searchParams, searchForm.value)
    getData()
  }

  // 显示对话框
  const showDialog = (type: 'add' | 'edit', data?: OauthConfigVO) => {
    dialogType.value = type
    currentConfigData.value = data
    dialogVisible.value = true
  }

  // 对话框成功回调
  const handleDialogSuccess = () => {
    refreshData()
  }

  // 选择变化
  const handleSelectionChange = (selection: OauthConfigVO[]) => {
    selectedIds.value = selection.map((item) => item.id)
  }

  // 删除单个
  const handleDelete = async (row: OauthConfigVO) => {
    try {
      await ElMessageBox.confirm(`确定要删除平台"${row.platformLabel}"的配置吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await fetchDeleteOauthConfig(row.id)
      ElMessage.success('删除成功')
      refreshData()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除失败:', error)
      }
    }
  }

  // 批量删除
  const handleBatchDelete = async () => {
    try {
      await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条配置吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await fetchDeleteOauthConfigBatch(selectedIds.value)
      ElMessage.success('删除成功')
      selectedIds.value = []
      refreshData()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量删除失败:', error)
      }
    }
  }
</script>

<style scoped lang="scss">
  .art-full-height {
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .art-table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;

    :deep(.el-card__body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      min-height: 0;
    }
  }
</style>


