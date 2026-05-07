<!-- GitHub OAuth 回调处理页面 -->
<template>
  <div class="github-callback-container">
    <div class="callback-content">
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-box">
        <div class="loading-spinner"></div>
        <p class="loading-text">{{ loadingText }}</p>
      </div>

      <!-- 成功状态 -->
      <div v-else-if="isSuccess" class="success-box">
        <div class="success-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 6L9 17l-5-5" />
          </svg>
        </div>
        <h2>登录成功！</h2>
        <p>正在跳转到首页...</p>
        <div v-if="userInfo" class="user-info">
          <img :src="userInfo.avatar" alt="avatar" class="avatar" />
          <div class="user-details">
            <p class="nickname">{{ userInfo.nickName }}</p>
            <p class="username">@{{ userInfo.userName }}</p>
          </div>
        </div>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="errorMessage" class="error-box">
        <div class="error-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="15" y1="9" x2="9" y2="15" />
            <line x1="9" y1="9" x2="15" y2="15" />
          </svg>
        </div>
        <h2>登录失败</h2>
        <p class="error-message">{{ errorMessage }}</p>
        <ElButton type="primary" @click="backToLogin">返回登录</ElButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { useUserStore } from '@/store/modules/user'
  import { fetchGetUserInfo } from '@/api/auth'
  import { ElNotification } from 'element-plus'

  defineOptions({ name: 'GitHubCallback' })

  const router = useRouter()
  const userStore = useUserStore()

  const isLoading = ref(true)
  const isSuccess = ref(false)
  const loadingText = ref('正在处理 GitHub 登录...')
  const errorMessage = ref('')
  const userInfo = ref<any>(null)

  // API 基础地址
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:7588'

  /**
   * 处理 GitHub OAuth 回调
   */
  const handleCallback = async () => {
    const urlParams = new URLSearchParams(window.location.search)
    const code = urlParams.get('code')
    const state = urlParams.get('state')
    const error = urlParams.get('error')

    // 检查是否有错误
    if (error) {
      isLoading.value = false
      errorMessage.value = `GitHub 授权失败: ${error}`
      return
    }

    // 检查是否有授权码
    if (!code) {
      isLoading.value = false
      errorMessage.value = '缺少授权码，请重新登录'
      return
    }

    try {
      loadingText.value = '正在获取用户信息...'

      // 调用后端回调接口
      const response = await fetch(
        `${API_BASE_URL}/auth/github/callback?code=${code}${state ? `&state=${state}` : ''}`
      )
      const result = await response.json()

      if (result.code === 200) {
        const data = result.data

        // 保存 Token
        userStore.setToken(data.accessToken, data.refreshToken)
        userStore.setLoginStatus(true)

        // 获取完整用户信息
        loadingText.value = '正在加载用户信息...'
        const fullUserInfo = await fetchGetUserInfo()
        userStore.setUserInfo(fullUserInfo)

        // 保存用户信息用于显示
        userInfo.value = {
          avatar: data.avatar,
          nickName: data.nickName,
          userName: data.userName
        }

        // 显示成功状态
        isSuccess.value = true
        isLoading.value = false

        // 显示欢迎通知
        ElNotification({
          title: '登录成功',
          message: `欢迎回来, ${data.nickName}!${data.isNewUser ? ' (新用户)' : ''}`,
          type: 'success',
          duration: 3000
        })

        // 清除 URL 中的参数
        window.history.replaceState({}, document.title, window.location.pathname)

        // 3秒后跳转到首页
        setTimeout(() => {
          router.push('/')
        }, 3000)
      } else {
        throw new Error(result.msg || 'GitHub 登录失败')
      }
    } catch (error) {
      isLoading.value = false
      errorMessage.value = error instanceof Error ? error.message : 'GitHub 登录失败，请稍后重试'
      console.error('GitHub OAuth 回调处理失败:', error)
    }
  }

  /**
   * 返回登录页面
   */
  const backToLogin = () => {
    router.push('/login')
  }

  // 组件挂载时处理回调
  onMounted(() => {
    handleCallback()
  })
</script>

<style scoped>
  .github-callback-container {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
  }

  .callback-content {
    background: white;
    border-radius: 16px;
    padding: 60px 40px;
    max-width: 500px;
    width: 100%;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    text-align: center;
  }

  .loading-box,
  .success-box,
  .error-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }

  .loading-spinner {
    width: 60px;
    height: 60px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }

  .loading-text {
    font-size: 16px;
    color: #666;
    margin: 0;
  }

  .success-icon,
  .error-icon {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .success-icon {
    background: #e8f5e9;
    color: #4caf50;
  }

  .error-icon {
    background: #ffebee;
    color: #f44336;
  }

  .success-icon svg,
  .error-icon svg {
    width: 40px;
    height: 40px;
  }

  h2 {
    font-size: 24px;
    font-weight: 600;
    margin: 0;
    color: #333;
  }

  p {
    font-size: 14px;
    color: #666;
    margin: 0;
  }

  .error-message {
    color: #f44336;
    font-size: 14px;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 20px;
    background: #f5f5f5;
    border-radius: 12px;
    margin-top: 10px;
  }

  .avatar {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    border: 3px solid #667eea;
  }

  .user-details {
    text-align: left;
  }

  .nickname {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0 0 5px 0;
  }

  .username {
    font-size: 14px;
    color: #999;
    margin: 0;
  }
</style>

