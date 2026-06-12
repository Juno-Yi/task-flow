/**
 * usePagination - 移动端分页管理 Hook
 *
 * 专为移动端设计的轻量级分页管理方案
 *
 * ## 主要功能
 *
 * 1. 分页状态管理 - 当前页、每页数量、总数、总页数
 * 2. 上拉加载更多 - 自动追加数据、加载状态管理
 * 3. 下拉刷新 - 重置分页、清空数据、重新加载
 * 4. 加载状态 - loading、finished、error 状态管理
 * 5. 数据处理 - 自动合并列表数据、去重处理
 *
 * @module hooks/usePagination
 */

import { ref, reactive, computed } from 'vue';
import type { PageResult } from '@/types';

/** 分页参数接口 */
export interface PaginationParams {
  current: number;
  size: number;
  total?: number;
}

/** 分页配置接口 */
export interface UsePaginationConfig<TApiFn extends (params: any) => Promise<any>> {
  /** API 请求函数 */
  apiFn: TApiFn;
  /** 默认请求参数 */
  apiParams?: Record<string, any>;
  /** 每页数量 */
  pageSize?: number;
  /** 是否立即加载 */
  immediate?: boolean;
  /** 数据转换函数 */
  dataTransformer?: <T>(data: T[]) => T[];
  /** 成功回调 */
  onSuccess?: <T>(data: T[], response: PageResult<T>) => void;
  /** 错误回调 */
  onError?: (error: Error) => void;
}

/**
 * 移动端分页 Hook
 */
export function usePagination<TApiFn extends (params: any) => Promise<any>>(
  config: UsePaginationConfig<TApiFn>
) {
  type TRecord = any; // 简化类型推导
  
  const {
    apiFn,
    apiParams = {},
    pageSize = 10,
    immediate = true,
    dataTransformer,
    onSuccess,
    onError
  } = config;

  // 加载状态
  const loading = ref(false);
  const finished = ref(false);
  const error = ref<Error | null>(null);

  // 数据列表
  const data = ref<TRecord[]>([]);

  // 分页参数
  const pagination = reactive<PaginationParams>({
    current: 1,
    size: pageSize,
    total: 0
  });

  // 是否有数据
  const hasData = computed(() => data.value.length > 0);

  // 是否有更多数据
  const hasMore = computed(() => {
    if (pagination.total === 0) return true;
    return data.value.length < pagination.total;
  });

  /**
   * 加载数据
   */
  const loadData = async (isLoadMore = false) => {
    if (loading.value) return;
    if (isLoadMore && finished.value) return;

    loading.value = true;
    error.value = null;

    try {
      const params = {
        ...apiParams,
        current: pagination.current,
        size: pagination.size
      };

      const response = await apiFn(params);

      // 处理响应数据
      const { list = [], total = 0, current = 1 } = response as PageResult<TRecord>;

      // 数据转换
      const transformedList = dataTransformer ? dataTransformer(list) : list;

      // 更新数据
      if (isLoadMore) {
        data.value = [...data.value, ...transformedList];
      } else {
        data.value = transformedList;
      }

      // 更新分页信息
      pagination.total = total;
      pagination.current = current;

      // 判断是否加载完成
      finished.value = data.value.length >= total;

      // 成功回调
      onSuccess?.(transformedList, response as PageResult<TRecord>);
    } catch (err: any) {
      error.value = err;
      finished.value = true;
      
      // 错误回调
      onError?.(err);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 加载更多（上拉加载）
   */
  const loadMore = async () => {
    if (finished.value || loading.value) return;
    
    pagination.current += 1;
    await loadData(true);
  };

  /**
   * 刷新数据（下拉刷新）
   */
  const refresh = async () => {
    pagination.current = 1;
    finished.value = false;
    data.value = [];
    await loadData(false);
  };

  /**
   * 重置分页
   */
  const reset = () => {
    pagination.current = 1;
    pagination.total = 0;
    finished.value = false;
    data.value = [];
    error.value = null;
  };

  // 立即加载
  if (immediate) {
    loadData();
  }

  return {
    // 数据
    data,
    hasData,
    hasMore,
    
    // 状态
    loading,
    finished,
    error,
    
    // 分页
    pagination,
    
    // 方法
    loadData,
    loadMore,
    refresh,
    reset
  };
}

