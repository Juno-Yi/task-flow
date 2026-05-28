package com.junoyi.task.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.task.domain.vo.TaskItemVO;
import com.junoyi.task.domain.vo.TaskListDetailVO;
import com.junoyi.task.service.IMyTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 我的任务控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/task/my-task")
@RequiredArgsConstructor
public class MyTaskController extends BaseController {

    private final IMyTaskService myTaskService;

    /**
     * 获取当前用户近一个月任务列表
     */
    @GetMapping("/list")
    @PlatformScope({PlatformType.ADMIN_WEB, PlatformType.FRONT_DESK_WEB})
    public R<List<TaskItemVO>> getCurrentUserTaskList(){
        Long userId = getUserId();
        if (userId == null || userId == 0){
            return R.fail("请登录后操作");
        }
        return R.ok(myTaskService.getCurrentMonthMyTask(userId));
    }

    /**
     * 获取我的任务详情
     */
    @GetMapping("/{taskId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<TaskListDetailVO> getMyTaskDetail(@PathVariable("taskId") Long taskId){
        Long userId = getUserId();
        if (userId == null || userId == 0){
            return R.fail("请登录后操作");
        }
        return R.ok(myTaskService.getMyTaskDetail(taskId, userId));
    }

    /**
     * 开始任务
     */
    @PutMapping("/start/{taskId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> startTask(@PathVariable("taskId") Long taskId){
        myTaskService.startTask(taskId);
        return R.ok();
    }
}