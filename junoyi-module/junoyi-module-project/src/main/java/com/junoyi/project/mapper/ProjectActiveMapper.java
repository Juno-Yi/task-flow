package com.junoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.project.domain.po.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活跃项目 Mapper
 *
 * @author Fan
 */
@Mapper
public interface ProjectActiveMapper extends BaseMapper<Project> {
}
