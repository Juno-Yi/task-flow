<!-- 快速入口工作台组件 - 类似企业微信/飞书/钉钉应用工作台 -->
<template>
  <ElCard shadow="never" class="quick-entry-card">
    <template #header>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <ArtSvgIcon icon="ri:apps-2-line" class="text-base text-primary" />
          <span class="text-sm font-semibold">应用工作台</span>
        </div>
        <!-- 分类切换 -->
        <div class="flex items-center gap-1">
          <span
            v-for="category in categories"
            :key="category.key"
            class="category-tag cursor-pointer rounded-md px-2.5 py-1 text-xs transition-all"
            :class="activeCategory === category.key
              ? 'bg-primary/10 text-primary font-medium'
              : 'text-gray-500 hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-gray-200'"
            @click="activeCategory = category.key"
          >
            {{ category.label }}
          </span>
        </div>
      </div>
    </template>

    <!-- 分类模式：显示分组 -->
    <div v-if="activeCategory === 'all'" class="space-y-5">
      <div v-for="group in appGroups" :key="group.key" class="app-group">
        <div class="mb-2.5 flex items-center gap-2">
          <span class="h-3.5 w-0.5 rounded-full bg-primary"></span>
          <span class="text-xs font-medium text-gray-600 dark:text-gray-300">{{ group.label }}</span>
          <span class="text-xs text-gray-400">({{ group.apps.length }})</span>
        </div>
        <div class="grid grid-cols-4 gap-3 sm:grid-cols-5 md:grid-cols-6 lg:grid-cols-8 xl:grid-cols-10">
          <div
            v-for="item in group.apps"
            :key="item.path"
            class="app-item flex cursor-pointer flex-col items-center justify-center rounded-lg p-3 transition-all hover:bg-gray-50 hover:shadow-sm dark:hover:bg-gray-800"
            @click="go(item.path)"
          >
            <div
              class="mb-2 flex h-10 w-10 items-center justify-center rounded-xl transition-transform hover:scale-110"
              :style="{ backgroundColor: item.bgColor }"
            >
              <ArtSvgIcon :icon="item.icon" class="text-xl text-white" />
            </div>
            <span class="max-w-full truncate text-center text-xs text-gray-600 dark:text-gray-300">{{ item.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 单分类模式：只显示对应分类的应用 -->
    <div v-else>
      <div class="grid grid-cols-4 gap-3 sm:grid-cols-5 md:grid-cols-6 lg:grid-cols-8 xl:grid-cols-10">
        <div
          v-for="item in filteredApps"
          :key="item.path"
          class="app-item flex cursor-pointer flex-col items-center justify-center rounded-lg p-3 transition-all hover:bg-gray-50 hover:shadow-sm dark:hover:bg-gray-800"
          @click="go(item.path)"
        >
          <div
            class="mb-2 flex h-10 w-10 items-center justify-center rounded-xl transition-transform hover:scale-110"
            :style="{ backgroundColor: item.bgColor }"
          >
            <ArtSvgIcon :icon="item.icon" class="text-xl text-white" />
          </div>
          <span class="max-w-full truncate text-center text-xs text-gray-600 dark:text-gray-300">{{ item.name }}</span>
        </div>
      </div>
    </div>
  </ElCard>
</template>

<script setup lang="ts">
import { usePermission } from '@/hooks/core/usePermission'

defineOptions({ name: 'QuickEntry' })

const router = useRouter()
const { hasPermission } = usePermission()

/** 应用项定义 */
interface AppItem {
  name: string
  path: string
  icon: string
  bgColor: string
  category: string
  /** 权限标识，为空则不做权限控制 */
  permission?: string
}

/** 分类定义 */
interface Category {
  key: string
  label: string
}

/** 应用分组 */
interface AppGroup {
  key: string
  label: string
  apps: AppItem[]
}

/** 分类列表 */
const categories = ref<Category[]>([
  { key: 'all', label: '全部' },
  { key: 'work', label: '协作办公' },
  { key: 'project', label: '研发管理' },
  { key: 'system', label: '系统管理' },
])

/** 当前选中的分类 */
const activeCategory = ref('all')

/** 所有应用列表 */
const appList = ref<AppItem[]>([
  // ========== 协作办公 ==========
  { name: '我的任务', path: '/dashboard/my-task', icon: 'ri:task-line', bgColor: '#409EFF', category: 'work' },
  { name: '项目动态', path: '/project/record', icon: 'ri:file-list-3-line', bgColor: '#F56C6C', category: 'work' },
  { name: '项目排期', path: '/project/schedule', icon: 'ri:calendar-schedule-line', bgColor: '#909399', category: 'work' },
  { name: '项目立项', path: '/project/setup', icon: 'ri:draft-line', bgColor: '#9B59B6', category: 'work' },
  { name: '项目执行', path: '/project/execution', icon: 'ri:play-circle-line', bgColor: '#3498DB', category: 'work' },
  { name: '项目结项', path: '/project/acceptance', icon: 'ri:checkbox-circle-line', bgColor: '#E67E22', category: 'work' },
  { name: '项目结后', path: '/project/end', icon: 'ri:flag-2-line', bgColor: '#1ABC9C', category: 'work' },
  { name: '项目归档', path: '/project/archived', icon: 'ri:archive-line', bgColor: '#7F8C8D', category: 'work'},

  // ========== 研发管理 ==========
  { name: '项目列表', path: '/project/list', icon: 'ri:folder-line', bgColor: '#67C23A', category: 'project', permission: 'project.ui.list.view' },
  { name: '任务管理', path: '/task/list', icon: 'ri:list-check', bgColor: '#E6A23C', category: 'project', permission: 'task.ui.list.view' },
  { name: '任务审批', path: '/task/approval', icon: 'ri:file-check-line', bgColor: '#F56C6C', category: 'project', permission: 'task.ui.approval.view' },
  { name: '任务分析', path: '/task/analysis', icon: 'ri:pie-chart-line', bgColor: '#9B59B6', category: 'project', permission: 'task.ui.analysis.view' },
  { name: '项目回收站', path: '/project/recycle', icon: 'ri:delete-bin-line', bgColor: '#909399', category: 'project', permission: 'project.ui.recycle.view' },

  // ========== 系统管理 ==========
  { name: '用户中心', path: '/system/user-center', icon: 'ri:user-heart-line', bgColor: '#00A870', category: 'system' },
  { name: '用户管理', path: '/system/user', icon: 'ri:user-settings-line', bgColor: '#409EFF', category: 'system', permission: 'system.ui.user.view' },
  { name: '角色管理', path: '/system/role', icon: 'ri:shield-user-line', bgColor: '#9B59B6', category: 'system', permission: 'system.ui.role.view' },
  { name: '部门管理', path: '/system/department', icon: 'ri:building-line', bgColor: '#E6A23C', category: 'system', permission: 'system.ui.dept.view' },
  { name: '权限组管理', path: '/system/permission', icon: 'ri:lock-line', bgColor: '#F56C6C', category: 'system', permission: 'system.ui.permission.view' },
  { name: '登录日志', path: '/system/log/auth-log', icon: 'ri:login-box-line', bgColor: '#3498DB', category: 'system', permission: 'system.ui.auth-log.view' },
  { name: '操作日志', path: '/system/log/oper-log', icon: 'ri:file-list-2-line', bgColor: '#1ABC9C', category: 'system', permission: 'system.ui.oper-log.view' },
  { name: '会话监控', path: '/system/session', icon: 'ri:computer-line', bgColor: '#E67E22', category: 'system', permission: 'system.ui.session.view' },
  { name: '缓存监控', path: '/system/cache', icon: 'ri:database-2-line', bgColor: '#7F8C8D', category: 'system', permission: 'system.ui.cache.view' },
])

/**
 * 根据权限过滤应用列表
 * - 没有 permission 字段的应用：所有人可见
 * - 有 permission 字段的应用：需要通过权限校验
 */
const visibleAppList = computed(() => {
  return appList.value.filter(app => {
    if (!app.permission) return true
    return hasPermission(app.permission)
  })
})

/** 按分类分组后的应用 */
const appGroups = computed<AppGroup[]>(() => {
  return categories.value
    .filter(c => c.key !== 'all')
    .map(c => ({
      key: c.key,
      label: c.label,
      apps: visibleAppList.value.filter(app => app.category === c.key),
    }))
    .filter(g => g.apps.length > 0)
})

/** 按当前分类过滤的应用 */
const filteredApps = computed(() => {
  if (activeCategory.value === 'all') return visibleAppList.value
  return visibleAppList.value.filter(app => app.category === activeCategory.value)
})

const go = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.quick-entry-card :deep(.el-card__header) {
  padding: 14px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.app-item:active {
  transform: scale(0.95);
}
</style>

