import { createApp } from 'vue';
import App from './App.vue';
import { setupI18n } from '@/locales';
import router from '@/router';
import store from '@/store';
import './assets/font/iconfont.css';
import '@/styles/index.scss';

// Element Plus
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

// 检查并清理旧的 localStorage 数据格式
const checkAndMigrateStorage = () => {
  try {
    const userDataStr = localStorage.getItem('user');
    if (userDataStr) {
      const userData = JSON.parse(userDataStr);
      // 检查是否是旧格式（只有 token 字段，没有 accessToken 和 refreshToken）
      if (userData.token && !userData.accessToken && !userData.refreshToken) {
        console.warn('检测到旧版本数据格式，正在清除...');
        localStorage.removeItem('user');
        console.log('旧数据已清除，请重新登录');
      }
    }
  } catch (error) {
    console.error('检查 localStorage 数据时出错:', error);
  }
};

// 执行数据迁移检查
checkAndMigrateStorage();

const app = createApp(App);

// 状态管理（必须在路由之前，因为路由守卫需要使用 store）
app.use(store);

// 路由
app.use(router);

// Element Plus
app.use(ElementPlus);

// 国际化
await setupI18n(app);

app.mount('#app');
