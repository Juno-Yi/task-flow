package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysConfigDTO;
import com.junoyi.system.domain.po.SysConfig;
import com.junoyi.system.domain.vo.SysConfigVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统参数转换器（静态工具类）
 * <p>
 * 提供系统参数配置在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）与 PO、VO 之间的转换
 * - 批量转换支持
 * - 部分字段更新支持
 * </p>
 * <p>
 * 特别注意：isSystem 字段在不同层级的表示形式不同：
 * - PO 层：Integer 类型（0-否，1-是）
 * - DTO/VO 层：String 类型（"N"-否，"Y"-是）
 * 转换器会自动处理这种格式转换。
 * </p>
 *
 * @author Fan
 */
public final class SysConfigConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysConfigConverter() {
    }

    /**
     * 将参数配置实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询参数配置后返回给前端展示。该方法会：
     * - 复制所有基本字段
     * - 将 isSystem 字段从 Integer 类型（0/1）转换为 String 类型（"N"/"Y"）
     * - 将 Date 类型的时间字段转换为 LocalDateTime 类型
     * </p>
     *
     * @param entity 参数配置实体对象，包含数据库中的完整参数信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysConfigVO toVo(SysConfig entity) {
        if (entity == null) {
            return null;
        }
        SysConfigVO vo = new SysConfigVO();
        vo.setConfigId(entity.getConfigId());
        vo.setConfigKey(entity.getConfigKey());
        vo.setConfigValue(entity.getConfigValue());
        vo.setConfigName(entity.getConfigName());
        vo.setConfigType(entity.getConfigType());
        vo.setSort(entity.getSort());

        // 将 isSystem 从 Integer(0/1) 转换为 String("N"/"Y")
        vo.setIsSystem(entity.getIsSystem() != null && entity.getIsSystem() == 1 ? "Y" : "N");
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());

        // 将 Date 类型的时间戳转换为 LocalDateTime 类型，适配 VO 层的时间格式要求
        vo.setCreateTime(entity.getCreateTime() != null
                ? new java.sql.Timestamp(entity.getCreateTime().getTime()).toLocalDateTime()
                : null);
        vo.setUpdateTime(entity.getUpdateTime() != null
                ? new java.sql.Timestamp(entity.getUpdateTime().getTime()).toLocalDateTime()
                : null);
        return vo;
    }

    /**
     * 将参数配置传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 主要用于接收前端传入的参数配置数据，准备保存到数据库时使用。该方法会：
     * - 复制所有基本字段
     * - 将 isSystem 字段从 String 类型（"Y"/"N"）转换为 Integer 类型（1/0）
     * - 时间字段由框架自动填充，此处不处理
     * </p>
     *
     * @param dto 参数配置传输对象，包含前端传入的字段信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysConfig toEntity(SysConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        SysConfig entity = new SysConfig();
        entity.setConfigId(dto.getConfigId());
        entity.setConfigKey(dto.getConfigKey());
        entity.setConfigValue(dto.getConfigValue());
        entity.setConfigName(dto.getConfigName());
        entity.setConfigType(dto.getConfigType());
        entity.setSort(dto.getSort());

        // 将 isSystem 从 String("Y"/"N") 转换为 Integer(1/0)
        entity.setIsSystem("Y".equals(dto.getIsSystem()) ? 1 : 0);
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
        return entity;
    }

    /**
     * 将参数配置传输对象（DTO）直接转换为视图对象（VO）
     * <p>
     * 主要用于在不涉及数据库操作的场景下，进行对象格式的转换，
     * 例如在业务逻辑层处理后直接返回给前端。此方法直接复制字段，
     * isSystem 字段保持原有的 String 类型不变。
     * </p>
     *
     * @param dto 参数配置传输对象
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysConfigVO dtoToVo(SysConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        SysConfigVO vo = new SysConfigVO();
        vo.setConfigId(dto.getConfigId());
        vo.setConfigKey(dto.getConfigKey());
        vo.setConfigValue(dto.getConfigValue());
        vo.setConfigName(dto.getConfigName());
        vo.setConfigType(dto.getConfigType());
        vo.setSort(dto.getSort());
        vo.setIsSystem(dto.getIsSystem());
        vo.setStatus(dto.getStatus());
        vo.setRemark(dto.getRemark());
        return vo;
    }

    /**
     * 批量将参数配置实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于分页查询或列表查询的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param entityList 参数配置实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysConfigVO> toVoList(List<SysConfig> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream()
                .map(SysConfigConverter::toVo)
                .collect(Collectors.toList());
    }

    /**
     * 批量将参数配置传输对象列表（DTO）转换为实体对象列表（PO）
     * <p>
     * 适用于批量导入或批量新增的场景，使用 Stream API 进行高效转换。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null。
     * </p>
     *
     * @param dtoList 参数配置传输对象列表
     * @return 转换后的实体对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysConfig> toEntityList(List<SysConfigDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .map(SysConfigConverter::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 使用 DTO 中的数据更新现有实体对象
     * <p>
     * 采用选择性更新策略：只有当 DTO 中的字段不为 null 时才会更新实体对象的对应字段。
     * 这种方式适用于部分更新的场景（如 PATCH 请求），避免将未提供的字段覆盖为 null 或默认值。
     * 对于 isSystem 字段，会将其从 String 类型（"Y"/"N"）转换为 Integer 类型（1/0）。
     * </p>
     *
     * @param dto    参数配置传输对象，包含需要更新的字段值（非 null 字段才会被更新）
     * @param entity 需要更新的实体对象，更新操作会直接修改该对象的状态
     */
    public static void updateEntity(SysConfigDTO dto, SysConfig entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getConfigId() != null) {
            entity.setConfigId(dto.getConfigId());
        }
        if (dto.getConfigKey() != null) {
            entity.setConfigKey(dto.getConfigKey());
        }
        if (dto.getConfigValue() != null) {
            entity.setConfigValue(dto.getConfigValue());
        }
        if (dto.getConfigName() != null) {
            entity.setConfigName(dto.getConfigName());
        }
        if (dto.getConfigType() != null) {
            entity.setConfigType(dto.getConfigType());
        }
        if (dto.getSort() != null) {
            entity.setSort(dto.getSort());
        }

        // 将 isSystem 从 String("Y"/"N") 转换为 Integer(1/0)
        if (dto.getIsSystem() != null) {
            entity.setIsSystem("Y".equals(dto.getIsSystem()) ? 1 : 0);
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            entity.setRemark(dto.getRemark());
        }
    }
}
