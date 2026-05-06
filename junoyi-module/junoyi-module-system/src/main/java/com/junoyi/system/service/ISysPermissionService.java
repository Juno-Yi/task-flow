package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysPermissionDTO;
import com.junoyi.system.domain.dto.SysPermissionQueryDTO;
import com.junoyi.system.domain.po.SysPermission;
import com.junoyi.system.domain.vo.SysPermissionVO;

import java.util.List;

/**
 * 权限池服务接口
 *
 * @author Fan
 */
public interface ISysPermissionService {

    /**
     * 分页查询权限池列表
     */
    PageResult<SysPermissionVO> getPermissionList(SysPermissionQueryDTO queryDTO, Page<SysPermission> page);

    /**
     * 获取所有启用的权限（下拉选项）
     */
    List<SysPermissionVO> getPermissionOptions();

    /**
     * 添加权限
     */
    void addPermission(SysPermissionDTO dto);

    /**
     * 删除权限
     */
    void deletePermission(Long id);

    /**
     * 批量删除权限
     */
    void deletePermissionBatch(List<Long> ids);

    /**
     * 更新权限状态
     */
    void updatePermissionStatus(Long id, Integer status);
}
