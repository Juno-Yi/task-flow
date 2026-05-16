package com.junoyi.project.service.impl;

import com.junoyi.project.mapper.ProjectListMapper;
import com.junoyi.project.service.IProjectListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 项目管理业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectListServiceImpl implements IProjectListService {

    private final ProjectListMapper projectListMapper;
}