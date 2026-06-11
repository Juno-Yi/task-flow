<!-- tab页 - 我的任务 -->
<template>
  <div class="my-task-page">
      <van-tabs v-model:active="active" @change="onTabChange" animated>
        <van-tab
            v-for="tab in tabs"
            :key="tab.status"
            :title="tab.title"
        >
          <div class="task-list-container">
            <van-pull-refresh
                :v-model="refreshing"
                @refresh="onRefresh"
            >
              <van-list
                  :loading="loading"
                  :finished="finished"
                  finished-text="没有更多了"
                  :load="onLoad"
              >
                <task-item v-for="item in list" :key="item.id" :data="item"/>
              </van-list>
            </van-pull-refresh>
          </div>
        </van-tab>
      </van-tabs>

  </div>
</template>

<script setup lang="ts">

  import TaskItem from "@/views/my-task/modules/task-item.vue";

  defineOptions({name:'MyTask'})

  const tabs = [
    { title: '待处理', status: 0, count: 10 },
    { title: '进行中', status: 1, count: 20 },
    { title: '待验收', status: 2, count: 10 },
    { title: '已驳回', status: 3, count: 22 },
    { title: '已完成', status: 4, count: 0 }
  ]

  const loading = ref<boolean>(false);
  const finished = ref<boolean>(false);
  const refreshing = ref<boolean>(false);
  const active = ref(0);

  const pageNum = ref(1)
  const list = ref([
    {
      id: 1,
      title: '测试1'
    },
    {
      id: 2,
      title: '测试2'
    },
    {
      id: 3,
      title: '测试3'
    },
    {
      id: 4,
      title: '测试4'
    },
    {
      id: 5,
      title: '测试5'
    },
    {
      id: 6,
      title: '测试6'
    },
    {
      id: 6,
      title: '测试6'
    },
    {
      id: 6,
      title: '测试6'
    },
    {
      id: 6,
      title: '测试6'
    },
    {
      id: 6,
      title: '测试6'
    },
    {
      id: 6,
      title: '测试6'
    },
    {
      id: 6,
      title: '测试6'
    },
    {
      id: 6,
      title: '测试6'
    },
    {
      id: 6,
      title: '测试6'
    },
  ]);

  /**
   * 当tab切换时候
   * @param index tab索引
   */
  const onTabChange = (index: number) => {
    console.log('tab索引：',index)
  }

  /**
   * 数据加载
   */
  const onLoad = async () => {

  }

  /**
   * 数据刷新
   */
  const onRefresh = async () => {
    try {

      finished.value = false
      // 重置分页
      pageNum.value = 1
      // 清空数据
      list.value = []
      // 重新加载
      await onLoad()

    } finally {
      refreshing.value = false
    }
  }
</script>


<style lang="scss" scoped>
@import './style.scss';
</style>