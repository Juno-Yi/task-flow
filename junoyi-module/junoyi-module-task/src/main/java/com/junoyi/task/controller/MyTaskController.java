package com.junoyi.task.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.task.domain.vo.TaskItemVO;
import com.junoyi.task.service.IMyTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}