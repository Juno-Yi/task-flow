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
import {fetchGetWeWorkAuthUrl, fetchWeWorkCallback} from "@/api/oauth/wework.ts";
import {fetchGetUserInfo} from "@/api/auth.ts";
import {showToast, showNotify} from 'vant';

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
        await handleWeWork()
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
const handleWeWork = async () => {
  try {
    // 通过企业微信Oauth按照流程获取授权code
    const urlParams = new URLSearchParams(window.location.search)
    const code = urlParams.get('code')

    if (code) {
      // 有 code，说明是从企业微信授权页面回调回来的
      statusText.value = '正在登录...'

      // 调用后端登录接口
      const res = await fetchWeWorkCallback(code)

      // 判断是否需要绑定账号
      if (res.needBind) {
        statusText.value = '该企业微信账号未绑定系统用户，请先绑定'

        // 跳转到绑定页面
        setTimeout(() => {
          router.replace({
            path: '/auth/login',
            query: { bindToken: res.code, platform: 'wework' },
          })
        }, 1500)
        return
      }

      // 已绑定，直接登录
      if (res.accessToken && res.refreshToken) {
        statusText.value = '登录成功，正在跳转...'

        // 存储 token
        userStore.setToken(res.accessToken, res.refreshToken)
        userStore.setLoginStatus(true)

        // 获取用户信息
        const userInfo = await fetchGetUserInfo()
        userStore.setInfo(userInfo)

        // 显示成功提示
        showToast({
          message: `欢迎回来，${userInfo.nickName}!`,
          icon: 'success',
        })

        // 跳转到首页
        setTimeout(() => {
          router.replace('/home')
        }, 1000)
      } else {
        throw new Error('登录响应数据不完整')
      }
      return
    }

    // 没有 code，需要跳转到企业微信授权页面
    statusText.value = '正在获取授权...'
    const res = await fetchGetWeWorkAuthUrl()

    if (!res || !res.authUrl) {
      throw new Error('获取授权地址失败')
    }

    // 跳转到企业微信授权页面
    window.location.href = res.authUrl

  } catch (error: any) {
    console.error('企业微信登录失败:', error)
    statusText.value = '登录失败'
    showToast({
      message: error.message || '企业微信登录失败',
      icon: 'fail',
    })

    // 3秒后跳转到登录页
    setTimeout(() => {
      router.replace('/auth/login')
    }, 3000)
  }
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