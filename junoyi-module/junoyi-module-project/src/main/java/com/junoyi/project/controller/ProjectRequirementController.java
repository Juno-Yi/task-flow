package com.junoyi.project.controller;

import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.service.IProjectRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目需求控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/requirement")
@RequiredArgsConstructor
public class ProjectRequirementController extends BaseController {

    private final IProjectRequirementService projectRequirementService;


}