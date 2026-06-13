<!-- 任务列表物品 -->
<template>
  <div class="task-item" @click="handleClick">
    <!-- 头部：标题和标签 -->
    <div class="task-header">
      <div class="task-title-row">
        <h4 class="task-title">{{ task.title }}</h4>
        <div class="task-tags">
          <van-tag v-if="task.isOverdue" type="danger" size="medium">逾期</van-tag>
          <van-tag
            :type="getPriorityConfig(task.priority).type"
            size="medium"
          >
            {{ getPriorityConfig(task.priority).text }}
          </van-tag>
        </div>
      </div>
    </div>

    <!-- 描述 -->
    <p v-if="task.description" class="task-description">
      {{ task.description }}
    </p>

    <!-- 底部信息 -->
    <div class="task-footer">
      <!-- 左侧：负责人、时间、工时 -->
      <div class="task-info">
        <!-- 负责人 -->
        <div v-if="task.ownerUser?.nickName" class="info-item">
          <van-icon name="manager-o" />
          <span>{{ task.ownerUser?.nickName }}</span>
        </div>

        <!-- 计划时间 -->
        <div v-if="task.planStartTime || task.planEndTime" class="info-item" :class="dueDateClass">
          <van-icon name="clock-o" />
          <span>{{ formatPlanPeriod(task.planStartTime, task.planEndTime) }}</span>
        </div>

        <!-- 预计工时 -->
        <div v-if="task.planStartTime && task.planEndTime" class="info-item">
          <van-icon name="todo-list-o" />
          <span>{{ calculateHours(task.planStartTime, task.planEndTime) }}</span>
        </div>
      </div>

      <!-- 右侧：负责人 + 协作人头像 -->
      <div v-if="avatarUsers.length > 0" class="task-users">
        <template
          v-for="user in avatarUsers.slice(0, 3)"
          :key="user.userId"
        >
          <van-image
            v-if="user.avatar"
            :src="user.avatar"
            round
            width="48"
            height="48"
            class="user-avatar"
          >
            <template #error>
              <div class="user-avatar user-avatar-fallback">
                {{ getUserInitial(user.nickName) }}
              </div>
            </template>
          </van-image>
          <div
            v-else
            class="user-avatar user-avatar-fallback"
          >
            {{ getUserInitial(user.nickName) }}
          </div>
        </template>
        <span v-if="avatarUsers.length > 3" class="more-users">
          +{{ avatarUsers.length - 3 }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

defineOptions({ name: 'TaskItem' });

interface Props {
  task: Api.Task.TaskItemVO;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  click: [];
}>();

/**
 * 获取用户昵称首字作为默认头像
 */
const getUserInitial = (nickName?: string) => {
  return nickName?.trim().charAt(0) || '?';
};

/**
 * 头像列表：负责人在前，协作人在后，并按 userId 去重
 */
const avatarUsers = computed<Api.Task.TaskUser[]>(() => {
  const users = [props.task.ownerUser, ...(props.task.taskUserList || [])].filter(Boolean) as Api.Task.TaskUser[];
  const userMap = new Map<number, Api.Task.TaskUser>();

  users.forEach(user => {
    if (!userMap.has(user.userId)) {
      userMap.set(user.userId, user);
    }
  });

  return Array.from(userMap.values());
});



// 优先级配置
const priorityConfig: Record<number, { type: 'default' | 'primary' | 'success' | 'warning' | 'danger'; text: string }> = {
  1: { type: 'default', text: '低' },
  2: { type: 'primary', text: '中' },
  3: { type: 'warning', text: '高' },
  4: { type: 'danger', text: '紧急' }
};

const getPriorityConfig = (priority?: number) => priorityConfig[priority ?? -1] || { type: 'default', text: '-' };

/**
 * 紧凑格式化时间（只显示月-日）
 */
const formatCompactTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (isNaN(date.getTime())) return value;
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${month}-${day}`;
};

/**
 * 格式化计划时间范围
 */
const formatPlanPeriod = (startTime?: string, endTime?: string) => {
  if (startTime && endTime) {
    return `${formatCompactTime(startTime)} ~ ${formatCompactTime(endTime)}`;
  } else if (startTime) {
    return `${formatCompactTime(startTime)} ~`;
  } else if (endTime) {
    return `~ ${formatCompactTime(endTime)}`;
  }
  return '-';
};

/**
 * 计算预计工时
 */
const calculateHours = (startTime?: string, endTime?: string) => {
  if (!startTime || !endTime) return '-';

  const start = new Date(startTime);
  const end = new Date(endTime);
  const diffMs = end.getTime() - start.getTime();
  const diffHours = diffMs / (1000 * 60 * 60);

  if (diffHours <= 0) return '-';

  // 如果小于1小时，显示分钟
  if (diffHours < 1) {
    const minutes = Math.round(diffHours * 60);
    return `${minutes}分钟`;
  }

  // 如果小于24小时，显示小时
  if (diffHours < 24) {
    return `${diffHours.toFixed(1)}小时`;
  }

  // 如果大于24小时，显示天数
  const days = Math.floor(diffHours / 24);
  const hours = Math.round(diffHours % 24);
  return hours > 0 ? `${days}天${hours}小时` : `${days}天`;
};

// 截止日期样式
const dueDateClass = computed(() => {
  // 已完成状态：只有逾期才显示红色
  if (props.task.status === 2) {
    return props.task.isOverdue ? 'overdue' : '';
  }

  // 其他状态：逾期显示红色
  if (props.task.isOverdue) return 'overdue';

  // 其他状态：3天内到期显示橙色
  const planEndTime = props.task.planEndTime;
  if (!planEndTime) return '';
  const today = new Date();
  const dueDate = new Date(planEndTime);
  if (isNaN(dueDate.getTime())) return '';
  const diffDays = Math.ceil((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

  if (diffDays <= 3) return 'soon';
  return '';
});

const handleClick = () => {
  emit('click');
};
</script>

<style scoped lang="scss">
.task-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;

  &:active {
    transform: scale(0.98);
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  }

  .task-header {
    margin-bottom: 10px;

    .task-title-row {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 8px;

      .task-title {
        flex: 1;
        font-size: 27px;
        font-weight: 600;
        color: #1a1a1a;
        line-height: 24px;
        margin: 0;
        word-break: break-word;
      }

      .task-tags {
        display: flex;
        gap: 4px;
        flex-shrink: 0;
      }
    }
  }

  .task-description {
    font-size: 20px;
    font-weight: 400;
    color: #666666;
    line-height: 22px;
    margin: 0 0 12px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    word-break: break-word;
  }

  .task-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;

    .task-info {
      flex: 1;
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      font-size: 20px;
      font-weight: 500;
      color: #666666;

      .info-item {
        display: flex;
        align-items: center;
        gap: 4px;

        .van-icon {
          font-size: 20px;
        }

        &.overdue {
          color: #ee0a24;
          font-weight: 600;
        }

        &.soon {
          color: #ff976a;
          font-weight: 600;
        }
      }
    }

    .task-users {
      display: flex;
      align-items: center;
      flex-shrink: 0;

      .user-avatar {
        border: 2px solid #fff;
        margin-left: -8px;

        &:first-child {
          margin-left: 0;
        }
      }

      .user-avatar-fallback {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        background: #f2f3f5;
        color: #8f959e;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        font-weight: 500;
        line-height: 1;
      }

      .more-users {
        margin-left: 4px;
        font-size: 18px;       
        font-weight: bold;   
        color: #666666;       
      }
    }
  }
}
</style>