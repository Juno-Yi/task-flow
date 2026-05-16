package com.junoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.project.domain.po.Project;
import org.apache.ibatis.annotations.Mapper;


/**
 * 已归档项目 Mapper
 *
 * @author Fan
 */
@Mapper
public interface ProjectArchivedMapper extends BaseMapper<Project> {
}
