package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysDictTypeDTO;
import com.junoyi.system.domain.po.SysDictType;
import com.junoyi.system.domain.vo.SysDictTypeVO;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典类型转换器（静态工具类）
 * <p>
 * 提供字典类型在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）与 PO、VO 之间的转换
 * - 批量转换支持
 * - 部分字段更新支持
 * </p>
 *
 * @author Fan
 */
public final class SysDictTypeConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysDictTypeConverter() {
    }

    /**
     * 将字典类型实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询字典类型后返回给前端展示。该方法会：
     * - 复制所有基本字段
     * - 将 Date 类型的时间字段转换为 LocalDateTime 类型，适配 VO 层的格式要求
     * </p>
     *
     * @param entity 字典类型实体对象，包含数据库中的完整字典类型信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysDictTypeVO toVo(SysDictType entity) {
        if (entity == null) {
            return null;
        }
        SysDictTypeVO vo = new SysDictTypeVO();
        vo.setDictId(entity.getDictId());
        vo.setDictName(entity.getDictName());
        vo.setDictType(entity.getDictType());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());

        // 将 Date 类型的时间戳转换为 LocalDateTime 类型，适配 VO 层的时间格式要求
        vo.setCreateTime(entity.getCreateTime() != null ? new Timestamp(entity.getCreateTime().getTime()).toLocalDateTime() : null);
        vo.setUpdateTime(entity.getUpdateTime() != null ? new Timestamp(entity.getUpdateTime().getTime()).toLocalDateTime() : null);
        return vo;
    }

    /**
     * 将字典类型传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 主要用于接收前端传入的字典类型数据，准备保存到数据库时使用。
     * 注意：此方法不会处理创建时间和更新时间，这些字段通常由框架自动填充。
     * </p>
     *
     * @param dto 字典类型传输对象，包含前端传入的字段信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysDictType toEntity(SysDictTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        SysDictType entity = new SysDictType();
        entity.setDictId(dto.getDictId());
        entity.setDictName(dto.getDictName());
        entity.setDictType(dto.getDictType());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
        return entity;
    }

    /**
     * 将字典类型传输对象（DTO）直接转换为视图对象（VO）
     * <p>
     * 主要用于在不涉及数据库操作的场景下，进行对象格式的转换，
     * 例如在业务逻辑层处理后直接返回给前端。
     * </p>
     *
     * @param dto 字典类型传输对象
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysDictTypeVO dtoToVo(SysDictTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        SysDictTypeVO vo = new SysDictTypeVO();
        vo.setDictId(dto.getDictId());
        vo.setDictName(dto.getDictName());
        vo.setDictType(dto.getDictType());
        vo.setStatus(dto.getStatus());
        vo.setRemark(dto.getRemark());
        return vo;
    }

    /**
     * 批量将字典类型实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于分页查询或列表查询的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param entityList 字典类型实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysDictTypeVO> toVoList(List<SysDictType> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream().map(SysDictTypeConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 批量将字典类型传输对象列表（DTO）转换为实体对象列表（PO）
     * <p>
     * 适用于批量导入或批量新增的场景，使用 Stream API 进行高效转换。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null。
     * </p>
     *
     * @param dtoList 字典类型传输对象列表
     * @return 转换后的实体对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysDictType> toEntityList(List<SysDictTypeDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(SysDictTypeConverter::toEntity).collect(Collectors.toList());
    }

    /**
     * 使用 DTO 中的数据更新现有实体对象
     * <p>
     * 采用选择性更新策略：只有当 DTO 中的字段不为 null 时才会更新实体对象的对应字段。
     * 这种方式适用于部分更新的场景（如 PATCH 请求），避免将未提供的字段覆盖为 null。
     * </p>
     *
     * @param dto    字典类型传输对象，包含需要更新的字段值（非 null 字段才会被更新）
     * @param entity 需要更新的实体对象，更新操作会直接修改该对象的状态
     */
    public static void updateEntity(SysDictTypeDTO dto, SysDictType entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getDictId() != null) entity.setDictId(dto.getDictId());
        if (dto.getDictName() != null) entity.setDictName(dto.getDictName());
        if (dto.getDictType() != null) entity.setDictType(dto.getDictType());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getRemark() != null) entity.setRemark(dto.getRemark());
    }
}
