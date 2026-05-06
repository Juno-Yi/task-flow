<template>
  <div class="system-info-page" v-loading="loading">
    <ElRow :gutter="16">
      <!-- 系统信息 -->
      <ElCol :span="12">
        <ElCard shadow="never" class="info-card">
          <template #header>
            <div class="card-header">
              <ArtSvgIcon icon="ri:information-line" class="mr-2" />
              <span>系统信息</span>
            </div>
          </template>
          <ElDescriptions :column="1" border>
            <ElDescriptionsItem label="系统名称">
              <ElTag type="primary">{{ monitorData?.systemInfo?.name || '-' }}</ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="系统版本">
              <ElTag type="success">{{ monitorData?.systemInfo?.version || '-' }}</ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="框架版本">
              {{ monitorData?.systemInfo?.frameworkVersion || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="运行环境">
              {{ monitorData?.systemInfo?.environment || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="启动时间">
              {{ monitorData?.systemInfo?.startTime || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="运行时长">
              <ElTag type="info">{{ monitorData?.systemInfo?.uptime || '-' }}</ElTag>
            </ElDescriptionsItem>
          </ElDescriptions>
        </ElCard>
      </ElCol>

      <!-- 服务器信息 -->
      <ElCol :span="12">
        <ElCard shadow="never" class="info-card">
          <template #header>
            <div class="card-header">
              <ArtSvgIcon icon="ri:server-line" class="mr-2" />
              <span>服务器信息</span>
            </div>
          </template>
          <ElDescriptions :column="1" border>
            <ElDescriptionsItem label="服务器名称">
              {{ monitorData?.serverInfo?.name || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="操作系统">
              {{ monitorData?.serverInfo?.os || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="系统架构">
              {{ monitorData?.serverInfo?.arch || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="CPU 核心数">
              {{ monitorData?.serverInfo?.cpuCores || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="服务器IP">
              {{ monitorData?.serverInfo?.ip || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="服务器时间">
              {{ currentTime }}
            </ElDescriptionsItem>
          </ElDescriptions>
        </ElCard>
      </ElCol>
    </ElRow>

    <ElRow :gutter="16" class="mt-4">
      <!-- Java 信息 -->
      <ElCol :span="12">
        <ElCard shadow="never" class="info-card">
          <template #header>
            <div class="card-header">
              <ArtSvgIcon icon="ri:code-box-line" class="mr-2" />
              <span>Java 信息</span>
            </div>
          </template>
          <ElDescriptions :column="1" border>
            <ElDescriptionsItem label="Java 版本">
              {{ monitorData?.javaInfo?.version || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="Java 供应商">
              {{ monitorData?.javaInfo?.vendor || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="Java Home">
              <div class="text-ellipsis" :title="monitorData?.javaInfo?.home">
                {{ monitorData?.javaInfo?.home || '-' }}
              </div>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="JVM 名称">
              {{ monitorData?.javaInfo?.jvmName || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="JVM 版本">
              {{ monitorData?.javaInfo?.jvmVersion || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="运行参数">
              <div class="text-ellipsis" :title="monitorData?.javaInfo?.args">
                {{ monitorData?.javaInfo?.args || '-' }}
              </div>
            </ElDescriptionsItem>
          </ElDescriptions>
        </ElCard>
      </ElCol>

      <!-- 内存信息 -->
      <ElCol :span="12">
        <ElCard shadow="never" class="info-card">
          <template #header>
            <div class="card-header">
              <ArtSvgIcon icon="ri:database-2-line" class="mr-2" />
              <span>内存信息</span>
            </div>
          </template>
          <ElDescriptions :column="1" border>
            <ElDescriptionsItem label="总内存">
              {{ monitorData?.memoryInfo?.total || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="已用内存">
              <ElTag :type="getMemoryType(monitorData?.memoryInfo?.usedPercent || 0)">
                {{ monitorData?.memoryInfo?.used || '-' }} ({{ monitorData?.memoryInfo?.usedPercent || 0 }}%)
              </ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="空闲内存">
              {{ monitorData?.memoryInfo?.free || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="JVM 总内存">
              {{ monitorData?.memoryInfo?.jvmTotal || '-' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="JVM 已用">
              <ElTag :type="getMemoryType(monitorData?.memoryInfo?.jvmUsedPercent || 0)">
                {{ monitorData?.memoryInfo?.jvmUsed || '-' }} ({{ monitorData?.memoryInfo?.jvmUsedPercent || 0 }}%)
              </ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="JVM 空闲">
              {{ monitorData?.memoryInfo?.jvmFree || '-' }}
            </ElDescriptionsItem>
          </ElDescriptions>
        </ElCard>
      </ElCol>
    </ElRow>

    <ElRow :gutter="16" class="mt-4">
      <!-- 磁盘信息 -->
      <ElCol :span="24">
        <ElCard shadow="never" class="info-card">
          <template #header>
            <div class="card-header">
              <ArtSvgIcon icon="ri:hard-drive-2-line" class="mr-2" />
              <span>磁盘信息</span>
              <ElButton 
                type="primary" 
                size="small" 
                :icon="Refresh" 
                @click="loadMonitorData"
                :loading="loading"
                class="ml-auto"
              >
                刷新
              </ElButton>
            </div>
          </template>
          <ElTable :data="monitorData?.diskInfo || []" border>
            <ElTableColumn prop="path" label="挂载点" width="150" />
            <ElTableColumn prop="type" label="文件系统" width="120" />
            <ElTableColumn prop="total" label="总容量" width="120" />
            <ElTableColumn prop="used" label="已用" width="120" />
            <ElTableColumn prop="free" label="可用" width="120" />
            <ElTableColumn prop="usedPercent" label="使用率" width="120">
              <template #default="{ row }">
                <ElProgress 
                  :percentage="row.usedPercent" 
                  :color="getProgressColor(row.usedPercent)"
                  :stroke-width="16"
                />
              </template>
            </ElTableColumn>
          </ElTable>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
import { Refresh } from '@element-plus/icons-vue'
import { ElCard, ElRow, ElCol, ElDescriptions, ElDescriptionsItem, ElTag, ElTable, ElTableColumn, ElProgress, ElButton, ElMessage } from 'element-plus'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { fetchGetSystemMonitor } from '@/api/system/monitor'

defineOptions({ name: 'SystemInfo' })

const loading = ref(false)
const monitorData = ref<Api.System.SystemMonitorVO | null>(null)
const currentTime = ref('')

/**
 * 加载系统监控数据
 */
const loadMonitorData = async () => {
  loading.value = true
  try {
    const data = await fetchGetSystemMonitor()
    monitorData.value = data
    // 初始化当前时间
    if (data.serverInfo?.time) {
      currentTime.value = data.serverInfo.time
    }
  } catch (error) {
    console.error('获取系统监控信息失败:', error)
    ElMessage.error('获取系统监控信息失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取内存使用率标签类型
 */
const getMemoryType = (percent: number): 'success' | 'warning' | 'danger' => {
  if (percent < 60) return 'success'
  if (percent < 80) return 'warning'
  return 'danger'
}

/**
 * 获取进度条颜色
 */
const getProgressColor = (percent: number): string => {
  if (percent < 60) return '#67c23a'
  if (percent < 80) return '#e6a23c'
  return '#f56c6c'
}

// 定时更新当前时间
let timeTimer: NodeJS.Timeout | null = null

onMounted(() => {
  // 加载数据
  loadMonitorData()
  
  // 每秒更新当前时间
  timeTimer = setInterval(() => {
    if (currentTime.value) {
      const date = new Date(currentTime.value)
      date.setSeconds(date.getSeconds() + 1)
      currentTime.value = date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      })
    }
  }, 1000)
})

onUnmounted(() => {
  if (timeTimer) {
    clearInterval(timeTimer)
    timeTimer = null
  }
})
</script>

<style scoped lang="scss">
.system-info-page {
  padding: 16px;
}

.info-card {
  margin-bottom: 16px;

  :deep(.el-card__header) {
    padding: 16px 20px;
    background-color: var(--el-fill-color-light);
  }
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 300px;
  cursor: help;
}

:deep(.el-descriptions__label) {
  width: 120px;
  font-weight: 500;
}

:deep(.el-table) {
  font-size: 14px;
}
</style>
