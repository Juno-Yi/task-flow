import type { RouteRecordRaw } from 'vue-router';

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/loading',
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: 'home',
        component: () => import('@/views/home/index.vue'),
        meta: {
          title: 'common.tabbar.home',
          keepAlive: true,
        },
      },
      {
        path: 'my-task',
        component: () => import('@/views/my-task/index.vue'),
        meta: {
          title: 'common.tabbar.task'
        }
      },
      {
        path: 'my-project',
        component: () => import('@/views/my-project/index.vue'),
        meta: {
          title: 'common.tabbar.my-project',
          keepAlive: true,
        },
      },
      {
        path: 'me',
        component: () => import('@/views/me/index.vue'),
        meta: {
          title: 'common.tabbar.me',
          keepAlive: true,
        },
      },

    ],
  },
  {
    name: 'oauth-loading',
    path: '/loading',
    component: () => import('@/views/auth/oauth-loading/index.vue'),
    meta: {
      title: '加载中...',
      keepAlive: true,
    },
  },
  {
    name: 'callback',
    path: '/auth/callback',
    component: () => import('@/views/auth2/callback.vue'),
    meta: {
      title: '登录中...',
      keepAlive: false,
    },
  },
  {
    name: 'login',
    path: '/auth/login',
    component: () => import('@/views/auth/login/index.vue'),
    meta: {
      title: '登录绑定',
      keepAlive: false,
    },
  },
  // 匹配不到重定向会主页
  {
    // 找不到路由重定向到首页
    path: '/:pathMatch(.*)',
    redirect: '/home',
  },
];

export default routes;
