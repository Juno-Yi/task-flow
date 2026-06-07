<!-- OAuth 自动登录加载页面 -->
<template>
  <div class="oauth-loading-container">
    <div class="loading-content">
      <van-loading size="48" vertical>
        <template #icon>
          <van-icon name="success" v-if="status === 'success'" color="#07c160" size="48" />
          <van-icon name="warning" v-else-if="status === 'error'" color="#ee0a24" size="48" />
        </template>
        {{ statusText }}
      </van-loading>

      <div class="tips" v-if="errorMessage">
        <p>{{ errorMessage }}</p>
        <van-button type="primary" size="small" @click="retry" v-if="canRetry">
          重试
        </van-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '@/store/modules/user';
import {ClientType, getClientType} from "@/utils/oauth";

defineOptions({name: 'OauthLoading'});

const router = useRouter();
const userStore = useUserStore();

const statusText = ref('正在初始化...');
const status = ref<'loading' | 'success' | 'error'>('loading');
const errorMessage = ref('');
const canRetry = ref(false);

// 获取平台名称
const getPlatformName = (platform: string): string => {
  const names: Record<string, string> = {
    wework: '企业微信',
    feishu: '飞书',
    dingtalk: '钉钉'
  };
  return names[platform] || platform;
};

// 重试
const retry = () => {
  status.value = 'loading';
  errorMessage.value = '';
  canRetry.value = false;
  init();
};

// 初始化
const init = async () => {
  try {
    // 检查是否已登录
    if (userStore.isLogin && userStore.accessToken) {
      statusText.value = '已登录，正在跳转...';
      setTimeout(() => {
        router.replace('/home');
      }, 500);
      return;
    }
    statusText.value = '正在检测运行环境...';

    const type = getClientType()
    if (type === ClientType.WEWORK)
      statusText.value = '当前运行环境为企业微信'
    if (type === ClientType.FEISHU)
      statusText.value = '当前运行环境为飞书'
    if (type === ClientType.DINGTALK)
      statusText.value = '当前运行环境为钉钉'
    if (type === ClientType.BROWSER)
      statusText.value = '当前运行环境为其他浏览器环境'


  } catch (error: any) {
    console.error('初始化失败:', error);
    status.value = 'error';
    statusText.value = '初始化失败';
    errorMessage.value = error.message || '发生未知错误';
    canRetry.value = true;
  }
};

onMounted(() => {
  init();
});

</script>

<style scoped lang="scss">

</style>