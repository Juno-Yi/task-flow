<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

/**
 * 检测并处理 OAuth 自动登录
 * 当从企业微信、飞书、钉钉等平台打开时，会自动携带 code 参数
 */
onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');
  const state = urlParams.get('state');

  // 如果 URL 中有 code 和 state 参数，且不在回调页面，则自动跳转到回调页面处理
  if (code && state && route.path !== '/auth/callback') {
    console.log('检测到 OAuth code，自动跳转到回调页面处理');

    // 保存 state 到 localStorage（用于回调页面验证）
    localStorage.setItem('oauth_state', state);

    // 跳转到回调页面
    router.replace({
      path: '/auth/callback',
      query: { code, state },
    });
  }
});
</script>

<style>
  #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
  }
</style>
