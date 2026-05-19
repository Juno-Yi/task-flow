package com.junoyi.project.convert;

import com.junoyi.project.domain.dto.ProjectListDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectOptionVO;
import org.springframework.beans.BeanUtils;

/**
 * 项目转换器工具类
 *
 * @author Fan
 */
public final class ProjectConverter {

    private ProjectConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 将ProjectListDTO转换成Project PO实体对象
     *
     * @param dto ProjectListDTO对象
     * @return Project PO实体对象
     */
    public static Project toPo(ProjectListDTO dto) {
        if (dto == null) {
            return null;
        }
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        return project;
    }

    /**
     * 将 Project 转换成 ProjectOptionVO 实体对象
     * @param entity Project实体
     * @return ProjectOptionVO实体
     */
    public static ProjectOptionVO toOptionVO(Project entity){
        ProjectOptionVO projectOptionVO = new ProjectOptionVO();
        projectOptionVO.setId(entity.getId());
        projectOptionVO.setNo(entity.getNo());
        projectOptionVO.setName(entity.getName());
        projectOptionVO.setStatus(entity.getStatus());
        projectOptionVO.setPriority(entity.getPriority());
        return projectOptionVO;
    }
}