<!-- 登录页面 -->
<template>
  <!-- 左右布局 -->
  <div class="flex w-full h-screen">
    <LoginBackground />

    <div class="relative flex-1">
      <AuthTopBar />

      <div class="auth-right-wrap">
        <div class="form">
          <h3 class="title">{{ $t('login.title') }}</h3>
          <p class="sub-title">{{ $t('login.subTitle') }}</p>

          <!-- 账号密码登录表单 -->
          <ElForm
            v-if="loginMode === 'password'"
            ref="formRef"
            :model="formData"
            :rules="rules"
            :key="formKey"
            @keyup.enter="handleSubmit"
            style="margin-top: 25px"
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

            <!-- 验证码 - 仅在启用时显示 -->
            <ElFormItem v-if="captchaEnabled" prop="code">
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

            <div class="mt-5 text-sm text-gray-600">
              <span>{{ $t('login.noAccount') }}</span>
              <RouterLink class="text-theme" :to="{ name: 'Register' }">{{
                $t('login.register')
              }}</RouterLink>
            </div>

            <!-- 第三方登录 -->
            <div class="mt-6">
              <div class="flex items-center mb-4">
                <div class="flex-1 h-px bg-gray-200"></div>
                <span class="px-3 text-xs text-gray-400">{{ $t('login.thirdParty.title') }}</span>
                <div class="flex-1 h-px bg-gray-200"></div>
              </div>
              <div class="flex justify-center gap-4">
                <ElTooltip :content="$t('login.thirdParty.wework')" placement="top">
                  <div class="third-party-btn" @click="handleThirdPartyLogin('wework')">
                    <img
                      :src="thirdPartyIcons.wework"
                      alt="企业微信"
                      class="third-party-icon"
                      @error="handleImageError"
                    />
                  </div>
                </ElTooltip>
                <ElTooltip :content="$t('login.thirdParty.feishu')" placement="top">
                  <div class="third-party-btn" @click="handleThirdPartyLogin('feishu')">
                    <img
                      :src="thirdPartyIcons.feishu"
                      alt="飞书"
                      class="third-party-icon"
                      @error="handleImageError"
                    />
                  </div>
                </ElTooltip>
                <ElTooltip :content="$t('login.thirdParty.dingtalk')" placement="top">
                  <div class="third-party-btn" @click="handleThirdPartyLogin('dingtalk')">
                    <img
                      :src="thirdPartyIcons.dingtalk"
                      alt="钉钉"
                      class="third-party-icon"
                      @error="handleImageError"
                    />
                  </div>
                </ElTooltip>
              </div>
            </div>
          </ElForm>

          <!-- 企业微信扫码登录 -->
          <div v-else-if="loginMode === 'wework'" class="wework-qrcode-container">
            <div class="qrcode-wrapper">
              <div id="wework_qrcode" class="qrcode-box"></div>
              <div v-if="qrcodeLoading" class="qrcode-loading">
                <ElIcon class="is-loading"><Loading /></ElIcon>
                <p>加载中...</p>
              </div>
            </div>
            <p class="qrcode-tip">请使用企业微信扫码登录</p>
            <ElButton text type="primary" @click="switchToPasswordLogin" class="mt-4">
              <ElIcon><ArrowLeft /></ElIcon>
              返回账号密码登录
            </ElButton>
          </div>
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
  import { nextTick } from 'vue'
  import { useUserStore } from '@/store/modules/user'
  import { useI18n } from 'vue-i18n'
  import { HttpError } from '@/utils/http/error'
  import { fetchLogin, fetchGetCaptcha, fetchGetUserInfo, fetchGetCaptchaConfig } from '@/api/auth'
  import { fetchGetSystemInfo, type SystemInfo } from '@/api/system/info'
  import { ElNotification, type FormInstance, type FormRules } from 'element-plus'
  import { Loading, ArrowLeft } from '@element-plus/icons-vue'
  import { fetchGetWeWorkLoginConfig, fetchWeWorkCallback } from '@/api/oauth/wework'

  // 引入第三方登录图标
  import weworkIcon from '@/assets/images/login/third-party/wework.png'
  import feishuIcon from '@/assets/images/login/third-party/feishu.png'
  import dingtalkIcon from '@/assets/images/login/third-party/dingtalk.png'

  defineOptions({ name: 'Login' })

  const { t, locale } = useI18n()
  const formKey = ref(0)

  // 监听语言切换，重置表单
  watch(locale, () => {
    formKey.value++
  })

  const userStore = useUserStore()

  // 第三方登录图标配置
  const thirdPartyIcons = {
    wework: weworkIcon,
    feishu: feishuIcon,
    dingtalk: dingtalkIcon
  }

  // 登录模式：password - 账号密码登录，wework - 企业微信扫码登录
  const loginMode = ref<'password' | 'wework'>('password')
  const qrcodeLoading = ref(false)
  const formRef = ref<FormInstance>()

  const router = useRouter()
  const route = useRoute()

  // 验证码相关
  const captchaEnabled = ref(false) // 验证码是否启用
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
    code: captchaEnabled.value
      ? [{ required: true, message: t('login.placeholder.captcha'), trigger: 'blur' }]
      : []
  }))

  const loading = ref(false)

  onMounted(() => {
    getCaptchaConfig()
    getSystemInfo()
    // 检查是否有企业微信回调参数
    checkWeWorkCallback()
  })

  // 获取验证码配置
  const getCaptchaConfig = async () => {
    try {
      const config = await fetchGetCaptchaConfig()
      console.log('[Login] 验证码配置:', config)
      captchaEnabled.value = config.enabled
      console.log('[Login] 验证码是否启用:', captchaEnabled.value)

      // 如果启用验证码，则获取验证码图片
      if (captchaEnabled.value) {
        await getCaptchaImage()
      }
    } catch (error) {
      console.error('获取验证码配置失败:', error)
      // 默认启用验证码（安全起见）
      captchaEnabled.value = true
      await getCaptchaImage()
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

      // 构建登录参数，仅在验证码启用时传递验证码相关参数
      const loginParams: any = {
        username,
        password
      }

      if (captchaEnabled.value) {
        loginParams.captchaId = captchaId
        loginParams.code = code
      }

      const { accessToken, refreshToken } = await fetchLogin(loginParams)

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
      // 仅在验证码启用时刷新验证码
      if (captchaEnabled.value) {
        getCaptchaImage()
        formData.code = ''
      }

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

  // 第三方登录
  const handleThirdPartyLogin = async (provider: string) => {
    if (provider === 'wework') {
      await switchToWeWorkLogin()
    } else {
      ElNotification({
        title: '提示',
        message: `${provider} 登录功能开发中...`,
        type: 'info',
        duration: 2000
      })
      console.log('第三方登录:', provider)
    }
  }

  // 切换到企业微信扫码登录
  const switchToWeWorkLogin = async () => {
    try {
      qrcodeLoading.value = true
      // 获取企业微信登录配置
      const config = await fetchGetWeWorkLoginConfig()

      if (!config || !config.corpId || !config.agentId) {
        ElNotification({
          title: '错误',
          message: '企业微信登录配置不完整，请联系管理员',
          type: 'error',
          duration: 3000
        })
        return
      }

      // 切换到扫码模式
      loginMode.value = 'wework'

      // 等待 DOM 更新
      await nextTick()

      // 初始化企业微信扫码登录
      initWeWorkQRCode(config)
    } catch (error) {
      console.error('获取企业微信配置失败:', error)
      ElNotification({
        title: '错误',
        message: '无法获取企业微信登录配置',
        type: 'error',
        duration: 3000
      })
    } finally {
      qrcodeLoading.value = false
    }
  }

  // 初始化企业微信二维码
  const initWeWorkQRCode = (config: any) => {
    try {
      const redirectUri = encodeURIComponent(config.redirectUri)

      // 动态加载企业微信 JS-SDK
      loadWeWorkScript().then(() => {
        // @ts-ignore
        if (window.WwLogin) {
          // @ts-ignore
          new window.WwLogin({
            id: 'wework_qrcode',
            appid: config.corpId,
            agentid: config.agentId,
            redirect_uri: redirectUri,
            state: config.state,
            href: '', // 可以自定义样式
            lang: 'zh'
          })

          // 监听扫码回调（通过 URL 参数）
          checkWeWorkCallback()
        } else {
          throw new Error('企业微信 SDK 加载失败')
        }
      }).catch(error => {
        console.error('加载企业微信 SDK 失败:', error)
        ElNotification({
          title: '错误',
          message: '加载企业微信 SDK 失败',
          type: 'error',
          duration: 3000
        })
      })
    } catch (error) {
      console.error('初始化企业微信二维码失败:', error)
      ElNotification({
        title: '错误',
        message: '初始化企业微信扫码失败',
        type: 'error',
        duration: 3000
      })
    }
  }

  // 动态加载企业微信 JS-SDK
  const loadWeWorkScript = (): Promise<void> => {
    return new Promise((resolve, reject) => {
      // @ts-ignore
      if (window.WwLogin) {
        resolve()
        return
      }

      const script = document.createElement('script')
      script.src = 'https://wwcdn.weixin.qq.com/node/wework/wwopen/js/wwLogin-1.2.8.js'
      script.onload = () => resolve()
      script.onerror = () => reject(new Error('Failed to load WeWork SDK'))
      document.head.appendChild(script)
    })
  }

  // 检查企业微信回调
  const checkWeWorkCallback = () => {
    const urlParams = new URLSearchParams(window.location.search)
    const code = urlParams.get('code')
    const state = urlParams.get('state')

    if (code && state) {
      handleWeWorkCallback(code)
    }
  }

  // 处理企业微信回调
  const handleWeWorkCallback = async (code: string) => {
    try {
      loading.value = true
      const response = await fetchWeWorkCallback(code)

      if (response.accessToken) {
        // 直接登录成功
        userStore.setToken(response.accessToken, response.refreshToken || '')
        userStore.setLoginStatus(true)

        // 获取用户信息
        const userInfo = await fetchGetUserInfo()
        userStore.setUserInfo(userInfo)

        await nextTick()
        showLoginSuccessNotice(userInfo.nickName)

        // 清除 URL 参数
        window.history.replaceState({}, '', window.location.pathname)

        // 跳转
        const redirect = route.query.redirect as string
        router.push(redirect || '/')
      } else if (response.needBind) {
        // 需要绑定账号
        ElNotification({
          title: '提示',
          message: '该企业微信账号未绑定，请先绑定账号',
          type: 'warning',
          duration: 3000
        })
        // TODO: 跳转到绑定页面或显示绑定表单
        // 这里可以保存 code 和 weWorkUserId，然后切换到绑定模式
      }
    } catch (error) {
      console.error('企业微信登录失败:', error)
      ElNotification({
        title: '登录失败',
        message: '企业微信登录失败，请重试',
        type: 'error',
        duration: 3000
      })
    } finally {
      loading.value = false
    }
  }

  // 切换回账号密码登录
  const switchToPasswordLogin = () => {
    loginMode.value = 'password'
    // 清除二维码容器
    const qrcodeContainer = document.getElementById('wework_qrcode')
    if (qrcodeContainer) {
      qrcodeContainer.innerHTML = ''
    }
  }

  // 图片加载错误处理
  const handleImageError = (e: Event) => {
    const img = e.target as HTMLImageElement
    // 图片加载失败时隐藏图片，避免显示破损图标
    img.style.display = 'none'
    console.warn('第三方登录图标加载失败:', img.src)
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

  .third-party-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--art-border-color);
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
    color: var(--el-text-color-regular);

    &:hover {
      border-color: var(--el-color-primary);
      color: var(--el-color-primary);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .third-party-icon {
    width: 24px;
    height: 24px;
    object-fit: contain;
  }

  // 企业微信扫码登录样式
  .wework-qrcode-container {
    margin-top: 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 400px;

    .qrcode-wrapper {
      position: relative;
      width: 300px;
      height: 300px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1px solid var(--art-border-color);
      border-radius: 8px;
      background: #fff;

      .qrcode-box {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .qrcode-loading {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background: rgba(255, 255, 255, 0.9);
        border-radius: 8px;

        .el-icon {
          font-size: 32px;
          color: var(--el-color-primary);
          margin-bottom: 12px;
        }

        p {
          font-size: 14px;
          color: var(--el-text-color-secondary);
        }
      }
    }

    .qrcode-tip {
      margin-top: 20px;
      font-size: 14px;
      color: var(--el-text-color-regular);
    }
  }
</style>
