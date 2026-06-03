<!-- 项目仓库搜索组件 -->
<template>
  <ElCard class="art-search-card" shadow="never">
    <ElForm :model="formData" label-width="80px">
      <ElRow :gutter="20">
        <ElCol :xs="24" :sm="12" :md="8" :lg="6">
          <ElFormItem label="项目编号">
            <ElInput
              v-model="formData.no"
              placeholder="请输入项目编号"
              clearable
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
        </ElCol>

        <ElCol :xs="24" :sm="12" :md="8" :lg="6">
          <ElFormItem label="项目名称">
            <ElInput
              v-model="formData.name"
              placeholder="请输入项目名称"
              clearable
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
        </ElCol>

        <ElCol :xs="24" :sm="12" :md="8" :lg="6">
          <ElFormItem label="项目类型">
            <ElSelect
              v-model="formData.type"
              placeholder="请选择项目类型"
              clearable
              class="w-full"
            >
              <ElOption
                v-for="item in projectTypeOptions"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="Number(item.dictValue)"
              />
            </ElSelect>
          </ElFormItem>
        </ElCol>

        <ElCol :xs="24" :sm="12" :md="8" :lg="6">
          <ElFormItem label-width="0">
            <ElSpace wrap>
              <ElButton type="primary" @click="handleSearch" v-ripple>
                <ArtSvgIcon icon="ri:search-line" class="mr-1" />
                搜索
              </ElButton>
              <ElButton @click="handleReset" v-ripple>
                <ArtSvgIcon icon="ri:refresh-line" class="mr-1" />
                重置
              </ElButton>
            </ElSpace>
          </ElFormItem>
        </ElCol>
      </ElRow>
    </ElForm>
  </ElCard>
</template>

<script setup lang="ts">
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { fetchGetDictDataByType } from '@/api/system/dict'

  defineOptions({ name: 'RepoSearch' })

  interface SearchForm {
    no?: string
    name?: string
    type?: number
    status?: number
  }

  const formData = defineModel<SearchForm>({ required: true })

  const emit = defineEmits<{
    search: [params: SearchForm]
    reset: []
  }>()

  // 项目类型字典选项
  const projectTypeOptions = ref<Api.System.DictDataVO[]>([])
  // 项目状态字典选项
  const projectStatusOptions = ref<Api.System.DictDataVO[]>([])

  /**
   * 加载字典数据
   */
  const loadDictData = async () => {
    try {
      // 加载项目类型字典
      projectTypeOptions.value = await fetchGetDictDataByType('project_type')

      // 加载项目状态字典
      projectStatusOptions.value = await fetchGetDictDataByType('project_status')
    } catch (error) {
      console.error('加载字典数据失败:', error)
    }
  }

  const handleSearch = () => {
    emit('search', formData.value)
  }

  const handleReset = () => {
    formData.value = {
      no: undefined,
      name: undefined,
      type: undefined,
      status: undefined
    }
    emit('reset')
  }

  // 组件挂载时加载字典数据
  onMounted(() => {
    loadDictData()
  })
</script>
