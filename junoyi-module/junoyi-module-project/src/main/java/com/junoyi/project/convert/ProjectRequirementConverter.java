package com.junoyi.project.convert;

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
}

