<!-- 工作台页面 -->
<template>
  <div>
    <ElRow :gutter="20">
      <ElCol :sm="24" :md="24" :lg="16">
        <Banner />
      </ElCol>
      <ElCol :sm="12" :md="12" :lg="4">
      </ElCol>
      <ElCol :sm="12" :md="12" :lg="4">
      </ElCol>
    </ElRow>

    <!-- 应用工作台 -->
    <ElRow :gutter="20">
      <ElCol :span="24">
        <QuickEntry />
      </ElCol>
    </ElRow>

    <DemoNotice />
  </div>
</template>

<script setup lang="ts">
  import DemoNotice from "@views/dashboard/console/modules/demo-notice.vue";
  import Banner from "@views/dashboard/console/modules/banner.vue";
  import QuickEntry from "@views/dashboard/console/modules/quick-entry.vue";

  defineOptions({ name: 'Console' })
</script>
