package com.junoyi.project.service;

import com.junoyi.project.domain.vo.ProjectDetailVO;

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
}