<!-- 项目协作 - 项目回收站 -->
<template>
  <div class="art-full-height">
    <div class="flex flex-col h-full">
      <!--   搜索区域   -->

      <!--   表格卡片   -->
      <ElCard class="flex flex-col flex-1 min-h-0 art-table-card" shadow="never">
        <h1>项目回收站</h1>
      </ElCard>
    </div>
  </div>
</template>

<script setup lang="ts">

import {DialogType} from "@/types";

defineOptions({ name: 'ProjectRecycle'})

const router = useRouter()

type ProjectList = Api.Project.ProjectListVO

// 弹窗相关
const dialogType = ref<DialogType>('add')
const dialogVisible = ref(false)
const currentRepoData = ref<Partial<ProjectList>>({})

// 选中行
const selectedRows = ref<ProjectList[]>([])

// 搜索表单
const searchForm = ref<Api.Project.ProjectListQueryDTO>({
  no: undefined,
  name: undefined,
  type: undefined,
  status: undefined
})
</script>


<style scoped>

</style>