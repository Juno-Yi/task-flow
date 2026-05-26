package com.junoyi.project.service;

import com.junoyi.project.domain.po.ProjectRecord;

/**
 * 项目动态记录业务接口
 *
 * @author Fan
 */
public interface IProjectRecordService {

    /**
     * 添加项目动态记录
     * @param projectRecord 项目动态记录
     */
    void addProjectRecord(ProjectRecord projectRecord);
}