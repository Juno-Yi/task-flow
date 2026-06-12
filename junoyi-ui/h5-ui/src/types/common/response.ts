/**
 * API 响应类型定义模块
 *
 * 提供统一的 API 响应结构类型定义
 *
 * @module types/common/response
 */

/** 基础 API 响应结构 */
export interface BaseResponse<T = unknown> {
  /** 状态码 */
  code: number;
  /** 消息 */
  msg?: string;
  message?: string;
  /** 数据 */
  data: T;
}

/** 分页参数 */
export interface PaginationParams {
  /** 当前页码 */
  current: number;
  /** 每页数量 */
  size: number;
  /** 总数 */
  total?: number;
}

/** 分页结果结构 */
export interface PageResult<T = unknown> {
  /** 数据列表 */
  list: T[];
  /** 总数 */
  total: number;
  /** 当前页码 */
  current?: number;
  /** 每页数量 */
  size?: number;
  /** 总页数 */
  pages?: number;
}

/** 分页响应（标准格式） */
export interface PaginatedResponse<T = unknown> {
  /** 数据列表 */
  list: T[];
  /** 总数 */
  total: number;
  /** 当前页码 */
  current: number;
  /** 每页数量 */
  size: number;
  /** 总页数 */
  pages?: number;
}

