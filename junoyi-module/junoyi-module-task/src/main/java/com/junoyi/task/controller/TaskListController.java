package com.junoyi.task.controller;


import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.task.domain.dto.TaskListDTO;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.vo.TaskListDetailVO;
import com.junoyi.task.domain.vo.TaskListVO;
import com.junoyi.task.service.ITaskListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 任务列表控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/task/list")
@RequiredArgsConstructor
public class TaskListController extends BaseController {

    private final ITaskListService taskListService;

    /**
     * 获取任务列表（分页查询）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"task.ui.list.view"}
    )
    public R<PageResult<TaskListVO>> getList(TaskListQueryDTO queryDTO) {
        return R.ok(taskListService.getTaskList(queryDTO, getPageQuery()));
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"task.ui.list.view"}
    )
    public R<TaskListDetailVO> getTaskDetail(@PathVariable("taskId") Long taskId){
        return R.ok(taskListService.getTaskDetail(taskId));
    }

    /**
     * 添加任务
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"task.ui.list.add.button"}
    )
    public R<Void> addTask(@RequestBody TaskListDTO dto){
        taskListService.addTask(dto);
        return R.ok();
    }

    /**
     * 修改任务
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"task.ui.list.edit.button"}
    )
    public R<Void> updateTask(@RequestBody TaskListDTO dto){
        taskListService.updateTask(dto);
        return R.ok();
    }

    /**
     * 催促提醒用户完成任务
     */
    @PostMapping("/remind/{taskId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"task.ui.list.edit.button"}
    )
    public R<Void> remindUserToCompleteTask(@PathVariable("taskId") Long taskId){
        taskListService.remindUserToCompleteTask(taskId);
        return R.ok();
    }
}
