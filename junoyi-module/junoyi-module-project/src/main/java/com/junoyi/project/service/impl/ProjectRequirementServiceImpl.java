package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.project.convert.ProjectRequirementConverter;
import com.junoyi.project.domain.dto.ProjectRequirementQueryDTO;
import com.junoyi.project.domain.po.ProjectRequirement;
import com.junoyi.project.domain.vo.ProjectRequirementVO;
import com.junoyi.project.mapper.ProjectRequirementMapper;
import com.junoyi.project.service.IProjectRequirementService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.vo.SysDictDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目需求业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectRequirementServiceImpl implements IProjectRequirementService {

    private final ProjectRequirementMapper projectRequirementMapper;
    private final SysDictApi sysDictApi;

    /**
     * 获取项目需求列表（分页）
     * @param projectId 项目ID
     * @param queryDTO 查询参数
     * @param page 分页
     * @return 需求列表
     */
    @Override
    public PageResult<ProjectRequirementVO> getRequirementList(Long projectId,
                                                               ProjectRequirementQueryDTO queryDTO,
                                                               Page<ProjectRequirement> page) {
        // 构建查询条件
        LambdaQueryWrapper<ProjectRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectRequirement::getProjectId, projectId)
                .eq(ProjectRequirement::getDelFlag, false)
                .like(StringUtils.isNotBlank(queryDTO.getTitle()), ProjectRequirement::getTitle, queryDTO.getTitle())
                .eq(queryDTO.getPriority() != null, ProjectRequirement::getPriority, queryDTO.getPriority())
                .eq(queryDTO.getStatus() != null, ProjectRequirement::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getSource() != null, ProjectRequirement::getSource, queryDTO.getSource())
                .eq(queryDTO.getType() != null, ProjectRequirement::getType, queryDTO.getType())
                .orderByDesc(ProjectRequirement::getCreateTime);

        // 分页查询
        Page<ProjectRequirement> resultPage = projectRequirementMapper.selectPage(page, wrapper);
        List<ProjectRequirement> requirements = resultPage.getRecords();

        // 如果没有数据，直接返回空结果
        if (requirements.isEmpty()) {
            return PageResult.of(
                    new ArrayList<>(),
                    resultPage.getTotal(),
                    (int) resultPage.getCurrent(),
                    (int) resultPage.getSize()
            );
        }

        // 批量查询字典数据（避免 N+1 查询）
        List<SysDictDataVO> priorityDictList = sysDictApi.getDictDataByType("project_requirement_priority");
        List<SysDictDataVO> statusDictList = sysDictApi.getDictDataByType("project_requirement_status");
        List<SysDictDataVO> sourceDictList = sysDictApi.getDictDataByType("project_requirement_source");
        List<SysDictDataVO> typeDictList = sysDictApi.getDictDataByType("project_requirement_type");

        // 转换为 Map 便于查找
        Map<String, SysDictDataVO> priorityDictMap = priorityDictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict));
        Map<String, SysDictDataVO> statusDictMap = statusDictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict));
        Map<String, SysDictDataVO> sourceDictMap = sourceDictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict));
        Map<String, SysDictDataVO> typeDictMap = typeDictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict));

        // 转换为VO并填充字典数据
        List<ProjectRequirementVO> voList = new ArrayList<>();
        for (ProjectRequirement requirement : requirements) {
            ProjectRequirementVO vo = ProjectRequirementConverter.toVO(requirement);

            // 字典翻译 - 优先级
            if (requirement.getPriority() != null) {
                SysDictDataVO priorityDict = priorityDictMap.get(String.valueOf(requirement.getPriority()));
                if (priorityDict != null) {
                    vo.setPriorityLabel(priorityDict.getDictLabel());
                    vo.setPriorityType(priorityDict.getListClass());
                }
            }

            // 字典翻译 - 状态
            if (requirement.getStatus() != null) {
                SysDictDataVO statusDict = statusDictMap.get(String.valueOf(requirement.getStatus()));
                if (statusDict != null) {
                    vo.setStatusLabel(statusDict.getDictLabel());
                    vo.setStatusType(statusDict.getListClass());
                }
            }

            // 字典翻译 - 来源
            if (requirement.getSource() != null) {
                SysDictDataVO sourceDict = sourceDictMap.get(String.valueOf(requirement.getSource()));
                if (sourceDict != null) {
                    vo.setSourceLabel(sourceDict.getDictLabel());
                    vo.setSourceType(sourceDict.getListClass());
                }
            }

            // 字典翻译 - 类型
            if (requirement.getType() != null) {
                SysDictDataVO typeDict = typeDictMap.get(String.valueOf(requirement.getType()));
                if (typeDict != null) {
                    vo.setTypeLabel(typeDict.getDictLabel());
                    vo.setTypeLabelType(typeDict.getListClass());
                }
            }

            voList.add(vo);
        }

        // 返回分页结果
        return PageResult.of(
                voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );
    }


}