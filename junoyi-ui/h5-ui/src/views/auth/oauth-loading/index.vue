<!-- OAuth 自动登录加载页面 -->
<template>
  <div class="oauth-loading-container">
    <van-loading size="64" vertical>
      {{ statusText }}
    </van-loading>
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
        // TODO: 获取企业微信code并登录
        handleWeWork()
        break
      case ClientType.FEISHU:
        statusText.value = '当前运行环境为飞书'
        // TODO: 获取飞书code并登录
        break
      case ClientType.DINGTALK:
        statusText.value = '当前运行环境为钉钉'
        // TODO: 获取钉钉code并登录
        break;
      default:
        statusText.value = '当前运行环境为其他浏览器环境'
        // TODO: 处理其他浏览器环境
        handleOtherBrowser()
        break
    }

  } catch (error: any) {
    console.error('初始化失败:', error);
    statusText.value = '初始化失败';
  }
};

/**
 * 处理企业微信登录逻辑
 */
const handleWeWork = () => {
  // 通过企业微信SDK按照流程获取授权code
  // 如果绑定过该用户后端返回token对

  // 如果没有绑定过，将bindToken存储到sessionStorage中临时存储，然后传递platform类型跳转到登录并绑定页面
  sessionStorage.setItem("wework_bind_token", "test1234")
  router.replace({
    path: '/auth/login',
    query: {
      platform: 'wework'
    }
  })
}

/**
 * 处理其他浏览器客户端登录逻辑
 */
const handleOtherBrowser = () => {
  // 如果不是企微、飞书、钉钉，那么就没有自动获取code然后一键授权登录
  // 默认跳转到登录绑定页面，跳转时候不提供绑定token
  router.replace({
    path: '/auth/login',
  })
}

onMounted(() => {
  init();
});

</script>

<style scoped lang="scss">
.oauth-loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;

  :deep(.van-loading__text) {
    font-size: 25px;
    color: #9a9797;
    margin-top: 100px;
    font-weight: 500;
  }
}
</style>