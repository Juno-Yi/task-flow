package com.junoyi.framework.core.convert;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct 全局配置
 * <p>
 * 业务模块的 Converter 可以引用此配置：
 * <pre>
 * &#64;Mapper(componentModel = "spring", config = MapStructConfig.class)
 * public interface SysUserConverter extends BaseConverter&lt;SysUserDTO, SysUser, SysUserVO&gt; {
 * }
 * </pre>
 *
 * @author Fan
 */
@MapperConfig(
        // 注册为 Spring Bean
        componentModel = "spring",
        // 忽略未映射的目标属性（不报错）
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        // 忽略未映射的源属性
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        // 源属性为 null 时跳过（不覆盖目标属性）
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        // 映射前检查 null
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MapStructConfig {
}
