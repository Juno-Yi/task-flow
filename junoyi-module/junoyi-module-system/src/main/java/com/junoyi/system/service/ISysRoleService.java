package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysRoleDTO;
import com.junoyi.system.domain.dto.SysRoleQueryDTO;
import com.junoyi.system.domain.po.SysRole;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.domain.vo.SysRoleVO;

import java.util.List;


/**
 * 系统角色业务接口类
 *
 * @author Fan
 */
public interface ISysRoleService {

    /**
     * 查询角色列表（分页）
     * @param queryDTO 查询参数
     * @param page 分页参数
     * @return 角色分页结果
     */
    PageResult<SysRoleVO> getRoleList(SysRoleQueryDTO queryDTO, Page<SysRole> page);

    /**
     * 获取角色列表
     * @return 角色列表
     */
    List<SysRoleVO> getRoleList();

    /**
     * 通过ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    SysRoleVO getRoleById(Long id);

    /**
     * 添加角色
     * @param roleDTO 角色信息
     */
    void addRole(SysRoleDTO roleDTO);

    /**
     * 修改角色
     * @param roleDTO 角色信息
     */
    void updateRole(SysRoleDTO roleDTO);

    /**
     * 删除角色
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 批量删除角色
     * @param ids 角色ID列表
     */
    void deleteRoleBatch(List<Long> ids);

    /**
     * 获取角色绑定的权限组列表
     * @param roleId 角色ID
     * @return 权限组列表
     */
    List<SysPermGroupVO> getRolePermGroups(Long roleId);

    /**
     * 更新角色权限组绑定
     * @param roleId 角色ID
     * @param groupIds 权限组ID列表
     */
    void updateRolePermGroups(Long roleId, List<Long> groupIds);
}
