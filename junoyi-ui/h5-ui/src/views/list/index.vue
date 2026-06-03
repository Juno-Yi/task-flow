<template>
  <nut-card
    v-for="(item, index) in list"
    :key="index"
    :img-url="item.imgUrl"
    :title="item.title"
    :price="item.price"
    :vip-price="item.vipPrice"
    :shop-name="item.shopName"
    :shopDesc="item.shopDesc"
    :delivery="item.delivery"
    @click="(_e: any) => toDetails(index)"
  />
</template>

<script setup lang="ts">
  import { listData } from './data';

  const router = useRouter();

  const list = ref(listData);

  const toDetails = (index: number) => {
    router.push({ path: '/details', query: { id: index } });
  };
</script>

<style lang="scss" scoped>
  .nut-card {
    padding: 15px 0;
    border-bottom: 1px solid #e5e5e5;
  }
</style>
