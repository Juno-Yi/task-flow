package com.junoyi.task.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.task.domain.bo.TaskActionBO;
import com.junoyi.task.domain.dto.TaskSubmitDTO;
import com.junoyi.task.domain.vo.TaskItemVO;
import com.junoyi.task.domain.vo.TaskListDetailVO;
import com.junoyi.task.domain.vo.TaskMonthStatisticsVO;
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
     * 获取当前用户近一个月任务列表（按照状态查询并且分页）
     *
     * @param status 任务状态
     * @return 分页任务列表
     */
    @GetMapping("/list/{status}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<TaskItemVO>> getCurrentUserTaskListByStatus(@PathVariable("status") Integer status) {
        // 获取当前登录用户ID
        Long userId = getUserId();
        if (userId == null || userId == 0) {
            return R.fail("请登录后操作");
        }

        return R.ok(myTaskService.getCurrentMonthMyTaskByStatus(userId, status, buildPage()));
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

    /**
     * 提交任务
     */
    @PostMapping("/commit")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> submitTask(@RequestBody TaskSubmitDTO dto){
        Long userId = getUserId();
        if (userId == null || userId == 0){
            return R.ok("请先登录");
        }
        TaskActionBO bo = TaskActionBO.builder()
                .userId(userId)
                // 任务操作类型为提交任务 1
                .taskActionType(1)
                .dto(dto)
                .build();
        myTaskService.submitTask(bo);
        return R.ok();
    }

    /**
     * 获取用户当前月任务统计数据
     */
    @GetMapping("/month/statistics")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<TaskMonthStatisticsVO> getTaskMonthStatistics(){
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == 0 || currentUserId == null)
            return R.fail("非法请求");
        return R.ok(myTaskService.getTaskMonthStatistics(currentUserId));
    }
}