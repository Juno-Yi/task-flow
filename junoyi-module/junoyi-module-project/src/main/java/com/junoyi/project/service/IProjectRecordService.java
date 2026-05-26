package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.po.ProjectRecord;
import com.junoyi.project.domain.vo.ProjectRecordVO;

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

    /**
     * 获取项目动态记录列表
     * @param projectNo 项目编号
     * @return 项目动态记录列表
     */
    PageResult<ProjectRecordVO> getProjectRecordList(String projectNo, Page<ProjectRecord> page);
}