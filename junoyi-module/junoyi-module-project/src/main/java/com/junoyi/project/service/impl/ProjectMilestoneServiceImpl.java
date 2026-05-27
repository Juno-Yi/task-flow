package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.convert.ProjectMilestoneConverter;
import com.junoyi.project.domain.dto.ProjectMilestoneDTO;
import com.junoyi.project.domain.po.ProjectMilestone;
import com.junoyi.project.domain.vo.ProjectMilestoneVO;
import com.junoyi.project.enums.ProjectRecordTargetType;
import com.junoyi.project.enums.ProjectRecordType;
import com.junoyi.project.event.ProjectRecordEvent;
import com.junoyi.project.exception.ProjectException;
import com.junoyi.project.mapper.ProjectMilestoneMapper;
import com.junoyi.project.service.IProjectMilestoneService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.vo.SysDictDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目里程碑业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectMilestoneServiceImpl implements IProjectMilestoneService {

    private final ProjectMilestoneMapper projectMilestoneMapper;
    private final SysDictApi sysDictApi;

    /**
     * 获取项目里程碑列表
     * @param projectId 项目ID
     * @return 项目里程碑列表
     */
    @Override
    public List<ProjectMilestoneVO> getProjectMilestoneList(Long projectId) {
        List<ProjectMilestoneVO> list = projectMilestoneMapper.selectProjectMilestoneVOList(projectId);

        // 使用字典翻译 status
        for (ProjectMilestoneVO vo : list) {
            if (vo.getStatus() != null) {
                SysDictDataVO statusDict = sysDictApi.getDictItem("project_milestone_status", String.valueOf(vo.getStatus()));
                if (statusDict != null) {
                    vo.setStatusLabel(statusDict.getDictLabel());
                    vo.setStatusType(statusDict.getListClass());
                }
            }
        }

        return list;
    }

    /**
     * 添加项目里程碑
     * @param dto 传输数据
     */
    @Override
    public void addProjectMilestone(ProjectMilestoneDTO dto) {
        ProjectMilestone projectMilestone = ProjectMilestoneConverter.toPO(dto);

        projectMilestone.setStatus(0);
        projectMilestone.setDelFlag(false);
        projectMilestone.setCreateBy(SecurityUtils.getUserName());
        projectMilestone.setCreateTime(DateUtils.getNowDate());

        projectMilestoneMapper.insert(projectMilestone);

        // 发布项目动态
        EventBus.get().callEvent(new ProjectRecordEvent(
                dto.getProjectId(),
                SecurityUtils.getUserId(),
                ProjectRecordType.CREATE_MILESTONE,
                ProjectRecordTargetType.MILESTONE,
                "创建了里程碑「" + dto.getName() + "」"
        ));
    }

    /**
     * 更新项目里程碑
     * @param dto 传输数据
     */
    @Override
    public void updateProjectMilestone(ProjectMilestoneDTO dto) {
        ProjectMilestone projectMilestone = ProjectMilestoneConverter.toPO(dto);

        projectMilestone.setUpdateBy(SecurityUtils.getUserName());
        projectMilestone.setUpdateTime(DateUtils.getNowDate());

        projectMilestoneMapper.updateById(projectMilestone);

        // 发布项目动态
        EventBus.get().callEvent(new ProjectRecordEvent(
                dto.getProjectId(),
                SecurityUtils.getUserId(),
                ProjectRecordType.UPDATE_MILESTONE,
                ProjectRecordTargetType.REQUIREMENT,
                projectMilestone.getId(),
                "更新了里程碑「" + projectMilestone.getName() + "」"
        ));
    }

    /**
     * 删除项目里程碑
     * @param projectId 项目ID
     * @param projectMilestoneId 项目里程碑ID
     */
    @Override
    public void deleteProjectMilestone(Long projectId, Long projectMilestoneId) {
        LambdaUpdateWrapper<ProjectMilestone> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectMilestone::getProjectId, projectId)
                .eq(ProjectMilestone::getId, projectMilestoneId)
                .eq(ProjectMilestone::getDelFlag, false)
                .set(ProjectMilestone::getDelFlag, true);

        int rows = projectMilestoneMapper.update(null, updateWrapper);
        // 删除失败
        if (rows <= 0)
            throw new ProjectException("项目里程碑不存在或已删除");

        ProjectMilestone projectMilestone = projectMilestoneMapper.selectById(projectMilestoneId);

        // 发布项目动态
        EventBus.get().callEvent(new ProjectRecordEvent(
                projectId,
                SecurityUtils.getUserId(),
                ProjectRecordType.DELETE_MILESTONE,
                ProjectRecordTargetType.MILESTONE,
                "删除了里程碑「" + projectMilestone.getName() + "」"
        ));
    }

    /**
     * 完成项目里程碑
     * @param projectId 项目ID
     * @param projectMilestoneId 项目里程碑ID
     */
    @Override
    public void completeProjectMilestone(Long projectId, Long projectMilestoneId) {
        LambdaUpdateWrapper<ProjectMilestone> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectMilestone::getProjectId, projectId)
                .eq(ProjectMilestone::getId, projectMilestoneId)
                .eq(ProjectMilestone::getDelFlag, false)
                .set(ProjectMilestone::getStatus, 1);

        int rows = projectMilestoneMapper.update(updateWrapper);
        // 更新失败
        if (rows <= 0)
            throw new ProjectException("项目里程碑不存在或已删除");

        ProjectMilestone projectMilestone = projectMilestoneMapper.selectById(projectMilestoneId);

        // 发布项目动态
        EventBus.get().callEvent(new ProjectRecordEvent(
                projectId,
                SecurityUtils.getUserId(),
                ProjectRecordType.COMPLETE_MILESTONE,
                ProjectRecordTargetType.MILESTONE,
                "完成了里程碑「" + projectMilestone.getName() + "」"
        ));
    }
}