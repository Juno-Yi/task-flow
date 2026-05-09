package com.junoyi.task.controller;

import com.junoyi.framework.web.domain.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务管理控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/task/list")
@RequiredArgsConstructor
public class TaskListController extends BaseController {
}