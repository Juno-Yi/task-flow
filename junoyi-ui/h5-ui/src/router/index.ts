import { createRouter, createWebHistory } from 'vue-router';
import type { Router } from 'vue-router';
import routes from './routes';
import { useUserStore } from '@/store/modules/user';

const router: Router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
});

// 白名单：不需要登录的页面
const whiteList = ['/login', '/auth/callback', '/auth/bind'];

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore();

  // 在白名单中，直接放行
  if (whiteList.includes(to.path)) {
    next();
    return;
  }

  // 检查是否登录（有 accessToken）
  if (!userStore.accessToken || !userStore.isLogin) {
    // 未登录，跳转到登录页
    next({
      path: '/login',
      query: { redirect: to.fullPath }, // 保存原始目标路径
    });
    return;
  }

  next();
});

export default router;
