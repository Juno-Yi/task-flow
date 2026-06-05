package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysPermGroupDTO;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.vo.SysPermGroupVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限组转换器（静态工具类）
 * <p>
 * 提供权限组在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）与 PO 之间的转换
 * - 批量转换支持
 * - 部分字段更新支持
 * </p>
 * <p>
 * 权限组包含权限集合（permissions）字段，该字段在数据库中以 JSON 格式存储，
 * 通过 JacksonTypeHandler 自动处理序列化与反序列化。
 * </p>
 *
 * @author Fan
 */
public final class SysPermGroupConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysPermGroupConverter() {
    }

    /**
     * 将权限组实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询权限组信息后返回给前端展示。该方法会复制所有字段，
     * 包括权限组的基础信息、优先级、描述、状态以及权限集合。
     * 时间字段保持原有的 Date 类型不变。
     * </p>
     *
     * @param permGroup 权限组实体对象，包含数据库中的完整权限组信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysPermGroupVO toVo(SysPermGroup permGroup) {
        if (permGroup == null) {
            return null;
        }
        SysPermGroupVO vo = new SysPermGroupVO();
        vo.setId(permGroup.getId());
        vo.setGroupCode(permGroup.getGroupCode());
        vo.setGroupName(permGroup.getGroupName());
        vo.setPriority(permGroup.getPriority());
        vo.setDescription(permGroup.getDescription());
        vo.setStatus(permGroup.getStatus());
        vo.setPermissions(permGroup.getPermissions());
        vo.setCreateTime(permGroup.getCreateTime());
        vo.setUpdateTime(permGroup.getUpdateTime());
        return vo;
    }

    /**
     * 批量将权限组实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于查询权限组列表的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param permGroupList 权限组实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysPermGroupVO> toVoList(List<SysPermGroup> permGroupList) {
        if (permGroupList == null || permGroupList.isEmpty()) {
            return Collections.emptyList();
        }
        return permGroupList.stream().map(SysPermGroupConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 将权限组传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 主要用于接收前端传入的权限组数据，准备保存到数据库时使用。
     * 该方法会复制所有字段，包括权限集合（permissions），该字段在数据库中
     * 以 JSON 格式存储，由 MyBatis-Plus 的 JacksonTypeHandler 自动处理。
     * </p>
     * <p>
     * 注意：此方法不会处理创建时间和更新时间，这些字段通常由框架自动填充。
     * </p>
     *
     * @param dto 权限组传输对象，包含前端传入的权限组信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysPermGroup toPo(SysPermGroupDTO dto) {
        if (dto == null) {
            return null;
        }
        SysPermGroup po = new SysPermGroup();
        po.setId(dto.getId());
        po.setGroupCode(dto.getGroupCode());
        po.setGroupName(dto.getGroupName());
        po.setPriority(dto.getPriority());
        po.setDescription(dto.getDescription());
        po.setStatus(dto.getStatus());
        po.setPermissions(dto.getPermissions());
        return po;
    }

    /**
     * 批量将权限组传输对象列表（DTO）转换为实体对象列表（PO）
     * <p>
     * 适用于批量导入或批量新增权限组的场景，使用 Stream API 进行高效转换。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null。
     * </p>
     *
     * @param dtoList 权限组传输对象列表
     * @return 转换后的实体对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysPermGroup> toPoList(List<SysPermGroupDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(SysPermGroupConverter::toPo).collect(Collectors.toList());
    }

    /**
     * 使用 DTO 中的数据更新现有实体对象
     * <p>
     * 采用选择性更新策略：只有当 DTO 中的字段不为 null 时才会更新实体对象的对应字段。
     * 这种方式适用于部分更新的场景（如 PATCH 请求），避免将未提供的字段覆盖为 null。
     * 对于权限组这种可能包含大量权限配置的對象，选择性更新可以避免意外清空权限集合。
     * </p>
     *
     * @param dto 权限组传输对象，包含需要更新的字段值（非 null 字段才会被更新）
     * @param po  需要更新的实体对象，更新操作会直接修改该对象的状态
     */
    public static void updatePo(SysPermGroupDTO dto, SysPermGroup po) {
        if (dto == null || po == null) {
            return;
        }
        if (dto.getId() != null) po.setId(dto.getId());
        if (dto.getGroupCode() != null) po.setGroupCode(dto.getGroupCode());
        if (dto.getGroupName() != null) po.setGroupName(dto.getGroupName());
        if (dto.getPriority() != null) po.setPriority(dto.getPriority());
        if (dto.getDescription() != null) po.setDescription(dto.getDescription());
        if (dto.getStatus() != null) po.setStatus(dto.getStatus());
        if (dto.getPermissions() != null) po.setPermissions(dto.getPermissions());
    }
}
