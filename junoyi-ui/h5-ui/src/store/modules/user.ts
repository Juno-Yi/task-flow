import { defineStore } from 'pinia';

interface StoreUser {
  accessToken: string;
  refreshToken: string;
  isLogin: boolean;
  info: Record<any, any>;
}

export const useUserStore = defineStore('user', {
  state: (): StoreUser => ({
    accessToken: '',
    refreshToken: '',
    isLogin: false,
    info: {},
  }),
  getters: {
    getUserInfo(): any {
      return this.info || {};
    },
    // 兼容旧代码的 token getter
    token(): string {
      return this.accessToken;
    },
  },
  actions: {
    /**
     * 设置用户信息
     */
    setInfo(info: any) {
      this.info = info ?? {};
    },

    /**
     * 设置登录状态
     */
    setLoginStatus(status: boolean) {
      this.isLogin = status;
    },

    /**
     * 设置令牌
     */
    setToken(newAccessToken: string, newRefreshToken?: string) {
      this.accessToken = newAccessToken;
      if (newRefreshToken) {
        this.refreshToken = newRefreshToken;
      }
    },

    /**
     * 清空用户信息和令牌
     */
    clearUser() {
      this.accessToken = '';
      this.refreshToken = '';
      this.isLogin = false;
      this.info = {};
    },

    /**
     * 退出登录
     */
    async logout() {
      // TODO: 调用后端退出接口
      this.clearUser();
    },
  },
  persist: {
    pick: ['accessToken', 'refreshToken', 'isLogin', 'info'],
    storage: localStorage,
  },
});
