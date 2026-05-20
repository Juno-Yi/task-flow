package com.junoyi.project.service.impl;

import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 项目成员业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements IProjectMemberService {

    private final ProjectMemberMapper projectMemberMapper;
}