<!-- tab页 - 我的任务 -->
<template>
  <div class="my-task-page">
    <van-tabs v-model:active="activeTab" @change="onTabChange" animated>
      <van-tab
        v-for="tab in tabs"
        :key="tab.status"
        :title="tab.title"
      />
    </van-tabs>

    <div class="task-list-container">
      <van-pull-refresh
        v-model="refreshing"
        @refresh="onRefresh"
      >
        <van-list
          v-model:loading="loading"
          :finished="finished"
          :immediate-check="false"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <!-- 任务列表 -->
          <TaskItem
            v-for="item in currentTaskList"
            :key="item.id"
            :task="item"
            @click="handleTaskClick(item)"
          />

          <!-- 空状态 -->
          <van-empty
            v-if="currentTaskList.length === 0 && !loading"
            description="暂无任务"
          />
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { showToast } from 'vant';
import TaskItem from '@/views/my-task/modules/task-item.vue';
import { fetchGetMyTaskList } from '@/api/task/my-task';

defineOptions({ name: 'MyTask' });

// Tab 配置
const tabs = [
  { title: '待处理', status: 0 },
  { title: '进行中', status: 1 },
  { title: '待验收', status: 2 },
  { title: '已驳回', status: 3 },
  { title: '已完成', status: 4 }
] as const;

type TaskStatus = (typeof tabs)[number]['status'];

// 当前激活的 tab
const activeTab = ref(0);

// 加载状态
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);

// 分页参数
const pageParams = ref({
  current: 1,
  size: 10
});

// 任务列表数据 - 按状态存储
const taskListMap = ref<Record<TaskStatus, Api.Task.TaskItemVO[]>>({
  0: [],
  1: [],
  2: [],
  3: [],
  4: []
});

// 当前选中的任务状态
const currentStatus = computed<TaskStatus>(() => tabs[activeTab.value]?.status ?? 0);

// 当前显示的任务列表
const currentTaskList = computed(() => taskListMap.value[currentStatus.value] || []);

/**
 * 获取任务列表
 */
const getTaskList = async (isRefresh = false) => {
  if (loading.value && !isRefresh) return;

  const status = currentStatus.value;

  try {
    if (isRefresh) {
      pageParams.value.current = 1;
      taskListMap.value[status] = [];
      finished.value = false;
    }

    loading.value = true;

    // request 已经解包响应 data，这里拿到的是分页对象 PageResult
    const res = await fetchGetMyTaskList({
      status,
      current: pageParams.value.current,
      size: pageParams.value.size
    });

    const { list = [], current = 1, pages = 1, total = 0 } = res;

    taskListMap.value[status] = isRefresh
      ? list
      : [...taskListMap.value[status], ...list];

    pageParams.value.current = current;
    finished.value = current >= pages || taskListMap.value[status].length >= total;
  } catch (error) {
    console.error('获取任务列表失败:', error);
    showToast('获取任务列表失败');
    finished.value = true;
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
};

/**
 * Tab 切换
 */
const onTabChange = (index: number) => {
  const tab = tabs[index];
  if (!tab) return;

  console.log('切换到 tab:', tab.title);
  pageParams.value.current = 1;
  finished.value = false;
  loading.value = false;

  getTaskList(true);
};

/**
 * 加载更多
 */
const onLoad = async () => {
  if (finished.value || loading.value) return;

  pageParams.value.current += 1;
  await getTaskList(false);
};

/**
 * 下拉刷新
 */
const onRefresh = async () => {
  await getTaskList(true);
  showToast('刷新成功');
};

/**
 * 任务点击
 */
const handleTaskClick = (task: Api.Task.TaskItemVO) => {
  console.log('点击任务:', task.id, task.title);
  showToast(`点击了任务：${task.title}`);
};

onMounted(() => {
  getTaskList(true);
});
</script>

<style lang="scss" scoped>
@use './style.scss' as *;
</style>