import { createRouter, createWebHistory } from 'vue-router';
import type { Router } from 'vue-router';
import routes from './routes';
import { setPageTitle } from '@/utils/page-title';

const router: Router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
});

// 白名单：不需要登录的页面
const whiteList = ['/loading',
  '/auth/login',
  '/auth/callback',
];

router.beforeEach(async (to, _from, next) => {
  // 在白名单中，直接放行
  if (whiteList.includes(to.path)) {
    next();
    return;
  }

  // 动态导入 store，避免循环依赖和初始化顺序问题
  const { useUserStore } = await import('@/store/modules/user');
  const userStore = useUserStore();

  // 检查是否登录（有 accessToken）
  if (!userStore.accessToken || !userStore.isLogin) {
    // 未登录，跳转到登录页
    next({
      path: '/loading',
      query: { redirect: to.fullPath }, // 保存原始目标路径
    });
    return;
  }

  next();
});

// 路由后置守卫：设置页面标题
router.afterEach((to) => {
  setPageTitle(to);
});

export default router;
