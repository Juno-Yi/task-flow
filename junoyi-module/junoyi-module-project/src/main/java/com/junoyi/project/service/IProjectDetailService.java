package com.junoyi.project.service;

import com.junoyi.project.domain.vo.ProjectDetailVO;
import com.junoyi.project.domain.vo.ProjectOverviewVO;

/**
 * 项目详情业务接口
 *
 * @author Fan
 */
public interface IProjectDetailService {

    /**
     * 通过项目编号获取项目详情
     * @param projectNo 项目编号
     * @return 项目详情
     */
    ProjectDetailVO getProjectDetailByNo(String projectNo);

    /**
     * 获取项目概览数据
     * @param projectNo 项目编号
     * @return 项目概览数据
     */
    ProjectOverviewVO getProjectOverview(String projectNo);
}