package com.junoyi.system.service;

import com.junoyi.framework.core.domain.page.PageQuery;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysSessionQueryDTO;
import com.junoyi.system.domain.po.SysSession;

import java.util.List;

/**
 * 系统会话服务接口
 *
 * @author Fan
 */
public interface ISysSessionService {

    /**
     * 获取会话列表（分页）
     */
    PageResult<SysSession> getSessionList(SysSessionQueryDTO queryDTO, PageQuery pageQuery);

    /**
     * 踢出指定会话
     */
    boolean kickOut(String sessionId);

    /**
     * 批量踢出会话
     */
    int kickOutBatch(List<String> sessionIds);
}
