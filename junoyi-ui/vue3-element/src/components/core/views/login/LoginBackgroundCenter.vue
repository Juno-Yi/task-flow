<!-- 登录背景 - 居中布局（全屏装饰背景，隐藏文字和图片） -->
<template>
  <div class="login-center-bg">
    <!-- 几何装饰元素 -->
    <div class="geometric-decorations">
      <!-- 基础几何形状 -->
      <div class="geo-element circle-outline animate-fade-in-up" style="animation-delay: 0s"></div>
      <div class="geo-element square-rotated animate-fade-in-left" style="animation-delay: 0s"></div>
      <div class="geo-element circle-small animate-fade-in-up" style="animation-delay: 0.3s"></div>
      <div class="geo-element square-bottom-right animate-fade-in-right" style="animation-delay: 0s"></div>

      <!-- 背景泡泡 -->
      <div class="geo-element bg-bubble animate-scale-in" style="animation-delay: 0.5"></div>
      <div class="geo-element bg-bubble-left animate-scale-in" style="animation-delay: 0.3"></div>

      <!-- 装饰点 -->
      <div class="geo-element dot dot-1 animate-bounce-in" style="animation-delay: 0s"></div>
      <div class="geo-element dot dot-2 animate-bounce-in" style="animation-delay: 0.1s"></div>
      <div class="geo-element dot dot-3 animate-bounce-in" style="animation-delay: 0.2s"></div>
      <div class="geo-element dot dot-4 animate-bounce-in" style="animation-delay: 0.3s"></div>

      <!-- 叠加方块组 - 左下 -->
      <div class="squares-group squares-left">
        <i class="geo-element square square-blue animate-fade-in-left-rotated" style="animation-delay: 0.2s"></i>
        <i class="geo-element square square-pink animate-fade-in-left-rotated" style="animation-delay: 0.4s"></i>
        <i class="geo-element square square-purple animate-fade-in-left" style="animation-delay: 0.6s"></i>
      </div>

      <!-- 叠加方块组 - 右上 -->
      <div class="squares-group squares-right">
        <i class="geo-element square square-blue animate-fade-in-right-rotated" style="animation-delay: 0.3s"></i>
        <i class="geo-element square square-pink animate-fade-in-right-rotated" style="animation-delay: 0.5s"></i>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'LoginBackgroundCenter' })
</script>

<style lang="scss" scoped>
  $primary-light-7: var(--el-color-primary-light-7);
  $primary-light-8: var(--el-color-primary-light-8);
  $primary-light-9: var(--el-color-primary-light-9);
  $primary-base: var(--el-color-primary);
  $main-bg: var(--default-box-color);

  $bg-mix-light-9: color-mix(in srgb, $primary-light-9 100%, $main-bg);
  $bg-mix-light-8: color-mix(in srgb, $primary-light-8 80%, $main-bg);
  $bg-mix-light-7: color-mix(in srgb, $primary-light-7 80%, $main-bg);

  .login-center-bg {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
    background-color: $bg-mix-light-9;
  }

  .geometric-decorations {
    .geo-element {
      position: absolute;
      opacity: 0;
      animation-fill-mode: forwards;
      animation-duration: 0.8s;
      animation-timing-function: cubic-bezier(0.25, 0.46, 0.45, 0.94);
    }

    // 动画
    @keyframes fadeInUp {
      from { opacity: 0; transform: translateY(30px); }
      to { opacity: 1; transform: translateY(0); }
    }

    @keyframes fadeInLeft {
      from { opacity: 0; transform: translateX(-30px); }
      to { opacity: 1; transform: translateX(0); }
    }

    @keyframes fadeInLeftRotated {
      from { opacity: 0; transform: translateX(-30px) rotate(-25deg); }
      to { opacity: 1; transform: translateX(0) rotate(-25deg); }
    }

    @keyframes fadeInRight {
      from { opacity: 0; transform: translateX(30px); }
      to { opacity: 1; transform: translateX(0); }
    }

    @keyframes fadeInRightRotated {
      from { opacity: 0; transform: translateX(30px) rotate(45deg); }
      to { opacity: 1; transform: translateX(0) rotate(45deg); }
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

    .animate-fade-in-up { animation-name: fadeInUp; }
    .animate-fade-in-left { animation-name: fadeInLeft; }
    .animate-fade-in-left-rotated { animation-name: fadeInLeftRotated; }
    .animate-fade-in-right { animation-name: fadeInRight; }
    .animate-fade-in-right-rotated { animation-name: fadeInRightRotated; }
    .animate-scale-in { animation-name: scaleIn; animation-duration: 1.2s; }
    .animate-bounce-in { animation-name: bounceIn; animation-duration: 0.6s; }

    // 几何形状
    .circle-outline {
      top: 15%;
      left: 20%;
      width: 50px;
      height: 50px;
      border: 2px solid $primary-light-8;
      border-radius: 50%;
    }

    .square-rotated {
      top: 40%;
      left: 8%;
      width: 70px;
      height: 70px;
      background-color: $bg-mix-light-8;
      transform: rotate(-25deg);
    }

    .circle-small {
      bottom: 30%;
      left: 25%;
      width: 20px;
      height: 20px;
      background-color: $primary-light-8;
      border-radius: 50%;
    }

    .square-bottom-right {
      right: 15%;
      bottom: 15%;
      width: 60px;
      height: 60px;
      background-color: $primary-light-8;
      transform: rotate(45deg);
    }

    // 背景泡泡
    .bg-bubble {
      top: -100px;
      right: -100px;
      width: 300px;
      height: 300px;
      background-color: $bg-mix-light-8;
      border-radius: 50%;
    }

    .bg-bubble-left {
      bottom: -80px;
      left: -80px;
      width: 250px;
      height: 250px;
      background-color: $bg-mix-light-8;
      border-radius: 50%;
    }

    // 装饰点
    .dot {
      width: 14px;
      height: 14px;
      background-color: $primary-light-7;
      border-radius: 50%;
    }

    .dot-1 { top: 20%; right: 25%; }
    .dot-2 { top: 60%; right: 10%; }
    .dot-3 { bottom: 25%; left: 40%; }
    .dot-4 { top: 30%; left: 45%; background-color: $primary-light-8; }

    // 方块组
    .squares-group {
      position: absolute;
      width: 140px;
      height: 140px;
      pointer-events: none;

      .square {
        position: absolute;
        display: block;
        border-radius: 8px;
        box-shadow: 0 8px 24px rgb(64 87 167 / 12%);
      }

      .square-blue {
        width: 50px;
        height: 50px;
        background-color: rgb(from $primary-base r g b / 30%);
      }

      .square-pink {
        width: 70px;
        height: 70px;
        background-color: rgb(from $primary-base r g b / 15%);
      }

      .square-purple {
        width: 32px;
        height: 32px;
        background-color: rgb(from $primary-base r g b / 45%);
      }
    }

    .squares-left {
      bottom: 30px;
      left: 30px;

      .square-blue { top: 12px; left: 30px; z-index: 2; transform: rotate(-10deg); }
      .square-pink { top: 30px; left: 48px; z-index: 1; transform: rotate(10deg); }
      .square-purple { top: 66px; left: 86px; z-index: 3; }
    }

    .squares-right {
      top: 80px;
      right: 60px;

      .square-blue { top: 0; right: 0; z-index: 2; transform: rotate(15deg); }
      .square-pink { top: 30px; right: 40px; z-index: 1; transform: rotate(-5deg); }
    }
  }

  // 暗色主题
  .dark .login-center-bg {
    background-color: color-mix(in srgb, $primary-light-9 60%, #070707);

    .square-rotated { background-color: $bg-mix-light-9; }
    .bg-bubble, .bg-bubble-left { background-color: $bg-mix-light-9; }
    .circle-small, .dot { background-color: $primary-light-8; }
    .square-bottom-right { background-color: $primary-light-9; }

    .squares-group .square {
      box-shadow: none;
      &.square-blue { background-color: rgb(from $primary-base r g b / 18%); }
      &.square-pink { background-color: rgb(from $primary-base r g b / 10%); }
      &.square-purple { background-color: rgb(from $primary-base r g b / 20%); }
    }
  }
</style>
