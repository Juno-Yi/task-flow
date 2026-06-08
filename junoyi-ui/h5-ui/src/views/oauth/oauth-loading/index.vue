<!-- OAuth 自动登录加载页面 -->
<template>
  <div class="oauth-loading-container">
    <div class="loading-content">
      <van-loading size="48" vertical>
        {{ statusText }}
      </van-loading>
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
    switch (type){
      case ClientType.WEWORK:
        statusText.value = '当前运行环境为企业微信'
        break
      case ClientType.FEISHU:
        statusText.value = '当前运行环境为飞书'
        break
      case ClientType.DINGTALK:
        statusText.value = '当前运行环境为钉钉'
        break;
      default:
        statusText.value = '当前运行环境为其他浏览器环境'
        handleOtherBrowser();

    }


  } catch (error: any) {
    console.error('初始化失败:', error);
    status.value = 'error';
    statusText.value = '初始化失败';
    errorMessage.value = error.message || '发生未知错误';
    canRetry.value = true;
  }
};

/**
 * 其他浏览器监听
 */
const handleOtherBrowser = () => {

}

onMounted(() => {
  init();
});

</script>

<style scoped lang="scss">

</style>