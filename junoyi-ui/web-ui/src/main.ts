import App from './App.vue'
import { createApp } from 'vue'
import { initStore } from './store'                 // Store
import { initRouter } from './router'               // Router
import language from './locales'                    // 国际化
import '@styles/core/tailwind.css'                  // tailwind
import '@styles/index.scss'                         // 样式
import '@utils/sys/console.ts'                      // 控制台输出内容
import { setupGlobDirectives } from './directives'
import { setupErrorHandle } from './utils/sys/error-handle'
import { fetchGetSystemInfo } from '@/api/system/info'
import { useSettingStore } from '@/store/modules/setting'

document.addEventListener(
  'touchstart',
  function () {},
  { passive: false }
)

const app = createApp(App)
initStore(app)
initRouter(app)
setupGlobDirectives(app)
setupErrorHandle(app)

app.use(language)

// 获取系统信息并保存到store
fetchGetSystemInfo()
  .then((res) => {
    const settingStore = useSettingStore()
    settingStore.setSystemInfo(res)
  })
  .catch((error) => {
    console.warn('[System] 获取系统信息失败:', error)
  })

app.mount('#app')