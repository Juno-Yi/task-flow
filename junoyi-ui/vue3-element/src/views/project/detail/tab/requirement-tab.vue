<!-- 需求Tab -->
<template>
  <div class="h-full flex flex-col">
    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div class="text-sm text-gray-500">
        共 {{ requirementList.length }} 个需求
      </div>
      <ElButton v-if="projectRole.isOwner.value" type="primary" @click="handleAdd">
        <ArtSvgIcon icon="ri:add-line" class="mr-1" />
        添加需求
      </ElButton>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { fetchGetProjectRequirementList } from "@/api/project/requirement";
  import {useProjectRole} from "@/hooks/useProjectRole";
  import ArtSvgIcon from "@/components/core/base/art-svg-icon/index.vue";

  defineOptions({name:'RequirementTab'})

  interface Props {
    projectInfo: Api.Project.ProjectDetailVO
  }

  const props = defineProps<Props>()

  // 使用项目角色权限
  const projectRole = useProjectRole(computed(() => props.projectInfo.currentUserRole))

  // 需求列表
  const requirementList = ref<Api.Project.ProjectRequirementVO[]>([]);
  const loading = ref(false)

  // 表单数据
  const formData = ref<Api.Project.ProjectRequirementDTO>({

  })


  /**
   * 加载需求列表数据
   */
  const loadRequirementList = async () => {
    try {
      loading.value = true
      const data = await fetchGetProjectRequirementList(props.projectInfo.id, {});
      console.log("调试：",data)
    } catch (error){
      console.error('加载需求列表失败：',error)
      ElMessage.error('加载仓库列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 添加需求
   */
  const handleAdd = async () => {

  }

  // 监听项目信息变化，重新加载数据
  watch(() => props.projectInfo.id, (newId) => {
    if (newId) {
      loadRequirementList()
    }
  }, {immediate: true})
</script>

<style scoped>

</style>