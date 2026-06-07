<!-- 第三方平台Oauth登录回调页面 -->
<template>
  <div class="callback-container">
    <el-result
        icon="info"
        title="登录处理中"
        sub-title="正在验证企业微信身份..."
    />
  </div>
</template>

<script setup lang="ts">
  import { onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { useUserStore } from '@/store/modules/user'
  defineOptions({ name: 'Callback' })

  const route = useRoute()
  const router = useRouter()
  const userStore = useUserStore()

  onMounted(async () => {
    const code = route.query.code as string
    const state = sessionStorage.getItem("oauth_state")
    console.log(`调试: code ${code} state ${state}`)
  })
</script>

<style scoped>

</style>