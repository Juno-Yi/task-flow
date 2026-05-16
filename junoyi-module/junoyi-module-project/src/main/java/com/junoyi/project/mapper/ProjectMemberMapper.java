package com.junoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.project.domain.po.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目成员 Mapper
 *
 * @author Fan
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
}
