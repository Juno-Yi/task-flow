<!-- 登录、注册、忘记密码右侧背景（右左布局用，镜像版本） -->
<template>
  <div class="login-right-view">
    <div class="logo">
      <ArtLogo class="icon" size="46" />
      <h1 class="title">{{ systemName }}</h1>
    </div>

    <div class="right-img">
      <ThemeSvg :src="loginIcon" size="100%" />
    </div>

    <div class="text-wrap">
      <h1> {{ $t('login.leftView.title') }} </h1>
      <p> {{ $t('login.leftView.subTitle') }} </p>
    </div>

    <!-- 几何装饰元素（镜像） -->
    <div class="geometric-decorations">
      <!-- 基础几何形状 -->
      <div class="geo-element circle-outline animate-fade-in-up" style="animation-delay: 0s"></div>
      <div class="geo-element square-rotated animate-fade-in-right" style="animation-delay: 0s"></div>
      <div class="geo-element circle-small animate-fade-in-up" style="animation-delay: 0.3s"></div>
      <div class="geo-element square-bottom-left animate-fade-in-left" style="animation-delay: 0s"></div>

      <!-- 背景泡泡 -->
      <div class="geo-element bg-bubble animate-scale-in" style="animation-delay: 0.5"></div>

      <!-- 太阳/月亮 -->
      <div class="geo-element circle-top-left animate-fade-in-down" style="animation-delay: 0.5"></div>

      <!-- 装饰点 -->
      <div class="geo-element dot dot-top-right animate-bounce-in" style="animation-delay: 0s"></div>
      <div class="geo-element dot dot-top-left animate-bounce-in" style="animation-delay: 0s"></div>
      <div class="geo-element dot dot-center-left animate-bounce-in" style="animation-delay: 0s"></div>

      <!-- 叠加方块组 -->
      <div class="squares-group">
        <i class="geo-element square square-blue animate-fade-in-right-rotated-blue" style="animation-delay: 0.2s"></i>
        <i class="geo-element square square-pink animate-fade-in-right-rotated-pink" style="animation-delay: 0.4s"></i>
        <i class="geo-element square square-purple animate-fade-in-right-no-rotation" style="animation-delay: 0.6s"></i>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import AppConfig from '@/config'
  import loginIcon from '@imgs/svg/login_icon.svg'
  import { useSettingStore } from '@/store/modules/setting'

  defineOptions({ name: 'LoginRightView' })

  const settingStore = useSettingStore()
  const { systemInfo } = storeToRefs(settingStore)
  
  // 优先使用接口返回的系统名称，如果没有则使用配置文件的
  const systemName = computed(() => systemInfo.value?.name || AppConfig.systemInfo.name)
</script>

<style lang="scss" scoped>
  // 颜色变量定义
  $primary-light-7: var(--el-color-primary-light-7);
  $primary-light-8: var(--el-color-primary-light-8);
  $primary-light-9: var(--el-color-primary-light-9);
  $primary-base: var(--el-color-primary);
  $main-bg: var(--default-box-color);

  // 混合颜色函数
  $bg-mix-light-9: color-mix(in srgb, $primary-light-9 100%, $main-bg);
  $bg-mix-light-8: color-mix(in srgb, $primary-light-8 80%, $main-bg);
  $bg-mix-light-7: color-mix(in srgb, $primary-light-7 80%, $main-bg);

  .login-right-view {
    position: relative;
    box-sizing: border-box;
    width: 65vw;
    height: 100%;
    padding: 15px;
    overflow: hidden;
    background-color: $bg-mix-light-9;

    .logo {
      position: absolute;
      top: 15px;
      right: 15px;
      z-index: 100;
      display: flex;
      align-items: center;

      .title {
        margin-left: 10px;
        font-size: 20px;
        font-weight: 400;
      }
    }

    .right-img {
      position: absolute;
      inset: 0 0 10.5%;
      z-index: 10;
      width: 40%;
      margin: auto;
      animation: slideInRight 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
    }

    .text-wrap {
      position: absolute;
      bottom: 80px;
      width: 100%;
      text-align: center;
      animation: slideInRight 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;

      h1 {
        font-size: 24px;
        font-weight: 400;
        color: var(--art-gray-900) !important;
      }

      p {
        margin-top: 10px;
        font-size: 14px;
        color: var(--art-gray-600) !important;
      }
    }

    .geometric-decorations {
      .geo-element {
        position: absolute;
        opacity: 0;
        animation-fill-mode: forwards;
        animation-duration: 0.8s;
        animation-timing-function: cubic-bezier(0.25, 0.46, 0.45, 0.94);
      }

      // 动画定义
      @keyframes fadeInUp {
        from { opacity: 0; transform: translateY(30px); }
        to { opacity: 1; transform: translateY(0); }
      }

      @keyframes fadeInDown {
        from { opacity: 0; transform: translateY(-30px); }
        to { opacity: 1; transform: translateY(0); }
      }

      @keyframes fadeInLeft {
        from { opacity: 0; transform: translateX(-30px); }
        to { opacity: 1; transform: translateX(0); }
      }

      @keyframes fadeInLeftRotated {
        from { opacity: 0; transform: translateX(-30px) rotate(45deg); }
        to { opacity: 1; transform: translateX(0) rotate(45deg); }
      }

      @keyframes fadeInRight {
        from { opacity: 0; transform: translateX(30px); }
        to { opacity: 1; transform: translateX(0); }
      }

      @keyframes fadeInRightRotated {
        from { opacity: 0; transform: translateX(30px) rotate(-25deg); }
        to { opacity: 1; transform: translateX(0) rotate(-25deg); }
      }

      @keyframes fadeInRightRotatedBlue {
        from { opacity: 0; transform: translateX(30px) rotate(10deg); }
        to { opacity: 1; transform: translateX(0) rotate(10deg); }
      }

      @keyframes fadeInRightRotatedPink {
        from { opacity: 0; transform: translateX(30px) rotate(-10deg); }
        to { opacity: 1; transform: translateX(0) rotate(-10deg); }
      }

      @keyframes fadeInRightNoRotation {
        from { opacity: 0; transform: translateX(30px); }
        to { opacity: 1; transform: translateX(0); }
      }

      @keyframes scaleIn {
        from { opacity: 0; transform: scale(0.8); }
        to { opacity: 1; transform: scale(1); }
      }

      @keyframes bounceIn {
        0% { opacity: 0; transform: scale(0.3); }
        50% { opacity: 1; transform: scale(1.05); }
        70% { transform: scale(0.9); }
        100% { opacity: 1; transform: scale(1); }
      }

      @keyframes slideInRight {
        from { opacity: 0; transform: translateX(30px); }
        to { opacity: 1; transform: translateX(0); }
      }

      @keyframes lineGrow {
        from { opacity: 0; }
        to { opacity: 1; }
      }

      // 动画类
      .animate-fade-in-up { animation-name: fadeInUp; }
      .animate-fade-in-down { animation-name: fadeInDown; }
      .animate-fade-in-left { animation-name: fadeInLeft; }
      .animate-fade-in-right { animation-name: fadeInRightRotated; }
      .animate-scale-in { animation-name: scaleIn; animation-duration: 1.2s; }
      .animate-bounce-in { animation-name: bounceIn; animation-duration: 0.6s; }
      .animate-fade-in-right-rotated-blue { animation-name: fadeInRightRotatedBlue; }
      .animate-fade-in-right-rotated-pink { animation-name: fadeInRightRotatedPink; }
      .animate-fade-in-right-no-rotation { animation-name: fadeInRightNoRotation; }

      // 基础几何形状（镜像位置）
      .circle-outline {
        top: 10%;
        right: 25%;
        width: 42px;
        height: 42px;
        border: 2px solid $primary-light-8;
        border-radius: 50%;
      }

      .square-rotated {
        top: 50%;
        right: 16%;
        width: 60px;
        height: 60px;
        background-color: $bg-mix-light-8;
      }

      .circle-small {
        bottom: 26%;
        right: 30%;
        width: 18px;
        height: 18px;
        background-color: $primary-light-8;
        border-radius: 50%;
      }

      // 太阳/月亮效果（左上角）
      .circle-top-left {
        top: 3%;
        left: 3%;
        z-index: 100;
        width: 50px;
        height: 50px;
        cursor: pointer;
        background: $bg-mix-light-7;
        border-radius: 50%;
        transition: all 0.3s;

        &::after {
          position: absolute;
          top: 50%;
          left: 50%;
          width: 100%;
          height: 100%;
          content: '';
          background: linear-gradient(to right, #fcbb04, #fffc00);
          border-radius: 50%;
          opacity: 0;
          transition: all 0.5s;
          transform: translate(-50%, -50%);
        }

        &:hover {
          box-shadow: 0 0 36px #fffc00;

          &::after {
            opacity: 1;
          }
        }
      }

      .square-bottom-left {
        left: 10%;
        bottom: 10%;
        width: 50px;
        height: 50px;
        background-color: $primary-light-8;

        &.animate-fade-in-left {
          animation-name: fadeInLeftRotated;
        }
      }

      // 背景泡泡（左上角）
      .bg-bubble {
        top: -120px;
        left: -120px;
        width: 360px;
        height: 360px;
        background-color: $bg-mix-light-8;
        border-radius: 50%;
      }

      // 装饰点（镜像位置）
      .dot {
        width: 14px;
        height: 14px;
        background-color: $primary-light-7;
        border-radius: 50%;

        &.dot-top-right {
          top: 140px;
          right: 100px;
        }

        &.dot-top-left {
          top: 140px;
          left: 120px;
        }

        &.dot-center-left {
          top: 46%;
          left: 22%;
          background-color: $primary-light-8;
        }
      }

      // 叠加方块组（右下角）
      .squares-group {
        position: absolute;
        bottom: 18px;
        right: 20px;
        width: 140px;
        height: 140px;
        pointer-events: none;

        .square {
          position: absolute;
          display: block;
          border-radius: 8px;
          box-shadow: 0 8px 24px rgb(64 87 167 / 12%);

          &.square-blue {
            top: 12px;
            right: 30px;
            z-index: 2;
            width: 50px;
            height: 50px;
            background-color: rgb(from $primary-base r g b / 30%);
          }

          &.square-pink {
            top: 30px;
            right: 48px;
            z-index: 1;
            width: 70px;
            height: 70px;
            background-color: rgb(from $primary-base r g b / 15%);
          }

          &.square-purple {
            top: 66px;
            right: 86px;
            z-index: 3;
            width: 32px;
            height: 32px;
            background-color: rgb(from $primary-base r g b / 45%);
          }
        }

        // 装饰线条
        &::after {
          position: absolute;
          top: 86px;
          right: 72px;
          width: 80px;
          height: 1px;
          content: '';
          background: linear-gradient(270deg, var(--el-color-primary-light-6), transparent);
          opacity: 0;
          transform: rotate(-50deg);
          animation: lineGrow 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
          animation-delay: 1.2s;
        }
      }
    }

    @media only screen and (width <= 1600px) {
      width: 60vw;

      .text-wrap {
        bottom: 40px;
      }
    }

    @media only screen and (width <= 1180px) {
      width: auto;
      height: auto;
      padding: 0;
      background: transparent;

      .right-img,
      .text-wrap,
      .geometric-decorations {
        display: none;
      }

      .logo {
        display: none;
      }
    }
  }

  // 暗色主题
  .dark .login-right-view {
    background-color: color-mix(in srgb, $primary-light-9 60%, #070707);

    @media only screen and (width <= 1180px) {
      background: transparent;
    }

    .geometric-decorations {
      // 月亮效果
      .circle-top-left {
        background-color: $bg-mix-light-8;
        box-shadow: 0 0 25px #333 inset;
        transition: all 0.3s ease-in-out 0.1s;
        rotate: 48deg;

        &::before {
          position: absolute;
          top: 0;
          right: 15px;
          width: 50px;
          height: 50px;
          content: '';
          background-color: $bg-mix-light-9;
          border-radius: 50%;
          transition: all 0.3s ease-in-out;
        }

        &:hover {
          background-color: transparent;
          box-shadow: 0 40px 25px #ddd inset;

          &::before {
            right: 18px;
          }

          &::after {
            opacity: 0;
          }
        }
      }

      .bg-bubble {
        background-color: $bg-mix-light-9;
      }

      .square-rotated {
        background-color: $bg-mix-light-9;
      }

      .circle-small,
      .dot {
        background-color: $primary-light-8;
      }

      .square-bottom-left {
        background-color: $primary-light-9;
      }

      .dot.dot-top-left {
        background-color: $primary-light-8;
      }
    }

    .squares-group {
      .square {
        box-shadow: none;

        &.square-blue {
          background-color: rgb(from $primary-base r g b / 18%);
        }

        &.square-pink {
          background-color: rgb(from $primary-base r g b / 10%);
        }

        &.square-purple {
          background-color: rgb(from $primary-base r g b / 20%);
        }
      }

      &::after {
        background: linear-gradient(270deg, $primary-light-8, transparent);
      }
    }
  }
</style>
