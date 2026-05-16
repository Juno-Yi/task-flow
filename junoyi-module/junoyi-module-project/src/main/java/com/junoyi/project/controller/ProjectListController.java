package com.junoyi.project.controller;

import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.service.IProjectListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目管理控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/list")
@RequiredArgsConstructor
public class ProjectListController extends BaseController {

    private final IProjectListService projectListService;
}