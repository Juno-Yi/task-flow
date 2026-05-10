package com.junoyi.task.controller;


import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.vo.TaskListVO;
import com.junoyi.task.service.ITaskListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 任务管理控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/task/list")
@RequiredArgsConstructor
public class TaskListController extends BaseController {

    private final ITaskListService taskListService;

    /**
     * 获取任务管理列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "task.ui.list.view"
    )
    public R<PageResult<TaskListVO>> getTaskList(TaskListQueryDTO queryDTO){
        return R.ok(taskListService.getTaskList(queryDTO,getPageQuery()));
    }

    /**
     * 添加任务
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "task.ui.list.button.add"
    )
    public R<Void> addTask(){
        return R.ok();
    }

    /**
     * 修改任务
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "task.ui.list.button.edit"
    )
    public R<Void> updateTask(){
        return R.ok();
    }
}