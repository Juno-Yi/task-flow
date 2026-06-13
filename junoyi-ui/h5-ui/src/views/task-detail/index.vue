<!-- 任务详情页面 -->
<template>
  <div class="task-detail-page">
    <p>任务ID: {{ taskId}}</p>

  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'TaskDetail'})

  const route = useRoute();
  const taskId = route.params.taskId;

  const taskDetail = ref<Api.Task.TaskListDetailVO>()

  /**
   * 加载数据
   */
  const loadData = async () => {

  }

  onMounted(() => {
    loadData()
  })
</script>

<style lang="scss" scoped>
@use './style.scss' as *;
</style>