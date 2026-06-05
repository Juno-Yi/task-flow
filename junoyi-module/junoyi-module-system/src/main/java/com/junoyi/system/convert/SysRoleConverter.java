package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysRoleDTO;
import com.junoyi.system.domain.po.SysRole;
import com.junoyi.system.domain.vo.SysRoleVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色转换器（静态工具类）
 * <p>
 * 提供系统角色在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）与 PO 之间的转换
 * - 批量转换支持
 * - 部分字段更新支持
 * </p>
 *
 * @author Fan
 */
public final class SysRoleConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysRoleConverter() {
    }

    /**
     * 将角色实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询角色信息后返回给前端展示。该方法会复制所有角色字段，
     * 包括角色名称、标识、排序、数据权限范围、状态以及时间信息。
     * 时间字段保持原有的 Date 类型不变。
     * </p>
     *
     * @param sysRole 角色实体对象，包含数据库中的完整角色信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysRoleVO toVo(SysRole sysRole) {
        if (sysRole == null) {
            return null;
        }
        SysRoleVO vo = new SysRoleVO();
        vo.setId(sysRole.getId());
        vo.setRoleName(sysRole.getRoleName());
        vo.setRoleKey(sysRole.getRoleKey());
        vo.setSort(sysRole.getSort());
        vo.setDataScope(sysRole.getDataScope());
        vo.setStatus(sysRole.getStatus());
        vo.setCreateTime(sysRole.getCreateTime());
        vo.setUpdateTime(sysRole.getUpdateTime());
        vo.setRemark(sysRole.getRemark());
        return vo;
    }

    /**
     * 批量将角色实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于查询角色列表的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param sysRoleList 角色实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysRoleVO> toVoList(List<SysRole> sysRoleList) {
        if (sysRoleList == null || sysRoleList.isEmpty()) {
            return Collections.emptyList();
        }
        return sysRoleList.stream().map(SysRoleConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 将角色传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 主要用于接收前端传入的角色数据，准备保存到数据库时使用。
     * 对于数值型字段（sort、status），采用选择性赋值策略：只有当 DTO 中对应字段不为 null 时才进行赋值，
     * 避免将基本类型字段设置为默认值 0。
     * </p>
     * <p>
     * 注意：此方法不会处理创建时间和更新时间，这些字段通常由框架自动填充；
     * delFlag 字段也未在此设置，使用默认值 false。
     * </p>
     *
     * @param roleDTO 角色传输对象，包含前端传入的角色信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysRole toPo(SysRoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }
        SysRole po = new SysRole();
        po.setId(roleDTO.getId());
        po.setRoleName(roleDTO.getRoleName());
        po.setRoleKey(roleDTO.getRoleKey());

        // 选择性赋值数值型字段，避免 null 转为基本类型的默认值 0
        if (roleDTO.getSort() != null) {
            po.setSort(roleDTO.getSort());
        }
        po.setDataScope(roleDTO.getDataScope());
        if (roleDTO.getStatus() != null) {
            po.setStatus(roleDTO.getStatus());
        }
        po.setRemark(roleDTO.getRemark());
        return po;
    }

    /**
     * 批量将角色传输对象列表（DTO）转换为实体对象列表（PO）
     * <p>
     * 适用于批量导入或批量新增角色的场景，使用 Stream API 进行高效转换。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null。
     * </p>
     *
     * @param dtoList 角色传输对象列表
     * @return 转换后的实体对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysRole> toPoList(List<SysRoleDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(SysRoleConverter::toPo).collect(Collectors.toList());
    }

    /**
     * 使用 DTO 中的数据更新现有实体对象
     * <p>
     * 采用选择性更新策略：只有当 DTO 中的字段不为 null 时才会更新实体对象的对应字段。
     * 这种方式适用于部分更新的场景（如 PATCH 请求），避免将未提供的字段覆盖为 null 或默认值。
     * 对于角色这种重要配置对象，选择性更新可以确保只修改需要变更的字段。
     * </p>
     *
     * @param dto 角色传输对象，包含需要更新的字段值（非 null 字段才会被更新）
     * @param po  需要更新的实体对象，更新操作会直接修改该对象的状态
     */
    public static void updatePo(SysRoleDTO dto, SysRole po) {
        if (dto == null || po == null) {
            return;
        }
        if (dto.getId() != null) po.setId(dto.getId());
        if (dto.getRoleName() != null) po.setRoleName(dto.getRoleName());
        if (dto.getRoleKey() != null) po.setRoleKey(dto.getRoleKey());
        if (dto.getSort() != null) po.setSort(dto.getSort());
        if (dto.getDataScope() != null) po.setDataScope(dto.getDataScope());
        if (dto.getStatus() != null) po.setStatus(dto.getStatus());
        if (dto.getRemark() != null) po.setRemark(dto.getRemark());
    }
}
