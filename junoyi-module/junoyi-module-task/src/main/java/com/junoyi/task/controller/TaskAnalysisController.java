package com.junoyi.task.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.web.domain.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 任务分析统计控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/task/analysis")
public class TaskAnalysisController extends BaseController {


    /**
     * 获取任务分析总览数据
     */
    @GetMapping("/overview")
    public R<String> getTaskAnalysisOverview(){
        return R.ok("这是响应体响应信息消息文本","Hello World，响应数据");
    }
}