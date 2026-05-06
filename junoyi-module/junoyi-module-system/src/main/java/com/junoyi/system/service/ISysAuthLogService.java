package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysAuthLogQueryDTO;
import com.junoyi.system.domain.po.SysAuthLog;
import com.junoyi.system.domain.vo.SysAuthLogVO;

/**
 * 系统登录日志业务接口类
 *
 * @author Fan
 */
public interface ISysAuthLogService {

    /**
     * 分页查询登录日志
     *
     * @param queryDTO 查询条件
     * @param page     分页参数
     * @return 分页结果
     */
    PageResult<SysAuthLogVO> getLoginLogList(SysAuthLogQueryDTO queryDTO, Page<SysAuthLog> page);

    /**
     * 记录登录日志
     *
     * @param authLog 登录日志
     */
    void recordLoginLog(SysAuthLog authLog);

    /**
     * 清空登录日志
     */
    void clearLoginLog();

    /**
     * 删除登录日志
     *
     * @param ids 日志ID数组
     */
    void deleteLoginLog(Long[] ids);
}
