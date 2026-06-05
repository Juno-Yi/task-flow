package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysUserDTO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysUserVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户对象转换器（静态工具类）
 * <p>
 * 提供系统用户在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）与 PO、VO 之间的转换
 * - 批量转换支持
 * - 部分字段更新支持
 * </p>
 * <p>
 * 注意：VO 中的敏感字段（邮箱、手机号）带有脱敏注解，但脱敏逻辑由框架层面处理，
 * 转换器仅负责字段的直接复制。密码字段在 toVo 方法中不会被复制，确保密码不会返回给前端。
 * </p>
 *
 * @author Fan
 */
public final class SysUserConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysUserConverter() {
    }

    /**
     * 将用户实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询用户信息后返回给前端展示。该方法会复制用户的基本信息字段，
     * 但不包含密码、盐值等敏感字段，确保这些敏感信息不会泄露到前端。
     * 时间字段保持原有的 Date 类型不变。
     * </p>
     * <p>
     * 注意：VO 中的 email 和 phonenumber 字段带有脱敏注解，实际脱敏由框架的 AOP 或序列化层处理。
     * </p>
     *
     * @param entity 用户实体对象，包含数据库中的完整用户信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysUserVO toVo(SysUser entity) {
        if (entity == null) {
            return null;
        }
        SysUserVO vo = new SysUserVO();
        vo.setUserId(entity.getUserId());
        vo.setUserName(entity.getUserName());
        vo.setNickName(entity.getNickName());
        vo.setAvatar(entity.getAvatar());
        vo.setEmail(entity.getEmail());
        vo.setPhonenumber(entity.getPhonenumber());
        vo.setSex(entity.getSex());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /**
     * 将用户传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 主要用于接收前端传入的用户数据，准备保存到数据库时使用。该方法会：
     * - 复制所有用户提供的基本信息字段
     * - 包含密码字段，用于新用户注册或密码修改场景
     * - 对于数值型字段（status），采用选择性赋值策略，避免 null 转为基本类型的默认值 0
     * </p>
     * <p>
     * 注意：此方法不会处理创建时间和更新时间，这些字段通常由框架自动填充；
     * salt、pwdUpdateTime、delFlag 等字段也未在此设置，由其他业务逻辑处理。
     * </p>
     *
     * @param dto 用户传输对象，包含前端传入的用户信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysUser toEntity(SysUserDTO dto) {
        if (dto == null) {
            return null;
        }
        SysUser entity = new SysUser();

        // 选择性赋值 ID 字段，仅在非空时设置
        if (dto.getId() != null) {
            entity.setUserId(dto.getId());
        }
        entity.setUserName(dto.getUserName());
        entity.setPassword(dto.getPassword());
        entity.setNickName(dto.getNickName());
        entity.setPhonenumber(dto.getPhonenumber());
        entity.setEmail(dto.getEmail());
        entity.setSex(dto.getSex());

        // 选择性赋值数值型字段，避免 null 转为基本类型的默认值 0
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        entity.setRemark(dto.getRemark());
        return entity;
    }

    /**
     * 将用户传输对象（DTO）直接转换为视图对象（VO）
     * <p>
     * 主要用于在不涉及数据库操作的场景下，进行对象格式的转换，
     * 例如在业务逻辑层处理用户数据后直接返回给前端。
     * 此方法不包含密码等敏感字段，确保数据安全。
     * </p>
     *
     * @param dto 用户传输对象
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysUserVO dtoToVo(SysUserDTO dto) {
        if (dto == null) {
            return null;
        }
        SysUserVO vo = new SysUserVO();
        vo.setUserId(dto.getId());
        vo.setUserName(dto.getUserName());
        vo.setNickName(dto.getNickName());
        vo.setPhonenumber(dto.getPhonenumber());
        vo.setEmail(dto.getEmail());
        vo.setSex(dto.getSex());
        vo.setStatus(dto.getStatus());
        return vo;
    }

    /**
     * 批量将用户实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于查询用户列表的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param entityList 用户实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysUserVO> toVoList(List<SysUser> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream().map(SysUserConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 批量将用户传输对象列表（DTO）转换为实体对象列表（PO）
     * <p>
     * 适用于批量导入或批量新增用户的场景，使用 Stream API 进行高效转换。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null。
     * </p>
     *
     * @param dtoList 用户传输对象列表
     * @return 转换后的实体对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysUser> toEntityList(List<SysUserDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(SysUserConverter::toEntity).collect(Collectors.toList());
    }

    /**
     * 使用 DTO 中的数据更新现有实体对象
     * <p>
     * 采用选择性更新策略：只有当 DTO 中的字段不为 null 时才会更新实体对象的对应字段。
     * 这种方式适用于部分更新的场景（如 PATCH 请求），避免将未提供的字段覆盖为 null 或默认值。
     * 对于用户这种包含敏感信息的对象，选择性更新尤为重要，可以只修改需要变更的字段，
     * 避免意外清空密码或其他重要信息。
     * </p>
     *
     * @param dto    用户传输对象，包含需要更新的字段值（非 null 字段才会被更新）
     * @param entity 需要更新的实体对象，更新操作会直接修改该对象的状态
     */
    public static void updateEntity(SysUserDTO dto, SysUser entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getId() != null) entity.setUserId(dto.getId());
        if (dto.getUserName() != null) entity.setUserName(dto.getUserName());
        if (dto.getPassword() != null) entity.setPassword(dto.getPassword());
        if (dto.getNickName() != null) entity.setNickName(dto.getNickName());
        if (dto.getPhonenumber() != null) entity.setPhonenumber(dto.getPhonenumber());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getSex() != null) entity.setSex(dto.getSex());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getRemark() != null) entity.setRemark(dto.getRemark());
    }
}
