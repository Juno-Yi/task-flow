package com.junoyi.project.convert;

import com.junoyi.project.domain.dto.ProjectRepositoryDTO;
import com.junoyi.project.domain.po.ProjectRepository;
import com.junoyi.project.domain.vo.ProjectRepositoryVO;

/**
 * 项目仓库转换器
 *
 * @author Fan
 */
public final class ProjectRepositoryConverter {

    /**
     * 将 ProjectRepositoryVO 转换 ProjectRepository
     * @param entity ProjectRepository
     * @return ProjectRepositoryVO
     */
    public static ProjectRepositoryVO toVO(ProjectRepository entity){
        ProjectRepositoryVO vo = new ProjectRepositoryVO();
        vo.setId(entity.getId());
        vo.setProjectId(entity.getProjectId());
        vo.setName(entity.getName());
        vo.setType(entity.getType());
        vo.setUrl(entity.getUrl());
        vo.setBranch(entity.getBranch());
        vo.setDescription(entity.getDescription());
        vo.setIsMain(entity.getIsMain());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateBy(entity.getCreateBy());
        vo.setCreateTime(entity.getCreateTime().toString());
        vo.setUpdateBy(entity.getUpdateBy());
        vo.setUpdateTime(entity.getUpdateTime().toString());
        return vo;
    }

    /**
     * 将 ProjectRepositoryDTO 转换成 ProjectRepository
     * @param dto
     * @return
     */
    public static ProjectRepository toPO(ProjectRepositoryDTO dto){

    }
}