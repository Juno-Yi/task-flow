<template>
<h1>首页</h1>
</template>

<script setup lang="ts">
  import { setLang } from '@/locales';
  const changeLang = (type: string) => {
    setLang(type);
  };
</script>

<style lang="scss">

</style>
