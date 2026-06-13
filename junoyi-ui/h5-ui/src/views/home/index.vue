<template>
  <div class="home-page">
    <!--  顶部欢迎banner  -->

    <!--  数据概览  -->

    <!--  风险提醒  -->
  </div>
</template>

<script setup lang="ts">
  import { setLang } from '@/locales';
  const changeLang = (type: string) => {
    setLang(type);
  };
</script>

<style lang="scss">
@use './style.scss' as *;
</style>
