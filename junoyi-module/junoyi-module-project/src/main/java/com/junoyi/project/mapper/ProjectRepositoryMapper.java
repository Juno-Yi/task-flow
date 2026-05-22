package com.junoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.project.domain.po.ProjectRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目仓库 Mapper
 *
 * @author Fan
 */
@Mapper
public interface ProjectRepositoryMapper extends BaseMapper<ProjectRepository> {
}
