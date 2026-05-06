package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.json.utils.JsonUtils;
import com.junoyi.system.event.UserOperationEvent;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysPermGroupConverter;
import com.junoyi.system.domain.dto.SysPermGroupDTO;
import com.junoyi.system.domain.dto.SysPermGroupQueryDTO;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.event.PermissionChangedEvent;
import com.junoyi.system.exception.SystemPermGroupProtectedException;
import com.junoyi.system.mapper.SysPermGroupMapper;
import com.junoyi.system.service.ISysPermGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 权限组服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysPermGroupServiceImpl implements ISysPermGroupService {

    private final SysPermGroupMapper sysPermGroupMapper;
    private final SysPermGroupConverter sysPermGroupConverter;

    /**
     * 获取权限组列表
     * @param queryDTO 查询条件DTO对象，包含权限组编码、名称、状态等查询条件
     * @param page 分页对象，包含分页参数
     * @return PageResult<SysPermGroupVO> 分页结果对象，包含权限组VO列表、总数、当前页码、每页大小
     */
    @Override
    public PageResult<SysPermGroupVO> getPermGroupList(SysPermGroupQueryDTO queryDTO, Page<SysPermGroup> page) {
        // 构建查询条件包装器
        LambdaQueryWrapper<SysPermGroup> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO != null) {
            // 根据查询条件构建动态SQL
            wrapper.like(StringUtils.hasText(queryDTO.getGroupCode()), SysPermGroup::getGroupCode, queryDTO.getGroupCode())
                    .like(StringUtils.hasText(queryDTO.getGroupName()), SysPermGroup::getGroupName, queryDTO.getGroupName())
                    .eq(queryDTO.getStatus() != null, SysPermGroup::getStatus, queryDTO.getStatus());
        }
        // 按优先级升序排序
        wrapper.orderByAsc(SysPermGroup::getPriority);

        Page<SysPermGroup> resultPage = sysPermGroupMapper.selectPage(page, wrapper);
        List<SysPermGroupVO> voList = sysPermGroupConverter.toVoList(resultPage.getRecords());
        
        return PageResult.of(voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize());
    }

    /**
     * 获取权限组下拉列表（启用状态）
     * @return 权限组VO列表
     */
    @Override
    public List<SysPermGroupVO> getPermGroupOptions() {
        LambdaQueryWrapper<SysPermGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermGroup::getStatus, 1)
                .orderByAsc(SysPermGroup::getPriority);
        List<SysPermGroup> permGroups = sysPermGroupMapper.selectList(wrapper);
        return sysPermGroupConverter.toVoList(permGroups);
    }

    /**
     * 新增权限组
     * @param dto 权限组DTO对象，包含权限组的基本信息
     */
    @Override
    public void addPermGroup(SysPermGroupDTO dto) {
        // DTO转换为PO对象
        SysPermGroup permGroup = sysPermGroupConverter.toPo(dto);
        // 设置创建人和创建时间
        permGroup.setCreateBy(SecurityUtils.getUserName());
        permGroup.setCreateTime(DateUtils.getNowDate());
        sysPermGroupMapper.insert(permGroup);

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("create", "perm_group",
                "创建了权限组「" + permGroup.getGroupName() + "」",
                String.valueOf(permGroup.getId()), permGroup.getGroupName(),
                JsonUtils.toJsonString(dto)));
    }

    /**
     * 更新权限组
     * @param dto 权限组DTO对象
     */
    @Override
    public void updatePermGroup(SysPermGroupDTO dto) {
        SysPermGroup permGroup = sysPermGroupConverter.toPo(dto);
        permGroup.setUpdateBy(SecurityUtils.getUserName());
        permGroup.setUpdateTime(DateUtils.getNowDate());
        sysPermGroupMapper.updateById(permGroup);
        
        // 发布权限变更事件，异步同步受影响用户的会话
        EventBus.get().callEvent(new PermissionChangedEvent(
                PermissionChangedEvent.ChangeType.PERM_GROUP_UPDATE, 
                dto.getId()
        ));

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("update", "perm_group",
                "更新了权限组「" + dto.getGroupName() + "」",
                String.valueOf(dto.getId()), dto.getGroupName(),
                JsonUtils.toJsonString(dto)));
    }

    /**
     * 删除权限组
     * @param id 权限组ID
     */
    @Override
    public void deletePermGroup(Long id) {
        // 检查是否为系统内置权限组（ID为1、2的权限组不允许删除）
        if (id != null && (id == 1L || id == 2L)) {
            throw new SystemPermGroupProtectedException(id);
        }
        // 物理删除
        sysPermGroupMapper.deleteById(id);
        
        // 发布权限变更事件
        EventBus.get().callEvent(new PermissionChangedEvent(
                PermissionChangedEvent.ChangeType.PERM_GROUP_DELETE, 
                id
        ));

        // 发布操作日志事件
        SysPermGroup group = sysPermGroupMapper.selectById(id);
        String groupName = group != null ? group.getGroupName() : String.valueOf(id);
        EventBus.get().callEvent(UserOperationEvent.of("delete", "perm_group",
                "删除了权限组「" + groupName + "」",
                String.valueOf(id), groupName));
    }

    /**
     * 批量删除权限组
     * @param ids 权限组ID列表
     */
    @Override
    public void deletePermGroupBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 检查是否包含系统内置权限组
        for (Long id : ids) {
            if (id != null && (id == 1L || id == 2L)) {
                throw new SystemPermGroupProtectedException(id);
            }
        }

        // 批量物理删除
        sysPermGroupMapper.deleteBatchIds(ids);
        
        // 发布权限变更事件
        for (Long id : ids) {
            EventBus.get().callEvent(new PermissionChangedEvent(
                    PermissionChangedEvent.ChangeType.PERM_GROUP_DELETE, 
                    id
            ));
        }

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of("delete", "perm_group",
                "批量删除了 " + ids.size() + " 个权限组",
                ids.toString(), null));
    }

}
