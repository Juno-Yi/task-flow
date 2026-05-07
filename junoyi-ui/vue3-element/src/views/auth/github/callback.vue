<!-- GitHub OAuth 回调页面 -->
<template>
  <div class="flex items-center justify-center h-screen">
    <div class="text-center">
      <ElIcon class="text-6xl text-primary mb-4" :class="{ 'animate-spin': loading }">
        <Loading v-if="loading" />
        <SuccessFilled v-else-if="success" />
        <CircleCloseFilled v-else />
      </ElIcon>
      <p class="text-lg">{{ message }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { useUserStore } from '@/store/modules/user'
  import { ElNotification } from 'element-plus'
  import { Loading, SuccessFilled, CircleCloseFilled } from '@element-plus/icons-vue'
  import { fetchGetUserInfo, fetchGithubCallback } from '@/api/auth'

  defineOptions({ name: 'GithubCallback' })

  const router = useRouter()
  const route = useRoute()
  const userStore = useUserStore()

  const loading = ref(true)
  const success = ref(false)
  const message = ref('正在处理 GitHub 登录...')

  onMounted(async () => {
    const code = route.query.code as string
    const state = route.query.state as string

    if (!code || !state) {
      message.value = '授权失败：缺少必要参数'
      loading.value = false
      setTimeout(() => {
        router.push('/auth/login')
      }, 2000)
      return
    }

    try {
      // 调用后端回调接口
      const result = await fetchGithubCallback(code, state)

      if (result) {
        const { accessToken, refreshToken } = result

        // 存储 token
        userStore.setToken(accessToken, refreshToken)
        userStore.setLoginStatus(true)

        // 获取用户信息
        const userInfo = await fetchGetUserInfo()
        userStore.setUserInfo(userInfo)

        success.value = true
        message.value = '登录成功，正在跳转...'

        ElNotification({
          title: '登录成功',
          message: `欢迎回来, ${userInfo.nickName}!`,
          type: 'success',
          duration: 2500
        })

        // 跳转到首页
        setTimeout(() => {
          router.push('/')
        }, 1000)
      } else {
        throw new Error('GitHub 登录失败')
      }
    } catch (error: any) {
      console.error('GitHub 登录失败:', error)
      loading.value = false
      message.value = `登录失败: ${error.message || '未知错误'}`

      ElNotification({
        title: '登录失败',
        message: error.message || 'GitHub 登录失败，请稍后重试',
        type: 'error',
        duration: 3000
      })

      setTimeout(() => {
        router.push('/auth/login')
      }, 3000)
    }
  })
</script>

<style scoped>
  .animate-spin {
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
</style>

