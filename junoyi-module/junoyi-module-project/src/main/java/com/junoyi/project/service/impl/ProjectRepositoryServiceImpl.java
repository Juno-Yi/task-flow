package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.convert.ProjectRepositoryConverter;
import com.junoyi.project.domain.dto.ProjectRepositoryDTO;
import com.junoyi.project.domain.po.ProjectRepository;
import com.junoyi.project.domain.vo.ProjectRepositoryVO;
import com.junoyi.project.enums.ProjectRecordTargetType;
import com.junoyi.project.enums.ProjectRecordType;
import com.junoyi.project.event.ProjectRecordEvent;
import com.junoyi.project.mapper.ProjectRepositoryMapper;
import com.junoyi.project.service.IProjectRepositoryService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.vo.SysDictDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目仓库业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectRepositoryServiceImpl implements IProjectRepositoryService {

    private final ProjectRepositoryMapper projectRepositoryMapper;
    private final SysDictApi sysDictApi;

    /**
     * 获取项目仓库列表
     * @param projectId 项目ID
     * @return 项目仓库列表
     */
    @Override
    public List<ProjectRepositoryVO> getRepositoryList(Long projectId) {
        // 查询仓库列表
        LambdaQueryWrapper<ProjectRepository> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectRepository::getProjectId, projectId)
                .orderByDesc(ProjectRepository::getIsMain)
                .orderByAsc(ProjectRepository::getId);

        List<ProjectRepository> repositories = projectRepositoryMapper.selectList(wrapper);

        // 转换为 VO
        List<ProjectRepositoryVO> voList = repositories.stream()
                .map(ProjectRepositoryConverter::toVO)
                .collect(Collectors.toList());

        // 填充字典数据
        fillDictData(voList);


        return voList;
    }

    /**
     * 填充字典数据
     */
    private void fillDictData(List<ProjectRepositoryVO> voList) {
        if (voList.isEmpty()) {
            return;
        }

        // 获取仓库平台字典
        List<SysDictDataVO> platformDict = sysDictApi.getDictDataByType("project_repo_platform");
        Map<String, SysDictDataVO> platformMap = platformDict.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, d -> d));

        // 填充字典标签
        for (ProjectRepositoryVO vo : voList) {
            // 仓库平台
            if (vo.getType() != null && platformMap.containsKey(vo.getType())) {
                vo.setTypeLabel(platformMap.get(vo.getType()).getDictLabel());
            }

            // 状态
            vo.setStatusLabel(vo.getStatus() == 1 ? "正常" : "禁用");
        }
    }


    /**
     * 添加项目仓库
     * @param dto 传输数据
     */
    @Override
    public void addRepository(ProjectRepositoryDTO dto) {
        ProjectRepository repository = ProjectRepositoryConverter.toPO(dto);
        repository.setCreateBy(SecurityUtils.getUserName());
        repository.setCreateTime(DateUtils.getNowDate());

        projectRepositoryMapper.insert(repository);

        // 发布项目动态事件
        EventBus.get().callEvent(new ProjectRecordEvent(
                repository.getProjectId(),
                SecurityUtils.getUserId(),
                ProjectRecordType.CREATE_REPOSITORY,
                ProjectRecordTargetType.REPOSITORY,
                "添加了仓库「" + repository.getName() + "」"
        ));
    }

    /**
     * 更新项目仓库
     * @param dto 传输数据
     */
    @Override
    public void updateRepository(ProjectRepositoryDTO dto) {
        ProjectRepository repository = ProjectRepositoryConverter.toPO(dto);
        repository.setUpdateBy(SecurityUtils.getUserName());
        repository.setUpdateTime(DateUtils.getNowDate());

        projectRepositoryMapper.updateById(repository);

        // 发布项目动态事件
        EventBus.get().callEvent(new ProjectRecordEvent(
                repository.getProjectId(),
                SecurityUtils.getUserId(),
                ProjectRecordType.UPDATE_REPOSITORY,
                ProjectRecordTargetType.REPOSITORY,
                "修改了仓库「" + repository.getName() + "」"
        ));
    }

    /**
     * 删除项目仓库
     * @param id 项目仓库ID
     */
    @Override
    public void deleteRepository(Long id) {
        ProjectRepository repository = projectRepositoryMapper.selectById(id);
        if (repository != null) {
            projectRepositoryMapper.deleteById(id);

            // 发布项目动态
            EventBus.get().callEvent(new ProjectRecordEvent(
                    repository.getProjectId(),
                    SecurityUtils.getUserId(),
                    ProjectRecordType.DELETE_REPOSITORY,
                    ProjectRecordTargetType.REPOSITORY,
                    "删除了仓库「" + repository.getName() + "」"
            ));
        }
    }


}