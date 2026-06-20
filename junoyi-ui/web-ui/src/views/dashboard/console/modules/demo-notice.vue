<template>
  <!-- 演示站公告弹窗 -->
  <ElDialog
      v-model="noticeVisible"
      title="钧逸研发管理系统"
      width="820px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      align-center
  >
    <div class="notice-container">

      <!-- 左侧 -->
      <div class="left">
        <div class="title">
          企业级研发任务协作平台
        </div>

        <div class="desc">
          钧逸研发管理系统专为软件开发团队、产品研发团队打造，
          支持需求管理、任务协同、项目跟踪、团队协作等研发场景。
        </div>

        <div class="desc">
          系统支持内嵌于企业微信、飞书、钉钉等平台，
          适用于中小型研发团队、技术创业团队以及外包团队。
        </div>

        <ElAlert
            title="当前版本为社区版"
            type="success"
            :closable="false"
            show-icon
        />

        <!-- 授权说明 -->
        <div class="license-box">

          <div class="license-title">
            开源协议说明
          </div>

          <div class="license-desc">
            本项目基于 MIT 协议免费开源，您可以自由使用、修改和分发。
          </div>

          <div class="license-section">
            <div class="section-title success">
              MIT 协议允许您
            </div>

            <div class="license-item">
              • 个人或企业免费使用
            </div>

            <div class="license-item">
              • 用于商业项目
            </div>

            <div class="license-item">
              • 自由修改和二次开发
            </div>

            <div class="license-item">
              • 闭源使用或开源分发
            </div>
          </div>

          <div class="license-section">
            <div class="section-title warning">
              使用须知
            </div>

            <div class="license-item">
              • 请保留原始版权声明和许可证
            </div>

            <div class="license-item">
              • 本软件按"原样"提供，不附带任何担保
            </div>
          </div>

          <div class="contact-box">
            <div>邮箱：support@junoyi.com</div>
            <div>手机：13160393978</div>
            <div>微信公众号：钧逸网络科技</div>
          </div>

        </div>
      </div>

      <!-- 右侧 -->
      <div class="right">

        <div class="qrcode-title">
          关注公众号获取
        </div>

        <img
            class="qrcode"
            :src="gongzhonghaoImage"
            alt="公众号二维码"
        >

        <div class="copy-box">

          <div class="copy-label">
            回复以下口令获取 SQL 文件：
          </div>

          <div class="copy-content">
            研发任务管理系统数据库
          </div>

          <ElButton
              type="primary"
              plain
              size="small"
              @click="handleCopy"
          >
            点击复制
          </ElButton>

        </div>

      </div>

    </div>

    <template #footer>
      <ElButton
          type="primary"
          @click="handleConfirm"
      >
        我已知晓
      </ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

import gongzhonghaoImage from '@/assets/images/gongzhonghao.jpg'

/**
 * 公告弹窗显示状态
 */
const noticeVisible = ref(false)

/**
 * 页面加载
 */
onMounted(() => {
  const hasReadNotice = localStorage.getItem('junoyi-demo-notice')

  // 没有确认过公告
  if (!hasReadNotice) {
    noticeVisible.value = true
  }
})

/**
 * 确认公告
 */
const handleConfirm = () => {
  localStorage.setItem('junoyi-demo-notice', '1')

  noticeVisible.value = false
}

/**
 * 复制口令
 */
const handleCopy = async () => {
  try {
    await navigator.clipboard.writeText('研发任务管理系统数据库')

    ElMessage.success('复制成功')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped lang="scss">
.notice-container {
  display: flex;
  gap: 36px;
}

.left {
  flex: 1;

  .title {
    font-size: 26px;
    font-weight: 700;
    margin-bottom: 18px;
    color: #303133;
    line-height: 1.4;
  }

  .desc {
    line-height: 1.9;
    color: #606266;
    margin-bottom: 16px;
    font-size: 14px;
  }
}

.license-box {
  margin-top: 20px;
  padding: 18px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #ebeef5;

  .license-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 14px;
  }

  .license-desc {
    margin-bottom: 18px;
    color: #606266;
    line-height: 1.7;
    font-size: 14px;
  }

  .license-section {
    margin-bottom: 18px;
  }

  .section-title {
    margin-bottom: 10px;
    font-size: 14px;
    font-weight: 600;

    &.success {
      color: #67c23a;
    }

    &.warning {
      color: #e6a23c;
    }

    &.danger {
      color: #f56c6c;
    }
  }

  .license-item {
    margin-bottom: 8px;
    color: #606266;
    font-size: 14px;
    line-height: 1.7;
  }

  .contact-box {
    margin-top: 18px;
    padding-top: 14px;
    border-top: 1px dashed #dcdfe6;

    div {
      margin-bottom: 8px;
      color: #606266;
      font-size: 14px;
    }
  }
}

.right {
  width: 240px;
  text-align: center;

  .qrcode-title {
    font-size: 17px;
    font-weight: 600;
    margin-bottom: 18px;
    color: #303133;
  }

  .qrcode {
    width: 220px;
    height: 220px;
    border-radius: 16px;
    border: 1px solid #ebeef5;
    object-fit: cover;
    background: #fff;
  }

  .copy-box {
    margin-top: 18px;

    .copy-label {
      font-size: 13px;
      color: #909399;
      margin-bottom: 10px;
      line-height: 1.6;
    }

    .copy-content {
      padding: 12px;
      border-radius: 10px;
      background: #f5f7fa;
      font-weight: 600;
      margin-bottom: 14px;
      letter-spacing: 1px;
      color: #303133;
      font-size: 14px;
      word-break: break-all;
    }
  }
}
</style>