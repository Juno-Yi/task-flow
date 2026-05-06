/// <reference types="vite/client" />

declare module 'nprogress'

declare module 'crypto-js'

declare module 'vue-img-cutter'

declare module 'file-saver'

declare module 'qrcode.vue' {
  export type Level = 'L' | 'M' | 'Q' | 'H'
  export type RenderAs = 'canvas' | 'svg'
  export type GradientType = 'linear' | 'radial'
  export interface ImageSettings {
    src: string
    height: number
    width: number
    excavate: boolean
  }
  export interface QRCodeProps {
    value: string
    size?: number
    level?: Level
    background?: string
    foreground?: string
    renderAs?: RenderAs
  }
  const QrcodeVue: any
  export default QrcodeVue
}

// 全局变量声明
declare const __APP_VERSION__: string // 版本号

// Vite 环境变量类型声明
interface ImportMetaEnv {
  readonly VITE_VERSION: string
  readonly VITE_PORT: string
  readonly VITE_BASE_URL: string
  readonly VITE_API_URL: string
  readonly VITE_API_PREFIX: string
  readonly VITE_WITH_CREDENTIALS: string
  readonly VITE_OPEN_ROUTE_INFO: string
  readonly VITE_LOCK_ENCRYPT_KEY: string
  readonly VITE_AUTO_FILL_LOGIN: string
  readonly VITE_DROP_CONSOLE: string
  readonly VITE_API_ENCRYPT: string
  readonly VITE_API_ENCRYPT_PUBLIC_KEY: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
