import { createApp } from 'vue';
import App from './App.vue';
import { setupI18n } from '@/locales';
import router from '@/router';
import store from '@/store';
import './assets/font/iconfont.css';
import '@/styles/index.scss';

import '@nutui/nutui/dist/packages/toast/style/css';
import '@nutui/nutui/dist/packages/notify/style/css';
import '@nutui/nutui/dist/packages/dialog/style/css';
import '@nutui/nutui/dist/packages/imagepreview/style/css';

const app = createApp(App);

// 路由
app.use(router);

// 国际化
await setupI18n(app);

// 状态管理
app.use(store);

app.mount('#app');
