package com.junoyi.project.service;

import com.junoyi.project.domain.dto.ProjectGanttQueryDTO;
import com.junoyi.project.domain.vo.ProjectGanttVO;

import java.util.List;

/**
 * 项目日程业务接口
 *
 * @author Fan
 */
public interface IProjectScheduleService {

    /**
     * 获取活跃的项目甘特图列表
     * @param queryDTO 查询参数
     * @return 返回甘特图列表
     */
    List<ProjectGanttVO> getActiveProjectGantList(ProjectGanttQueryDTO queryDTO);
}
