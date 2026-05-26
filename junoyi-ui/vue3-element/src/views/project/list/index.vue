<!-- 项目协作 - 项目仓库 -->
<template>
  <div class="art-full-height">
    <div class="flex flex-col h-full">
      <!-- 搜索区域 -->
      <RepoSearch v-model="searchForm" @search="handleSearch" @reset="resetSearchParams" />

      <!-- 表格卡片 -->
      <ElCard class="flex flex-col flex-1 min-h-0 art-table-card" shadow="never">
        <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
          <template #left>
            <ElSpace wrap>
              <ElButton 
                @click="showDialog('add')"
                v-permission="'project.ui.list.add.button'"
                v-ripple
              >
                <ArtSvgIcon icon="ri:add-line" class="mr-1" />
                新建项目
              </ElButton>
              <ElButton 
                :disabled="selectedRows.length === 0"
                @click="batchDeleteRepos"
                v-permission="'project.ui.list.delete.button'"
                v-ripple
              >
                <ArtSvgIcon icon="ri:delete-bin-line" class="mr-1" />
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
    </div>

    <!-- 仓库弹窗 -->
    <RepoDialog
      v-model:visible="dialogVisible"
      :type="dialogType"
      :repo-data="currentRepoData"
      @submit="handleDialogSubmit"
    />

    <!-- 删除验证弹窗 -->
    <DeleteVerifyDialog
      ref="deleteVerifyDialogRef"
      v-model:visible="deleteVerifyDialogVisible"
      :project-count="deleteProjectCount"
      @confirm="handleDeleteConfirm"
    />
  </div>
</template>

<script setup lang="ts">
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import ArtButtonMore, { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { useRouter } from 'vue-router'
  import RepoSearch from './modules/repo-search.vue'
  import RepoDialog from './modules/repo-dialog.vue'
  import DeleteVerifyDialog from './modules/delete-verify-dialog.vue'
  import { ElTag, ElMessageBox, ElProgress } from 'element-plus'
  import { DialogType } from '@/types'
  import { fetchGetRepoList, fetchDeleteRepo, fetchDeleteRepoBatch, fetchExportProjectBook } from '@/api/project/list'

  defineOptions({ name: 'ProjectRepo' })

  const router = useRouter()

  // 临时类型定义，后续需要在 types 中定义
  interface RepoVO extends Api.Project.ProjectListVO {
    // 扩展字段（如果需要）
  }

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentRepoData = ref<Partial<RepoVO>>({})

  // 删除验证弹窗
  const deleteVerifyDialogVisible = ref(false)
  const deleteVerifyDialogRef = ref()
  const deleteProjectCount = ref(0)
  const pendingDeleteIds = ref<number[]>([])

  // 选中行
  const selectedRows = ref<RepoVO[]>([])

  // 搜索表单
  const searchForm = ref<Api.Project.ProjectListQueryDTO>({
    no: undefined,
    name: undefined,
    type: undefined,
    status: undefined
  })

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    searchParams,
    resetSearchParams: _resetSearchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    core: {
      apiFn: fetchGetRepoList,
      apiParams: {
        current: 1,
        size: 20,
        ...searchForm.value
      },
      columnsFactory: () => [
        { type: 'selection', width: 50 },
        {
          prop: 'no',
          label: '项目编号',
          width: 180,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            return h('span', {
              class: 'font-mono text-primary font-medium',
              onClick: () => viewRepo(row)
            }, row.no)
          }
        },
        {
          prop: 'name',
          label: '项目名称',
          width: 200,
          align: 'left',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            return h('div', { 
              class: 'flex items-center cursor-pointer hover:text-primary transition-colors',
              onClick: () => viewRepo(row)
            }, [
              h(ArtSvgIcon, { 
                icon: 'ri:git-repository-line', 
                class: 'mr-2 text-lg text-primary' 
              }),
              h('span', { class: 'font-medium' }, row.name)
            ])
          }
        },
        {
          prop: 'description',
          label: '项目描述',
          minWidth: 180,
          align: 'left',
          headerAlign: 'center',
          formatter: (row: RepoVO) => row.description || '-'
        },
        {
          prop: 'leaderName',
          label: '项目负责人',
          width: 120,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            return h('div', { class: 'flex items-center justify-center' }, [
              h(ArtSvgIcon, { 
                icon: 'ri:user-line', 
                class: 'mr-1 text-primary' 
              }),
              h('span', row.leaderName)
            ])
          }
        },
        {
          prop: 'memberCount',
          label: '成员',
          width: 80,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            return h('div', {
              class: 'flex items-center justify-center'
            }, [
              h(ArtSvgIcon, { 
                icon: 'ri:team-line', 
                class: 'mr-1 text-primary' 
              }),
              h('span', { class: 'font-medium' }, row.memberCount)
            ])
          }
        },
        {
          prop: 'progress',
          label: '项目进度',
          width: 150,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            return h('div', { class: 'w-full px-2' }, [
              h('div', { class: 'flex items-center justify-between mb-1' }, [
                h('span', { class: 'text-sm font-medium' }, `${row.progress}%`),
                h('span', { class: 'text-xs text-gray-500' }, `${row.completedTasks}/${row.totalTasks}`)
              ]),
              h(ElProgress, {
                percentage: row.progress,
                strokeWidth: 6,
                showText: false
              })
            ])
          }
        },
        {
          prop: 'typeLabel',
          label: '项目类型',
          width: 110,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            const validTypes = ['success', 'info', 'warning', 'danger']
            const type = validTypes.includes(row.typeLabelType || '') ? row.typeLabelType : 'info'
            return h(ElTag, { 
              type: type as 'success' | 'info' | 'warning' | 'danger', 
              size: 'small' 
            }, () => row.typeLabel || '-')
          }
        },
        {
          prop: 'statusLabel',
          label: '状态',
          width: 90,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            const validTypes = ['success', 'info', 'warning', 'danger']
            const type = validTypes.includes(row.statusType || '') ? row.statusType : 'info'
            return h(ElTag, { 
              type: type as 'success' | 'info' | 'warning' | 'danger', 
              size: 'small' 
            }, () => row.statusLabel || '-')
          }
        },
        {
          prop: 'priorityLabel',
          label: '优先级',
          width: 90,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            if (!row.priorityLabel) return h('span', '-')
            const validTypes = ['success', 'info', 'warning', 'danger']
            const type = validTypes.includes(row.priorityType || '') ? row.priorityType : 'info'
            return h(ElTag, { 
              type: type as 'success' | 'info' | 'warning' | 'danger', 
              size: 'small' 
            }, () => row.priorityLabel)
          }
        },
        {
          prop: 'planPeriod',
          label: '计划周期',
          width: 210,
          align: 'center',
          headerAlign: 'center',
          formatter: (row: RepoVO) => {
            if (row.planStartTime && row.planEndTime) {
              const startDate = formatTime(row.planStartTime as any)
              const endDate = formatTime(row.planEndTime as any)
              return `${startDate} ~ ${endDate}`
            } else if (row.planStartTime) {
              return `${formatTime(row.planStartTime as any)} ~ 未设置`
            } else if (row.planEndTime) {
              return `未设置 ~ ${formatTime(row.planEndTime as any)}`
            }
            return '未设置'
          }
        },
        {
          prop: 'operation',
          label: '操作',
          width: 80,
          align: 'center',
          headerAlign: 'center',
          fixed: 'right',
          formatter: (row: RepoVO) => {
            const list: ButtonMoreItem[] = [
              {
                key: 'edit',
                label: '编辑',
                icon: 'ri:edit-line',
                auth: 'project.ui.list.edit.button'
              },
              {
                key: 'delete',
                label: '删除',
                icon: 'ri:delete-bin-4-line',
                auth: 'project.ui.list.delete.button',
                color: '#f56c6c'
              }
            ]
            
            return h(ArtButtonMore, {
              list,
              onClick: (item: ButtonMoreItem) => handleButtonMoreClick(item, row)
            })
          }
        }
      ]
    }
  })

  /**
   * 格式化时间 - 只显示年月日
   */
  const formatTime = (time: string | undefined): string => {
    if (!time) return '-'
    // 处理ISO 8601格式 (2026-02-15T04:29:18.000+08:00) 或普通格式 (2024-01-15 10:30:00)
    if (time.includes('T')) {
      return time.split('T')[0] // ISO格式，取T之前的日期部分
    }
    return time.split(' ')[0] // 普通格式，取空格之前的日期部分
  }

  /**
   * 复制到剪贴板
   */
  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text).then(() => {
      ElMessage.success('已复制到剪贴板')
    }).catch(() => {
      ElMessage.error('复制失败')
    })
  }

  /**
   * 搜索处理
   */
  const handleSearch = (params: Record<string, any>) => {
    Object.assign(searchParams, params)
    getData()
  }



  /**
   * 重置搜索参数
   */
  const resetSearchParams = () => {
    _resetSearchParams()
    getData()
  }

  /**
   * 查看项目详情
   */
  const viewRepo = (row: RepoVO) => {
    // 跳转到项目详情页，使用查询参数传递项目编号
    router.push({
      path: '/project/detail',
      query: { no: row.no }
    })
  }

  /**
   * 显示仓库弹窗
   */
  const showDialog = (type: DialogType, row?: RepoVO): void => {
    dialogType.value = type
    currentRepoData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 操作按钮点击
   */
  const handleButtonMoreClick = (item: ButtonMoreItem, row: RepoVO) => {
    switch (item.key) {
      case 'edit':
        showDialog('edit', row)
        break
      case 'delete':
        deleteRepo(row)
        break
    }
  }

  /**
   * 删除项目
   */
  const deleteRepo = async (row: RepoVO): Promise<void> => {
    try {
      await ElMessageBox.confirm(`确定要删除项目 "${row.name}" 吗？`, '删除项目', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      // 设置待删除的项目
      pendingDeleteIds.value = [row.id]
      deleteProjectCount.value = 1
      deleteVerifyDialogVisible.value = true
    } catch (error) {
      // 用户取消
    }
  }

  /**
   * 批量删除项目
   */
  const batchDeleteRepos = async (): Promise<void> => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请先选择要删除的项目')
      return
    }
    try {
      await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个项目吗？`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      // 设置待删除的项目
      pendingDeleteIds.value = selectedRows.value.map(row => row.id)
      deleteProjectCount.value = selectedRows.value.length
      deleteVerifyDialogVisible.value = true
    } catch (error) {
      // 用户取消
    }
  }

  /**
   * 处理删除确认（密码验证通过后）
   */
  const handleDeleteConfirm = async (credentials: { password: string }) => {
    try {
      deleteVerifyDialogRef.value?.setLoading(true)
      
      if (pendingDeleteIds.value.length === 1) {
        // 单个删除
        await fetchDeleteRepo(pendingDeleteIds.value[0], credentials)
      } else {
        // 批量删除
        await fetchDeleteRepoBatch({
          ids: pendingDeleteIds.value,
          ...credentials
        })
      }
      
      deleteVerifyDialogVisible.value = false
      selectedRows.value = []
      getData()
    } catch (error: any) {
      // 错误会由 HTTP 工具自动显示，不需要额外处理
      // 不关闭弹窗，让用户可以重新输入密码
    } finally {
      deleteVerifyDialogRef.value?.setLoading(false)
    }
  }

  /**
   * 导出项目书
   */
  const exportProjectBook = async (): Promise<void> => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请先选择要导出的项目')
      return
    }
    
    try {
      const projectNames = selectedRows.value.map(row => row.name).join('、')
      ElMessage.info(`正在生成项目书：${projectNames}`)
      
      const projectIds = selectedRows.value.map(row => row.id)
      await fetchExportProjectBook(projectIds)
      
      ElMessage.success(`已成功导出 ${selectedRows.value.length} 个项目的项目书`)
    } catch (error) {
      console.error('导出失败:', error)
      ElMessage.error('导出失败')
    }
  }

  /**
   * 处理弹窗提交事件
   */
  const handleDialogSubmit = async () => {
    try {
      dialogVisible.value = false
      currentRepoData.value = {}
      getData()
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: RepoVO[]): void => {
    selectedRows.value = selection
  }
</script>

<style scoped>
  /* 自定义样式 */
</style>
