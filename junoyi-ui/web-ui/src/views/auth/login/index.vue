<!-- 登录页面 -->
<template>
  <!-- 居中布局：全屏背景 + 毛玻璃卡片 -->
  <div v-if="authLayout === 'center'" class="login-center-layout">
    <!-- 全屏背景装饰 -->
    <LoginBackgroundCenter />

    <AuthTopBar />

    <!-- 毛玻璃表单卡片 -->
    <div class="center-form-wrapper">
      <div class="center-form-card">
        <div class="form">
          <h3 class="title">{{ $t('login.title') }}</h3>

          <!-- Tab 切换 -->
          <ElTabs v-model="activeTab" class="login-tabs" @tab-change="handleTabChange">
            <!-- 企业微信登录 Tab -->
            <ElTabPane label="企业微信" name="wework">
              <div class="wework-login-container">
                <div id="wework-qrcode-center" class="wework-qrcode"></div>
                <p class="wework-tip">请使用企业微信扫码登录</p>
              </div>
            </ElTabPane>

            <!-- 账号密码登录 Tab -->
            <ElTabPane label="账号登录" name="account">
              <ElForm
                  ref="formRef"
                  :model="formData"
                  :rules="rules"
                  :key="formKey"
                  @keyup.enter="handleSubmit"
                  style="margin-top: 10px"
              >
                <ElFormItem prop="username">
                  <ElInput
                      class="custom-height"
                      :placeholder="$t('login.placeholder.username')"
                      v-model.trim="formData.username"
                  />
                </ElFormItem>
                <ElFormItem prop="password">
                  <ElInput
                      class="custom-height"
                      :placeholder="$t('login.placeholder.password')"
                      v-model.trim="formData.password"
                      type="password"
                      autocomplete="off"
                      show-password
                  />
                </ElFormItem>

                <!-- 验证码 -->
                <ElFormItem prop="code">
                  <div class="flex w-full gap-3">
                    <ElInput
                        class="custom-height flex-1"
                        :placeholder="$t('login.placeholder.captcha')"
                        v-model.trim="formData.code"
                    />
                    <div
                        class="captcha-img cursor-pointer rounded-lg overflow-hidden flex-shrink-0"
                        @click="getCaptchaImage"
                        :title="$t('login.refreshCaptcha')"
                    >
                      <img
                          v-if="captchaImage"
                          :src="'data:image/png;base64,' + captchaImage"
                          alt="captcha"
                          class="h-10 w-28 object-cover"
                      />
                      <div
                          v-else
                          class="h-10 w-28 bg-gray-100 flex items-center justify-center text-gray-400 text-sm"
                      >
                        {{ captchaLoading ? '加载中...' : '点击获取' }}
                      </div>
                    </div>
                  </div>
                </ElFormItem>

                <div class="flex-cb mt-2 text-sm">
                  <ElCheckbox v-model="formData.rememberPassword">{{
                      $t('login.rememberPwd')
                    }}</ElCheckbox>
                  <RouterLink class="text-theme" :to="{ name: 'ForgetPassword' }">{{
                      $t('login.forgetPwd')
                    }}</RouterLink>
                </div>

                <div style="margin-top: 30px">
                  <ElButton
                      class="w-full custom-height"
                      type="primary"
                      @click="handleSubmit"
                      :loading="loading"
                      v-ripple
                  >
                    {{ $t('login.btnText') }}
                  </ElButton>
                </div>

                <div class="mt-5 text-sm text-gray-600 text-center">
                  <span>{{ $t('login.noAccount') }}</span>
                  <RouterLink class="text-theme" :to="{ name: 'Register' }">{{
                      $t('login.register')
                    }}</RouterLink>
                </div>
              </ElForm>
            </ElTabPane>
          </ElTabs>
        </div>
      </div>
      <!-- 版权信息 -->
      <div v-if="systemInfo" class="center-footer">
        <p class="copyright">
          Copyright © {{ systemInfo.copyrightYear }} {{ systemInfo.copyright }}
        </p>
        <p v-if="systemInfo.registration" class="registration">
          {{ systemInfo.registration }}
        </p>
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
          <h3 class="title">{{ $t('login.title') }}</h3>

          <!-- Tab 切换 -->
          <ElTabs v-model="activeTab" class="login-tabs" @tab-change="handleTabChange">
            <!-- 账号密码登录 Tab -->
            <ElTabPane label="账号登录" name="account">
              <ElForm
                  ref="formRef"
                  :model="formData"
                  :rules="rules"
                  :key="formKey"
                  @keyup.enter="handleSubmit"
                  style="margin-top: 10px"
              >
                <ElFormItem prop="username">
                  <ElInput
                      class="custom-height"
                      :placeholder="$t('login.placeholder.username')"
                      v-model.trim="formData.username"
                  />
                </ElFormItem>
                <ElFormItem prop="password">
                  <ElInput
                      class="custom-height"
                      :placeholder="$t('login.placeholder.password')"
                      v-model.trim="formData.password"
                      type="password"
                      autocomplete="off"
                      show-password
                  />
                </ElFormItem>
                <!-- 验证码 -->
                <ElFormItem prop="code">
                  <div class="flex w-full gap-3">
                    <ElInput
                        class="custom-height flex-1"
                        :placeholder="$t('login.placeholder.captcha')"
                        v-model.trim="formData.code"
                    />
                    <div
                        class="captcha-img cursor-pointer rounded-lg overflow-hidden flex-shrink-0"
                        @click="getCaptchaImage"
                        :title="$t('login.refreshCaptcha')"
                    >
                      <img
                          v-if="captchaImage"
                          :src="'data:image/png;base64,' + captchaImage"
                          alt="captcha"
                          class="h-10 w-28 object-cover"
                      />
                      <div
                          v-else
                          class="h-10 w-28 bg-gray-100 flex items-center justify-center text-gray-400 text-sm"
                      >
                        {{ captchaLoading ? '加载中...' : '点击获取' }}
                      </div>
                    </div>
                  </div>
                </ElFormItem>
                <div class="flex-cb mt-2 text-sm">
                  <ElCheckbox v-model="formData.rememberPassword">{{
                      $t('login.rememberPwd')
                    }}</ElCheckbox>
                  <RouterLink class="text-theme" :to="{ name: 'ForgetPassword' }">{{
                      $t('login.forgetPwd')
                    }}</RouterLink>
                </div>
                <div style="margin-top: 30px">
                  <ElButton
                      class="w-full custom-height"
                      type="primary"
                      @click="handleSubmit"
                      :loading="loading"
                      v-ripple
                  >
                    {{ $t('login.btnText') }}
                  </ElButton>
                </div>
                <div class="mt-5 text-sm text-gray-600 text-center">
                  <span>{{ $t('login.noAccount') }}</span>
                  <RouterLink class="text-theme" :to="{ name: 'Register' }">{{
                      $t('login.register')
                    }}</RouterLink>
                </div>
              </ElForm>
            </ElTabPane>

            <!-- 企业微信登录 Tab -->
            <ElTabPane label="企业微信登录" name="wework">
              <div class="wework-login-container">
                <div id="wework-qrcode-side" class="wework-qrcode"></div>
              </div>
            </ElTabPane>

            <!--    飞书登录 Tab   -->
            <ElTabPane label="飞书登录" name="feishu">

            </ElTabPane>

            <!--    钉钉登录 Tab   -->
            <ElTabPane label="钉钉登录" name="dingtalk">

            </ElTabPane>


          </ElTabs>
        </div>
      </div>

      <!-- 版权信息 -->
      <div v-if="systemInfo" class="login-footer">
        <p class="copyright">
          Copyright © {{ systemInfo.copyrightYear }} {{ systemInfo.copyright }}
        </p>
        <p v-if="systemInfo.registration" class="registration">
          {{ systemInfo.registration }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount } from 'vue'
import AppConfig from '@/config'
import { useUserStore } from '@/store/modules/user'
import { useSettingStore } from '@/store/modules/setting'
import { useI18n } from 'vue-i18n'
import { HttpError } from '@/utils/http/error'
import { fetchLogin, fetchGetCaptcha, fetchGetUserInfo } from '@/api/auth'
import { fetchGetWeWorkLoginConfig } from '@/api/oauth/wework'
import { fetchGetSystemInfo, type SystemInfo } from '@/api/system/info'
import { ElNotification, type FormInstance, type FormRules } from 'element-plus'
import LoginBackgroundCenter from '@/components/core/views/login/LoginBackgroundCenter.vue'
import AuthTopBar from '@/components/core/views/login/AuthTopBar.vue'
// @ts-ignore
import * as ww from '@wecom/jssdk'

defineOptions({ name: 'Login' })

const { t, locale } = useI18n()
const formKey = ref(0)
const activeTab = ref('account') // 默认显示账号密码登录

// 监听语言切换，重置表单
watch(locale, () => {
  formKey.value++
})

const userStore = useUserStore()
const settingStore = useSettingStore()
const { authLayout, systemInfo: storeSystemInfo } = storeToRefs(settingStore)

const systemName = computed(() => storeSystemInfo.value?.name || AppConfig.systemInfo.name)
const formRef = ref<FormInstance>()

const router = useRouter()
const route = useRoute()

// 验证码相关
const captchaImage = ref('')
const captchaLoading = ref(false)

// 系统信息
const systemInfo = ref<SystemInfo | null>(null)

// 是否自动填充登录信息
const autoFillLogin = import.meta.env.VITE_AUTO_FILL_LOGIN === 'true'

const formData = reactive({
  captchaId: '',
  username: autoFillLogin ? 'super_admin' : '',
  password: autoFillLogin ? 'admin123' : '',
  code: '',
  rememberPassword: true
})

const rules = computed<FormRules>(() => ({
  username: [{ required: true, message: t('login.placeholder.username'), trigger: 'blur' }],
  password: [{ required: true, message: t('login.placeholder.password'), trigger: 'blur' }],
  code: [{ required: true, message: t('login.placeholder.captcha'), trigger: 'blur' }]
}))

const loading = ref(false)
const weWorkLoading = ref(false)
const isInitializingWeWork = ref(false)
let wwLoginPanel: any = null

onMounted(() => {
  getCaptchaImage()
  getSystemInfo()
  // 默认初始化企业微信登录
  nextTick(() => {
    initWeWorkLogin()
  })
})

onBeforeUnmount(() => {
  // 清理企业微信登录组件
  if (wwLoginPanel) {
    try {
      wwLoginPanel = null
    } catch (e) {
      console.error('清理企业微信登录组件失败:', e)
    }
  }
})

// Tab 切换处理
const handleTabChange = (tabName: string | number) => {
  console.log('Tab 切换:', tabName)
  if (tabName === 'wework') {
    // 延迟初始化，确保 DOM 已更新
    setTimeout(() => {
      initWeWorkLogin()
    }, 100)
  } else if (tabName === 'account') {
    // 切换到账号登录时，清理企业微信组件
    if (wwLoginPanel) {
      wwLoginPanel = null
    }
    getCaptchaImage()
  }
}

// 初始化企业微信登录
const initWeWorkLogin = async () => {
  // 防止重复初始化
  if (isInitializingWeWork.value) {
    console.log('企业微信登录正在初始化中，跳过')
    return
  }

  try {
    isInitializingWeWork.value = true

    // 获取企业微信配置
    const config = await fetchGetWeWorkLoginConfig()

    if (!config || !config.corpId) {
      console.error('获取企业微信配置失败')
      return
    }

    // 暂存 state，在回调时候识别
    sessionStorage.setItem(
        'oauth_state',
        config.state
    )

    // 根据布局选择不同的容器ID
    const containerId = authLayout.value === 'center' ? '#wework-qrcode-center' : '#wework-qrcode-side'

    // 清理之前的实例
    if (wwLoginPanel) {
      wwLoginPanel = null
    }

    // 确保容器存在
    await nextTick()
    const container = document.querySelector(containerId)
    if (!container) {
      console.error('企业微信登录容器不存在:', containerId)
      return
    }

    // 清空容器内容
    container.innerHTML = ''

    // 稍微延迟，确保 DOM 完全准备好
    await new Promise(resolve => setTimeout(resolve, 50))

    // 创建企业微信登录面板
    wwLoginPanel = ww.createWWLoginPanel({
      el: containerId,
      params: {
        login_type: 'CorpApp' as any,
        appid: config.corpId,
        agentid: config.agentId,
        redirect_uri: config.redirectUri,
        state: config.state,
        redirect_type: 'callback' as any,
      },
      onLoginSuccess: ({ code }: { code: string }) => {
        console.log('企业微信登录成功，code:', code)
        // 跳转到回调页面处理登录
        router.push({
          name: 'Callback',
          query: { code }
        })
      },
      onLoginFail: (err: any) => {
        console.error('企业微信登录失败:', err)
        ElNotification({
          title: '登录失败',
          message: '企业微信登录失败，请重试',
          type: 'error',
          duration: 3000
        })
      }
    })

    console.log('企业微信登录组件初始化完成')
  } catch (error) {
    console.error('初始化企业微信登录失败:', error)
  } finally {
    isInitializingWeWork.value = false
  }
}

// 获取系统信息
const getSystemInfo = async () => {
  try {
    systemInfo.value = await fetchGetSystemInfo()
  } catch (error) {
    console.error('获取系统信息失败:', error)
  }
}

// 获取验证码
const getCaptchaImage = async () => {
  try {
    captchaLoading.value = true
    const res = await fetchGetCaptcha()
    formData.captchaId = res.captchaId
    captchaImage.value = res.image
  } catch (error) {
    console.error('获取验证码失败:', error)
  } finally {
    captchaLoading.value = false
  }
}

// 登录
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    // 表单验证
    const valid = await formRef.value.validate()
    if (!valid) return

    loading.value = true

    // 登录请求
    const { username, password, code, captchaId } = formData

    const { accessToken, refreshToken } = await fetchLogin({
      captchaId,
      username,
      password,
      code
    })

    // 验证token
    if (!accessToken) {
      throw new Error('Login failed - no token received')
    }

    // 存储 token 和登录状态
    userStore.setToken(accessToken, refreshToken)
    userStore.setLoginStatus(true)

    // 获取用户信息
    const userInfo = await fetchGetUserInfo()
    userStore.setUserInfo(userInfo)

    // 等待 Vue 响应式系统更新完成
    await nextTick()

    // 登录成功处理
    showLoginSuccessNotice(userInfo.nickName)

    // 获取 redirect 参数，如果存在则跳转到指定页面，否则跳转到首页
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (error) {
    // 刷新验证码
    getCaptchaImage()
    formData.code = ''

    if (error instanceof HttpError) {
      console.log(error.code)
    } else {
      console.error('[Login] Unexpected error:', error)
    }
  } finally {
    loading.value = false
  }
}

// 登录成功提示
const showLoginSuccessNotice = (nickName: string) => {
  setTimeout(() => {
    ElNotification({
      title: t('login.success.title'),
      type: 'success',
      duration: 2500,
      zIndex: 10000,
      message: `${t('login.success.message')}, ${nickName}!`
    })
  }, 1000)
}
</script>

<style scoped>
@import './style.css';
</style>

<style lang="scss" scoped>
.captcha-img {
  border: 1px solid var(--art-border-color);
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }
}

.login-tabs {
  margin-top: 20px;

  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__item) {
    font-size: 15px;
    font-weight: 500;
    position: relative;
    z-index: 10;
  }

  :deep(.el-tabs__header) {
    position: relative;
    z-index: 10;
  }

  :deep(.el-tabs__content) {
    position: relative;
    z-index: 1;
  }
}

.wework-login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  min-height: 450px;
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.wework-qrcode {
  width: 400px;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  position: relative;
  z-index: 1;
  overflow: hidden;

  :deep(iframe) {
    width: 400px !important;
    height: 400px !important;
    border: none;
    overflow: hidden !important;
  }

  :deep(.impowerBox) {
    width: 400px !important;
    height: 400px !important;
    overflow: hidden !important;
  }

  :deep(.wrp_code) {
    width: 400px !important;
    height: 400px !important;
    overflow: hidden !important;
  }
}

.wework-tip {
  margin-top: 16px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  text-align: center;
}

.wework-icon-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;

  &:hover {
    border-color: #0082EF;
    background: #f0f9ff;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 130, 239, 0.15);
  }

  &:active {
    transform: translateY(0);
  }

  .wework-icon {
    width: 24px;
    height: 24px;
  }
}

.login-footer {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 12px;
  color: var(--el-text-color-secondary);

  .copyright {
    margin-bottom: 4px;
  }

  .registration {
    color: var(--el-text-color-placeholder);
  }
}
</style>
