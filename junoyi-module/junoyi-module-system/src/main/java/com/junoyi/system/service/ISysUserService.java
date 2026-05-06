package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysUserDTO;
import com.junoyi.system.domain.dto.SysUserQueryDTO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.domain.vo.SysDeptVO;
import com.junoyi.system.domain.vo.SysRoleVO;
import com.junoyi.system.domain.vo.SysUserPermVO;
import com.junoyi.system.domain.vo.SysUserVO;

import java.util.List;

/**
 * 系统用户业务接口类
 *
 * @author Fan
 */
public interface ISysUserService {

    /**
     * 获取用户列表（分页）
     * @param queryDTO 查询条件
     * @param page 分页参数
     * @return 用户分页列表
     */
    PageResult<SysUserVO> getUserList(SysUserQueryDTO queryDTO, Page<SysUser> page);

    /**
     * 添加用户
     * @param userDTO 用户信息
     */
    void addUser(SysUserDTO userDTO);

    /**
     * 更新用户（不更新密码）
     * @param userDTO 用户信息
     */
    void updateUser(SysUserDTO userDTO);

    /**
     * 删除用户（逻辑删除）
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户（逻辑删除）
     * @param ids 用户ID列表
     */
    void deleteUserBatch(List<Long> ids);

    /**
     * 获取用户绑定的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleVO> getUserRoles(Long userId);

    /**
     * 更新用户角色绑定
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void updateUserRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户绑定的部门列表
     * @param userId 用户ID
     * @return 部门列表
     */
    List<SysDeptVO> getUserDepts(Long userId);

    /**
     * 更新用户部门绑定
     * @param userId 用户ID
     * @param deptIds 部门ID列表
     */
    void updateUserDepts(Long userId, List<Long> deptIds);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 获取用户绑定的权限组列表
     * @param userId 用户ID
     * @return 权限组列表
     */
    List<SysPermGroupVO> getUserPermGroups(Long userId);

    /**
     * 更新用户权限组绑定
     * @param userId 用户ID
     * @param groupIds 权限组ID列表
     */
    void updateUserPermGroups(Long userId, List<Long> groupIds);

    /**
     * 获取用户独立权限列表
     * @param userId 用户ID
     * @return 独立权限列表
     */
    List<SysUserPermVO> getUserPerms(Long userId);

    /**
     * 添加用户独立权限（增量添加，已存在的不会重复）
     * @param userId 用户ID
     * @param permissions 权限字符串列表
     */
    void updateUserPerms(Long userId, List<String> permissions);

    /**
     * 删除用户独立权限
     * @param userId 用户ID
     * @param permId 权限ID
     */
    void deleteUserPerm(Long userId, Long permId);
}
