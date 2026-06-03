<template>
  <header class="header">
    <img src="https://cdn.jsdelivr.net/gh/fonghehe/picture/vue-h5-template/logo.png" alt="" /><span> {{ $t('common.title') }}</span>
  </header>
  <div class="intro-header">
    <div>{{ $t('common.introduction') }} </div>
    <a href="https://github.com/sunniejs/vue-h5-template.git">
      <Github />
    </a>
  </div>
  <nut-cell-group :title="$t('common.home.support')" class="supportList">
    <nut-cell v-for="(item, index) in cellList" :key="index" :title="item">
      <template #icon>
        <Check />
      </template>
    </nut-cell>
  </nut-cell-group>
  <div class="btn-wrap">
    <nut-button shape="square" size="small" type="default" @click="changeLang('zh-CN')">
      {{ $t('common.language.zh') }}
    </nut-button>
    <nut-button shape="square" size="small" type="default" @click="changeLang('en-US')"> {{ $t('common.language.en') }}</nut-button>
  </div>
</template>

<script setup lang="ts">
  import { setLang } from '@/locales';
  import { Github, Check } from '@nutui/icons-vue';

  const cellList = ['vue3', 'vite', 'vue-router', 'axios', 'Pinia', 'vue-i18n', 'postcss-px-to-viewport', 'varlet / vant / nutUI', 'eruda'];

  const changeLang = (type: string) => {
    setLang(type);
  };
</script>

<style lang="scss">
  .header {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px 20px;
    font-size: 36px;

    img {
      width: 60px;
      height: 60px;
    }
  }

  .intro-header {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 20px;
    font-size: 24px;
  }

  .supportList {
    margin: 0 16px;

    .nut-cell-group__title {
      margin-top: 30px;
    }
  }

  .btn-wrap {
    margin: 20px;
  }
</style>
