package com.junoyi.project.listener;

import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.annotation.EventListener;
import com.junoyi.framework.event.enums.EventPriority;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.project.domain.po.ProjectRecord;
import com.junoyi.project.event.ProjectRecordEvent;
import com.junoyi.project.service.IProjectRecordService;
import lombok.RequiredArgsConstructor;

/**
 * 项目动态记录事件监听器
 *
 * @author Fan
 */
@EventListener
@RequiredArgsConstructor
public class ProjectRecordListener {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(ProjectRecordListener.class);

    private final IProjectRecordService projectRecordService;

    /**
     * 当项目动态记录事件被触发时
     * @param event 项目动态记录事件
     */
    @EventHandler(priority = EventPriority.NORMAL, async = true)
    public void onProjectRecordEvent(ProjectRecordEvent event){
        try {
            ProjectRecord projectRecord = new ProjectRecord();

            projectRecord.setProjectId(event.getProjectId());
            projectRecord.setOperatorId(event.getOperatorId());
            projectRecord.setType(event.getType());
            projectRecord.setTargetType(event.getTargetType());
            projectRecord.setTargetId(event.getTargetId());
            projectRecord.setContent(event.getContent());
            projectRecord.setCreateTime(DateUtils.getNowDate());

            projectRecordService.addProjectRecord(projectRecord);
        } catch (Exception e){
            log.error("projectRecord","记录项目动态失败：{}",e.getMessage());
        }

    }
}