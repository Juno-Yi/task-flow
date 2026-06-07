<!-- 第三方平台Oauth登录回调页面 -->
<template>
  <div class="callback-container">
    <el-result
        icon="info"
        title="登录处理中"
        :sub-title="statusText"
    />
  </div>
</template>

<script setup lang="ts">
  import { onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import {ElMessage, ElNotification} from 'element-plus'
  import { useUserStore } from '@/store/modules/user'
  import {fetchWeWorkCallback} from "@/api/oauth/wework";
  import {fetchGetUserInfo} from "@/api/auth";
  defineOptions({ name: 'Callback' })

  const route = useRoute()
  const router = useRouter()
  const userStore = useUserStore()

  const statusText = ref('正在验证授权信息...')

  onMounted(async () => {
    const code = route.query.code as string
    const state = sessionStorage.getItem("oauth_state")

    if (!code || !state) {
      ElMessage.error('授权信息不存在')
      await router.replace('/auth/login')
      return
    }
    // 检查state类型，不同类型去调用不同平台回调
    try {
      //WEWORK 企业微信
      if (state.startsWith('WEWORK:')) {
        await handlerWeWorkCallBack(code)
        return
      }
      //FEISHU 飞书

      //DINGTALK 钉钉

    } catch (error) {
      console.error(error)
      ElMessage.error('登录失败')
      await router.replace('/auth/login')
    }

    //调试: code mvjTBxtPxz7J35EImj8FlUuzfgVmPS4lxkDe6OZFsRw state WEWORK:c77637bbb0854213a6d701ad0a1d8425
    console.log(`调试: code ${code} state ${state}`)
  })

  /**
   * 处理企业微信登录回调
   * @param code code授权码
   */
  const handlerWeWorkCallBack = async (code: string) => {
    const data = await fetchWeWorkCallback(code)

    if (data == null)
      throw Error("企业微信登录回调异常")

    // 是否需要绑定账号
    if (data.needBind){
      // 携带参数跳转绑定账号页面
      statusText.value = '该企业微信账号并没有绑定系统用户，请先绑定'
      ElNotification({
        title: '需要绑定账号',
        message: '该企业微信账号未绑定系统用户，请先绑定',
        type: 'warning',
        duration: 3000
      })
      // 跳转到绑定页面，携带bindToken（从response.code获取，而不是URL的OAuth code）
      setTimeout(() => {
        router.push({
          name: 'Bind',
          query: { code: data.code }
        })
      }, 1500)
      return
    }

    // 如果已经绑定过账号获取token设置登录状态
    const { accessToken, refreshToken } = data
    if (!accessToken || !refreshToken) {
      throw new Error('企业微信登录返回的 token 不完整')
    }

    // 存储 token
    userStore.setToken(accessToken, refreshToken)
    userStore.setLoginStatus(true)

    // 获取用户信息
    const userInfo = await fetchGetUserInfo()
    userStore.setUserInfo(userInfo)

    // 登录成功提示
    statusText.value = '验证成功，即将跳转...'
    ElNotification({
      title: '登录成功',
      message: `欢迎回来, ${userInfo.nickName}!`,
      type: 'success',
      duration: 2500
    })

    // 跳转到首页
    setTimeout(() => {
      router.push('/')
    }, 500)
  }
</script>

<style scoped>

</style>