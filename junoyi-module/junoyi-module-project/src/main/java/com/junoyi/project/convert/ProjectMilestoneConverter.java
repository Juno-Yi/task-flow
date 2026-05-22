package com.junoyi.project.convert;

import com.junoyi.project.domain.dto.ProjectMilestoneDTO;
import com.junoyi.project.domain.po.ProjectMilestone;
import com.junoyi.project.domain.vo.ProjectMilestoneVO;

/**
 * 项目里程碑转换器
 *
 * @author Fan
 */
public final class ProjectMilestoneConverter {

    /**
     * Entity 转换 VO
     * @param entity 实体
     * @return VO响应
     */
    public static ProjectMilestoneVO toVO(ProjectMilestone entity){
        ProjectMilestoneVO vo = new ProjectMilestoneVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setDueTime(entity.getDueTime());
        vo.setFinishTime(entity.getFinishTime());
        vo.setSort(entity.getSort());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /**
     * DTO 转换 PO
     * @param dto DTO
     * @return PO
     */
    public static ProjectMilestone toPO(ProjectMilestoneDTO dto){
        ProjectMilestone po = new ProjectMilestone();
        po.setId(dto.getId());
        po.setProjectId(dto.getProjectId());
        po.setName(dto.getName());
        po.setDescription(dto.getDescription());
        po.setDueTime(dto.getDueTime());
        po.setSort(dto.getSort());
        po.setOwnerId(dto.getOwnerId());
        return po;
    }
}