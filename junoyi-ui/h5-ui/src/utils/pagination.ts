/**
 * 分页工具函数
 *
 * 提供分页相关的实用工具函数
 *
 * @module utils/pagination
 */

import type { PaginatedResponse, PageResult } from '@/types/common/response';

/**
 * 标准分页响应适配器
 * 将不同格式的分页响应转换为统一格式
 */
export function defaultPaginationAdapter<T>(response: any): PaginatedResponse<T> {
  // 如果已经是标准格式
  if (response.list && response.total !== undefined) {
    return {
      list: response.list,
      total: response.total,
      current: response.current || 1,
      size: response.size || 10,
      pages: response.pages
    };
  }

  // 如果是其他格式，尝试提取
  if (response.records) {
    return {
      list: response.records,
      total: response.total || 0,
      current: response.current || 1,
      size: response.size || 10,
      pages: response.pages
    };
  }

  // 如果是数组，返回简单格式
  if (Array.isArray(response)) {
    return {
      list: response,
      total: response.length,
      current: 1,
      size: response.length
    };
  }

  // 默认返回空数据
  return {
    list: [],
    total: 0,
    current: 1,
    size: 10
  };
}

/**
 * 计算总页数
 */
export function calculateTotalPages(total: number, size: number): number {
  return Math.ceil(total / size);
}

/**
 * 判断是否有下一页
 */
export function hasNextPage(current: number, total: number, size: number): boolean {
  const totalPages = calculateTotalPages(total, size);
  return current < totalPages;
}

/**
 * 判断是否有上一页
 */
export function hasPrevPage(current: number): boolean {
  return current > 1;
}

/**
 * 获取页码范围
 * @param current 当前页
 * @param total 总页数
 * @param range 显示的页码范围
 * @returns 页码数组
 */
export function getPageRange(current: number, total: number, range: number = 5): number[] {
  const pages: number[] = [];
  const half = Math.floor(range / 2);
  
  let start = Math.max(1, current - half);
  let end = Math.min(total, start + range - 1);
  
  // 如果结尾不够，调整开始
  if (end - start + 1 < range) {
    start = Math.max(1, end - range + 1);
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  
  return pages;
}

/**
 * 数组去重
 */
export function uniqueByKey<T>(array: T[], key: keyof T): T[] {
  const seen = new Set();
  return array.filter(item => {
    const value = item[key];
    if (seen.has(value)) {
      return false;
    }
    seen.add(value);
    return true;
  });
}

/**
 * 合并分页数据（用于加载更多）
 */
export function mergePaginationData<T>(
  oldData: T[],
  newData: T[],
  uniqueKey?: keyof T
): T[] {
  const merged = [...oldData, ...newData];
  
  // 如果指定了唯一键，进行去重
  if (uniqueKey) {
    return uniqueByKey(merged, uniqueKey);
  }
  
  return merged;
}

/**
 * 创建空的分页响应
 */
export function createEmptyPaginationResponse<T>(): PaginatedResponse<T> {
  return {
    list: [],
    total: 0,
    current: 1,
    size: 10
  };
}

