<!-- tab页 - 我的任务 -->
<template>
  <div class="my-task-page">
    <van-tabs v-model:active="active">
      <van-tab title="待处理" >
        <van-list
            :loading="loading"
            :finished="finished"
            finished-text="没有更多了"
            :load="onLoad"
        >
        </van-list>
      </van-tab>

      <van-tab title="进行中">
        <h1>进行中任务</h1>
      </van-tab>

      <van-tab title="待验收">
        <h1>待验收任务</h1>
      </van-tab>

      <van-tab title="已驳回">
        <h1>已驳回任务</h1>
      </van-tab>

      <van-tab title="已完成">
        <h1>已完成任务</h1>
      </van-tab>

    </van-tabs>
  </div>
</template>

<script setup lang="ts">

  defineOptions({name:'MyTask'})

  const loading = ref<boolean>(true);
  const finished = ref<boolean>(false);
  const active = ref(0);
  const list = ref([]);

  /**
   * 数据加载
   */
  const onLoad = async () => {

  }
</script>


<style lang="scss" scoped>
@import './style.scss';
</style>