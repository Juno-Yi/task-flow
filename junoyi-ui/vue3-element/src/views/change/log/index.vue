<!-- 更新日志页面 -->
<template>
  <div class="mx-auto pt-5 mb-5">
    <!-- 标题 -->
    <h3 class="text-2xl font-medium text-g-900 mb-8">更新日志</h3>

    <!-- 日志列表 -->
    <div class="space-y-5">
      <div
        v-for="item in upgradeLogList"
        :key="item.version"
        class="art-card-sm rounded-lg p-6 transition-shadow max-md:p-4"
      >
        <!-- 版本和日期 -->
        <div class="flex-cb gap-3 mb-4 flex-wrap">
          <span class="px-3 py-1 bg-theme/10 text-theme text-sm font-medium rounded-full">
            {{ item.version }}
          </span>
          <span class="text-sm text-g-500">{{ item.date }}</span>
        </div>

        <!-- 标题 -->
        <h4 class="text-lg font-medium text-g-900 mb-4">{{ item.title }}</h4>

        <!-- 分类详情 -->
        <div v-if="item.detail" class="space-y-4">
          <!-- 新增 Added -->
          <div v-if="item.detail.added?.length">
            <h5 class="flex items-center gap-2 text-base font-medium text-g-800 mb-2">
              <span>✨</span>
              <span>新增（Added）</span>
            </h5>
            <ul class="space-y-1.5 pl-6">
              <li
                v-for="(text, index) in item.detail.added"
                :key="index"
                class="flex gap-2 text-sm text-g-600"
              >
                <span class="mt-0.5">•</span>
                <span class="text-g-700">{{ text }}</span>
              </li>
            </ul>
          </div>

          <!-- 优化 Changed -->
          <div v-if="item.detail.changed?.length">
            <h5 class="flex items-center gap-2 text-base font-medium text-g-800 mb-2">
              <span>🔧</span>
              <span>优化（Changed）</span>
            </h5>
            <ul class="space-y-1.5 pl-6">
              <li
                v-for="(text, index) in item.detail.changed"
                :key="index"
                class="flex gap-2 text-sm text-g-600"
              >
                <span class="mt-0.5">•</span>
                <span class="text-g-700">{{ text }}</span>
              </li>
            </ul>
          </div>

          <!-- 修复 Fixed -->
          <div v-if="item.detail.fixed?.length">
            <h5 class="flex items-center gap-2 text-base font-medium text-g-800 mb-2">
              <span>🐛</span>
              <span>修复（Fixed）</span>
            </h5>
            <ul class="space-y-1.5 pl-6">
              <li
                v-for="(text, index) in item.detail.fixed"
                :key="index"
                class="flex gap-2 text-sm text-g-600"
              >
                <span class="mt-0.5">•</span>
                <span class="text-g-700">{{ text }}</span>
              </li>
            </ul>
          </div>

          <!-- 测试/示例 Demo -->
          <div v-if="item.detail.demo?.length">
            <h5 class="flex items-center gap-2 text-base font-medium text-g-800 mb-2">
              <span>🧪</span>
              <span>测试 / 示例（Demo）</span>
            </h5>
            <ul class="space-y-1.5 pl-6">
              <li
                v-for="(text, index) in item.detail.demo"
                :key="index"
                class="flex gap-2 text-sm text-g-600"
              >
                <span class="mt-0.5">•</span>
                <span class="text-g-700">{{ text }}</span>
              </li>
            </ul>
          </div>
        </div>

        <!-- 备注 -->
        <div v-if="item.remark" class="text-sm text-g-800 bg-g-300/60 rounded p-3 mt-4">
          {{ item.remark }}
        </div>

        <!-- 标签 -->
        <ElTag v-if="item.breakingChange" type="danger" size="small" class="mt-3">破坏性更新</ElTag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { upgradeLogList } from '@/mock/upgrade/changeLog'

  defineOptions({ name: 'ChangeLog' })
</script>
