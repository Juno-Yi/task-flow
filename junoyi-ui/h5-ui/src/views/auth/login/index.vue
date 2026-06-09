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
        </div>
      </div>

      <!--   登录核心表单   -->
      <div class="form-box">
        <nut-form ref="ruleForm" :model-value="formData">
          <nut-form-item label="用户名" required prop="name" :rules="[{ required: true, message: '请输入用户名' }]">
            <nut-input v-model="formData.username" placeholder="请输入账号" type="text" />
          </nut-form-item>
        </nut-form>
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
  import {type BindWeWorkAccountParams, fetchBindWeWorkAccount} from "@/api/oauth/wework.ts";
  import {useUserStore} from "@/store/modules/user.ts";
  import {fetchGetCaptcha} from "@/api";
  import logo from '@/assets/image/LOGO.png'

  defineOptions({name: 'Bind'})

  const route = useRoute()
  const router = useRouter()

  const userStore = useUserStore()

  // 验证码加载
  const captchaLoading = ref<boolean>(false)
  const captchaImage = ref<string>()

  // 表单数据
  const formData = reactive({
    username: '',
    password: '',
    captchaCode: '',
    captchaId: '',
  })

  // 从 URL 中获取参数
  const platform = computed(() => route.query.type as string)
  const bindToken = computed(() => route.query.bindToken as string)

  // 页面标题和副标题
  const pageTitle = computed(() => {
    if (platform.value) {
      return `${getPlatformName(platform.value)}账号绑定`
    }
    return '欢迎登录'
  })

  const pageSubtitle = computed(() => {
    if (platform.value) {
      return '请登录系统账号完成绑定'
    }
    return '智能任务流程管理平台'
  })

  const submitButtonText = computed(() => {
    return platform.value ? '登录并绑定' : '登录'
  })

  const bindTipText = computed(() => {
    if (platform.value) {
      return `该${getPlatformName(platform.value)}账号未绑定系统用户`
    }
    return ''
  })


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
      if (platform.value && bindToken.value) {
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
      // const userInfo = await fetchGetUserInfo()
      // userStore.setInfo(userInfo)

      // 显示成功提示
      const successMsg = platform.value
        ? `${getPlatformName(platform.value)}账号绑定成功！`
        : '登录成功！'

      showToast({
        message: successMsg,
        icon: 'success',
      })

      // 跳转到首页或原始目标页面
      setTimeout(() => {
        const redirect = (route.query.redirect as string) || '/home'
        router.replace(redirect)
      }, 1000)
    } catch (error: any) {
      console.error('操作失败:', error)
      showToast(error.message || '操作失败，请重试')

      // 刷新验证码
      // getCaptcha()
      formData.captchaCode = ''
    } finally {
      // loading.value = false
    }
  }

  /**
   * 处理账号绑定
   */
  const handleBind = async () => {
    if (!platform.value || !bindToken.value) {
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
        throw new Error(`不支持的绑定类型: ${platform.value}`)
    }

    return { accessToken, refreshToken }
  }

  /**
   * 处理普通登录
   */
  const handleLogin = async () => {
    // const res = await fetchLogin({
    //   username: formData.username,
    //   password: formData.password,
    //   code: formData.captchaCode,
    //   captchaId: formData.captchaId,
    // })


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
    // await getCaptchaImage()

  }

  onMounted(() => {
    init()
  })
</script>

<style scoped>
@import './style.css';
</style>