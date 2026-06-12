<!-- tab页 - 我的任务 -->
<template>
  <div class="my-task-page">
    <van-tabs v-model:active="activeTab" @change="onTabChange" animated>
      <van-tab
        v-for="tab in tabs"
        :key="tab.status"
        :title="tab.title"
      >
        <div class="task-list-container">
          <van-pull-refresh
            v-model="refreshing"
            @refresh="onRefresh"
          >
            <van-list
              v-model:loading="loading"
              :finished="finished"
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
      </van-tab>
    </van-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { showToast } from 'vant';
import TaskItem from '@/views/my-task/modules/task-item.vue';

defineOptions({ name: 'MyTask' });

// Tab 配置
const tabs = [
  { title: '待处理', status: 0 },
  { title: '进行中', status: 1 },
  { title: '待验收', status: 2 },
  { title: '已驳回', status: 3 },
  { title: '已完成', status: 4 }
];

// 当前激活的 tab
const activeTab = ref(0);

// 加载状态
const loading = ref(false);
const finished = ref(true); // 默认完成，因为是固定数据
const refreshing = ref(false);

// Mock 数据 - 模拟不同状态的任务
const mockTaskList: Record<number, Api.Task.TaskItemVO[]> = {
  // 待处理
  0: [
    {
      id: 1,
      title: '完成需求文档编写',
      description: '需要完成详细的需求分析，包括功能模块划分和技术实现方案',
      status: 0,
      priority: 3,
      ownerUser: { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' },
      taskUserList: [
        { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' },
        { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' }
      ],
      planStartTime: '2024-01-05 09:00:00',
      planEndTime: '2024-01-15 18:00:00',
      isOverdue: false
    },
    {
      id: 2,
      title: '修复登录页面Bug',
      description: '用户反馈登录时出现白屏，需要排查并修复',
      status: 0,
      priority: 4,
      ownerUser: { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' },
      taskUserList: [
        { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' }
      ],
      planStartTime: '2024-01-10 09:00:00',
      planEndTime: '2024-01-12 18:00:00',
      isOverdue: true
    },
    {
      id: 3,
      title: '优化首页加载性能',
      description: '首页加载时间过长，需要优化资源加载和渲染性能',
      status: 0,
      priority: 2,
      ownerUser: { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' },
      taskUserList: [
        { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' },
        { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' },
        { userId: 4, nickName: '赵六', avatar: 'https://picsum.photos/100/100?random=4' }
      ],
      planStartTime: '2024-01-08 09:00:00',
      planEndTime: '2024-01-20 18:00:00',
      isOverdue: false
    }
  ],
  // 进行中
  1: [
    {
      id: 11,
      title: '设计数据库表结构',
      description: '根据业务需求设计合理的数据库表结构',
      status: 1,
      priority: 3,
      ownerUser: { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' },
      taskUserList: [
        { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' }
      ],
      planStartTime: '2024-01-03 09:00:00',
      planEndTime: '2024-01-18 18:00:00',
      isOverdue: false
    },
    {
      id: 12,
      title: '编写单元测试',
      description: '编写核心业务逻辑的单元测试，覆盖率要达到80%以上',
      status: 1,
      priority: 2,
      ownerUser: { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' },
      taskUserList: [
        { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' },
        { userId: 4, nickName: '赵六', avatar: 'https://picsum.photos/100/100?random=4' }
      ],
      planStartTime: '2024-01-06 09:00:00',
      planEndTime: '2024-01-16 18:00:00',
      isOverdue: false
    },
    {
      id: 13,
      title: '代码Review',
      status: 1,
      priority: 1,
      ownerUser: { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' },
      taskUserList: [
        { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' }
      ],
      planStartTime: '2024-01-12 09:00:00',
      planEndTime: '2024-01-13 18:00:00',
      isOverdue: false
    },
    {
      id: 14,
      title: '前端UI组件开发',
      description: '根据设计稿完成前端组件开发',
      status: 1,
      priority: 3,
      ownerUser: { userId: 4, nickName: '赵六', avatar: 'https://picsum.photos/100/100?random=4' },
      taskUserList: [
        { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' },
        { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' },
        { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' },
        { userId: 5, nickName: '钱七', avatar: 'https://picsum.photos/100/100?random=5' }
      ],
      planStartTime: '2024-01-01 09:00:00',
      planEndTime: '2024-01-25 18:00:00',
      isOverdue: false
    }
  ],
  // 待验收
  2: [
    {
      id: 21,
      title: '接口文档更新',
      description: '更新API接口文档，确保文档与代码一致',
      status: 2,
      priority: 2,
      ownerUser: { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' },
      taskUserList: [
        { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' }
      ],
      planStartTime: '2024-01-08 09:00:00',
      planEndTime: '2024-01-14 18:00:00',
      isOverdue: false
    },
    {
      id: 22,
      title: '移动端适配',
      description: '适配移动端不同屏幕尺寸，确保良好的用户体验',
      status: 2,
      priority: 3,
      ownerUser: { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' },
      taskUserList: [
        { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' }
      ],
      planStartTime: '2024-01-05 09:00:00',
      planEndTime: '2024-01-15 18:00:00',
      isOverdue: false
    }
  ],
  // 已驳回
  3: [
    {
      id: 31,
      title: '用户反馈问题处理',
      description: '处理用户在反馈系统中提交的问题和建议',
      status: 3,
      priority: 2,
      ownerUser: { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' },
      taskUserList: [
        { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' }
      ],
      planStartTime: '2024-01-07 09:00:00',
      planEndTime: '2024-01-10 18:00:00',
      isOverdue: true,
      remark: '需求不明确，需要重新评审'
    }
  ],
  // 已完成
  4: [
    {
      id: 41,
      title: '系统架构设计',
      description: '设计系统整体架构，包括前后端分离、微服务等',
      status: 4,
      priority: 4,
      ownerUser: { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' },
      taskUserList: [
        { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' },
        { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' }
      ],
      planStartTime: '2024-01-01 09:00:00',
      planEndTime: '2024-01-10 18:00:00',
      isOverdue: false
    },
    {
      id: 42,
      title: '技术方案评审',
      description: '组织技术方案评审会议，确保方案的可行性',
      status: 4,
      priority: 3,
      ownerUser: { userId: 2, nickName: '李四', avatar: 'https://picsum.photos/100/100?random=2' },
      taskUserList: [
        { userId: 1, nickName: '张三', avatar: 'https://picsum.photos/100/100?random=1' }
      ],
      planStartTime: '2024-01-02 09:00:00',
      planEndTime: '2024-01-08 18:00:00',
      isOverdue: false
    },
    {
      id: 43,
      title: '产品演示准备',
      status: 4,
      priority: 2,
      ownerUser: { userId: 3, nickName: '王五', avatar: 'https://picsum.photos/100/100?random=3' },
      taskUserList: [
        { userId: 4, nickName: '赵六', avatar: 'https://picsum.photos/100/100?random=4' }
      ],
      planStartTime: '2024-01-03 09:00:00',
      planEndTime: '2024-01-09 18:00:00',
      isOverdue: false
    }
  ]
};

// 当前显示的任务列表
const currentTaskList = computed(() => {
  return mockTaskList[tabs[activeTab.value].status] || [];
});

/**
 * Tab 切换
 */
const onTabChange = (index: number) => {
  console.log('切换到 tab:', tabs[index].title, '任务数量:', currentTaskList.value.length);
};

/**
 * 加载更多
 */
const onLoad = async () => {
  // Mock 数据是固定的，不需要加载更多
  loading.value = false;
  finished.value = true;
};

/**
 * 下拉刷新
 */
const onRefresh = async () => {
  // 模拟刷新延迟
  await new Promise(resolve => setTimeout(resolve, 500));
  refreshing.value = false;
  showToast('刷新成功');
};

/**
 * 任务点击
 */
const handleTaskClick = (task: Api.Task.TaskItemVO) => {
  console.log('点击任务:', task.id, task.title);
  showToast(`点击了任务：${task.title}`);
};
</script>

<style lang="scss" scoped>
@use './style.scss';
</style>