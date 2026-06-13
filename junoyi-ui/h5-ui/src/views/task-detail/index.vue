<!-- 任务详情页面 -->
<template>
  <div class="task-detail-page">
    <van-pull-refresh v-model="refreshing" @refresh="loadData">
      <van-skeleton title :row="10" :loading="loading">
        <template v-if="taskDetail">
          <section class="overview-card">
            <div class="overview-top">
              <div class="task-no">任务 #{{ taskDetail.id }}</div>
              <div class="overview-tags">
                <van-tag :type="statusConfig.type">{{ taskDetail.statusLabel || statusConfig.text }}</van-tag>
                <van-tag :type="priorityConfig.type">{{ taskDetail.priorityLabel || priorityConfig.text }}</van-tag>
              </div>
            </div>
            <h1>{{ taskDetail.title || '-' }}</h1>
            <p class="description">{{ taskDetail.description || '暂无任务描述' }}</p>
            <van-grid class="metric-grid" :column-num="2" :border="false">
              <van-grid-item>
                <div class="metric-item">
                  <span>预计工时</span>
                  <strong>{{ calculateHours(taskDetail.planStartTime, taskDetail.planEndTime) }}</strong>
                </div>
              </van-grid-item>
              <van-grid-item>
                <div class="metric-item">
                  <span>逾期状态</span>
                  <strong :class="taskDetail.isOverdue ? 'danger-text' : 'success-text'">{{ taskDetail.isOverdue ? '已逾期' : '未逾期' }}</strong>
                </div>
              </van-grid-item>
            </van-grid>
          </section>

          <van-cell-group inset class="info-card project-card">
            <van-cell title="项目信息" icon="folder-o">
              <template #value>
                <van-tag v-if="projectInfo?.typeLabel" size="medium" type="primary" plain>{{ projectInfo.typeLabel }}</van-tag>
              </template>
              <template #label>
                <div class="project-name">{{ projectInfo?.name || `项目 ID：${taskDetail.projectId || '-'}` }}</div>
                <div v-if="projectInfo?.description" class="project-desc">{{ projectInfo.description }}</div>
              </template>
            </van-cell>
          </van-cell-group>

          <van-cell-group inset class="info-card member-card">
            <van-cell title="任务成员" icon="friends-o" />
            <van-cell title="负责人">
              <template #label>
                <div class="user-chip">
                  <van-image v-if="taskDetail.ownerUser?.avatar" :src="taskDetail.ownerUser.avatar" round width="36" height="36" />
                  <div v-else class="avatar-fallback">{{ getUserInitial(taskDetail.ownerUser?.nickName) }}</div>
                  <span>{{ taskDetail.ownerUser?.nickName || '-' }}</span>
                </div>
              </template>
            </van-cell>
            <van-cell title="协作人">
              <template #label>
                <div v-if="taskDetail.taskUserList?.length" class="user-list">
                  <div v-for="user in taskDetail.taskUserList" :key="user.userId" class="user-chip">
                    <van-image v-if="user.avatar" :src="user.avatar" round width="36" height="36" />
                    <div v-else class="avatar-fallback">{{ getUserInitial(user.nickName) }}</div>
                    <span>{{ user.nickName || `用户${user.userId}` }}</span>
                  </div>
                </div>
                <span v-else class="empty-text">暂无协作人</span>
              </template>
            </van-cell>
          </van-cell-group>

          <van-cell-group inset class="info-card time-card">
            <van-cell title="时间信息" icon="underway-o" />
            <van-cell title="计划时间" :label="formatPlanPeriod(taskDetail.planStartTime, taskDetail.planEndTime)" />
            <van-cell title="实际开始" :value="formatTime(taskDetail.startTime)" />
            <van-cell title="实际完成" :value="formatTime(taskDetail.endTime)" />
            <van-cell title="创建时间" :value="formatTime(taskDetail.createTime)" />
            <van-cell title="更新时间" :value="formatTime(taskDetail.updateTime)" />
          </van-cell-group>

          <van-cell-group inset class="info-card remark-card">
            <van-cell title="补充信息" icon="description-o" :label="taskDetail.remark || '无备注'" />
          </van-cell-group>

          <van-cell-group v-if="taskDetail.latestRejectRecord" inset class="info-card highlight-card danger-card">
            <van-cell title="最近一次驳回" icon="warning-o" />
            <van-cell title="驳回人" :value="taskDetail.latestRejectRecord.operatorName || '-'" />
            <van-cell title="驳回时间" :value="formatTime(taskDetail.latestRejectRecord.createTime)" />
            <van-cell title="驳回原因" :label="taskDetail.latestRejectRecord.remark || '-'" />
          </van-cell-group>

          <van-cell-group v-if="taskDetail.latestSubmitRecord" inset class="info-card highlight-card primary-card">
            <van-cell title="最近一次提交" icon="completed-o" />
            <van-cell title="提交人" :value="taskDetail.latestSubmitRecord.operatorName || '-'" />
            <van-cell title="提交时间" :value="formatTime(taskDetail.latestSubmitRecord.createTime)" />
            <van-cell title="提交说明" :label="taskDetail.latestSubmitRecord.remark || '-'" />
            <van-cell v-if="taskDetail.latestSubmitRecord.attachments?.length" title="提交附件">
              <template #label>
                <div class="attachment-list">
                  <a v-for="item in taskDetail.latestSubmitRecord.attachments" :key="`${item.id}-${item.fileUrl}`" :href="getFileUrl(item.fileUrl)" target="_blank">{{ item.fileName || '附件' }}</a>
                </div>
              </template>
            </van-cell>
          </van-cell-group>

          <section v-if="taskDetail.recordList?.length" class="info-card record-card">
            <div class="card-title"><van-icon name="orders-o" />处理记录</div>
            <van-steps direction="vertical" :active="0">
              <van-step v-for="record in taskDetail.recordList" :key="record.id">
                <div class="record-title">{{ record.actionTypeLabel || '任务操作' }}</div>
                <div class="record-meta">{{ record.operatorName || '-' }} · {{ formatTime(record.createTime) }}</div>
                <div class="record-remark">{{ record.remark || '无说明' }}</div>
                <div v-if="record.attachments?.length" class="attachment-list">
                  <a v-for="item in record.attachments" :key="`${item.id}-${item.fileUrl}`" :href="getFileUrl(item.fileUrl)" target="_blank">{{ item.fileName || '附件' }}</a>
                </div>
              </van-step>
            </van-steps>
          </section>
        </template>
        <van-empty v-else description="暂无任务详情" />
      </van-skeleton>
    </van-pull-refresh>

    <div v-if="taskDetail?.status === 0" class="bottom-actions"><van-button block round type="primary" size="large" :loading="startLoading" @click="handleStartTask">开始任务</van-button></div>
    <div v-else-if="taskDetail?.status === 1 || taskDetail?.status === 3" class="bottom-actions"><van-button block size="large" round type="warning" :loading="submitLoading" @click="submitVisible = true">提交任务</van-button></div>

    <van-dialog
      v-model:show="submitVisible"
      title="提交任务"
      class-name="submit-task-dialog"
      show-cancel-button
      :before-close="handleSubmitBeforeClose"
    >
      <div class="submit-dialog-content">
        <div class="submit-label">提交说明</div>
        <van-field
          v-model="submitRemark"
          class="submit-textarea"
          rows="5"
          autosize
          type="textarea"
          placeholder="请输入提交说明"
        />
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast } from 'vant';
import { fetchGetMyTaskDetail, fetchStartMyTask, fetchSubmitMyTask } from '@/api/task/my-task';
import { fetchGetProjectInfo } from '@/api/project/detail';

defineOptions({ name: 'TaskDetail' });

const route = useRoute();
const router = useRouter();
const taskId = Number(route.params.taskId);
const loading = ref(false);
const refreshing = ref(false);
const startLoading = ref(false);
const submitLoading = ref(false);
const submitVisible = ref(false);
const submitRemark = ref('');
const taskDetail = ref<Api.Task.TaskListDetailVO>();
const projectInfo = ref<Api.Project.ProjectInfoVO>();
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

const priorityConfig = computed(() => getPriorityConfig(taskDetail.value?.priority));
const statusConfig = computed(() => getStatusConfig(taskDetail.value?.status));

/**
 * 获取任务优先级配置
 * @param priority - 优先级数值（1:低, 2:中, 3:高, 4:紧急）
 * @returns 包含显示文本和样式类型的配置对象
 */
const getPriorityConfig = (priority?: number) => {
  const map = {
    1: { text: '低', type: 'default' },
    2: { text: '中', type: 'primary' },
    3: { text: '高', type: 'warning' },
    4: { text: '紧急', type: 'danger' }
  } as const;
  return map[priority as keyof typeof map] || { text: '-', type: 'default' as const };
};

/**
 * 获取任务状态配置
 * @param status - 状态数值（0:待处理, 1:进行中, 2:待验收, 3:已驳回, 4:已完成）
 * @returns 包含显示文本和样式类型的配置对象
 */
const getStatusConfig = (status?: number) => {
  const map = {
    0: { text: '待处理', type: 'default' },
    1: { text: '进行中', type: 'primary' },
    2: { text: '待验收', type: 'warning' },
    3: { text: '已驳回', type: 'danger' },
    4: { text: '已完成', type: 'success' }
  } as const;
  return map[status as keyof typeof map] || { text: '-', type: 'default' as const };
};

/**
 * 加载任务详情及关联项目信息
 */
const loadData = async () => {
  if (!taskId) return;
  loading.value = !refreshing.value;
  try {
    taskDetail.value = await fetchGetMyTaskDetail(taskId);
    if (taskDetail.value?.projectId) {
      projectInfo.value = await fetchGetProjectInfo(taskDetail.value.projectId).catch(() => undefined);
    }
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
};

/**
 * 处理开始任务操作
 * 调用接口后刷新数据并跳转到任务列表页
 */
const handleStartTask = async () => {
  if (!taskDetail.value?.id || startLoading.value) return;
  startLoading.value = true;
  try {
    await fetchStartMyTask(taskDetail.value.id);
    showToast('任务已开始');
    await loadData();
    await delay(1200);
    router.replace('/my-task');
  } finally {
    startLoading.value = false;
  }
};

/**
 * 处理提交任务前的校验和提交逻辑
 * @param action - 操作类型，'cancel' 表示取消操作
 * @returns 是否允许关闭弹窗
 */
const handleSubmitBeforeClose = async (action: string) => {
  if (action === 'cancel') return true;
  if (!taskDetail.value?.id) return true;
  if (!submitRemark.value.trim()) {
    showToast('请填写提交说明');
    return false;
  }

  submitLoading.value = true;
  try {
    await fetchSubmitMyTask({ taskId: taskDetail.value.id, remark: submitRemark.value.trim(), attachments: [] });
    submitVisible.value = false;
    submitRemark.value = '';
    showToast('任务提交成功');
    await loadData();
    await delay(1200);
    router.replace('/my-task');
    return true;
  } finally {
    submitLoading.value = false;
  }
};

/**
 * 格式化时间为完整格式
 * @param value - 时间字符串
 * @returns 格式化后的时间字符串（YYYY-MM-DD HH:mm）或 '-'
 */
const formatTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  const pad = (num: number) => String(num).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

/**
 * 格式化时间为紧凑格式
 * @param value - 时间字符串
 * @returns 格式化后的时间字符串（MM-DD HH:mm）或 '-'
 */
const formatCompactTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return `${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

/**
 * 格式化计划时间段
 * @param startTime - 开始时间
 * @param endTime - 结束时间
 * @returns 格式化后的时间段字符串
 */
const formatPlanPeriod = (startTime?: string, endTime?: string) => {
  if (startTime && endTime) return `${formatCompactTime(startTime)} ~ ${formatCompactTime(endTime)}`;
  if (startTime) return `${formatCompactTime(startTime)} ~ 未设置`;
  if (endTime) return `未设置 ~ ${formatCompactTime(endTime)}`;
  return '未设置';
};

/**
 * 计算两个时间点之间的小时数差值
 * @param startTime - 开始时间
 * @param endTime - 结束时间
 * @returns 格式化后的时长字符串（分钟/小时/天）或 '-'
 */
const calculateHours = (startTime?: string, endTime?: string) => {
  if (!startTime || !endTime) return '-';
  const diffHours = (new Date(endTime).getTime() - new Date(startTime).getTime()) / 36e5;

  if (diffHours <= 0 || Number.isNaN(diffHours)) return '-';
  if (diffHours < 1) return `${Math.round(diffHours * 60)} 分钟`;
  if (diffHours < 24) return `${diffHours.toFixed(1)} 小时`;
  const days = Math.floor(diffHours / 24);
  const hours = Math.round(diffHours % 24);
  return hours > 0 ? `${days} 天 ${hours} 小时` : `${days} 天`;
};

/**
 * 获取用户昵称的首字母
 * @param nickName - 用户昵称
 * @returns 首字母字符，如果为空则返回 '?'
 */
const getUserInitial = (nickName?: string) => nickName?.trim().charAt(0) || '?';

/**
 * 获取文件URL，如果为空则返回占位符
 * @param url - 文件URL
 * @returns 原始URL或占位符
 */
const getFileUrl = (url?: string) => url || 'javascript:void(0)';

onMounted(loadData);

</script>

<style lang="scss" scoped>
@use './style.scss' as *;
</style>
