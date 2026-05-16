package com.junoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活跃项目 Mapper
 *
 * @author Fan
 */
@Mapper
public interface ProjectActiveMapper extends BaseMapper<Project> {

    /**
     * 获取活跃项目列表（状态为1、3、6）
     * @param page 分页对象
     * @param queryDTO 查询条件
     * @param accessibleProjectIds 用户可访问的项目ID列表（权限控制）
     * @return 活跃项目列表
     */
    Page<ProjectListVO> getActiveList(
            Page<ProjectListVO> page,
            @Param("queryDTO") ProjectListQueryDTO queryDTO,
            @Param("accessibleProjectIds") List<Long> accessibleProjectIds
    );
}
