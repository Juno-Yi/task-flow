package com.junoyi.oauth.controller;

import com.junoyi.framework.web.domain.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 钉钉 Oauth 控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/auth/dingtalk")
@RequiredArgsConstructor
public class DingTalkOauthController extends BaseController {
}