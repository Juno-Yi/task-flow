<template>
  <div class="avatar-wrap">
    <van-image
      round
      width="60"
      height="60"
      src="https://img12.360buyimg.com/imagetools/jfs/t1/143702/31/16654/116794/5fc6f541Edebf8a57/4138097748889987.png"
    />
    <div class="member-detail">
      <p class="nickname">
        <span v-if="getUserInfo">用户名:{{ getUserInfo }}</span>
        <van-button v-else size="small" type="default" @click="goLogin">去登录</van-button>
      </p>
      <p class="info"> 个人其他信息，后续补充.... </p>
    </div>
  </div>
  <van-grid :column-num="5">
    <van-grid-item v-for="item in 10" :key="item" icon="star-o" text="文字" />
  </van-grid>
</template>

<script setup lang="ts">
  import { useUserStore } from '@/store/modules/user';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const userStore = useUserStore();
  const getUserInfo = computed(() => {
    const { name = '' } = userStore.getUserInfo || {};
    return name;
  });
  const goLogin = () => {
    router.push('/login');
  };
</script>

<style lang="scss">
  .avatar-wrap {
    display: flex;
    align-items: center;
    margin: 10px 40px;

    .member-detail {
      margin-left: 20px;

      .nickname {
        font-size: 32px;
        font-weight: bold;

        .van-button {
          margin-left: 10px;
        }
      }

      .info {
        margin-top: 10px;
        font-size: 24px;
      }
    }
  }
</style>
