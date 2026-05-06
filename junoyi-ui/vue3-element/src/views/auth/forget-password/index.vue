<!-- 忘记密码页面 -->
<template>
  <!-- 居中布局 -->
  <div v-if="authLayout === 'center'" class="login-center-layout">
    <LoginBackgroundCenter />
    <AuthTopBar />
    
    <div class="center-form-wrapper">
      <div class="center-form-card">
        <div class="form">
          <h3 class="title">{{ $t('forgetPassword.title') }}</h3>
          <p class="sub-title">{{ $t('forgetPassword.subTitle') }}</p>
          <div class="mt-5">
            <span class="input-label" v-if="showInputLabel">账号</span>
            <ElInput
              class="custom-height"
              :placeholder="$t('forgetPassword.placeholder')"
              v-model.trim="username"
            />
          </div>

          <div style="margin-top: 15px">
            <ElButton
              class="w-full custom-height"
              type="primary"
              @click="handleSubmit"
              :loading="loading"
              v-ripple
            >
              {{ $t('forgetPassword.submitBtnText') }}
            </ElButton>
          </div>

          <div style="margin-top: 15px">
            <ElButton class="w-full custom-height" plain @click="toLogin">
              {{ $t('forgetPassword.backBtnText') }}
            </ElButton>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 左右/右左布局 -->
  <div v-else class="flex w-full h-screen" :class="{ 'flex-row-reverse': authLayout === 'right-left' }">
    <LoginBackground />

    <div class="relative flex-1">
      <AuthTopBar />

      <div class="auth-right-wrap" :class="{ 'animate-left': authLayout === 'right-left' }">
        <div class="form">
          <h3 class="title">{{ $t('forgetPassword.title') }}</h3>
          <p class="sub-title">{{ $t('forgetPassword.subTitle') }}</p>
          <div class="mt-5">
            <span class="input-label" v-if="showInputLabel">账号</span>
            <ElInput
              class="custom-height"
              :placeholder="$t('forgetPassword.placeholder')"
              v-model.trim="username"
            />
          </div>

          <div style="margin-top: 15px">
            <ElButton
              class="w-full custom-height"
              type="primary"
              @click="handleSubmit"
              :loading="loading"
              v-ripple
            >
              {{ $t('forgetPassword.submitBtnText') }}
            </ElButton>
          </div>

          <div style="margin-top: 15px">
            <ElButton class="w-full custom-height" plain @click="toLogin">
              {{ $t('forgetPassword.backBtnText') }}
            </ElButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useSettingStore } from '@/store/modules/setting'

  defineOptions({ name: 'ForgetPassword' })

  const router = useRouter()
  const settingStore = useSettingStore()
  const { authLayout } = storeToRefs(settingStore)

  const showInputLabel = ref(false)
  const username = ref('')
  const loading = ref(false)

  const handleSubmit = async () => {
    // TODO: 实现忘记密码逻辑
  }

  const toLogin = () => {
    router.push({ name: 'Login' })
  }
</script>

<style scoped>
  @import '../login/style.css';
</style>
