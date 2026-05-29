package com.junoyi.task.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.vo.TaskListVO;
import com.junoyi.task.service.ITaskApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务审核控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/task/approval")
@RequiredArgsConstructor
public class TaskApprovalController extends BaseController {

    private final ITaskApprovalService taskApprovalService;

    /**
     * 获取任务审核列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "task.ui.approval.view"
    )
    public R<PageResult<TaskListVO>> getApprovalList(TaskListQueryDTO queryDTO) {
        return R.ok(taskApprovalService.getApprovalList(queryDTO, buildPage()));
    }
}