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


    <!--  快速入口  -->
    <ElRow :gutter="20" class="quick-entry">
      <ElCol :span="24">
        <ElCard shadow="never" class="quick-entry-card">
          <template #header>
            <div class="flex items-center gap-2">
              <ArtSvgIcon icon="ri:rocket-line" class="text-base text-primary" />
              <span class="text-sm font-semibold">快速入口</span>
            </div>
          </template>
          <div class="grid grid-cols-3 gap-3 sm:grid-cols-4 md:grid-cols-6 lg:grid-cols-8">
            <div
                v-for="item in quickList"
                :key="item.path"
                class="quick-item flex cursor-pointer flex-col items-center justify-center rounded-lg p-3 transition-all hover:bg-gray-50 hover:shadow-md dark:hover:bg-gray-800"
                @click="go(item.path)"
            >
              <div class="mb-1.5 flex h-10 w-10 items-center justify-center rounded-full" :style="{ backgroundColor: item.bgColor }">
                <ArtSvgIcon :icon="item.icon" class="text-xl text-white" />
              </div>
              <span class="text-xs font-medium text-gray-700 dark:text-gray-300">{{ item.name }}</span>
            </div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>

    <DemoNotice />
  </div>
</template>

<script setup lang="ts">
  import DemoNotice from "@views/dashboard/console/modules/demo-notice.vue";
  import Banner from "@views/dashboard/console/modules/banner.vue";

  defineOptions({ name: 'Console' })

  const router = useRouter()

  interface QuickItem {
    name: string
    path: string
    icon: string
    bgColor: string
    count?: number
  }

  const quickList = ref<QuickItem[]>([
    { name: '我的任务', path: '/dashboard/my-task', icon: 'ri:task-line', bgColor: '#409EFF'},
    { name: '项目列表', path: '/project/list', icon: 'ri:folder-line', bgColor: '#67C23A'},
    { name: '任务管理', path: '/task/list', icon: 'ri:list-check', bgColor: '#E6A23C'},
    { name: '审批日志', path: '/approval/log', icon: 'ri:file-list-3-line', bgColor: '#F56C6C'},
    { name: '变更日志', path: '/change/log', icon: 'ri:git-commit-line', bgColor: '#909399'},
    { name: '用户中心', path: '/system/user-center', icon: 'ri:user-settings-line', bgColor: '#00A870'}
  ])

  const go = (path: string) => {
    router.push(path)
  }

</script>

