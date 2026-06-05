package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysPermissionDTO;
import com.junoyi.system.domain.po.SysPermission;
import com.junoyi.system.domain.vo.SysPermissionVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限池对象转换器（静态工具类）
 * <p>
 * 提供权限池在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）与 PO、VO 之间的转换
 * - 批量转换支持
 * - 部分字段更新支持
 * </p>
 * <p>
 * 权限池用于管理系统中的所有权限标识，每个权限包含唯一的权限标识、
 * 描述信息和状态控制，是权限管理体系的核心数据实体。
 * </p>
 *
 * @author Fan
 */
public final class SysPermissionConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysPermissionConverter() {
    }

    /**
     * 将权限实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询权限信息后返回给前端展示。该方法会复制所有权限字段，
     * 包括权限标识、描述、状态以及时间信息。时间字段保持原有的 Date 类型不变。
     * </p>
     *
     * @param entity 权限实体对象，包含数据库中的完整权限信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysPermissionVO toVo(SysPermission entity) {
        if (entity == null) {
            return null;
        }
        SysPermissionVO vo = new SysPermissionVO();
        vo.setId(entity.getId());
        vo.setPermission(entity.getPermission());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /**
     * 将权限传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 主要用于接收前端传入的权限数据，准备保存到数据库时使用。
     * 该方法会复制权限的核心字段：权限标识、描述和状态。
     * </p>
     * <p>
     * 注意：此方法不会处理创建时间和更新时间，这些字段通常由框架自动填充。
     * </p>
     *
     * @param dto 权限传输对象，包含前端传入的权限信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysPermission toEntity(SysPermissionDTO dto) {
        if (dto == null) {
            return null;
        }
        SysPermission entity = new SysPermission();
        entity.setId(dto.getId());
        entity.setPermission(dto.getPermission());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    /**
     * 将权限传输对象（DTO）直接转换为视图对象（VO）
     * <p>
     * 主要用于在不涉及数据库操作的场景下，进行对象格式的转换，
     * 例如在业务逻辑层处理权限数据后直接返回给前端。
     * </p>
     *
     * @param dto 权限传输对象
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysPermissionVO dtoToVo(SysPermissionDTO dto) {
        if (dto == null) {
            return null;
        }
        SysPermissionVO vo = new SysPermissionVO();
        vo.setId(dto.getId());
        vo.setPermission(dto.getPermission());
        vo.setDescription(dto.getDescription());
        vo.setStatus(dto.getStatus());
        return vo;
    }

    /**
     * 批量将权限实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于查询权限列表的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param entityList 权限实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysPermissionVO> toVoList(List<SysPermission> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream().map(SysPermissionConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 批量将权限传输对象列表（DTO）转换为实体对象列表（PO）
     * <p>
     * 适用于批量导入或批量新增权限的场景，使用 Stream API 进行高效转换。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null。
     * </p>
     *
     * @param dtoList 权限传输对象列表
     * @return 转换后的实体对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysPermission> toEntityList(List<SysPermissionDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(SysPermissionConverter::toEntity).collect(Collectors.toList());
    }

    /**
     * 使用 DTO 中的数据更新现有实体对象
     * <p>
     * 采用选择性更新策略：只有当 DTO 中的字段不为 null 时才会更新实体对象的对应字段。
     * 这种方式适用于部分更新的场景（如 PATCH 请求），避免将未提供的字段覆盖为 null。
     * </p>
     *
     * @param dto    权限传输对象，包含需要更新的字段值（非 null 字段才会被更新）
     * @param entity 需要更新的实体对象，更新操作会直接修改该对象的状态
     */
    public static void updateEntity(SysPermissionDTO dto, SysPermission entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getId() != null) entity.setId(dto.getId());
        if (dto.getPermission() != null) entity.setPermission(dto.getPermission());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
    }
}
