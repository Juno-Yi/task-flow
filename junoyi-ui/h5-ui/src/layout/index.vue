<template>
  <div class="main-page">
<!--    <van-nav-bar :title="$t($route.meta.title as string)" :left-arrow="!tabbarVisible" @click-left="goBack" />-->
    <div class="main-box" :class="{ tabbar: tabbarVisible, border: showBorder }">
      <RouterView v-slot="{ Component }" v-if="$route.meta.keepAlive">
        <keep-alive>
          <component :is="Component" :key="$route.path" />
        </keep-alive>
      </RouterView>
      <RouterView v-if="!$route.meta.keepAlive" :key="$route.path" />
    </div>
    <van-tabbar
      v-model="activeTab"
      v-show="tabbarVisible"
      @change="tabSwitch"
      safe-area-inset-bottom
      active-color="#1989fa"
      inactive-color="#364636"
    >
      <van-tabbar-item v-for="item in tabItem" :key="item.key" :icon="item.icon">
        {{ $t(`common.tabbar.${item.key}`) }}
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script lang="ts" setup name="BasicLayoutPage">
  const tabItem = [
    { key: 'home', icon: 'home-o' },
    { key: 'my-task', icon: 'completed' },
    { key: 'my-project', icon: 'apps-o' },
    { key: 'me', icon: 'user-o' },
  ];

  const router = useRouter();

  const activeTab = ref(0);

  const tabbarVisible = ref(true);

  const showBorder = ref(true);

  const route = useRoute();

  watch(
    () => route.path,
    (path) => {
      // 获取一级路由路径（处理多级路由的情况）
      const pathSegments = path.split('/').filter(Boolean);
      const currentKey = pathSegments[0] || '';

      // 查找当前路由在 tabItem 中的索引
      const tabIndex = tabItem.findIndex((item) => item.key === currentKey);
      const isTabRoute = tabIndex !== -1;

      // 只有当路由在 tabItem 中时才更新 activeTab
      if (isTabRoute) {
        activeTab.value = tabIndex;
      }

      tabbarVisible.value = isTabRoute;
      showBorder.value = isTabRoute;
    },
    { immediate: true },
  );

  const tabSwitch = (index: number) => {
    const tab = tabItem[index];
    if (tab && index >= 0 && index < tabItem.length) {
      router.push(`/${tab.key}`);
    }
  };

  const goBack = () => {
    router.go(-1);
  };
</script>

<style scoped lang="scss">
  .van-nav-bar {
    margin-bottom: 0;
  }

  .main-page {
    display: flex;
    flex-direction: column;
    width: 100dvw;
    height: 100dvh;

    .main-box {
      flex: auto;
      min-height: 0;
      overflow: hidden auto;
    }
  }

  .border {
    padding-right: 30px;
    padding-left: 30px;
  }
</style>
