package com.junoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.project.domain.po.ProjectRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目动态记录 Mapper
 *
 * @author Fan
 */
@Mapper
public interface ProjectRecordMapper extends BaseMapper<ProjectRecord> {
}
