<!-- 登录并绑定页面 -->
<!-- 本页面主要就是负责账号密码登录和账号绑定，如果用户企业微信、飞书、钉钉没有绑定对应的账号 -->
<!-- 那么，url中携带后端传过来的bindToken，进行登录并绑定，-->
<!-- 如果，url中没有携带bindToken，只说明用户没有在企业微信、飞书、钉钉平台上打开，在其他浏览器环境中打开，只需要正常登录即可-->
<template>
  <div class="login-page">

    <div class="body-wrapper">
      <!--   标题头   -->
      <div class="title-box">
        <div class="logo">
          <img :src="logo" />
        </div>
        <div class="title">
          <h2>钧逸研发协作管理平台</h2>
          <p v-if="isBinding">请输入账号密码进行登录绑定平台账号</p>
        </div>
      </div>

      <!--   登录核心表单   -->
      <div class="form-box">
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
                placeholder="请输入账号"
                v-model.trim="formData.username"
            />
          </ElFormItem>
          <ElFormItem prop="password">
            <ElInput
                class="custom-height"
                placeholder="请输入密码"
                v-model.trim="formData.password"
                type="password"
                autocomplete="off"
                show-password
            />
          </ElFormItem>
          <!-- 验证码 -->
          <ElFormItem prop="code">
            <div class="captcha-wrapper">
              <ElInput
                  class="custom-height captcha-input"
                  placeholder="请输入验证码"
                  v-model.trim="formData.captchaCode"
              />
              <div
                  class="captcha-img"
                  @click="getCaptchaImage"
                  title="刷新验证码"
              >
                <img
                    v-if="captchaImage"
                    :src="'data:image/png;base64,' + captchaImage"
                    alt="验证码"
                />
                <div v-else class="captcha-placeholder">
                  {{ captchaLoading ? '加载中...' : '点击获取' }}
                </div>
              </div>
            </div>
          </ElFormItem>
          <div class="submit-btn-wrapper">
            <ElButton
                class="submit-btn"
                type="primary"
                @click="handleSubmit"
                :loading="loading"
                v-ripple
            >
              {{ isBinding ? '登录并绑定' : '登录' }}
            </ElButton>
          </div>
        </ElForm>
      </div>

    </div>

    <!--  底部版权  -->
    <div class="footer">
      <p>Copyright @ 2026 钧逸科技 所有</p>
      <p>未经许可，不予商用或企业内部闭源使用</p>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useRoute } from 'vue-router'
  import {fetchBindWeWorkAccount} from "@/api/oauth/wework.ts";
  import {useUserStore} from "@/store/modules/user.ts";
  import {fetchGetCaptcha, fetchGetUserInfo, fetchLogin} from "@/api";
  import logo from '@/assets/image/LOGO.png'
  import {ElMessage, type FormRules} from "element-plus";

  defineOptions({name: 'Login'})

  const route = useRoute()
  const router = useRouter()

  const userStore = useUserStore()

  const loading = ref<boolean>(false)
  const isBinding = computed(() => {
    return !!platform.value && !!bindToken.value
  })

  // 验证码加载
  const captchaLoading = ref<boolean>(false)
  const captchaImage = ref<string>()

  // 表单数据
  const formKey = ref(0)
  const formData = reactive({
    username: '',
    password: '',
    captchaCode: '',
    captchaId: '',
  })

  const rules = computed<FormRules>(() => ({
    username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
    captchaCode: [{ required: true, message: '验证码不能为空', trigger: 'blur' }]
  }))


  // 从 URL 中获取参数
  const platform = computed(() => route.query.platform as string)
  const bindToken = computed(() => route.query.bindToken as string)
  /**
   * 获取平台名称
   */
  const getPlatformName = (platformType: string): string => {
    const platformNames: Record<string, string> = {
      wework: '企业微信',
      feishu: '飞书',
      dingtalk: '钉钉',
    }
    return platformNames[platformType] || platformType
  }

  /**
   * 处理表单提交
   */
  const handleSubmit = async () => {
    if (!formData.username || !formData.password || !formData.captchaCode) {
      return
    }

    try {

      let accessToken = ''
      let refreshToken = ''

      // 判断是绑定还是普通登录
      if (isBinding.value) {
        // 账号绑定逻辑
        const result = await handleBind()
        accessToken = result.accessToken
        refreshToken = result.refreshToken
      } else {
        // 普通登录逻辑
        const result = await handleLogin()
        accessToken = result.accessToken
        refreshToken = result.refreshToken
      }

      if (!accessToken) {
        throw new Error('登录失败，未获取到 Token')
      }

      // 存储 token 和登录状态
      userStore.setToken(accessToken, refreshToken)
      userStore.setLoginStatus(true)

      // 获取用户信息
      const userInfo = await fetchGetUserInfo()
      userStore.setInfo(userInfo)

      // 显示成功提示
      const successMsg = platform.value
          ? `${getPlatformName(platform.value)}账号绑定成功！`
          : '登录成功！'

      ElMessage.success(successMsg)
      setTimeout(() => {
        const redirect = (route.query.redirect as string) || '/home'
        router.replace(redirect)
      }, 1000)


    } catch (error: any) {
      // 登录提交如果失败

      console.error('操作失败:', error)
      showToast(error.message || '操作失败，请重试')

      // 刷新验证码
      await getCaptchaImage()
      formData.captchaCode = ''
    } finally {

      loading.value = false
    }
  }

  /**
   * 处理账号绑定
   */
  const handleBind = async () => {
    if (!platform || !bindToken) {
      throw new Error('绑定信息不完整')
    }

    let accessToken = ''
    let refreshToken = ''

    // 根据平台类型调用不同的绑定接口
    switch (platform.value) {
      case 'wework': {
        const params: BindWeWorkAccountParams = {
          username: formData.username,
          password: formData.password,
          code: bindToken.value,
          captchaId: formData.captchaId,
          captchaCode: formData.captchaCode,
        }
        const res = await fetchBindWeWorkAccount(params)
        accessToken = res.accessToken || ''
        refreshToken = res.refreshToken || ''
        break
      }

      case 'feishu': {
        // const params: BindFeishuAccountParams = {
        //   username: formData.username,
        //   password: formData.password,
        //   code: code.value,
        //   captchaId: formData.captchaId,
        //   captchaCode: formData.captchaCode,
        // }
        // const res = await fetchBindFeishuAccount(params)
        // accessToken = res.accessToken || ''
        // refreshToken = res.refreshToken || ''
        break
      }

      case 'dingtalk': {
        // const params: BindDingtalkAccountParams = {
        //   username: formData.username,
        //   password: formData.password,
        //   code: code.value,
        //   captchaId: formData.captchaId,
        //   captchaCode: formData.captchaCode,
        // }
        // const res = await fetchBindDingtalkAccount(params)
        // accessToken = res.accessToken || ''
        // refreshToken = res.refreshToken || ''
        break
      }

      default:
        throw new Error(`不支持的绑定类型: ${platform}`)
    }

    return { accessToken, refreshToken }
  }

  /**
   * 处理普通登录
   */
  const handleLogin = async () => {
    const res = await fetchLogin({
      username: formData.username,
      password: formData.password,
      code: formData.captchaCode,
      captchaId: formData.captchaId,
    })
    return {
      accessToken: res.accessToken,
      refreshToken: res.refreshToken
    }
  }

  /**
   * 获取验证码
   */
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

  /**
   * 初始化
   */
  const init = async () => {
    // 检查是否已登录
    if (userStore.isLogin && userStore.accessToken) {
      setTimeout(() => {
        router.replace('/home')
      }, 1000)
      return
    }

    // 获取验证码
    await getCaptchaImage()

  }

  onMounted(() => {
    init()
  })
</script>

<style scoped>
@import 'style.scss';
</style>