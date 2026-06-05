package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysDictDataDTO;
import com.junoyi.system.domain.po.SysDictData;
import com.junoyi.system.domain.vo.SysDictDataVO;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典数据转换器（静态工具类）
 * <p>
 * 提供字典数据在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）与 PO、VO 之间的转换
 * - 批量转换支持
 * </p>
 *
 * @author Fan
 */
public final class SysDictDataConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysDictDataConverter() {
    }

    /**
     * 将字典数据实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询后返回给前端展示，包含时间戳到 LocalDateTime 的转换。
     * </p>
     *
     * @param entity 字典数据实体对象，包含数据库中的完整字段信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysDictDataVO toVo(SysDictData entity) {
        if (entity == null) {
            return null;
        }
        SysDictDataVO vo = new SysDictDataVO();
        vo.setDictCode(entity.getDictCode());
        vo.setDictSort(entity.getDictSort());
        vo.setDictLabel(entity.getDictLabel());
        vo.setDictValue(entity.getDictValue());
        vo.setDictType(entity.getDictType());
        vo.setCssClass(entity.getCssClass());
        vo.setListClass(entity.getListClass());
        vo.setIsDefault(entity.getIsDefault());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());

        // 将 Date 类型的时间戳转换为 LocalDateTime 类型，适配 VO 层的时间格式要求
        vo.setCreateTime(entity.getCreateTime() != null ? new Timestamp(entity.getCreateTime().getTime()).toLocalDateTime() : null);
        vo.setUpdateTime(entity.getUpdateTime() != null ? new Timestamp(entity.getUpdateTime().getTime()).toLocalDateTime() : null);
        return vo;
    }

    /**
     * 将字典数据传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 主要用于接收前端传入的数据，准备保存到数据库时使用。
     * 注意：此方法不会处理创建时间和更新时间，这些字段通常由框架自动填充。
     * </p>
     *
     * @param dto 字典数据传输对象，包含前端传入的字段信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysDictData toEntity(SysDictDataDTO dto) {
        if (dto == null) {
            return null;
        }
        SysDictData entity = new SysDictData();
        entity.setDictCode(dto.getDictCode());
        entity.setDictSort(dto.getDictSort());
        entity.setDictLabel(dto.getDictLabel());
        entity.setDictValue(dto.getDictValue());
        entity.setDictType(dto.getDictType());
        entity.setCssClass(dto.getCssClass());
        entity.setListClass(dto.getListClass());
        entity.setIsDefault(dto.getIsDefault());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
        return entity;
    }

    /**
     * 将字典数据传输对象（DTO）直接转换为视图对象（VO）
     * <p>
     * 主要用于在不涉及数据库操作的场景下，进行对象格式的转换，
     * 例如在业务逻辑层处理后直接返回给前端。
     * </p>
     *
     * @param dto 字典数据传输对象
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysDictDataVO dtoToVo(SysDictDataDTO dto) {
        if (dto == null) {
            return null;
        }
        SysDictDataVO vo = new SysDictDataVO();
        vo.setDictCode(dto.getDictCode());
        vo.setDictSort(dto.getDictSort());
        vo.setDictLabel(dto.getDictLabel());
        vo.setDictValue(dto.getDictValue());
        vo.setDictType(dto.getDictType());
        vo.setCssClass(dto.getCssClass());
        vo.setListClass(dto.getListClass());
        vo.setIsDefault(dto.getIsDefault());
        vo.setStatus(dto.getStatus());
        vo.setRemark(dto.getRemark());
        return vo;
    }

    /**
     * 批量将字典数据实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于分页查询或列表查询的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param entityList 字典数据实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysDictDataVO> toVoList(List<SysDictData> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream().map(SysDictDataConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 批量将字典数据传输对象列表（DTO）转换为实体对象列表（PO）
     * <p>
     * 适用于批量导入或批量新增的场景，使用 Stream API 进行高效转换。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null。
     * </p>
     *
     * @param dtoList 字典数据传输对象列表
     * @return 转换后的实体对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysDictData> toEntityList(List<SysDictDataDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(SysDictDataConverter::toEntity).collect(Collectors.toList());
    }

    /**
     * 使用 DTO 中的数据更新现有实体对象
     * <p>
     * 采用选择性更新策略：只有当 DTO 中的字段不为 null 时才会更新实体对象的对应字段。
     * 这种方式适用于部分更新的场景（如 PATCH 请求），避免将未提供的字段覆盖为 null。
     * </p>
     *
     * @param dto    字典数据传输对象，包含需要更新的字段值（非 null 字段才会被更新）
     * @param entity 需要更新的实体对象，更新操作会直接修改该对象的状态
     */
    public static void updateEntity(SysDictDataDTO dto, SysDictData entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getDictCode() != null) entity.setDictCode(dto.getDictCode());
        if (dto.getDictSort() != null) entity.setDictSort(dto.getDictSort());
        if (dto.getDictLabel() != null) entity.setDictLabel(dto.getDictLabel());
        if (dto.getDictValue() != null) entity.setDictValue(dto.getDictValue());
        if (dto.getDictType() != null) entity.setDictType(dto.getDictType());
        if (dto.getCssClass() != null) entity.setCssClass(dto.getCssClass());
        if (dto.getListClass() != null) entity.setListClass(dto.getListClass());
        if (dto.getIsDefault() != null) entity.setIsDefault(dto.getIsDefault());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getRemark() != null) entity.setRemark(dto.getRemark());
    }
}
