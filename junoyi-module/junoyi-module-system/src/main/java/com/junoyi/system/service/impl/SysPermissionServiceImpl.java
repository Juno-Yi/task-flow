package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysPermissionConverter;
import com.junoyi.system.domain.dto.SysPermissionDTO;
import com.junoyi.system.domain.dto.SysPermissionQueryDTO;
import com.junoyi.system.domain.po.SysPermission;
import com.junoyi.system.domain.vo.SysPermissionVO;
import com.junoyi.system.mapper.SysPermissionMapper;
import com.junoyi.system.service.ISysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 权限池服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements ISysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysPermissionConverter sysPermissionConverter;

    /**
     * 获取权限列表（分页）
     * @param queryDTO 查询条件DTO对象，包含权限、描述、状态等查询条件
     * @param page 分页对象，包含分页参数
     * @return PageResult<SysPermissionVO> 分页结果对象，包含权限VO列表、总数、当前页码、每页大小
     */
    @Override
    public PageResult<SysPermissionVO> getPermissionList(SysPermissionQueryDTO queryDTO, Page<SysPermission> page) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO != null) {
            // 构建查询条件：权限名称模糊匹配、描述模糊匹配、状态精确匹配
            wrapper.like(StringUtils.hasText(queryDTO.getPermission()), SysPermission::getPermission, queryDTO.getPermission())
                    .like(StringUtils.hasText(queryDTO.getDescription()), SysPermission::getDescription, queryDTO.getDescription())
                    .eq(queryDTO.getStatus() != null, SysPermission::getStatus, queryDTO.getStatus());
        }
        // 按创建时间降序排列
        wrapper.orderByDesc(SysPermission::getCreateTime);

        Page<SysPermission> resultPage = sysPermissionMapper.selectPage(page, wrapper);
        List<SysPermissionVO> voList = sysPermissionConverter.toVoList(resultPage.getRecords());

        return PageResult.of(voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize());
    }

    /**
     * 获取权限选项列表
     * @return List<SysPermissionVO> 启用状态的权限VO列表，按权限名称升序排列
     */
    @Override
    public List<SysPermissionVO> getPermissionOptions() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        // 查询启用状态的权限，按权限名称升序排列
        wrapper.eq(SysPermission::getStatus, 1)
                .orderByAsc(SysPermission::getPermission);
        List<SysPermission> permissions = sysPermissionMapper.selectList(wrapper);
        return sysPermissionConverter.toVoList(permissions);
    }

    /**
     * 添加权限
     * @param dto 权限DTO对象，包含权限信息
     */
    @Override
    public void addPermission(SysPermissionDTO dto) {
        SysPermission permission = sysPermissionConverter.toEntity(dto);
        // 设置创建人和创建时间
        permission.setCreateBy(SecurityUtils.getUserName());
        permission.setCreateTime(DateUtils.getNowDate());
        sysPermissionMapper.insert(permission);
    }

    /**
     * 删除权限
     * @param id 权限ID
     */
    @Override
    public void deletePermission(Long id) {
        sysPermissionMapper.deleteById(id);
    }

    /**
     * 批量删除权限
     * @param ids 权限ID列表
     */
    @Override
    public void deletePermissionBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        sysPermissionMapper.deleteBatchIds(ids);
    }

    /**
     * 更新权限状态
     * @param id 权限ID
     * @param status 状态值
     */
    @Override
    public void updatePermissionStatus(Long id, Integer status) {
        LambdaUpdateWrapper<SysPermission> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysPermission::getId, id)
                .set(SysPermission::getStatus, status)
                .set(SysPermission::getUpdateBy, SecurityUtils.getUserName())
                .set(SysPermission::getUpdateTime, DateUtils.getNowDate());
        sysPermissionMapper.update(null, wrapper);
    }

}
