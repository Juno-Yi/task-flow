package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.po.ProjectRecord;
import com.junoyi.project.domain.vo.ProjectRecordVO;
import com.junoyi.project.enums.ProjectRecordTargetType;
import com.junoyi.project.enums.ProjectRecordType;
import com.junoyi.project.mapper.ProjectMapper;
import com.junoyi.project.mapper.ProjectRecordMapper;
import com.junoyi.project.service.IProjectRecordService;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目动态记录业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectRecordServiceImpl implements IProjectRecordService {

    private final ProjectRecordMapper projectRecordMapper;
    private final ProjectMapper projectMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 添加项目动态记录
     * @param projectRecord 项目动态记录
     */
    @Override
    public void addProjectRecord(ProjectRecord projectRecord) {
        projectRecordMapper.insert(projectRecord);
    }

    /**
     * 获取项目动态记录列表
     * @param projectNo 项目编号
     * @return 项目动态记录列表
     */
    @Override
    public PageResult<ProjectRecordVO> getProjectRecordList(String projectNo, Page<ProjectRecord> page) {
        LambdaQueryWrapper<ProjectRecord> wrapper = new LambdaQueryWrapper<>();

        // 如果指定了项目编号，先查询项目ID
        if (StringUtils.hasText(projectNo)) {
            LambdaQueryWrapper<Project> projectWrapper = new LambdaQueryWrapper<>();
            projectWrapper.eq(Project::getNo, projectNo)
                    .eq(Project::isDelFlag, false);
            Project project = projectMapper.selectOne(projectWrapper);

            if (project != null) {
                wrapper.eq(ProjectRecord::getProjectId, project.getId());
            } else {
                // 项目不存在，返回空结果
                return PageResult.of(List.of(), 0L, (int) page.getCurrent(), (int) page.getSize());
            }
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(ProjectRecord::getCreateTime);

        // 分页查询
        Page<ProjectRecord> resultPage = projectRecordMapper.selectPage(page, wrapper);
        List<ProjectRecord> records = resultPage.getRecords();

        if (records.isEmpty()) {
            return PageResult.of(List.of(), 0L, (int) page.getCurrent(), (int) page.getSize());
        }

        // 批量查询项目信息
        List<Long> projectIds = records.stream()
                .map(ProjectRecord::getProjectId)
                .distinct()
                .toList();

        LambdaQueryWrapper<Project> projectWrapper = new LambdaQueryWrapper<>();
        projectWrapper.in(Project::getId, projectIds);
        List<Project> projects = projectMapper.selectList(projectWrapper);
        Map<Long, String> projectTitleMap = projects.stream()
                .collect(Collectors.toMap(Project::getId, Project::getName));

        // 批量查询操作者信息
        List<Long> operatorIds = records.stream()
                .map(ProjectRecord::getOperatorId)
                .distinct()
                .toList();

        List<SysUser> users = sysUserMapper.selectBatchIds(operatorIds);
        Map<Long, String> userNickNameMap = users.stream()
                .collect(Collectors.toMap(SysUser::getUserId, SysUser::getNickName));

        // 转换为 VO
        List<ProjectRecordVO> voList = records.stream().map(record -> {
            ProjectRecordVO vo = new ProjectRecordVO();
            BeanUtils.copyProperties(record, vo);

            // 设置项目标题
            vo.setProjectTitle(projectTitleMap.getOrDefault(record.getProjectId(), "未知项目"));

            // 设置操作者昵称
            vo.setOperatorNickName(userNickNameMap.getOrDefault(record.getOperatorId(), "未知用户"));

            // 设置操作类型标签
            vo.setTypeLabel(getRecordTypeLabel(record.getType()));

            // 设置操作目标类型标签
            vo.setTargetTypeLabel(getRecordTargetTypeLabel(record.getTargetType()));

            return vo;
        }).toList();

        return PageResult.of(voList, resultPage.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 获取操作类型标签
     */
    private String getRecordTypeLabel(Integer type) {
        if (type == null) {
            return "";
        }
        for (ProjectRecordType recordType : ProjectRecordType.values()) {
            if (recordType.getCode().equals(type)) {
                return recordType.getLabel();
            }
        }
        return "";
    }

    /**
     * 获取操作目标类型标签
     */
    private String getRecordTargetTypeLabel(Integer targetType) {
        if (targetType == null) {
            return "";
        }
        for (ProjectRecordTargetType recordTargetType : ProjectRecordTargetType.values()) {
            if (recordTargetType.getCode().equals(targetType)) {
                return recordTargetType.getLabel();
            }
        }
        return "";
    }
}