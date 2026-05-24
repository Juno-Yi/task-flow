package com.junoyi.project.convert;

import com.junoyi.project.domain.dto.ProjectRequirementDTO;
import com.junoyi.project.domain.po.ProjectRequirement;
import com.junoyi.project.domain.vo.ProjectRequirementVO;
import org.springframework.beans.BeanUtils;

/**
 * 项目需求转换器
 *
 * @author Fan
 */
public final class ProjectRequirementConverter {

    private ProjectRequirementConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 将 ProjectRequirement 实体对象转换成 ProjectRequirementVO 对象
     *
     * @param entity ProjectRequirement实体对象
     * @return ProjectRequirementVO 对象
     */
    public static ProjectRequirementVO toVO(ProjectRequirement entity) {
        if (entity == null) {
            return null;
        }
        ProjectRequirementVO vo = new ProjectRequirementVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 将 ProjectRequirementDTO 转换成 ProjectRequirement 实体对象
     *
     * @param dto ProjectRequirementDTO对象
     * @return ProjectRequirement 实体对象
     */
    public static ProjectRequirement toPO(ProjectRequirementDTO dto) {
        if (dto == null) {
            return null;
        }
        ProjectRequirement po = new ProjectRequirement();
        po.setId(dto.getId());
        po.setTitle(dto.getTitle());
        po.setDescription(dto.getDescription());
        po.setPriority(dto.getPriority());
        po.setSource(dto.getSource());
        po.setType(dto.getType());
        return po;
    }
}

