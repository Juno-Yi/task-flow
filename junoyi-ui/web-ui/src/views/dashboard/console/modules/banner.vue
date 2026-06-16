<template>
  <ArtBasicBanner
    class="justify-center !h-53 mb-5 max-sm:!pt-8 max-sm:!h-48 max-sm:mb-4"
    :title="`欢迎回来 ${userInfo.nickName}`"
    boxStyle="!bg-theme/10"
    titleColor="var(--art-gray-900)"
    :decoration="false"
    :meteorConfig="{ enabled: true, count: 10 }"
    :buttonConfig="{ show: false, text: '' }"
    :imageConfig="{ src: bannerCover, width: '18rem', bottom: '-7.5rem' }"
    @click="handleBannerClick"
  >
    <div class="mt-6 flex">
      <div class="mr-8 border-r border-g-400 pr-8 dark:border-g-300/60">
        <p class="text-3xl">
          <ArtCountTo class="number box-title" :target="pendingTaskCount" :duration="1200" />
          <ArtSvgIcon icon="ri:task-line" class="relative -top-1 ml-1 text-xl text-warning" />
        </p>
        <p class="mt-1 text-sm text-g-700">待完成任务数量</p>
      </div>
      <div class="mr-8 border-r border-g-400 pr-8 dark:border-g-300/60">
        <p class="text-3xl">
          <ArtCountTo class="number box-title" :target="completedTaskCount" :duration="1200" />
          <ArtSvgIcon icon="ri:checkbox-circle-line" class="relative -top-1 ml-1 text-xl text-success" />
        </p>
        <p class="mt-1 text-sm text-g-700">已完成任务数量</p>
      </div>
      <div class="mr-8">
        <p class="text-3xl">
          <ArtCountTo class="number box-title" :target="monthTaskCount" :duration="1200" />
          <ArtSvgIcon icon="ri:calendar-check-line" class="relative -top-1 ml-1 text-xl text-primary" />
        </p>
        <p class="mt-1 text-sm text-g-700">本月总任务量</p>
      </div>
    </div>
  </ArtBasicBanner>
</template>

<script setup lang="ts">
import bannerCover from '@imgs/login/lf_icon2.webp'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const userInfo = computed(() => userStore.getUserInfo)
const taskList = ref<Api.Task.TaskItemVO[]>([])

const monthTaskCount = computed(() => taskList.value.length)
const pendingTaskCount = computed(() => taskList.value.filter(item => item.status !== 4).length)
const completedTaskCount = computed(() => taskList.value.filter(item => item.status === 4).length)

const loadTaskStats = async () => {
  // 加载数据
  taskList.value = []
}

const handleBannerClick = (): void => {
  // TODO: 添加横幅点击处理逻辑
}

onMounted(loadTaskStats)
</script>
