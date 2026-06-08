<!-- 账号绑定页面 -->
<template>
  <div class="bind-container">
    <div class="bind-header">
      <h2>账号绑定</h2>
      <p class="tip">未绑定过任何账号，请先登录并绑定！</p>
    </div>

    <van-form @submit="handleSubmit" class="bind-form">
      <van-cell-group inset>
        <van-field
          v-model="formData.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
        />
        <van-field
          v-model="formData.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
        <van-field
          v-model="formData.captchaCode"
          name="captchaCode"
          label="验证码"
          placeholder="请输入验证码"
          :rules="[{ required: true, message: '请输入验证码' }]"
        >
          <template #button>
            <van-image
              v-if="captchaImage"
              :src="'data:image/png;base64,' + captchaImage"
              width="100"
              height="40"
              fit="cover"
              @click="getCaptcha"
            />
            <van-button
              v-else
              size="small"
              type="primary"
              @click="getCaptcha"
              :loading="captchaLoading"
            >
              获取验证码
            </van-button>
          </template>
        </van-field>
      </van-cell-group>

      <div style="margin: 16px;">
        <van-button
          round
          block
          type="primary"
          native-type="submit"
          :loading="loading"
        >
          登录并绑定
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast, showNotify } from 'vant';
import { useUserStore } from '@/store/modules/user';
import { fetchGetCaptcha, fetchGetUserInfo } from '@/api/auth';
import { fetchBindWeWorkAccount, type BindWeWorkAccountParams } from '@/api/oauth/wework';
import { fetchBindFeishuAccount, type BindFeishuAccountParams } from '@/api/oauth/feishu';
import { fetchBindDingtalkAccount, type BindDingtalkAccountParams } from '@/api/oauth/dingtalk';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const captchaLoading = ref(false);
const captchaImage = ref('');

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  captchaCode: '',
  captchaId: '',
});

// 绑定信息
const code = ref('');
const type = ref('');

onMounted(() => {
  code.value = route.query.code as string;
  type.value = route.query.type as string;

  if (!code.value || !type.value) {
    showToast('绑定信息不完整');
    router.replace('/login');
    return;
  }

  getCaptcha();
});

/**
 * 获取验证码
 */
const getCaptcha = async () => {
  try {
    captchaLoading.value = true;
    const res = await fetchGetCaptcha();
    formData.captchaId = res.captchaId;
    captchaImage.value = res.image;
  } catch (error: any) {
    showToast(error.message || '获取验证码失败');
  } finally {
    captchaLoading.value = false;
  }
};

/**
 * 获取平台名称
 */
const getPlatformName = (platformType: string): string => {
  const platformNames: Record<string, string> = {
    wework: '企业微信',
    feishu: '飞书',
    dingtalk: '钉钉',
  };
  return platformNames[platformType] || platformType;
};

/**
 * 提交绑定
 */
const handleSubmit = async () => {
  if (!formData.username || !formData.password || !formData.captchaCode) {
    return;
  }

  try {
    loading.value = true;

    let accessToken = '';
    let refreshToken = '';

    // 根据类型调用不同的绑定接口
    switch (type.value) {
      case 'wework': {
        const params: BindWeWorkAccountParams = {
          username: formData.username,
          password: formData.password,
          code: code.value,
          captchaId: formData.captchaId,
          captchaCode: formData.captchaCode,
        };
        const res = await fetchBindWeWorkAccount(params);
        accessToken = res.accessToken || '';
        refreshToken = res.refreshToken || '';
        break;
      }

      case 'feishu': {
        const params: BindFeishuAccountParams = {
          username: formData.username,
          password: formData.password,
          code: code.value,
          captchaId: formData.captchaId,
          captchaCode: formData.captchaCode,
        };
        const res = await fetchBindFeishuAccount(params);
        accessToken = res.accessToken || '';
        refreshToken = res.refreshToken || '';
        break;
      }

      case 'dingtalk': {
        const params: BindDingtalkAccountParams = {
          username: formData.username,
          password: formData.password,
          code: code.value,
          captchaId: formData.captchaId,
          captchaCode: formData.captchaCode,
        };
        const res = await fetchBindDingtalkAccount(params);
        accessToken = res.accessToken || '';
        refreshToken = res.refreshToken || '';
        break;
      }

      default:
        throw new Error(`不支持的绑定类型: ${type.value}`);
    }

    if (!accessToken) {
      throw new Error('绑定失败，未获取到 Token');
    }

    // 存储 token 和登录状态
    userStore.setToken(accessToken, refreshToken);
    userStore.setLoginStatus(true);

    // 获取用户信息
    const userInfo = await fetchGetUserInfo();
    userStore.setInfo(userInfo);

    // 显示成功提示
    showNotify({
      type: 'success',
      message: `${getPlatformName(type.value)}账号绑定成功！`,
    });

    // 跳转到首页
    setTimeout(() => {
      const redirect = (route.query.redirect as string) || '/';
      router.replace(redirect);
    }, 1000);
  } catch (error: any) {
    console.error('绑定失败:', error);
    showToast(error.message || '绑定失败');
    
    // 刷新验证码
    getCaptcha();
    formData.captchaCode = '';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped lang="scss">
.bind-container {
  min-height: 100vh;
  background: #f7f8fa;
  padding: 20px;

  .bind-header {
    text-align: center;
    margin-bottom: 30px;
    padding-top: 40px;

    h2 {
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 10px;
    }

    .tip {
      font-size: 14px;
      color: #969799;
    }
  }

  .bind-form {
    :deep(.van-cell-group) {
      margin-bottom: 20px;
    }

    :deep(.van-image) {
      border-radius: 4px;
      cursor: pointer;
    }
  }
}
</style>

