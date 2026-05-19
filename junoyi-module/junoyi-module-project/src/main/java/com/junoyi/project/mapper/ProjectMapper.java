package com.junoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.project.domain.po.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目列表 Mapper
 *
 * @author Fan
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
