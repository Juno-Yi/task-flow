<!-- 登录并绑定页面 -->
<!-- 本页面主要就是负责账号密码登录和账号绑定，如果用户企业微信、飞书、钉钉没有绑定对应的账号 -->
<!-- 那么，url中携带后端传过来的bindToken，进行登录并绑定，-->
<!-- 如果，url中没有携带bindToken，只说明用户没有在企业微信、飞书、钉钉平台上打开，在其他浏览器环境中打开，只需要正常登录即可-->
<template>
  <h1>登录与绑定页面</h1>
  <p v-if="platform">{{platform}}</p>
  <p v-else>其他浏览器环境</p>

  <p>{{bindToken}}</p>
</template>

<script setup lang="ts">
  import { useRoute } from 'vue-router'
  import { fetchBindWeWorkAccount} from "@/api/oauth/wework.ts";

  defineOptions({name: 'Bind'})

  const route = useRoute()

  // 从 URL 中获取 platform
  const platform = route.query.platform as string
  // 绑定token
  const bindToken = route.query.bindToken as string

  /**
   * 初始化页面
   */
  const init = async () => {
    if (!platform)
      return
  }

  onMounted(() => {
    init()
  })

</script>

<style scoped>

</style>