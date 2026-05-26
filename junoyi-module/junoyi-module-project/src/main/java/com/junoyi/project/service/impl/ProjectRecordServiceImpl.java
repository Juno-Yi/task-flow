package com.junoyi.project.service.impl;

import com.junoyi.project.domain.po.ProjectRecord;
import com.junoyi.project.mapper.ProjectRecordMapper;
import com.junoyi.project.service.IProjectRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 项目动态记录业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectRecordServiceImpl implements IProjectRecordService {

    private final ProjectRecordMapper projectRecordMapper;

    /**
     * 添加项目动态记录
     * @param projectRecord 项目动态记录
     */
    @Override
    public void addProjectRecord(ProjectRecord projectRecord) {
        projectRecordMapper.insert(projectRecord);
    }
}