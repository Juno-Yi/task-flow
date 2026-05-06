package com.junoyi.framework.core.convert;

import java.util.List;

/**
 * 基础对象转换器接口
 * <p>
 * 定义 DTO/BO/PO/VO 之间的通用转换方法
 * 业务模块的 Converter 继承此接口，由 MapStruct 自动生成实现
 * <p>
 * 使用示例：
 * <pre>
 * &#64;Mapper(componentModel = "spring")
 * public interface SysUserConverter extends BaseConverter&lt;SysUserDTO, SysUser, SysUserVO&gt; {
 *     SysUserConverter INSTANCE = Mappers.getMapper(SysUserConverter.class);
 * }
 * </pre>
 *
 * @param <D> DTO 类型 (Data Transfer Object - 接收请求参数)
 * @param <E> Entity/PO 类型 (Persistent Object - 数据库实体)
 * @param <V> VO 类型 (View Object - 返回给前端)
 * @author Fan
 */
public interface BaseConverter<D, E, V> {

    /**
     * DTO 转 Entity
     *
     * @param dto DTO 对象
     * @return Entity 对象
     */
    E toEntity(D dto);

    /**
     * Entity 转 VO
     *
     * @param entity Entity 对象
     * @return VO 对象
     */
    V toVo(E entity);

    /**
     * DTO 转 VO
     *
     * @param dto DTO 对象
     * @return VO 对象
     */
    V dtoToVo(D dto);

    /**
     * DTO 列表转 Entity 列表
     *
     * @param dtoList DTO 列表
     * @return Entity 列表
     */
    List<E> toEntityList(List<D> dtoList);

    /**
     * Entity 列表转 VO 列表
     *
     * @param entityList Entity 列表
     * @return VO 列表
     */
    List<V> toVoList(List<E> entityList);

    /**
     * 更新 Entity（将 DTO 的非空字段更新到 Entity）
     *
     * @param dto    DTO 对象（源）
     * @param entity Entity 对象（目标）
     */
    void updateEntity(D dto, @org.mapstruct.MappingTarget E entity);
}
