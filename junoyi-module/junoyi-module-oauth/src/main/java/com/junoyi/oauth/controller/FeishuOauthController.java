package com.junoyi.oauth.controller;

import com.junoyi.framework.web.domain.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 飞书 Oauth控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/auth/feishu")
@RequiredArgsConstructor
public class FeishuOauthController extends BaseController {
}