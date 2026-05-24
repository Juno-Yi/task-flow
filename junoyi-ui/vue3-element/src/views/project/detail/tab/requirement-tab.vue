<!-- 需求Tab -->
<template>
  <h1>项目需求管理tab</h1>
</template>

<script setup lang="ts">
  import { fetchGetProjectRequirementList } from "@/api/project/requirement";
  import {useProjectRole} from "@/hooks/useProjectRole";

  defineOptions({name:'RequirementTab'})

  interface Props {
    projectInfo: Api.Project.ProjectDetailVO
  }

  const props = defineProps<Props>()

  // 使用项目角色权限
  const projectRole = useProjectRole(computed(() => props.projectInfo.currentUserRole))

  // 需求列表
  const requirementList = ref<Api.Project.ProjectRequirementVO[]>();
  const loading = ref(false)

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

  // 监听项目信息变化，重新加载数据
  watch(() => props.projectInfo.id, (newId) => {
    if (newId) {
      loadRequirementList()
    }
  }, {immediate: true})
</script>

<style scoped>

</style>