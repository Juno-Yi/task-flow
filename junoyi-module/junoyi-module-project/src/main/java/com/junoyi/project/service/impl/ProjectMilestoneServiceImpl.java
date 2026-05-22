package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.convert.ProjectMilestoneConverter;
import com.junoyi.project.domain.dto.ProjectMilestoneDTO;
import com.junoyi.project.domain.po.ProjectMilestone;
import com.junoyi.project.domain.vo.ProjectMilestoneVO;
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

        // TODO: 发布项目动态
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

        // TODO: 发布项目动态
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

        // TODO: 发布项目动态
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

        // TODO: 发布项目动态
    }
}