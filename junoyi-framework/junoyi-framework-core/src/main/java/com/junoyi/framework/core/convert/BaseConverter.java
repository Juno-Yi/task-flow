package com.junoyi.framework.core.convert;

import java.util.List;

/**
 * 基础对象转换器接口
 * <p>
 * 定义 DTO/PO/VO 之间的通用转换方法。
 * 该接口仅用于约定转换能力，不依赖任何特定转换框架。
 *
 * @param <D> DTO 类型 (Data Transfer Object - 接收请求参数)
 * @param <E> Entity/PO 类型 (Persistent Object - 数据库实体)
 * @param <V> VO 类型 (View Object - 返回给前端)
 * @author Fan
 */
public interface BaseConverter<D, E, V> {

    E toEntity(D dto);

    V toVo(E entity);

    V dtoToVo(D dto);

    List<E> toEntityList(List<D> dtoList);

    List<V> toVoList(List<E> entityList);

    void updateEntity(D dto, E entity);
}
