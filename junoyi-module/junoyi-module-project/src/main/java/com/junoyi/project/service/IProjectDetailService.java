package com.junoyi.project.service;

import com.junoyi.project.domain.vo.ProjectDetailVO;

/**
 * 项目详情业务接口
 *
 * @author Fan
 */
public interface IProjectDetailService {

    /**
     * 判断用户是否能查看项目详情的权限
     * @param projectNo 项目编号
     * @param userId 用户ID
     * @return 如果有权限返回true，没有权限就返回false
     */
    boolean hasProjectViewDetailPermission(String projectNo, Long userId);

    /**
     * 通过项目编号获取项目详情
     * @param projectNo 项目编号
     * @return 项目详情
     */
    ProjectDetailVO getProjectDetailByNo(String projectNo);
}