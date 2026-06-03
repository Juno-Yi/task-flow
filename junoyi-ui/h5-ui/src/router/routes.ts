import type { RouteRecordRaw } from 'vue-router';

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home',
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
    name: 'login',
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '',
      keepAlive: true,
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
