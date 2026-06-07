<!-- OAuth 登录回调处理页面 -->
<template>
  <div class="callback-container">
    <div class="callback-content">
      <van-loading size="48" vertical>{{ statusText }}</van-loading>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast, showNotify } from 'vant';
import { useUserStore } from '@/store/modules/user';
import { fetchWeWorkCallback } from '@/api/oauth/wework';
import { fetchFeishuCallback } from '@/api/oauth/feishu';
import { fetchDingtalkCallback } from '@/api/oauth/dingtalk';
import { fetchGetUserInfo } from '@/api/auth';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const statusText = ref('正在验证授权信息...');

onMounted(async () => {
  const code = route.query.code as string;
  const state = localStorage.getItem('oauth_state');

  if (!code) {
    showToast('授权码不存在');
    await router.replace('/login');
    return;
  }

  if (!state) {
    showToast('授权状态不存在');
    await router.replace('/login');
    return;
  }

  try {
    // 根据 state 类型调用不同平台的回调
    if (state.startsWith('WEWORK:')) {
      await handleWeWorkCallback(code);
    } else if (state.startsWith('FEISHU:')) {
      await handleFeishuCallback(code);
    } else if (state.startsWith('DINGTALK:')) {
      await handleDingtalkCallback(code);
    } else {
      throw new Error('不支持的授权平台');
    }
  } catch (error: any) {
    console.error('OAuth 回调处理失败:', error);
    showToast(error.message || '登录失败');
    await router.replace('/login');
  } finally {
    // 清除 state
    localStorage.removeItem('oauth_state');
  }
});

/**
 * 处理企业微信回调
 */
const handleWeWorkCallback = async (code: string) => {
  const data = await fetchWeWorkCallback(code);

  if (!data) {
    throw new Error('企业微信登录回调异常');
  }

  // 需要绑定账号
  if (data.needBind) {
    statusText.value = '该企业微信账号未绑定系统用户，请先绑定';
    showNotify({
      type: 'warning',
      message: '该企业微信账号未绑定系统用户，请先绑定',
      duration: 3000,
    });
    
    setTimeout(() => {
      router.push({
        path: '/auth/bind',
        query: { code: data.code, type: 'wework' },
      });
    }, 1500);
    return;
  }

  // 已绑定，直接登录
  await loginWithTokens(data.accessToken!, data.refreshToken!);
};

/**
 * 处理飞书回调
 */
const handleFeishuCallback = async (code: string) => {
  const data = await fetchFeishuCallback(code);

  if (!data) {
    throw new Error('飞书登录回调异常');
  }

  // 需要绑定账号
  if (data.needBind) {
    statusText.value = '该飞书账号未绑定系统用户，请先绑定';
    showNotify({
      type: 'warning',
      message: '该飞书账号未绑定系统用户，请先绑定',
      duration: 3000,
    });
    
    setTimeout(() => {
      router.push({
        path: '/auth/bind',
        query: { code: data.code, type: 'feishu' },
      });
    }, 1500);
    return;
  }

  // 已绑定，直接登录
  await loginWithTokens(data.accessToken!, data.refreshToken!);
};

/**
 * 处理钉钉回调
 */
const handleDingtalkCallback = async (code: string) => {
  const data = await fetchDingtalkCallback(code);

  if (!data) {
    throw new Error('钉钉登录回调异常');
  }

  // 需要绑定账号
  if (data.needBind) {
    statusText.value = '该钉钉账号未绑定系统用户，请先绑定';
    showNotify({
      type: 'warning',
      message: '该钉钉账号未绑定系统用户，请先绑定',
      duration: 3000,
    });
    
    setTimeout(() => {
      router.push({
        path: '/auth/bind',
        query: { code: data.code, type: 'dingtalk' },
      });
    }, 1500);
    return;
  }

  // 已绑定，直接登录
  await loginWithTokens(data.accessToken!, data.refreshToken!);
};

/**
 * 使用 token 登录
 */
const loginWithTokens = async (accessToken: string, refreshToken: string) => {
  if (!accessToken || !refreshToken) {
    throw new Error('Token 不完整');
  }

  statusText.value = '登录成功，正在跳转...';

  // 存储 token
  userStore.setToken(accessToken, refreshToken);
  userStore.setLoginStatus(true);

  // 获取用户信息
  const userInfo = await fetchGetUserInfo();
  userStore.setInfo(userInfo);

  // 显示成功提示
  showToast({
    message: `欢迎回来，${userInfo.nickName}!`,
    icon: 'success',
  });

  // 跳转到首页或原始目标页面
  const redirect = (route.query.redirect as string) || '/';
  setTimeout(() => {
    router.replace(redirect);
  }, 1000);
};
</script>

<style scoped lang="scss">
.callback-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f7f8fa;

  .callback-content {
    text-align: center;
    padding: 40px;
  }
}
</style>

