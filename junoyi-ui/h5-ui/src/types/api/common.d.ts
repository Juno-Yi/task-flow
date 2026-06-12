/**
 * 全局 API 命名空间类型定义
 */

declare namespace Api {
  /**
   * 通用类型
   */
  namespace Common {
    /** 分页参数 */
    interface PaginationParams {
      current: number;
      size: number;
      total?: number;
    }

    /** 分页响应（与 web-ui 保持一致，使用 PageResult）*/
    interface PaginatedResponse<T = any> {
      list: T[];
      total: number;
      current?: number;
      size?: number;
      pages?: number;
    }

    /** 基础响应 */
    interface BaseResponse<T = any> {
      code: number;
      msg?: string;
      message?: string;
      data: T;
    }
  }
}

export {};

