package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysDeptDTO;
import com.junoyi.system.domain.po.SysDept;
import com.junoyi.system.domain.vo.SysDeptVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统部门转换器（静态工具类）
 * <p>
 * 提供系统部门在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）到 PO 的转换
 * - 批量转换支持
 * </p>
 *
 * @author Fan
 */
public final class SysDeptConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysDeptConverter() {
    }

    /**
     * 将部门实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询部门信息后返回给前端展示。
     * 该方法会复制所有基本字段，时间字段保持原有类型（Date）。
     * </p>
     *
     * @param sysDept 部门实体对象，包含数据库中的完整部门信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysDeptVO toVo(SysDept sysDept) {
        if (sysDept == null) {
            return null;
        }
        SysDeptVO vo = new SysDeptVO();
        vo.setId(sysDept.getId());
        vo.setParentId(sysDept.getParentId());
        vo.setName(sysDept.getName());
        vo.setSort(sysDept.getSort());
        vo.setLeader(sysDept.getLeader());
        vo.setPhonenumber(sysDept.getPhonenumber());
        vo.setEmail(sysDept.getEmail());
        vo.setStatus(sysDept.getStatus());
        vo.setCreateTime(sysDept.getCreateTime());
        vo.setUpdateTime(sysDept.getUpdateTime());
        vo.setRemark(sysDept.getRemark());
        return vo;
    }

    /**
     * 批量将部门实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于查询部门列表的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param sysDeptList 部门实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysDeptVO> toVoList(List<SysDept> sysDeptList) {
        if (sysDeptList == null || sysDeptList.isEmpty()) {
            return Collections.emptyList();
        }
        return sysDeptList.stream()
                .map(SysDeptConverter::toVo)
                .collect(Collectors.toList());
    }

    /**
     * 将部门传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 采用选择性赋值策略：对于可能为 null 的数值型字段（如 id、parentId、sort、status），
     * 只有当 DTO 中对应字段不为 null 时才进行赋值，避免将基本类型字段设置为默认值 0。
     * 对于字符串类型字段，直接赋值（允许为 null）。
     * </p>
     * <p>
     * 注意：delFlag 字段未在此方法中设置，通常由框架自动处理或使用默认值。
     * </p>
     *
     * @param dto 部门传输对象，包含前端传入或业务层处理的部门信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysDept toPo(SysDeptDTO dto) {
        if (dto == null) {
            return null;
        }
        SysDept entity = new SysDept();

        // 选择性赋值数值型字段，避免 null 转为基本类型的默认值 0
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        if (dto.getParentId() != null) {
            entity.setParentId(dto.getParentId());
        }
        entity.setName(dto.getName());
        if (dto.getSort() != null) {
            entity.setSort(dto.getSort());
        }
        entity.setLeader(dto.getLeader());
        entity.setPhonenumber(dto.getPhonenumber());
        entity.setEmail(dto.getEmail());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        entity.setRemark(dto.getRemark());
        return entity;
    }
}
