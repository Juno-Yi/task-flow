package com.junoyi.task.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.task.domain.bo.TaskActionBO;
import com.junoyi.task.domain.dto.TaskApprovalDTO;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.vo.TaskListVO;
import com.junoyi.task.service.ITaskApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 驳回任务
     */
    @PostMapping("/reject")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "task.ui.approval.reject.button"
    )
    public R<Void> rejectTask(@RequestBody TaskApprovalDTO dto) {
        Long userId = getUserId();
        if (userId == null || userId == 0) {
            return R.fail("请先登录");
        }
        TaskActionBO bo = TaskActionBO.builder()
                .userId(userId)
                .taskActionType(2)
                .dto(dto)
                .build();
//        taskApprovalService.rejectTask(bo);
        return R.ok();
    }


    /**
     * 审核通过任务
     */
    @PostMapping("/pass")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "task.ui.approval.pass.button"
    )
    public R<Void> passTask(@RequestBody TaskApprovalDTO dto) {
        Long userId = getUserId();
        if (userId == null || userId == 0) {
            return R.fail("请先登录");
        }
        TaskActionBO bo = TaskActionBO.builder()
                .userId(userId)
                .taskActionType(3)
                .dto(dto)
                .build();
        taskApprovalService.passTask(bo);
        return R.ok();
    }
}