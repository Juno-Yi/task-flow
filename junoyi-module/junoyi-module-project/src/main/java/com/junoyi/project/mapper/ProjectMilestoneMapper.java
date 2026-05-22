package com.junoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.project.domain.po.ProjectMilestone;
import com.junoyi.project.domain.vo.ProjectMilestoneVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 项目里程碑 Mapper
 *
 * @author Fan
 */
@Mapper
public interface ProjectMilestoneMapper extends BaseMapper<ProjectMilestone> {

    /**
     * 查询项目里程碑列表
     * @param projectId 项目ID
     * @return 项目里程碑VO列表
     */
    List<ProjectMilestoneVO> selectProjectMilestoneVOList(Long projectId);
}
